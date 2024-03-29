package com.bs.openbanking.bank.service;

import com.bs.openbanking.bank.client.OpenBankApiClient;
import com.bs.openbanking.bank.client.OpenBankUtil;
import com.bs.openbanking.bank.dto.*;

import com.bs.openbanking.bank.dto.openbank.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Service
@Slf4j
public class OpenBankService {
    private final String useCode;
    private final String clientId;
    private final String client_secret;

    //redirect 주소는 프론트엔드 주소로 변경
    private final  String redirect_uri;
    private final OpenBankApiClient openBankApiClient;
    public OpenBankService(@Value("${openbank.useCode}") String useCode,
                           @Value("${openbank.client-id}") String clientId,
                           @Value("${openbank.client-secret}") String client_secret,
                           @Value("${openbank.redirect-url}") String redirect_uri,
                           OpenBankApiClient openBankApiClient){
        this.useCode = useCode;
        this.clientId = clientId;
        this.client_secret = client_secret;
        this.redirect_uri = redirect_uri;
        this.openBankApiClient = openBankApiClient;
    }

    /**
     * 토큰요청
     * @param
     * @return
     */

    public OpenBankResponseToken requestToken(TokenRequestDto tokenRequestDto){
        OpenBankRequestToken openBankRequestToken = OpenBankRequestToken.builder()
                .code(tokenRequestDto.getCode())
                .client_id(clientId)
                .client_secret(client_secret)
                .redirect_uri(redirect_uri)
                .grant_type("authorization_code")
                .build();
        OpenBankResponseToken openBankResponseToken = openBankApiClient.requestToken(openBankRequestToken);
        return openBankResponseToken;
    }


    /**
     * 계좌조회
     * @param
     * @return
     */
    public OpenBankAccountSearchResponseDto findAccount(AccountRequestDto accountRequestDto){
        OpenBankAccountSearchRequestDto searchRequestDto = OpenBankAccountSearchRequestDto.builder().user_seq_no(accountRequestDto.getOpenBankId())
                .access_token(accountRequestDto.getAccessToken())
                .include_cancel_yn("N")
                .sort_order("Y")
                .build();
        return openBankApiClient.requestAccountList(searchRequestDto);
    }

    /**
     * 잔액조회
     * @return
     */
    public OpenBankBalanceResponseDto findBalance(BalanceRequestDto balanceRequestDto){
        /**
         * bank_tran_id의 경우 규칙이있다. 핀테크이용번호+ "U" + "랜덤한 9자리숫자"
         */
        OpenBankBalanceRequestDto openBankBalanceRequestDto = OpenBankBalanceRequestDto.builder()
                .accessToken(balanceRequestDto.getAccessToken())
                .fintech_use_num(balanceRequestDto.getFintechUseNum())
                .bank_tran_id(OpenBankUtil.getRandomNumber(useCode + "U"))
                .tran_dtime(OpenBankUtil.getTransTime())
                .build();

        OpenBankBalanceResponseDto openBankBalanceResponseDto = openBankApiClient.requestBalance(openBankBalanceRequestDto);
        return openBankBalanceResponseDto;
    }

    /**
     * 출금이체
     * @param access_token
     * @param accountTransferRequestDto
     * @return
     */
    public AccountTransferResponseDto accountTransfer(String access_token, AccountTransferRequestDto accountTransferRequestDto){
        return openBankApiClient.requestTransfer(access_token,accountTransferRequestDto);
    }

    /**
     * -------------사용 xxxx -> account service 로 옮김-----------
     * 계좌조회 및 금액조회
     * @param
     * @return
     */
    public List<OpenBankAccountResponseDto> getAccountWithBalance(AccountRequestDto accountRequestDto){
        /**
         * 계좌정보 불러옴
         */
        List<OpenBankAccountDto> openBankAccountDtoList = findAccount(accountRequestDto).getRes_list();
        /**
         * 풀에서 관리하는 스레드 수를 설정한다.
         * 한 계좌번호당 스레드가 할당되도록 하며 최대개수는 100이하로 설정
         */
        ExecutorService executorService = Executors.newFixedThreadPool(Math.min(openBankAccountDtoList.size(), 100), r -> {
            Thread t = new Thread(r);
            t.setDaemon(true); //프로그램 종료 방해않는 데몬 스레드 사용.
            return t;
        });

        /**
         * 비동기 요청
         */
        List<OpenBankAccountResponseDto> openBankAccountResponseDtoList = openBankAccountDtoList.stream()
                .map(openBankAccountDto -> CompletableFuture.supplyAsync(() -> {
                    String amt = "";
                    try {
                        OpenBankBalanceResponseDto balance = findBalance(BalanceRequestDto
                                .builder()
                                .fintechUseNum(openBankAccountDto.getFintech_use_num())
                                .accessToken(accountRequestDto.getAccessToken()).build());
                        amt = balance.getBalance_amt();
                    }
                    catch (RuntimeException e){
                        log.error(e.getMessage());
                    }
                    OpenBankAccountResponseDto openBankAccountResponseDto = OpenBankAccountResponseDto
                                    .builder()
                                    .account_num(openBankAccountDto.getAccount_num())
                                    .account_num_masked(openBankAccountDto.getAccount_num_masked())
                                    .fintech_use_num(openBankAccountDto.getFintech_use_num())
                                    .bank_name(openBankAccountDto.getBank_name())
                                    .balance_amt(amt)
                                    .build();

                            return openBankAccountResponseDto;
                            }, executorService)
                )
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        return openBankAccountResponseDtoList;
    }

    /**
     * 오픈뱅킹이 가지고 있는 user ci 정보 가지고 오기
     */
    public OpenBankUserInfoResponseDto findOpenBankUserInfo(OpenBankUserInfoRequestDto openBankUserInfoRequestDto){
        return openBankApiClient.requestOpenBankUserInfo(openBankUserInfoRequestDto);
    }
}
