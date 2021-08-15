package com.bs.openbanking.bank;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
public class controller {

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
    @GetMapping("/auth/openbank/callback")
    public BankAcountSearchResponse openBacnkCallback(BankRequestToken banRequestToken, Model model){
        //post 방식으로 key=vale 데이터 요청 (금결원)

        RestTemplate rt = new RestTemplate();

        //http 헤더 오브젝트 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");

        //httpBody 오브젝트 생성
        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        //param.add("key",value);
        param.add("code", banRequestToken.getCode());
        param.add("client_id","318b30f8-87cc-4e10-aff2-027ebd3e3b3a");
        param.add("client_secret","5081186a-2d57-49cb-ab45-03dc853079b8");
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

        //https://openapi.openbanking.or.kr/v2.0/user/me
        RestTemplate rt2 = new RestTemplate();

        //http 헤더 오브젝트 생성
        HttpHeaders httpHeaders2 = new HttpHeaders();
        httpHeaders2.add("Authorization", "Bearer "+bankReponseToken.getAccess_token());
        String url = "https://testapi.openbanking.or.kr/v2.0/account/list"; //등록계좌조회
        HttpEntity<String> openBankAcountSerchRequest =
                new HttpEntity<>(httpHeaders2);
        //Http 요청하기 - post 방식으로
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("user_seq_no", bankReponseToken.getUser_seq_no())
                .queryParam("include_cancel_yn", "Y")
                .queryParam("sort_order", "D")
                .build(false);
        System.out.println(builder);
        ResponseEntity<String> response = rt2.exchange(builder.toUriString(), HttpMethod.GET, openBankAcountSerchRequest, String.class);
        BankAcountSearchResponse bankAcountSearchResponse = null;
        try {
            bankAcountSearchResponse = objectMapper.readValue(response.getBody(), BankAcountSearchResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return bankAcountSearchResponse;
    }

}
