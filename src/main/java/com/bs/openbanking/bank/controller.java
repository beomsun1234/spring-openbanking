package com.bs.openbanking.bank;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Controller
public class controller {
    private final HttpSession session;
    /**
     * clientID = 	318b30f8-87cc-4e10-aff2-027ebd3e3b3a
     * http://localhost:8080/auth/openbank/callback
     * Client Secret = 5081186a-2d57-49cb-ab45-03dc853079b8
     *
     * 토큰 발급 요청 주소(POST)
     * https://testapi.openbanking.or.kr/oauth/2.0/token
     * code <authorization_code> Y 사용자인증 성공 후 획득한 Authorization Code
     *
     * client_id <client_id> (Max: 40 bytes) Y 오픈뱅킹에서 발급한 이용기관 앱의 Client ID
     *
     * client_secret <client_secret> (Max: 40 bytes) Y 오픈뱅킹에서 발급한 이용기관 앱의 Client Secret

     *redirect_uri <callback_uri> Y
     *
     * Access Token을 전달받을 Callback URL
     *
     * (Authorization Code 획득 시 요청했던 Callback URL)

     * grant_type
     */
    @Value("${openbank.useCode}")
    private String useCode;
    @Value("${openbank.client-id}")
    private String clientId;
    @Value("${openbank.client-secret}")
    private String client_secret;
    @GetMapping("/auth/openbank/callback")
    public String openBacnkCallback(String code, String scope,Model model){
        //post 방식으로 key=vale 데이터 요청 (금결원)
        RestTemplate rt = new RestTemplate();
        //http 헤더 오브젝트 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
        //httpBody 오브젝트 생성
        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        //param.add("key",value);
        param.add("code", code);
        param.add("client_id",clientId);
        param.add("client_secret",client_secret);
        param.add("redirect_uri","http://localhost:8080/auth/openbank/callback");
        param.add("grant_type","authorization_code");
        // HttpHeader 와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String,String>> openBankTokenRequest =
                new HttpEntity<>(param,httpHeaders);
        //Http 요청하기 - post 방식으로
        ResponseEntity<String> responseEntity = rt.exchange(
                "https://testapi.openbanking.or.kr/oauth/2.0/token",
                HttpMethod.POST,
                openBankTokenRequest,
                String.class
        );

        //Gson, Json Simple, Object Mapper
        ObjectMapper objectMapper = new ObjectMapper();
        BankReponseToken bankReponseToken = null;
        try {
            bankReponseToken = objectMapper.readValue(responseEntity.getBody(), BankReponseToken.class);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //
        /// 계좌조회
        model.addAttribute("bankReponseToken",bankReponseToken);
        session.setAttribute("token", bankReponseToken);
        log.info("bankReponseToken={}", bankReponseToken);
        return "v1/bank";
    }


    @GetMapping("/acount/list")
    public String searchAcountList(String access_token,String user_seq_no, String include_cancel_yn, String sort_order, Model model){
        log.info("access_token={}",access_token);
        log.info("user_seq_no={}",user_seq_no);
        String client = clientId;
        log.info("include_cancel_yn={}",include_cancel_yn);
        log.info("sort_order={}",sort_order);
        RestTemplate rt2 = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer "+access_token);
        String url = "https://testapi.openbanking.or.kr/v2.0/account/list";//등록계좌조회
        HttpEntity<String> openBankAcountSerchRequest = new HttpEntity<>(httpHeaders);
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("user_seq_no", user_seq_no)
                .queryParam("include_cancel_yn", include_cancel_yn)
                .queryParam("sort_order", sort_order)
                .build();
        ResponseEntity<String> response = rt2.exchange(builder.toUriString(), HttpMethod.GET, openBankAcountSerchRequest, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        BankAcountSearchResponse bankAcountSearchResponse=null;
        try {
            bankAcountSearchResponse = objectMapper.readValue(response.getBody(), BankAcountSearchResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        model.addAttribute("bankAccounts", bankAcountSearchResponse);
        log.info("계좌조회 성공 뷰로 넘어가야함");
        model.addAttribute("access_token",access_token);
        model.addAttribute("clientId", client);
        model.addAttribute("useCode",useCode);
        return "v1/accountList";
    }
    /**
     * 잔액조회
     */
    @GetMapping("/balance")
    public String searchBalance(String access_token, String bank_code, String fintech_use_num, Model model){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
        String url = "https://testapi.openbanking.or.kr/v2.0/account/balance/fin_num"; //잔액조회
        String now = localDateTime.format(dateTimeFormatter);
        String  bank_tran_id = bank_code.concat(getRandomNumber());

        log.info("bank_tran_id",bank_tran_id);
        log.info("day={}",now);
        String randomNumber = getRandomNumber();
        RestTemplate rt3 = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer "+access_token);
        HttpEntity<String> balance = new HttpEntity<>(httpHeaders);

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("bank_tran_id",bank_tran_id)
                .queryParam("fintech_use_num", fintech_use_num)
                .queryParam("tran_dtime", "20210816101213")
                .build();
        ResponseEntity<String> response = rt3.exchange(builder.toUriString(), HttpMethod.GET, balance, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        BankBalanceResponse bankBalanceResponse=null;
        try {
            bankBalanceResponse = objectMapper.readValue(response.getBody(), BankBalanceResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.info("response={}", bankBalanceResponse.getAvailable_amt());
        model.addAttribute("accountBalance",bankBalanceResponse);
        return "v1/balance";
    }
    public String getRandomNumber(){

        Random rand = new Random();
        String rst = Integer.toString(rand.nextInt(8) + 1);
        for(int i=0; i < 8; i++){
            rst += Integer.toString(rand.nextInt(9));
        }
        return rst;

    }

    /**
     * RestTemplate rt2 = new RestTemplate();
     *
     *         //http 헤더 오브젝트 생성
//     *         HttpHeaders httpHeaders2 = new HttpHeaders();
//     *         httpHeaders2.add("Authorization", "Bearer "+bankReponseToken.getAccess_token());
//     *         String url = "https://testapi.openbanking.or.kr/v2.0/account/list"; //등록계좌조회
//     *         HttpEntity<String> openBankAcountSerchRequest =
//     *                 new HttpEntity<>(httpHeaders2);
//     *         //Http 요청하기 - post 방식으로
//     *         UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
//     *                 .queryParam("user_seq_no", bankReponseToken.getUser_seq_no())
//     *                 .queryParam("include_cancel_yn", "Y")
//     *                 .queryParam("sort_order", "D")
//     *                 .build(false);
//     *         System.out.println(builder);
//     *         ResponseEntity<String> response = rt2.exchange(builder.toUriString(), HttpMethod.GET, openBankAcountSerchRequest, String.class);
//     *         BankAcountSearchResponse bankAcountSearchResponse = null;
//     *         try {
//     *             bankAcountSearchResponse = objectMapper.readValue(response.getBody(), BankAcountSearchResponse.class);
//     *         } catch (JsonProcessingException e) {
//     *             e.printStackTrace();
//     *         }
     */
}
