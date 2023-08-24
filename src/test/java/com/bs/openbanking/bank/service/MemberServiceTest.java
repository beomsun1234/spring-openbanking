package com.bs.openbanking.bank.service;

import com.bs.openbanking.bank.domain.Member;
import com.bs.openbanking.bank.dto.MemberDto;
import com.bs.openbanking.bank.dto.SignInDto;
import com.bs.openbanking.bank.dto.SignUpDto;
import com.bs.openbanking.bank.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 성공")
    void 회원가입() {
        //given
        Mockito.when(memberRepository.save(Mockito.any(Member.class))).thenReturn(Member.builder().id(1L).email("test").build());
        //when, then
        memberService.signUp(SignUpDto.builder().email("test").password("test").build());
    }

    @Test
    @DisplayName("회원조회")
    void 회원조회() {
        //given
        Member expect = Member.builder().id(1L).email("test").build();
        Mockito.when(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expect));
        //when
        MemberDto result = memberService.findMemberById(1L);
        //then
        Assertions.assertEquals(expect.getId(), result.getId());
    }

    @Test
    @DisplayName("회원조회 실패")
    void 회원조회_실패() {
        //given
        Mockito.when(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));
        //when//then
        Assertions.assertThrows(NoSuchElementException.class, ()-> memberService.findMemberById(1L));
    }

    @Test
    @DisplayName("로그인 성공")
    void 로그인_성공() {
        //given
        SignInDto signInDto = SignInDto.builder().email("test").password("test").build();
        Member expect = Member.builder().id(1L).email("test").password("test").build();
        Mockito.when(memberRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(expect));
        //when
        MemberDto memberDto = memberService.signIn(signInDto);
        //then
        Assertions.assertEquals(expect.getId(), memberDto.getId());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 이메일")
    void 로그인_실패() {
        //given
        SignInDto signInDto = SignInDto.builder().email("test").password("test").build();
        Mockito.when(memberRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.ofNullable(null));
        //when//then
        assertThrows(NoSuchElementException.class, ()-> memberService.signIn(signInDto));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 틀림")
    void 로그인_실패2() {
        //given
        SignInDto signInDto = SignInDto.builder().email("test").password("1234").build();
        Member expect = Member.builder().id(1L).email("test").password("test").build();
        Mockito.when(memberRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(expect));
        //when//then
        assertThrows(IllegalArgumentException.class, ()->memberService.signIn(signInDto));
    }
}