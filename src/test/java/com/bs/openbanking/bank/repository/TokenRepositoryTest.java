package com.bs.openbanking.bank.repository;

import com.bs.openbanking.bank.domain.Member;
import com.bs.openbanking.bank.domain.OpenBankToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("openbank-ex")
class TokenRepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private MemberRepository memberRepository;


    @Test
    @DisplayName("오픈뱅킹 토큰 조회 - memberId")
    @Rollback
    void findOpenBankTokenByMemberId() {
        //given
        Member member = memberRepository.save(Member.builder().email("test").openBankId("test").openBankId("test").build());
        tokenRepository.save(OpenBankToken.builder().accessToken("test").refreshToken("test").memberId(member.getId()).build());
        //when
        OpenBankToken openBankToken = tokenRepository.findOpenBankTokenByMemberId(member.getId()).orElseThrow();
        //then
        Assertions.assertEquals("test", openBankToken.getAccessToken());
        Assertions.assertEquals(member.getId(), openBankToken.getMemberId());
    }

    @Test
    @DisplayName("오픈뱅킹 토큰 확인 - memberId")
    @Rollback
    void existsOpenBankTokenByMemberId() {
        //given
        Member member = memberRepository.save(Member.builder().email("test").openBankId("test").openBankId("test").build());
        tokenRepository.save(OpenBankToken.builder().accessToken("test").refreshToken("test").memberId(member.getId()).build());
        //when
        boolean result = tokenRepository.existsOpenBankTokenByMemberId(member.getId());
        //then
        Assertions.assertEquals(true, result);
    }
    @Test
    @DisplayName("오픈뱅킹 토큰 확인 실패 - memberId")
    @Rollback
    void existsOpenBankTokenByMemberId_실패() {
        //given
        Member member = memberRepository.save(Member.builder().email("test").openBankId("test").openBankId("test").build());
        //when
        boolean result = tokenRepository.existsOpenBankTokenByMemberId(member.getId());
        //then
        Assertions.assertEquals(false, result);
    }
}