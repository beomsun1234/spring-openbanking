package com.bs.openbanking.bank.repository;

import com.bs.openbanking.bank.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {
    List<Account> findAccountsByMemberId(Long memberId);
    @Query("select a from Account as a where a.memberId= :memberId and a.accountType = 'MAIN'")
    Optional<Account> findMainAccountByMemberId(@Param("memberId") Long memberId);
}
