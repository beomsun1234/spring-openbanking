package com.bs.openbanking.bank.controller;


import com.bs.openbanking.bank.api.OpenBankUtil;
import com.bs.openbanking.bank.dto.*;
import com.bs.openbanking.bank.service.OpenBankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequiredArgsConstructor
@Controller
public class OpenBankController {
    private final OpenBankUtil openBankUtil;
    /**
     * http://localhost:8080/auth/openbank/callback
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
    private final OpenBankService openBankService;

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("clientId", clientId);
        model.addAttribute("access_token",access_token);
        return "/home";
    }

    /**
     * 계좌조회
     * dto 만들기
     * @param accountRequestDto
     * @param model
     * @return
     */
    @GetMapping("/account/list")
    public String searchAccountList(AccountRequestDto accountRequestDto, Model model){
        OpenBankAccountSearchResponseDto account = openBankService.findAccount(accountRequestDto);
        model.addAttribute("bankAccounts",account);
        model.addAttribute("useCode",useCode);
        model.addAttribute("access_token",access_token);
        return "v1/accountList";
    }

    /**
     * 잔액조회
     */
    @GetMapping("/balance")
    public String searchBalance(BalanceRequestDto balanceRequestDto, Model model){
        OpenBankBalanceResponseDto openBankBalanceResponseDto = openBankService.findBalance(balanceRequestDto);
        System.out.println(openBankBalanceResponseDto);
        model.addAttribute("accountBalance", openBankBalanceResponseDto);
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
        model.addAttribute("transferForm",new AccountTransferRequestDto(openBankUtil.getRandomNumber(bank_tran_id),fintech_use_num,req_client_name,openBankUtil.trimAccountNum(account_num, account_num.length()),openBankUtil.trimAccountNum(account_num, account_num.length())));
        return "v1/transferForm";
    }
    @PostMapping("/transfer")
    public @ResponseBody AccountTransferResponseDto transfer(String access_token,AccountTransferRequestDto accountTransferRequestDto){
        return openBankService.accountTransfer(access_token,accountTransferRequestDto);
    }


}
