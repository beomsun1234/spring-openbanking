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
        OpenBankRequestToken openBankRequestToken = new OpenBankRequestToken();
        openBankRequestToken.setCode("test");
        openBankRequestToken.setBankRequestToken("test", "test", "test", "test");

        OpenBankReponseToken expect = new OpenBankReponseToken();
        expect.setAccess_token("test");

        Mockito.when(openBankApiClient.requestToken(openBankRequestToken)).thenReturn(expect);
        //when
        OpenBankReponseToken result = openBankService.requestToken(openBankRequestToken);
        //then
        Assertions.assertEquals(expect.getAccess_token(), result.getAccess_token());
    }

    @Test
    void findAccountTest() {
        //given
        OpenBankAccountSearchRequestDto openBankAccountSearchRequestDto = new OpenBankAccountSearchRequestDto();
        openBankAccountSearchRequestDto.setUser_seq_no("1");
        openBankAccountSearchRequestDto.setSort_order("D");
        openBankAccountSearchRequestDto.setAccess_token("test");
        openBankAccountSearchRequestDto.setInclude_cancel_yn("N");

        OpenBankAccountSearchResponseDto expect = new OpenBankAccountSearchResponseDto();
        expect.setUser_name("park");

        Mockito.when(openBankApiClient.requestAccountList(openBankAccountSearchRequestDto)).thenReturn(expect);
        //when
        OpenBankAccountSearchResponseDto result = openBankService.findAccount(openBankAccountSearchRequestDto);
        //then
        Assertions.assertEquals(expect.getUser_name(), result.getUser_name());
    }

    @Test
    void findBalanceTest() {
        //given
        OpenBankBalanceRequestDto openBankBalanceRequestDto = OpenBankBalanceRequestDto.builder()
                .fintech_use_num("123").build();

        OpenBankBalanceResponseDto expect = new OpenBankBalanceResponseDto();
        expect.setBalance_amt("10000");

        Mockito.when(openBankApiClient.requestBalance(openBankBalanceRequestDto, "test")).thenReturn(expect);
        //when
        OpenBankBalanceResponseDto result = openBankService.findBalance("test", openBankBalanceRequestDto);
        //then
        Assertions.assertEquals(expect.getBalance_amt(), result.getBalance_amt());
    }

    @Test
    void getAccountWithBalance() throws ExecutionException, InterruptedException {
        //given
        OpenBankAccountSearchRequestDto openBankAccountSearchRequestDto = new OpenBankAccountSearchRequestDto();
        openBankAccountSearchRequestDto.setUser_seq_no("1");
        openBankAccountSearchRequestDto.setSort_order("D");
        openBankAccountSearchRequestDto.setAccess_token("test");
        openBankAccountSearchRequestDto.setInclude_cancel_yn("N");

        OpenBankAccountSearchResponseDto expect = new OpenBankAccountSearchResponseDto();
        expect.setUser_name("park");

        OpenBankAccountDto resAccount= new OpenBankAccountDto();
        resAccount.setAccount_num("1234");
        resAccount.setAccount_num_masked("1234");
        resAccount.setFintech_use_num("111111");
        resAccount.setAccount_seq("1");
        resAccount.setBank_name("신한");

        OpenBankAccountDto resAccount2 = new OpenBankAccountDto();
        resAccount2.setAccount_num("2345");
        resAccount2.setAccount_num_masked("2345");
        resAccount2.setFintech_use_num("22222");
        resAccount2.setAccount_seq("1");
        resAccount2.setBank_name("농협");

        expect.setRes_list(List.of(resAccount, resAccount2));

        Mockito.when(openBankApiClient.requestAccountList(openBankAccountSearchRequestDto)).thenReturn(expect);

        expect.getRes_list().forEach(openBankAccountDto1 -> {
            CompletableFuture.runAsync(()->{
                OpenBankBalanceResponseDto expect2 = new OpenBankBalanceResponseDto();
                expect2.setBalance_amt("20000");
                expect2.setBank_name(openBankAccountDto1.getBank_name());
                expect2.setFintech_use_num(openBankAccountDto1.getFintech_use_num());
                expect2.setBank_name(openBankAccountDto1.getBank_name());

                Mockito.when(openBankApiClient.requestBalance(Mockito.any(OpenBankBalanceRequestDto.class), Mockito.eq("test")))
                        .thenReturn(expect2);
            }).join();
        });

        //when
        List<OpenBankAccountResponseDto> result = openBankService.getAccountWithBalance(openBankAccountSearchRequestDto);
        //then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("20000", result.get(0).getBalance_amt());
        Assertions.assertEquals("신한", result.get(0).getBank_name());
        Assertions.assertEquals("20000", result.get(1).getBalance_amt());
        Assertions.assertEquals("농협", result.get(1).getBank_name());
    }
}