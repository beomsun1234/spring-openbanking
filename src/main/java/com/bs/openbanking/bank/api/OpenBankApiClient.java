package com.bs.openbanking.bank.api;

import com.bs.openbanking.bank.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


@Slf4j
@RequiredArgsConstructor
@Service
public class OpenBankApiClient {
    private final OpenBankUtil openBankutil;
    private final HttpHeaders httpHeaders;
    private final RestTemplate restTemplate;

    @Value("${openbank.useCode}")
    private String useCode; // 핀테크번호+U -> 거래고유번호 생성기
    @Value("${openbank.client-id}")
    private String clientId;
    @Value("${openbank.client-secret}")
    private String client_secret;

    private final static String redirect_uri = "http://localhost:8080/auth/openbank/callback";
    private final static String base_url = "https://testapi.openbanking.or.kr/v2.0";

    /**
     * 토큰발급요청
     * post 방식으로 key=vale 데이터 요청 (금결원)
     * 토큰요청 http 헤더타입은 application/x-www-form-urlencoded
     */
    public BankReponseToken requestToken(BankRequestToken bankRequestToken){
        httpHeaders.add("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");

        bankRequestToken.setBankRequestToken(clientId,client_secret,redirect_uri,"authorization_code");
        /**
         * 헤더의 컨텐트 타입이 application/x-www-form-urlencoded;charset=UTF-8이므로 객체를 집어넣을수 없음.. 그러므로 MultiValueMap 사용 해야함
         */
        HttpEntity httpEntity = generateHttpEntityWithBody(httpHeaders, bankRequestToken.toMultiValueMap());

        return restTemplate.exchange("https://testapi.openbanking.or.kr/oauth/2.0/token",HttpMethod.POST, httpEntity ,BankReponseToken.class).getBody();
    }
    private HttpEntity generateHttpEntityWithBody(HttpHeaders httpHeaders, MultiValueMap body) {
        return new HttpEntity<>(body, httpHeaders);
    }

    /**
     * 계좌조회
     * @param accountSearchRequestDto
     * @return
     */
    public BankAccountSearchResponseDto requestAccountList(AccountSearchRequestDto accountSearchRequestDto){
        String url = base_url+"/account/list";

        HttpEntity httpEntity = generateHttpEntity(generateAuthHeader(accountSearchRequestDto.getAccess_token()));

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("user_seq_no", accountSearchRequestDto.getUser_seq_no())
                .queryParam("include_cancel_yn", accountSearchRequestDto.getInclude_cancel_yn())
                .queryParam("sort_order", accountSearchRequestDto.getSort_order())
                .build();

        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET,httpEntity , BankAccountSearchResponseDto.class).getBody();
    }
    /**
     * 잔액조회
     */
    public BankBalanceResponseDto requestBalance(BankBalanceRequestDto bankBalanceRequestDto, String access_token){
        String url = base_url+"/account/balance/fin_num";

        HttpEntity httpEntity = generateHttpEntity(generateAuthHeader(access_token));

        bankBalanceRequestDto.setBankTransIdAndTranssDtime(openBankutil.getRandomNumber(bankBalanceRequestDto.getBank_tran_id()),openBankutil.getTransTime());

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("bank_tran_id",bankBalanceRequestDto.getBank_tran_id())
                .queryParam("fintech_use_num", bankBalanceRequestDto.getFintech_use_num())
                .queryParam("tran_dtime",bankBalanceRequestDto.getTran_dtime())
                .build();

        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, BankBalanceResponseDto.class).getBody();
    }

    /**
     *
     * 계좌이체
     */
    public AccountTransferResponseDto requestTransfer(String access_token, AccountTransferRequestDto accountTransferRequestDto){
        String url = base_url+"//transfer/withdraw/fin_num";

        accountTransferRequestDto.setTran_dtime(openBankutil.getTransTime());

        ResponseEntity<AccountTransferRequestDto> param = new ResponseEntity<>(accountTransferRequestDto,generateAuthHeader(access_token),HttpStatus.OK);

        return restTemplate.exchange(url, HttpMethod.POST, param, AccountTransferResponseDto.class).getBody();
    }

    /**
     * 요청할 HttpEntity 생성
    */
    private HttpEntity generateHttpEntity(HttpHeaders httpHeaders) {

        return new HttpEntity<>(httpHeaders);
    }

    /**
     * 헤더에 엑세스 토큰넣기
     */
    private HttpHeaders generateAuthHeader(String access_token){
        httpHeaders.add("Authorization", "Bearer "+access_token);
        return httpHeaders;
    }

}
