package com.bs.openbanking.bank.service;

import com.bs.openbanking.bank.api.OpenBankApiClient;
import com.bs.openbanking.bank.dto.*;
import lombok.RequiredArgsConstructor;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OpenBankService {

    private final OpenBankApiClient openBankApiClient;
    public BankReponseToken requestToken(BankRequestToken bankRequestToken){
        return openBankApiClient.requestToken(bankRequestToken);
    }
    public BankAcountSearchResponseDto findAccount(AccountSearchRequestDto accountSearchRequestDto){
       return openBankApiClient.requestAccountList(accountSearchRequestDto);
    }
    public BankBalanceResponseDto findBalance(String access_token, BankBalanceRequestDto bankBalanceRequestDto){
        return openBankApiClient.requestBalance(bankBalanceRequestDto,access_token);
    }
    public AccountTransferResponseDto accountTransfer(String access_token, AccountTransferRequestDto accountTransferRequestDto){
        return openBankApiClient.requestTransfer(access_token,accountTransferRequestDto);
    }

}
