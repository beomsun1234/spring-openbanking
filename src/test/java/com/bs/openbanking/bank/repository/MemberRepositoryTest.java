package com.bs.openbanking.bank.repository;

import com.bs.openbanking.bank.domain.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("openbank-ex")
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("exists test")
    @Test
    @Rollback(value = true)
    void exists_test(){
        //given
        Member initMember = Member.builder().email("test").openBankId("test").password("Test").build();
        Member expect = memberRepository.save(initMember);
        //when
        boolean result = memberRepository.existsMemberById(expect.getId());
        //then
        Assertions.assertEquals(true, result);
    }
}