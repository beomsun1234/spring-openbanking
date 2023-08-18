package com.bs.openbanking.bank.api;

import com.bs.openbanking.bank.configuration.Config;
import com.bs.openbanking.bank.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Config.class, OpenBankUtil.class, OpenBankApiClient.class})
@ActiveProfiles(profiles = "openbank")
class OpenBankApiClientTest {

    @Autowired
    private OpenBankApiClient openBankApiClient;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;
    private String baseUrl = "https://testapi.openbanking.or.kr/v2.0";

    private ObjectMapper mapper = new ObjectMapper();
    @BeforeEach
    void setUp(){
        this.mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void requestTokenTest() throws JsonProcessingException {
        //given
        OpenBankRequestToken openBankRequestToken = new OpenBankRequestToken();
        openBankRequestToken.setCode("test");
        openBankRequestToken.setBankRequestToken("test", "test", "test", "test");

        OpenBankReponseToken expect = new OpenBankReponseToken();
        expect.setAccess_token("test");

        String resBody = mapper.writeValueAsString(expect);

        this.mockServer
                .expect(requestTo(baseUrl + "/token"))
                .andRespond(withSuccess(resBody, MediaType.APPLICATION_JSON));
        //when
        OpenBankReponseToken result = openBankApiClient.requestToken(openBankRequestToken);
        //then
        Assertions.assertEquals(expect.getAccess_token(),result.getAccess_token() );
    }

    @Test
    void requestAccountTest() throws JsonProcessingException {
        //given
        OpenBankAccountSearchRequestDto openBankAccountSearchRequestDto = new OpenBankAccountSearchRequestDto();
        openBankAccountSearchRequestDto.setUser_seq_no("1");
        openBankAccountSearchRequestDto.setSort_order("D");
        openBankAccountSearchRequestDto.setAccess_token("test");
        openBankAccountSearchRequestDto.setInclude_cancel_yn("N");

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl+ "/account/list")
                .queryParam("user_seq_no", openBankAccountSearchRequestDto.getUser_seq_no())
                .queryParam("include_cancel_yn", openBankAccountSearchRequestDto.getInclude_cancel_yn())
                .queryParam("sort_order", openBankAccountSearchRequestDto.getSort_order())
                .build().toUriString();

        OpenBankAccountSearchResponseDto expect = new OpenBankAccountSearchResponseDto();
        expect.setUser_name("park");

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
    void requestBalanceTest() throws JsonProcessingException {
        //given
        OpenBankBalanceRequestDto openBankBalanceRequestDto = OpenBankBalanceRequestDto.builder().bank_tran_id("test").fintech_use_num("123").tran_dtime(OpenBankUtil.getTransTime()).build();

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl+"/account/balance/fin_num")
                .queryParam("bank_tran_id", openBankBalanceRequestDto.getBank_tran_id())
                .queryParam("fintech_use_num", openBankBalanceRequestDto.getFintech_use_num())
                .queryParam("tran_dtime", openBankBalanceRequestDto.getTran_dtime())
                .build().toUriString();

        OpenBankBalanceResponseDto expect = new OpenBankBalanceResponseDto();
        expect.setBalance_amt("10000");

        String resBody = mapper.writeValueAsString(expect);

        this.mockServer
                .expect(requestTo(url))
                .andRespond(withSuccess(resBody, MediaType.APPLICATION_JSON));
        //when
        OpenBankBalanceResponseDto result = openBankApiClient.requestBalance(openBankBalanceRequestDto, "test");
        //then
        Assertions.assertEquals( expect.getBalance_amt(),result.getBalance_amt() );
    }

}