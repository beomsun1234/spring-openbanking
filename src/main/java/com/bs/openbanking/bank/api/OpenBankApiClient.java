package com.bs.openbanking.bank.api;

import com.bs.openbanking.bank.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final RestTemplate restTemplate;
    private static final String base_url = "https://testapi.openbanking.or.kr/v2.0";

    /**
     * 토큰발급요청
     * post 방식으로 key=vale 데이터 요청 (금결원)
     * 토큰요청 http 헤더타입은 application/x-www-form-urlencoded
     */
    public OpenBankReponseToken requestToken(OpenBankRequestToken openBankRequestToken){
        HttpHeaders httpHeaders = generateHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        /**
         * 헤더의 컨텐트 타입이 application/x-www-form-urlencoded;charset=UTF-8이므로 객체를 집어넣을수 없음.. 그러므로 MultiValueMap 사용 해야함
         */
        HttpEntity httpEntity = generateHttpEntityWithBody(httpHeaders, openBankRequestToken.toMultiValueMap());

        return restTemplate.exchange(base_url + "/token",HttpMethod.POST, httpEntity , OpenBankReponseToken.class).getBody();
    }
    private HttpEntity generateHttpEntityWithBody(HttpHeaders httpHeaders, MultiValueMap body) {
        return new HttpEntity<>(body, httpHeaders);
    }

    /**
     * 계좌조회
     * @param openBankAccountSearchRequestDto
     * @return
     */
    public OpenBankAccountSearchResponseDto requestAccountList(OpenBankAccountSearchRequestDto openBankAccountSearchRequestDto){
        String url = base_url+"/account/list";

        HttpEntity httpEntity = generateHttpEntity(generateHeader("Authorization", openBankAccountSearchRequestDto.getAccess_token()));

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("user_seq_no", openBankAccountSearchRequestDto.getUser_seq_no())
                .queryParam("include_cancel_yn", openBankAccountSearchRequestDto.getInclude_cancel_yn())
                .queryParam("sort_order", openBankAccountSearchRequestDto.getSort_order())
                .build();

        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET,httpEntity , OpenBankAccountSearchResponseDto.class).getBody();
    }
    /**
     * 잔액조회
     */
    public OpenBankBalanceResponseDto requestBalance(OpenBankBalanceRequestDto openBankBalanceRequestDto, String access_token){
        String url = base_url+"/account/balance/fin_num";

        HttpEntity httpEntity = generateHttpEntity(generateHeader("Authorization",access_token));

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("bank_tran_id", openBankBalanceRequestDto.getBank_tran_id())
                .queryParam("fintech_use_num", openBankBalanceRequestDto.getFintech_use_num())
                .queryParam("tran_dtime", openBankBalanceRequestDto.getTran_dtime())
                .build();

        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, OpenBankBalanceResponseDto.class).getBody();
    }

    /**
     *
     * 계좌이체
     */
    public AccountTransferResponseDto requestTransfer(String access_token, AccountTransferRequestDto accountTransferRequestDto){
        String url = base_url+"//transfer/withdraw/fin_num";

        accountTransferRequestDto.setTran_dtime(OpenBankUtil.getTransTime());

        ResponseEntity<AccountTransferRequestDto> param = new ResponseEntity<>(accountTransferRequestDto,generateHeader("Authorization",access_token),HttpStatus.OK);

        return restTemplate.exchange(url, HttpMethod.POST, param, AccountTransferResponseDto.class).getBody();
    }

    /**
     * 요청할 HttpEntity 생성
    */
    private HttpEntity generateHttpEntity(HttpHeaders httpHeaders) {
        return new HttpEntity<>(httpHeaders);
    }


    /**
     * 헤더 생성
     */
    private HttpHeaders generateHeader(String name ,String val){
        HttpHeaders httpHeaders = new HttpHeaders();
        if (name.equals("Authorization")) {
            httpHeaders.add(name, "Bearer "+val);
            return httpHeaders;
        }
        httpHeaders.add(name, val);
        return httpHeaders;
    }


}
