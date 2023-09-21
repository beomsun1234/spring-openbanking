package com.bs.openbanking.bank.client;

import com.bs.openbanking.bank.dto.openbank.*;
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
    private static final String base_url = "https://testapi.openbanking.or.kr";
    private static final String successCode = "A0000";
    /**
     * 토큰발급요청
     * post 방식으로 key=vale 데이터 요청 (금결원)
     * 토큰요청 http 헤더타입은 application/x-www-form-urlencoded
     */
    public OpenBankResponseToken requestToken(OpenBankRequestToken openBankRequestToken){
        HttpHeaders httpHeaders = generateHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        /**
         * 헤더의 컨텐트 타입이 application/x-www-form-urlencoded;charset=UTF-8이므로 객체를 집어넣을수 없음.. 그러므로 MultiValueMap 사용 해야함
         */
        HttpEntity httpEntity = generateHttpEntityWithBody(httpHeaders, openBankRequestToken.toMultiValueMap());

        OpenBankResponseToken openBankResponseToken = restTemplate.exchange(base_url + "/oauth/2.0/token", HttpMethod.POST, httpEntity, OpenBankResponseToken.class).getBody();

        if(isErrorCode(openBankResponseToken.getRsp_code())){
            log.error("error code : {}, error msg : {}", openBankResponseToken.getRsp_code(), openBankResponseToken.getRsp_message());
            throw new RuntimeException(openBankResponseToken.getRsp_message());
        }
        return openBankResponseToken;

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
        String url = base_url+"/v2.0/account/list";

        HttpEntity httpEntity = generateHttpEntity(generateHeader("Authorization", openBankAccountSearchRequestDto.getAccess_token()));

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("user_seq_no", openBankAccountSearchRequestDto.getUser_seq_no())
                .queryParam("include_cancel_yn", openBankAccountSearchRequestDto.getInclude_cancel_yn())
                .queryParam("sort_order", openBankAccountSearchRequestDto.getSort_order())
                .build();

        OpenBankAccountSearchResponseDto bankAccountSearchResponseDto = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, OpenBankAccountSearchResponseDto.class).getBody();
        if(isErrorCode(bankAccountSearchResponseDto.getRsp_code())){
            log.error("error code : {}, error msg : {}", bankAccountSearchResponseDto.getRsp_code(), bankAccountSearchResponseDto.getRsp_message());
            throw new RuntimeException(bankAccountSearchResponseDto.getRsp_message());
        }
        return bankAccountSearchResponseDto;
    }
    /**
     * 잔액조회
     */
    public OpenBankBalanceResponseDto requestBalance(OpenBankBalanceRequestDto openBankBalanceRequestDto){
        String url = base_url+"/v2.0/account/balance/fin_num";

        HttpEntity httpEntity = generateHttpEntity(generateHeader("Authorization",openBankBalanceRequestDto.getAccess_token()));

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("bank_tran_id", openBankBalanceRequestDto.getBank_tran_id())
                .queryParam("fintech_use_num", openBankBalanceRequestDto.getFintech_use_num())
                .queryParam("tran_dtime", openBankBalanceRequestDto.getTran_dtime())
                .build();

        OpenBankBalanceResponseDto openBankBalanceResponseDto = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, OpenBankBalanceResponseDto.class).getBody();
        if(isErrorCode(openBankBalanceResponseDto.getRsp_code())){
            log.error("error code : {}, error msg : {}", openBankBalanceResponseDto.getRsp_code(), openBankBalanceResponseDto.getRsp_message());
            throw new RuntimeException(openBankBalanceResponseDto.getRsp_message());
        }
        return openBankBalanceResponseDto;
    }

    /**
     *
     * 계좌이체
     */
    public AccountTransferResponseDto requestTransfer(String access_token, AccountTransferRequestDto accountTransferRequestDto){
        String url = base_url+"/v2.0/transfer/withdraw/fin_num";

        accountTransferRequestDto.setTran_dtime(OpenBankUtil.getTransTime());

        ResponseEntity<AccountTransferRequestDto> param = new ResponseEntity<>(accountTransferRequestDto,generateHeader("Authorization",access_token),HttpStatus.OK);

        AccountTransferResponseDto transferResponseDto = restTemplate.exchange(url, HttpMethod.POST, param, AccountTransferResponseDto.class).getBody();

        return transferResponseDto;
    }

    /**
     * 유저정보 가져오기 - ci 값, 등록계좌
     */
    public OpenBankUserInfoResponseDto requestOpenBankUserInfo(OpenBankUserInfoRequestDto openBankUserInfoRequestDto){
        String url = base_url+"/v2.0/user/me";

        HttpEntity httpEntity = generateHttpEntity(generateHeader("Authorization", openBankUserInfoRequestDto.getAccessToken()));

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("user_seq_no", openBankUserInfoRequestDto.getOpenBankId())
                .build();
        OpenBankUserInfoResponseDto openBankUserInfoResponseDto = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, OpenBankUserInfoResponseDto.class).getBody();

        if (isErrorCode(openBankUserInfoResponseDto.getRsp_code())){
            log.error("error code : {}, error msg : {}", openBankUserInfoResponseDto.getRsp_code(), openBankUserInfoResponseDto.getRsp_message());
            throw new RuntimeException(openBankUserInfoResponseDto.getRsp_message());
        }
        return openBankUserInfoResponseDto;
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

    private boolean isErrorCode(String code){
        if (code==null) return false;
        if (code.equals(successCode)){
            return false;
        }
        return true;
    }




}
