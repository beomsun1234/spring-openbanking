package com.bs.openbanking.bank.service;

import com.bs.openbanking.bank.api.OpenBankApiClient;
import com.bs.openbanking.bank.dto.AccountSearchRequestDto;
import com.bs.openbanking.bank.dto.BankAcountSearchResponseDto;
import com.bs.openbanking.bank.dto.BankBalanceRequestDto;
import com.bs.openbanking.bank.dto.BankBalanceResponseDto;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OpenBankService {

    private final OpenBankApiClient openBankApiClient;


    public BankAcountSearchResponseDto findAccount(AccountSearchRequestDto accountSearchRequestDto){
       return openBankApiClient.requestAccountList(accountSearchRequestDto);
    }
    public BankBalanceResponseDto findBalance(String access_token, BankBalanceRequestDto bankBalanceRequestDto){
        return openBankApiClient.requestBalance(bankBalanceRequestDto,access_token);
    }

}
