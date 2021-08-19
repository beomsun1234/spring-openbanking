package com.bs.openbanking.bank.api;

import com.bs.openbanking.bank.dto.AccountSearchRequestDto;
import com.bs.openbanking.bank.dto.BankAcountSearchResponseDto;
import com.bs.openbanking.bank.dto.BankBalanceRequestDto;
import com.bs.openbanking.bank.dto.BankBalanceResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;



@RequiredArgsConstructor
@Service
public class OpenBankApiClient {

//    @Value("${openbank.useCode}")
//    private String useCode; // 핀테크번호+U -> 거래고유번호 생성기
//    @Value("${openbank.client-id}")
//    private String clientId;
//    @Value("${openbank.client-secret}")
//    private String client_secret;
//    private String redirect_uri = "http://localhost:8080/auth/openbank/callback";
    //private final HttpHeaders httpHeaders;
    private String base_url = "https://testapi.openbanking.or.kr/v2.0";
    private final OpenBankutil openBankutil;
    public BankAcountSearchResponseDto requestAccountList(AccountSearchRequestDto accountSearchRequestDto){
        RestTemplate restTemplate = new RestTemplate();
        String url = base_url+"/account/list";
        HttpEntity<String> openBankAcountSerchRequest = new HttpEntity<>(setHeader(accountSearchRequestDto.getAccess_token()));
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("user_seq_no", accountSearchRequestDto.getUser_seq_no())
                .queryParam("include_cancel_yn", accountSearchRequestDto.getInclude_cancel_yn())
                .queryParam("sort_order", accountSearchRequestDto.getSort_order())
                .build();

        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, openBankAcountSerchRequest, BankAcountSearchResponseDto.class).getBody();
    }
    /**
     * 잔액조회
     */
    public BankBalanceResponseDto requestBalance(BankBalanceRequestDto bankBalanceRequestDto, String access_token){
        RestTemplate restTemplate = new RestTemplate();
        String url = base_url+"/account/balance/fin_num";
        HttpEntity<String> balance = new HttpEntity<>(setHeader(access_token));
        bankBalanceRequestDto.setBankTransIdAndTranssDtime(openBankutil.getRandomNumber(bankBalanceRequestDto.getBank_tran_id()),openBankutil.getTransTime());
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("bank_tran_id",bankBalanceRequestDto.getBank_tran_id())
                .queryParam("fintech_use_num", bankBalanceRequestDto.getFintech_use_num())
                .queryParam("tran_dtime",bankBalanceRequestDto.getTran_dtime())
                .build();

        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, balance, BankBalanceResponseDto.class).getBody();
    }


    public final HttpHeaders setHeader(String access_token){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer "+access_token);
        return httpHeaders;
    }

}
