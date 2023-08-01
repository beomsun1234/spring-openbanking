package com.bs.openbanking.bank.service;

import com.bs.openbanking.bank.api.OpenBankApiClient;
import com.bs.openbanking.bank.dto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ExtendWith({SpringExtension.class})
@ActiveProfiles(profiles = "openbank")
class OpenBankServiceTest {
    @Mock
    private OpenBankApiClient openBankApiClient;

    @InjectMocks
    private OpenBankService openBankService;


    @Test
    void requestTokenTest() {
        //given
        BankRequestToken bankRequestToken = new BankRequestToken();
        bankRequestToken.setCode("test");
        bankRequestToken.setBankRequestToken("test", "test", "test", "test");

        BankReponseToken expect = new BankReponseToken();
        expect.setAccess_token("test");

        Mockito.when(openBankApiClient.requestToken(bankRequestToken)).thenReturn(expect);
        //when
        BankReponseToken result = openBankService.requestToken(bankRequestToken);
        //then
        Assertions.assertEquals(expect.getAccess_token(), result.getAccess_token());
    }

    @Test
    void findAccountTest() {
        //given
        AccountSearchRequestDto accountSearchRequestDto = new AccountSearchRequestDto();
        accountSearchRequestDto.setUser_seq_no("1");
        accountSearchRequestDto.setSort_order("D");
        accountSearchRequestDto.setAccess_token("test");
        accountSearchRequestDto.setInclude_cancel_yn("N");

        BankAccountSearchResponseDto expect = new BankAccountSearchResponseDto();
        expect.setUser_name("park");

        Mockito.when(openBankApiClient.requestAccountList(accountSearchRequestDto)).thenReturn(expect);
        //when
        BankAccountSearchResponseDto result = openBankService.findAccount(accountSearchRequestDto);
        //then
        Assertions.assertEquals(expect.getUser_name(), result.getUser_name());
    }

    @Test
    void findBalanceTest() {
        //given
        BankBalanceRequestDto bankBalanceRequestDto = BankBalanceRequestDto.builder()
                .fintech_use_num("123").build();

        BankBalanceResponseDto expect = new BankBalanceResponseDto();
        expect.setBalance_amt("10000");

        Mockito.when(openBankApiClient.requestBalance(bankBalanceRequestDto, "test")).thenReturn(expect);
        //when
        BankBalanceResponseDto result = openBankService.findBalance("test", bankBalanceRequestDto);
        //then
        Assertions.assertEquals(expect.getBalance_amt(), result.getBalance_amt());
    }

    @Test
    void getAccountWithBalance() throws ExecutionException, InterruptedException {
        //given
        AccountSearchRequestDto accountSearchRequestDto = new AccountSearchRequestDto();
        accountSearchRequestDto.setUser_seq_no("1");
        accountSearchRequestDto.setSort_order("D");
        accountSearchRequestDto.setAccess_token("test");
        accountSearchRequestDto.setInclude_cancel_yn("N");

        BankAccountSearchResponseDto expect = new BankAccountSearchResponseDto();
        expect.setUser_name("park");

        AccountDto resAccount= new AccountDto();
        resAccount.setAccount_num("1234");
        resAccount.setAccount_num_masked("1234");
        resAccount.setFintech_use_num("111111");
        resAccount.setAccount_seq("1");
        resAccount.setBank_name("신한");

        AccountDto resAccount2 = new AccountDto();
        resAccount2.setAccount_num("2345");
        resAccount2.setAccount_num_masked("2345");
        resAccount2.setFintech_use_num("22222");
        resAccount2.setAccount_seq("1");
        resAccount2.setBank_name("농협");

        expect.setRes_list(List.of(resAccount, resAccount2));

        Mockito.when(openBankApiClient.requestAccountList(accountSearchRequestDto)).thenReturn(expect);

        expect.getRes_list().forEach(accountDto1 -> {
            CompletableFuture.runAsync(()->{
                BankBalanceResponseDto expect2 = new BankBalanceResponseDto();
                expect2.setBalance_amt("20000");
                expect2.setBank_name(accountDto1.getBank_name());
                expect2.setFintech_use_num(accountDto1.getFintech_use_num());
                expect2.setBank_name(accountDto1.getBank_name());

                Mockito.when(openBankApiClient.requestBalance(Mockito.any(BankBalanceRequestDto.class), Mockito.eq("test")))
                        .thenReturn(expect2);
            }).join();
        });

        //when
        List<AccountResponseDto> result = openBankService.getAccountWithBalance(accountSearchRequestDto);
        //then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("20000", result.get(0).getBalance_amt());
        Assertions.assertEquals("신한", result.get(0).getBank_name());
        Assertions.assertEquals("20000", result.get(1).getBalance_amt());
        Assertions.assertEquals("농협", result.get(1).getBank_name());
    }
}