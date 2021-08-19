package com.bs.openbanking.bank;


import com.bs.openbanking.bank.api.OpenBankutil;
import com.bs.openbanking.bank.dto.*;
import com.bs.openbanking.bank.service.OpenBankService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
public class OpenBankApiController {
    private final HttpSession session;
    private final OpenBankutil openBankutil;
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

    @Value("${openbank.access-token}")
    private String access_token;
    private String redirect_uri = "http://localhost:8080/auth/openbank/callback";
    private final OpenBankService openBankService;
    /**
     * 토큰요청
     * @param model
     * @return
     */
    @GetMapping("/auth/openbank/callback")
    public String getToken(BankRequestToken bankRequestToken, Model model){
        BankReponseToken token = openBankService.requestToken(bankRequestToken);
        model.addAttribute("bankReponseToken",token);
        log.info("bankReponseToken={}", token);
        return "v1/bank";
    }

    /**
     * 계좌조회
     * dto 만들기
     * @param accountSearchRequestDto
     * @param model
     * @return
     */
    @GetMapping("/acount/list")
    public String searchAcountList(AccountSearchRequestDto accountSearchRequestDto, Model model){
        BankAcountSearchResponseDto account = openBankService.findAccount(accountSearchRequestDto);
        model.addAttribute("bankAccounts",account);
        model.addAttribute("useCode",useCode);
        model.addAttribute("access_token",access_token);
        return "v1/accountList";
    }

    /**
     * 잔액조회
     */
    @GetMapping("/balance")
    public String searchBalance(String access_token, BankBalanceRequestDto bankBalanceRequestDto, Model model){
        model.addAttribute("accountBalance", openBankService.findBalance(access_token,bankBalanceRequestDto));
        return "v1/balance";
    }

    /**
     * 계좌이체
     * 계좌이체 처리 테스트에 등록된 값만 계좌이체가능!! 
     */
    @GetMapping("/transfer")
    public String openTransfer(Model model, String bank_tran_id,String access_token, String fintech_use_num, String account_num, String req_client_name){
        /**
         * 20000, 100000원만 등록되어있음
         */
        //계좌번호 마스킹된값 제거(계좌번호 보여주는건 계약된 사용자만가능(그래서 마스킹된 3자리 잘라서 보내주고 클라이언트에서 3자리 더해줌
        model.addAttribute("token", access_token);
        model.addAttribute("transferForm",new AccountTransferRequestDto(openBankutil.getRandomNumber(bank_tran_id),fintech_use_num,req_client_name,openBankutil.trimAccountNum(account_num, account_num.length()),openBankutil.trimAccountNum(account_num, account_num.length())));
        return "v1/transferForm";
    }
    @PostMapping("/transfer")
    public @ResponseBody AccountTransferResponseDto transfer(String access_token,AccountTransferRequestDto accountTransferRequestDto){
        return openBankService.accountTransfer(access_token,accountTransferRequestDto);
    }


}
