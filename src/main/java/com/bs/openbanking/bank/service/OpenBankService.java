package com.bs.openbanking.bank.service;

import com.bs.openbanking.bank.api.OpenBankApiClient;
import com.bs.openbanking.bank.api.OpenBankUtil;
import com.bs.openbanking.bank.dto.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Service
public class OpenBankService {
    private final String useCode;
    private final String clientId;
    private final String client_secret;
    private static final  String redirect_uri = "http://localhost:8080/auth/openbank/callback";
    private final OpenBankApiClient openBankApiClient;
    public OpenBankService(@Value("${openbank.useCode}") String useCode,
                           @Value("${openbank.client-id}") String clientId,
                           @Value("${openbank.client-secret}") String client_secret,
                           OpenBankApiClient openBankApiClient){
        this.useCode = useCode;
        this.clientId = clientId;
        this.client_secret = client_secret;
        this.openBankApiClient = openBankApiClient;
    }

    /**
     * 토큰요청
     * @param bankRequestToken
     * @return
     */
    public BankReponseToken requestToken(BankRequestToken bankRequestToken){
        bankRequestToken.setBankRequestToken(clientId,client_secret,redirect_uri,"authorization_code");
        return openBankApiClient.requestToken(bankRequestToken);
    }

    /**
     * 계좌조회
     * @param accountSearchRequestDto
     * @return
     */
    public BankAccountSearchResponseDto findAccount(AccountSearchRequestDto accountSearchRequestDto){
       return openBankApiClient.requestAccountList(accountSearchRequestDto);
    }

    /**
     * 잔액조회
     * @param access_token
     * @param bankBalanceRequestDto
     * @return
     */
    public BankBalanceResponseDto findBalance(String access_token, BankBalanceRequestDto bankBalanceRequestDto){
        /**
         * bank_tran_id의 경우 규칙이있다. 핀테크이용번호+ "U" + "랜덤한 9자리숫자"
         */
        bankBalanceRequestDto.setBankTransIdAndTransDateTime(OpenBankUtil.getRandomNumber(useCode + "U"), OpenBankUtil.getTransTime());

        BankBalanceResponseDto bankBalanceResponseDto = openBankApiClient.requestBalance(bankBalanceRequestDto, access_token);
        return bankBalanceResponseDto;
    }
    public AccountTransferResponseDto accountTransfer(String access_token, AccountTransferRequestDto accountTransferRequestDto){
        return openBankApiClient.requestTransfer(access_token,accountTransferRequestDto);
    }

    /**
     * 계좌조회 및 금액조회
     * @param accountSearchRequestDto
     * @return
     */
    public List<AccountResponseDto> getAccountWithBalance(AccountSearchRequestDto accountSearchRequestDto){
        /**
         * 계좌정보 불러옴
         */
        List<AccountDto> accountDtoList = findAccount(accountSearchRequestDto).getRes_list();
        /**
         * 풀에서 관리하는 스레드 수를 설정한다.
         * 한 계좌번호당 스레드가 할당되도록 하며 최대개수는 100이하로 설정
         */
        ExecutorService executorService = Executors.newFixedThreadPool(Math.min(accountDtoList.size(), 100), r -> {
            Thread t = new Thread(r);
            t.setDaemon(true); //프로그램 종료 방해않는 데몬 스레드 사용.
            return t;
        });

        /**
         * 비동기 요청
         */
        List<AccountResponseDto> accountResponseDtoList = accountDtoList.stream()
                .map(accountDto -> CompletableFuture.supplyAsync(() -> {
                            BankBalanceResponseDto balance = findBalance(accountSearchRequestDto.getAccess_token(), BankBalanceRequestDto
                                    .builder().fintech_use_num(accountDto.getFintech_use_num()).build());

                            AccountResponseDto accountResponseDto = AccountResponseDto
                                    .builder()
                                    .account_num(accountDto.getAccount_num())
                                    .account_num_masked(accountDto.getAccount_num_masked())
                                    .fintech_use_num(accountDto.getFintech_use_num())
                                    .bank_name(accountDto.getBank_name())
                                    .balance_amt(balance.getBalance_amt())
                                    .build();

                            return accountResponseDto;
                        }, executorService)
                )
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        return accountResponseDtoList;
    }
}
