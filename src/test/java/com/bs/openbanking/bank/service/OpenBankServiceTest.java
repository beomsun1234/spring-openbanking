package com.bs.openbanking.bank.service;

import com.bs.openbanking.bank.client.OpenBankApiClient;
import com.bs.openbanking.bank.dto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

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

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(openBankService,
                "clientId",
                "test");
        ReflectionTestUtils.setField(openBankService,
                "client_secret",
                "test");
        ReflectionTestUtils.setField(openBankService,
                "useCode",
                "test");
        ReflectionTestUtils.setField(openBankService,
                "redirect_uri",
                "http://localhsot:30000000/test");
    }

    @Test
    void requestTokenTest() {
        //given
        TokenRequestDto tokenRequestDto = TokenRequestDto.builder().code("test").memberId(1L).build();
        OpenBankReponseToken expect = new OpenBankReponseToken();
        expect.setAccess_token("test");

        Mockito.when(openBankApiClient.requestToken(Mockito.any(OpenBankRequestToken.class))).thenReturn(expect);
        //when
        OpenBankReponseToken result = openBankService.requestToken(tokenRequestDto);
        //then
        Assertions.assertEquals(expect.getAccess_token(), result.getAccess_token());
    }

    @Test
    void findAccountTest() {
        //given
        AccountRequestDto accountRequestDto = AccountRequestDto.builder().accessToken("test").openBankId("1").build();

        OpenBankAccountSearchResponseDto expect = new OpenBankAccountSearchResponseDto();
        expect.setUser_name("park");

        Mockito.when(openBankApiClient.requestAccountList(Mockito.any(OpenBankAccountSearchRequestDto.class))).thenReturn(expect);
        //when
        OpenBankAccountSearchResponseDto result = openBankService.findAccount(accountRequestDto);
        //then
        Assertions.assertEquals(expect.getUser_name(), result.getUser_name());
    }

    @Test
    void findBalanceTest() {
        //given
        BalanceRequestDto balanceRequestDto = BalanceRequestDto.builder().fintechUseNum("test").accessToken("test").memberId(1L).build();

        OpenBankBalanceResponseDto expect = new OpenBankBalanceResponseDto();
        expect.setBalance_amt("10000");

        Mockito.when(openBankApiClient.requestBalance(Mockito.any(OpenBankBalanceRequestDto.class))).thenReturn(expect);
        //when
        OpenBankBalanceResponseDto result = openBankService.findBalance(balanceRequestDto);
        //then
        Assertions.assertEquals(expect.getBalance_amt(), result.getBalance_amt());
    }

    @Test
    void getAccountWithBalance() throws ExecutionException, InterruptedException {
        //given
        AccountRequestDto accountRequestDto = AccountRequestDto.builder().accessToken("test").openBankId("1").build();

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

        Mockito.when(openBankApiClient.requestAccountList(Mockito.any(OpenBankAccountSearchRequestDto.class))).thenReturn(expect);

        expect.getRes_list().forEach(openBankAccountDto1 -> {
            CompletableFuture.runAsync(()->{
                OpenBankBalanceResponseDto expect2 = new OpenBankBalanceResponseDto();
                expect2.setBalance_amt("20000");
                expect2.setBank_name(openBankAccountDto1.getBank_name());
                expect2.setFintech_use_num(openBankAccountDto1.getFintech_use_num());
                expect2.setBank_name(openBankAccountDto1.getBank_name());

                Mockito.when(openBankApiClient.requestBalance(Mockito.any(OpenBankBalanceRequestDto.class)))
                        .thenReturn(expect2);
            }).join();
        });

        //when
        List<OpenBankAccountResponseDto> result = openBankService.getAccountWithBalance(accountRequestDto);
        //then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("20000", result.get(0).getBalance_amt());
        Assertions.assertEquals("신한", result.get(0).getBank_name());
        Assertions.assertEquals("20000", result.get(1).getBalance_amt());
        Assertions.assertEquals("농협", result.get(1).getBank_name());
    }
    @Test
    @DisplayName("금액 불러오기 실패")
    void getAccountWithBalance_금액불러오기_실패시() throws ExecutionException, InterruptedException {
        //given
        AccountRequestDto accountRequestDto = AccountRequestDto.builder().accessToken("test").openBankId("1").build();

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

        Mockito.when(openBankApiClient.requestAccountList(Mockito.any(OpenBankAccountSearchRequestDto.class))).thenReturn(expect);

        expect.getRes_list().forEach(openBankAccountDto1 -> {
            CompletableFuture.runAsync(()->{
                OpenBankBalanceResponseDto expect2 = new OpenBankBalanceResponseDto();
                expect2.setBank_name(openBankAccountDto1.getBank_name());
                expect2.setFintech_use_num(openBankAccountDto1.getFintech_use_num());
                expect2.setBank_name(openBankAccountDto1.getBank_name());
                Mockito.when(openBankApiClient.requestBalance(Mockito.any(OpenBankBalanceRequestDto.class)))
                        .thenThrow(RuntimeException.class);
            }).join();
        });

        //when
        List<OpenBankAccountResponseDto> result = openBankService.getAccountWithBalance(accountRequestDto);
        //then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("", result.get(0).getBalance_amt());
        Assertions.assertEquals("신한", result.get(0).getBank_name());
        Assertions.assertEquals("", result.get(1).getBalance_amt());
        Assertions.assertEquals("농협", result.get(1).getBank_name());
    }
}