package com.bs.openbanking.bank.service;

import com.bs.openbanking.bank.domain.Account;
import com.bs.openbanking.bank.domain.AccountType;
import com.bs.openbanking.bank.domain.OpenBankToken;
import com.bs.openbanking.bank.dto.*;
import com.bs.openbanking.bank.dto.openbank.OpenBankAccountDto;
import com.bs.openbanking.bank.dto.openbank.OpenBankAccountSearchResponseDto;
import com.bs.openbanking.bank.dto.openbank.OpenBankBalanceResponseDto;
import com.bs.openbanking.bank.repository.AccountRepository;
import com.bs.openbanking.bank.repository.MemberRepository;
import com.bs.openbanking.bank.repository.TokenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.*;

import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
class AccountServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private OpenBankService openBankService;
    @InjectMocks
    private AccountService accountService;


    @Test
    void convertHashMapTest(){
        List<Account> accounts = List.of(
                Account.builder().fintechUseNum("test").build(),
                Account.builder().fintechUseNum("test1").build(),
                Account.builder().fintechUseNum("test2").build(),
                Account.builder().fintechUseNum("test3").build(),
                Account.builder().fintechUseNum("test4").build(),
                Account.builder().fintechUseNum("test5").build()
        );
        HashMap<String, String> result = accounts.parallelStream()
                .collect(Collectors.toMap(
                        account -> account.getFintechUseNum(),
                        account -> account.getFintechUseNum(),
                        (key, value) -> value,
                        HashMap::new
                ));

        Assertions.assertEquals(true, result.containsKey("test"));
        Assertions.assertEquals(false, result.containsKey("test6"));
    }

    @Test
    void 계좌저장_성공() {
        //given
        Long memberId = 1L;

        OpenBankToken openBankToken = OpenBankToken.builder().openBankId("1234").accessToken("test").refreshToken("test").memberId(memberId).id(1L).build();
        //account 1
        OpenBankAccountDto resAccount= new OpenBankAccountDto();
        resAccount.setFintech_use_num("111111");
        //account2
        OpenBankAccountDto resAccount2 = new OpenBankAccountDto();
        resAccount2.setFintech_use_num("22222");

        OpenBankAccountSearchResponseDto openBankAccountSearchResponseDto = new OpenBankAccountSearchResponseDto();
        openBankAccountSearchResponseDto.setRes_list(List.of(resAccount,resAccount2));

        Mockito.when(memberRepository.existsMemberById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.of(openBankToken));
        Mockito.when(openBankService.findAccount(Mockito.any(AccountRequestDto.class))).thenReturn(openBankAccountSearchResponseDto);
        Mockito.when(accountRepository.findAccountsByMemberId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        //when
        Long size = accountService.saveAccounts(memberId);
        //then
        Assertions.assertEquals(2, size);
    }

    @Test
    @DisplayName("오픈뱅킹 요청시 2개의 계좌를 response 받았고 1개의 계좌('111111')는 이미 db에 가지고있고 1개의 계좌는 가지고 있지 않을경우 1개만 저장된다.")
    void 계좌저장_성공2() {
        //given
        Long memberId = 1L;

        OpenBankToken openBankToken = OpenBankToken.builder().openBankId("1234").accessToken("test").refreshToken("test").memberId(memberId).id(1L).build();
        List<Account> myAccounts = List.of(
                Account.builder().fintechUseNum("111111").build()
        );
        //account 1
        OpenBankAccountDto resAccount= new OpenBankAccountDto();
        resAccount.setFintech_use_num("111111");
        //account2
        OpenBankAccountDto resAccount2 = new OpenBankAccountDto();
        resAccount2.setFintech_use_num("22222");

        OpenBankAccountSearchResponseDto openBankAccountSearchResponseDto = new OpenBankAccountSearchResponseDto();
        openBankAccountSearchResponseDto.setRes_list(List.of(resAccount,resAccount2));

        Mockito.when(memberRepository.existsMemberById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.of(openBankToken));
        Mockito.when(openBankService.findAccount(Mockito.any(AccountRequestDto.class))).thenReturn(openBankAccountSearchResponseDto);
        Mockito.when(accountRepository.findAccountsByMemberId(Mockito.anyLong())).thenReturn(myAccounts);
        //when
        Long size = accountService.saveAccounts(memberId);
        //then
        Assertions.assertEquals(1, size);
    }
    @Test
    @DisplayName("오픈뱅킹 요청시 2개의 계좌를 response 받았고 2개의 계좌모두 db에 가지고있고 있다면 저장되지 않는다.")
    void 계좌저장_성공3() {
        Long memberId = 1L;

        OpenBankToken openBankToken = OpenBankToken.builder().openBankId("1234").accessToken("test").refreshToken("test").memberId(memberId).id(1L).build();

        List<Account> myAccounts = List.of(
                Account.builder().fintechUseNum("111111").build(),
                Account.builder().fintechUseNum("222222").build()
        );
        //account 1
        OpenBankAccountDto resAccount= new OpenBankAccountDto();
        resAccount.setFintech_use_num("111111");
        //account2
        OpenBankAccountDto resAccount2 = new OpenBankAccountDto();
        resAccount2.setFintech_use_num("222222");

        OpenBankAccountSearchResponseDto openBankAccountSearchResponseDto = new OpenBankAccountSearchResponseDto();
        openBankAccountSearchResponseDto.setRes_list(List.of(resAccount,resAccount2));

        Mockito.when(memberRepository.existsMemberById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.of(openBankToken));
        Mockito.when(openBankService.findAccount(Mockito.any(AccountRequestDto.class))).thenReturn(openBankAccountSearchResponseDto);
        Mockito.when(accountRepository.findAccountsByMemberId(Mockito.anyLong())).thenReturn(myAccounts);
        //when,then
        Long size = accountService.saveAccounts(memberId);
        //then
        Assertions.assertEquals(0, size);
    }

    @Test
    @DisplayName("member가 존재하지 않는다.")
    void 계좌저장_실패_1() {
        //given
        Long memberId = 1L;

        OpenBankToken openBankToken = OpenBankToken.builder().openBankId("1234").accessToken("test").refreshToken("test").memberId(memberId).id(1L).build();

        //account 1
        OpenBankAccountDto resAccount= new OpenBankAccountDto();
        resAccount.setFintech_use_num("111111");

        //account2
        OpenBankAccountDto resAccount2 = new OpenBankAccountDto();
        resAccount2.setFintech_use_num("22222");

        OpenBankAccountSearchResponseDto openBankAccountSearchResponseDto = new OpenBankAccountSearchResponseDto();
        openBankAccountSearchResponseDto.setRes_list(List.of(resAccount,resAccount2));

        Mockito.when(memberRepository.existsMemberById(Mockito.anyLong())).thenReturn(false);
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.of(openBankToken));
        Mockito.when(openBankService.findAccount(Mockito.any(AccountRequestDto.class))).thenReturn(openBankAccountSearchResponseDto);
        Mockito.when(accountRepository.findAccountsByMemberId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        //when,then
        Assertions.assertThrows(NoSuchElementException.class, ()-> accountService.saveAccounts(memberId));
    }
    @Test
    @DisplayName("오픈뱅킹 토큰이 존재하지 않는다.")
    void 계좌저장_실패_2() {
        //given
        Long memberId = 1L;
        //account 1
        OpenBankAccountDto resAccount= new OpenBankAccountDto();
        resAccount.setFintech_use_num("111111");
        //account2
        OpenBankAccountDto resAccount2 = new OpenBankAccountDto();
        resAccount2.setFintech_use_num("22222");

        OpenBankAccountSearchResponseDto openBankAccountSearchResponseDto = new OpenBankAccountSearchResponseDto();
        openBankAccountSearchResponseDto.setRes_list(List.of(resAccount,resAccount2));

        Mockito.when(memberRepository.existsMemberById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));
        Mockito.when(openBankService.findAccount(Mockito.any(AccountRequestDto.class))).thenReturn(openBankAccountSearchResponseDto);
        Mockito.when(accountRepository.findAccountsByMemberId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        //when,then
        Assertions.assertThrows(NoSuchElementException.class, ()-> accountService.saveAccounts(memberId));
    }

    @Test
    void 계좌조회_성공() {
        //given
        Long memberId = 1L;
        OpenBankToken openBankToken = OpenBankToken.builder().openBankId("1234").accessToken("test").refreshToken("test").memberId(memberId).id(1L).build();
        String account_1 = "111111";
        String account_2 = "222222";
        List<Account> myAccounts = List.of(
                Account.builder().memberId(memberId).id(1L).fintechUseNum(account_1).build(),
                Account.builder().memberId(memberId).id(2L).fintechUseNum(account_2).build()
        );

        Mockito.when(memberRepository.existsMemberById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.of(openBankToken));
        Mockito.when(accountRepository.findAccountsByMemberId(Mockito.anyLong())).thenReturn(myAccounts);

        myAccounts.forEach(
                account -> {
                    OpenBankBalanceResponseDto openBankBalanceResponseDto = new OpenBankBalanceResponseDto();
                    openBankBalanceResponseDto.setBalance_amt("10000");
                    openBankBalanceResponseDto.setFintech_use_num(account.getFintechUseNum());
                    Mockito.when(openBankService.findBalance(Mockito.any(BalanceRequestDto.class))).thenReturn(openBankBalanceResponseDto);
                }
        );
        //when
        List<AccountDto> accounts = accountService.findAccountsByMemberId(memberId);
        //then
        System.out.println(accounts);
        Assertions.assertEquals(2,accounts.size());
        Assertions.assertEquals(2L,accounts.get(0).getId());
        Assertions.assertEquals("10000",accounts.get(0).getBalanceAmt());
    }
    @Test
    @DisplayName("member가 존재하지 않는다.")
    void 계좌조회_실패() {
        //given
        Long memberId = 1L;
        OpenBankToken openBankToken = OpenBankToken.builder().openBankId("1234").accessToken("test").refreshToken("test").memberId(memberId).id(1L).build();
        String account_1 = "111111";
        String account_2 = "222222";
        List<Account> myAccounts = List.of(
                Account.builder().memberId(memberId).id(1L).fintechUseNum(account_1).build(),
                Account.builder().memberId(memberId).id(2L).fintechUseNum(account_2).build()
        );

        Mockito.when(memberRepository.existsMemberById(Mockito.anyLong())).thenReturn(false);
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.of(openBankToken));
        Mockito.when(accountRepository.findAccountsByMemberId(Mockito.anyLong())).thenReturn(myAccounts);

        myAccounts.forEach(
                account -> {
                    OpenBankBalanceResponseDto openBankBalanceResponseDto = new OpenBankBalanceResponseDto();
                    openBankBalanceResponseDto.setBalance_amt("10000");
                    openBankBalanceResponseDto.setFintech_use_num(account.getFintechUseNum());
                    Mockito.when(openBankService.findBalance(Mockito.any(BalanceRequestDto.class))).thenReturn(openBankBalanceResponseDto);
                }
        );
        //when, then
        Assertions.assertThrows(NoSuchElementException.class, ()-> accountService.findAccountsByMemberId(memberId));
    }
    @Test
    @DisplayName("오픈뱅킹 토큰이 존재하지 않는다.")
    void 계좌조회_실패2() {
        //given
        Long memberId = 1L;
        String account_1 = "111111";
        String account_2 = "222222";
        List<Account> myAccounts = List.of(
                Account.builder().memberId(memberId).id(1L).fintechUseNum(account_1).build(),
                Account.builder().memberId(memberId).id(2L).fintechUseNum(account_2).build()
        );

        Mockito.when(memberRepository.existsMemberById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));
        Mockito.when(accountRepository.findAccountsByMemberId(Mockito.anyLong())).thenReturn(myAccounts);

        myAccounts.forEach(
                account -> {
                    OpenBankBalanceResponseDto openBankBalanceResponseDto = new OpenBankBalanceResponseDto();
                    openBankBalanceResponseDto.setBalance_amt("10000");
                    openBankBalanceResponseDto.setFintech_use_num(account.getFintechUseNum());
                    Mockito.when(openBankService.findBalance(Mockito.any(BalanceRequestDto.class))).thenReturn(openBankBalanceResponseDto);
                }
        );
        //when, then
        Assertions.assertThrows(NoSuchElementException.class, ()-> accountService.findAccountsByMemberId(memberId));
    }
    @Test
    @DisplayName("계좌가 존재하지 않는다.")
    void 계좌조회_실패3() {
        //given
        Long memberId = 1L;
        OpenBankToken openBankToken = OpenBankToken.builder().openBankId("1234").accessToken("test").refreshToken("test").memberId(memberId).id(1L).build();
        Mockito.when(memberRepository.existsMemberById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.of(openBankToken));
        Mockito.when(accountRepository.findAccountsByMemberId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        //when, then
        Assertions.assertThrows(IllegalArgumentException.class, ()-> accountService.findAccountsByMemberId(memberId));
    }
    @Test
    @DisplayName("금액 불러오기실패시 빈값을 리턴한다")
    void 계좌조회_금액불러오기실패시_빈값리턴한다() {
        //given
        Long memberId = 1L;
        OpenBankToken openBankToken = OpenBankToken.builder().openBankId("1234").accessToken("test").refreshToken("test").memberId(memberId).id(1L).build();
        String account_1 = "111111";
        String account_2 = "222222";
        List<Account> myAccounts = List.of(
                Account.builder().memberId(memberId).id(1L).fintechUseNum(account_1).build(),
                Account.builder().memberId(memberId).id(2L).fintechUseNum(account_2).build()
        );

        Mockito.when(memberRepository.existsMemberById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.of(openBankToken));
        Mockito.when(accountRepository.findAccountsByMemberId(Mockito.anyLong())).thenReturn(myAccounts);

        myAccounts.forEach(
                account -> {
                    Mockito.when(openBankService.findBalance(Mockito.any(BalanceRequestDto.class))).thenThrow(RuntimeException.class);
                }
        );
        //when
        List<AccountDto> accounts = accountService.findAccountsByMemberId(memberId);
        //then
        System.out.println(accounts);
        Assertions.assertEquals(2,accounts.size());
        Assertions.assertEquals("",accounts.get(0).getBalanceAmt());
        Assertions.assertEquals("",accounts.get(1).getBalanceAmt());
    }

    @Test
    @DisplayName("주계좌가 없을 경우 바로 업데이트")
    void updateAccountType(){
        Account account = Account.builder().memberId(1L).id(1L).accountType(AccountType.SUB).build();
        Mockito.when(accountRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(account));
        Mockito.when(accountRepository.findMainAccountByMemberId(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));
        //when, then
        accountService.updateAccountType(1L,1L);
    }
    @Test
    @DisplayName("주계좌가 있을경우 기존 주계좌는 sub로 변경 후 업데이트")
    void updateAccountType_2(){
        Account account = Account.builder().memberId(1L).id(1L).accountType(AccountType.SUB).build();
        Account pre_account = Account.builder().memberId(1L).id(2L).accountType(AccountType.MAIN).build();
        Mockito.when(accountRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(account));
        Mockito.when(accountRepository.findMainAccountByMemberId(Mockito.anyLong())).thenReturn(Optional.ofNullable(pre_account));
        //when
        accountService.updateAccountType(1L,1L);
        //then
        Assertions.assertEquals(true, account.isMainAccount());
        Assertions.assertEquals(false, pre_account.isMainAccount());
    }
}