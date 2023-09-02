package com.bs.openbanking.bank.repository;

import com.bs.openbanking.bank.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account,Long> {
    List<Account> findAccountsByMemberId(Long memberId);
}
