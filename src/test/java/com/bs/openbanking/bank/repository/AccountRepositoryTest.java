package com.bs.openbanking.bank.repository;

import com.bs.openbanking.bank.domain.Account;
import com.bs.openbanking.bank.domain.AccountType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("openbank-ex")
class AccountRepositoryTest {

    @Autowired private AccountRepository accountRepository;

    @Test
    @Rollback
    void findAccountsByMemberId() {
        //given
        accountRepository.saveAll(List.of(
                Account.builder().fintechUseNum("1234").memberId(1L).accountNum("1234").build(),
                Account.builder().fintechUseNum("2222").memberId(1L).accountNum("2222").build()
        ));
        //when
        List<Account> accounts = accountRepository.findAccountsByMemberId(1L);
        //then
        Assertions.assertEquals(2, accounts.size());
        Assertions.assertEquals(1L, accounts.get(0).getMemberId());
        Assertions.assertEquals(AccountType.SUB, accounts.get(0).getAccountType());
    }

    @Test
    @Rollback
    void findMainAccountByMemberId() {
        //given
        accountRepository.saveAll(List.of(
                Account.builder().fintechUseNum("1234").memberId(1L).accountNum("1234").accountType(AccountType.MAIN).build(),
                Account.builder().fintechUseNum("2222").memberId(1L).accountNum("2222").build()
        ));
        //when
        Account account = accountRepository.findMainAccountByMemberId(1L).orElseThrow();
        //then
        Assertions.assertEquals("1234", account.getAccountNum());
        Assertions.assertEquals("1234", account.getFintechUseNum());
        Assertions.assertEquals(AccountType.MAIN, account.getAccountType());
    }

    @Test
    @Rollback
    @DisplayName("accountType 설정안하고 저장시 기본으로 sub")
    void save() {
        //given
        Account save = accountRepository.save(Account.builder().fintechUseNum("2222").memberId(1L).accountNum("2222").build());
        //when
        Account account = accountRepository.findById(save.getId()).orElseThrow();
        //then
        Assertions.assertEquals(AccountType.SUB, account.getAccountType());
    }
}