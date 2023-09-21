package com.bs.openbanking.bank.client;

import com.bs.openbanking.bank.configuration.RestTemplateConfig;
import com.bs.openbanking.bank.dto.openbank.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "openbank")
@ContextConfiguration(classes = {RestTemplateConfig.class, OpenBankApiClient.class})
class OpenBankApiClientTest {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private OpenBankApiClient openBankApiClient;
    private MockRestServiceServer mockServer;
    private String baseUrl = "https://testapi.openbanking.or.kr";

    private ObjectMapper mapper = new ObjectMapper();

    private static final String successCode = "A0000";
    @BeforeEach
    void setUp(){
        this.mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("성공- 토큰 요청의 경우 성공시 rsp_code = null 값")
    void requestTokenTest() throws JsonProcessingException {
        //given
        OpenBankRequestToken openBankRequestToken = OpenBankRequestToken
                .builder()
                .code("test")
                .client_id("test")
                .redirect_uri("test")
                .grant_type("test")
                .client_secret("test")
                .build();

        OpenBankResponseToken expect = new OpenBankResponseToken();
        expect.setAccess_token("test");
        expect.setRsp_code(null);

        String resBody = mapper.writeValueAsString(expect);

        this.mockServer
                .expect(requestTo(baseUrl + "/oauth/2.0/token"))
                .andRespond(withSuccess(resBody, MediaType.APPLICATION_JSON));
        //when
        OpenBankResponseToken result = openBankApiClient.requestToken(openBankRequestToken);
        //then
        Assertions.assertEquals(expect.getAccess_token(),result.getAccess_token() );
    }
    @Test
    @DisplayName("실패")
    void requestTokenTest_실패() throws JsonProcessingException {
        //given
        OpenBankRequestToken openBankRequestToken = OpenBankRequestToken
                .builder()
                .code("test")
                .client_id("test")
                .redirect_uri("test")
                .grant_type("test")
                .client_secret("test")
                .build();

        OpenBankResponseToken expect = new OpenBankResponseToken();
        expect.setRsp_code("O0001");

        String resBody = mapper.writeValueAsString(expect);

        this.mockServer
                .expect(requestTo(baseUrl + "/oauth/2.0/token"))
                .andRespond(withSuccess(resBody, MediaType.APPLICATION_JSON));
        //when, then
        Assertions.assertThrows(
                RuntimeException.class, ()-> openBankApiClient.requestToken(openBankRequestToken)
        );
    }

    @Test
    @DisplayName("성공")
    void requestAccountTest() throws JsonProcessingException {
        //given
        OpenBankAccountSearchRequestDto openBankAccountSearchRequestDto  =
                OpenBankAccountSearchRequestDto.builder()
                        .access_token("test")
                        .sort_order("D")
                        .user_seq_no("1")
                        .include_cancel_yn("N").build();

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl+ "/v2.0/account/list")
                .queryParam("user_seq_no", openBankAccountSearchRequestDto.getUser_seq_no())
                .queryParam("include_cancel_yn", openBankAccountSearchRequestDto.getInclude_cancel_yn())
                .queryParam("sort_order", openBankAccountSearchRequestDto.getSort_order())
                .build().toUriString();

        OpenBankAccountSearchResponseDto expect = new OpenBankAccountSearchResponseDto();
        expect.setUser_name("park");
        expect.setRsp_code(successCode);

        String resBody = mapper.writeValueAsString(expect);

        this.mockServer
                .expect(requestTo(url))
                .andRespond(withSuccess(resBody, MediaType.APPLICATION_JSON));
        //when
        OpenBankAccountSearchResponseDto result = openBankApiClient.requestAccountList(openBankAccountSearchRequestDto);
        //then
        Assertions.assertEquals(expect.getUser_name(),result.getUser_name());
    }
    @Test
    @DisplayName("실패")
    void requestAccountTest_실패() throws JsonProcessingException {
        //given
        OpenBankAccountSearchRequestDto openBankAccountSearchRequestDto  =
                OpenBankAccountSearchRequestDto.builder()
                        .access_token("test")
                        .sort_order("D")
                        .user_seq_no("1")
                        .include_cancel_yn("N").build();

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl+ "/v2.0/account/list")
                .queryParam("user_seq_no", openBankAccountSearchRequestDto.getUser_seq_no())
                .queryParam("include_cancel_yn", openBankAccountSearchRequestDto.getInclude_cancel_yn())
                .queryParam("sort_order", openBankAccountSearchRequestDto.getSort_order())
                .build().toUriString();

        OpenBankAccountSearchResponseDto expect = new OpenBankAccountSearchResponseDto();
        expect.setRsp_code("O0001");

        String resBody = mapper.writeValueAsString(expect);

        this.mockServer
                .expect(requestTo(url))
                .andRespond(withSuccess(resBody, MediaType.APPLICATION_JSON));
        //when, then
        Assertions.assertThrows(RuntimeException.class, () -> openBankApiClient.requestAccountList(openBankAccountSearchRequestDto));
    }

    @Test
    void requestBalanceTest() throws JsonProcessingException {
        //given
        OpenBankBalanceRequestDto openBankBalanceRequestDto = OpenBankBalanceRequestDto.builder()
                .bank_tran_id("test")
                .fintech_use_num("123")
                .accessToken("test")
                .tran_dtime(OpenBankUtil.getTransTime()).build();

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl+"/v2.0/account/balance/fin_num")
                .queryParam("bank_tran_id", openBankBalanceRequestDto.getBank_tran_id())
                .queryParam("fintech_use_num", openBankBalanceRequestDto.getFintech_use_num())
                .queryParam("tran_dtime", openBankBalanceRequestDto.getTran_dtime())
                .build().toUriString();

        OpenBankBalanceResponseDto expect = new OpenBankBalanceResponseDto();
        expect.setBalance_amt("10000");
        expect.setRsp_code(successCode);

        String resBody = mapper.writeValueAsString(expect);

        this.mockServer
                .expect(requestTo(url))
                .andRespond(withSuccess(resBody, MediaType.APPLICATION_JSON));
        //when
        OpenBankBalanceResponseDto result = openBankApiClient.requestBalance(openBankBalanceRequestDto);
        //then
        Assertions.assertEquals( expect.getBalance_amt(),result.getBalance_amt() );
    }

    @Test
    @DisplayName("실패")
    void requestBalanceTest_실패() throws JsonProcessingException {
        //given
        OpenBankBalanceRequestDto openBankBalanceRequestDto = OpenBankBalanceRequestDto.builder().bank_tran_id("test").fintech_use_num("123").tran_dtime(OpenBankUtil.getTransTime()).build();

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl+"/v2.0/account/balance/fin_num")
                .queryParam("bank_tran_id", openBankBalanceRequestDto.getBank_tran_id())
                .queryParam("fintech_use_num", openBankBalanceRequestDto.getFintech_use_num())
                .queryParam("tran_dtime", openBankBalanceRequestDto.getTran_dtime())
                .build().toUriString();

        OpenBankBalanceResponseDto expect = new OpenBankBalanceResponseDto();
        expect.setRsp_code("O0001");

        String resBody = mapper.writeValueAsString(expect);

        this.mockServer
                .expect(requestTo(url))
                .andRespond(withSuccess(resBody, MediaType.APPLICATION_JSON));
        //when, then
        Assertions.assertThrows(RuntimeException.class, ()-> openBankApiClient.requestBalance(openBankBalanceRequestDto));
    }

    @Test
    @DisplayName("성공")
    void 유저ci값_가져오기() throws JsonProcessingException {
        //given
        OpenBankUserInfoRequestDto openBankUserInfoRequestDto = OpenBankUserInfoRequestDto.builder().openBankId("1234").accessToken("test").build();

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl+"/v2.0/user/me")
                .queryParam("user_seq_no", openBankUserInfoRequestDto.getOpenBankId())
                .build().toUriString();

        OpenBankUserInfoResponseDto expect = OpenBankUserInfoResponseDto.builder().user_ci("test").rsp_code(successCode).build();

        String resBody = mapper.writeValueAsString(expect);

        this.mockServer
                .expect(requestTo(url))
                .andRespond(withSuccess(resBody, MediaType.APPLICATION_JSON));
        //when
        OpenBankUserInfoResponseDto result = openBankApiClient.requestOpenBankUserInfo(openBankUserInfoRequestDto);
        //then
        Assertions.assertEquals(expect.getUser_ci(), result.getUser_ci());
    }
    @Test
    @DisplayName("실패 - rsp_code = 알파벳 O로 시작하면 에러이다.")
    void 유저ci값_가져오기_실패() throws JsonProcessingException {
        //given
        OpenBankUserInfoRequestDto openBankUserInfoRequestDto = OpenBankUserInfoRequestDto.builder().openBankId("1234").accessToken("test").build();

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl+"/v2.0/user/me")
                .queryParam("user_seq_no", openBankUserInfoRequestDto.getOpenBankId())
                .build().toUriString();

        OpenBankUserInfoResponseDto expect = OpenBankUserInfoResponseDto.builder().user_ci("test").rsp_code("O").build();

        String resBody = mapper.writeValueAsString(expect);

        this.mockServer
                .expect(requestTo(url))
                .andRespond(withSuccess(resBody, MediaType.APPLICATION_JSON));
        //when, then
        Assertions.assertThrows(RuntimeException.class, ()->openBankApiClient.requestOpenBankUserInfo(openBankUserInfoRequestDto) );
    }


}