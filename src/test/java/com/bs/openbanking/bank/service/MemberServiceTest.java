package com.bs.openbanking.bank.service;

import com.bs.openbanking.bank.domain.Member;
import com.bs.openbanking.bank.domain.OpenBankToken;
import com.bs.openbanking.bank.dto.*;
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

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private OpenBankService openBankService;
    @Mock
    private TokenRepository tokenRepository;
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

    @Test
    @DisplayName("오픈뱅킹에서 유저 ci 정보 가져와서 저장하기")
    void 유저_ci_가져오기_성공() {
        //given
        OpenBankUserInfoResponseDto userInfoResponseDto = OpenBankUserInfoResponseDto.builder().user_ci("test").build();
        Long memberId = 1L;
        String openBankId = "1234";
        Member expect = Member.builder().id(memberId).email("test").password("test").openBankId("1234").build();
        OpenBankToken openBankToken = OpenBankToken.builder().openBankId("test").memberId(memberId).accessToken("test").openBankId("1234").build();
        Mockito.when(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expect));
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.of(openBankToken));
        Mockito.when(openBankService.findOpenBankUserInfo(Mockito.any(OpenBankUserInfoRequestDto.class))).thenReturn(userInfoResponseDto);
        //when//then
        memberService.addOpenBankInfo(1L);
    }
    @Test
    @DisplayName("ci정보가지고있는지확인")
    void ci정보가지고있는지확인(){
        Member member = Member.builder().openBankCi("").build();
        boolean hasOpenBankCi = member.hasOpenBankCi();

        Assertions.assertEquals(false, hasOpenBankCi);
    }
    @Test
    @DisplayName("오픈뱅킹에서 유저 ci 정보 가져와서 저장하기 성공 - 이미 가지고있다. 빈문자")
    void 유저_ci_가져오기_상공_빈문자() {
        //given
        OpenBankUserInfoResponseDto userInfoResponseDto = OpenBankUserInfoResponseDto.builder().user_ci("").build();
        Long memberId = 1L;
        String openBankId = "1234";
        Member expect = Member.builder().openBankCi("").id(memberId).email("test").password("test").openBankId("1234").build();
        OpenBankToken openBankToken = OpenBankToken.builder().openBankId("test").memberId(memberId).accessToken("test").build();
        Mockito.when(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expect));
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.of(openBankToken));
        Mockito.when(openBankService.findOpenBankUserInfo(Mockito.any(OpenBankUserInfoRequestDto.class))).thenReturn(userInfoResponseDto);
        //when//then
        memberService.addOpenBankInfo(1L);
    }
    @Test
    @DisplayName("오픈뱅킹에서 유저 ci 정보 가져와서 저장하기 실패 - 이미 가지고있다.")
    void 유저_ci_가져오기_실패_이미가지고있다() {
        //given
        OpenBankUserInfoResponseDto userInfoResponseDto = OpenBankUserInfoResponseDto.builder().user_ci("test").build();
        Long memberId = 1L;
        String openBankId = "1234";
        Member expect = Member.builder().openBankCi("test").id(memberId).email("test").password("test").openBankId("1234").build();
        OpenBankToken openBankToken = OpenBankToken.builder().openBankId("test").memberId(memberId).accessToken("test").openBankId("1234").build();
        Mockito.when(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expect));
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.of(openBankToken));
        Mockito.when(openBankService.findOpenBankUserInfo(Mockito.any(OpenBankUserInfoRequestDto.class))).thenReturn(userInfoResponseDto);
        //when//then
        assertThrows(RuntimeException.class, ()->memberService.addOpenBankInfo(1L));
    }
    @Test
    @DisplayName("오픈뱅킹에서 유저 ci 정보 가져와서 저장하기 실패 - 존재하지 않는 member이다.")
    void 유저_ci_가져오기_실패_존재하지않는유저() {
        //given
        OpenBankUserInfoResponseDto userInfoResponseDto = OpenBankUserInfoResponseDto.builder().user_ci("test").build();
        Member member = null;
        OpenBankToken openBankToken = OpenBankToken.builder().openBankId("test").memberId(1L).accessToken("test").openBankId("1234").build();
        Mockito.when(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(member));
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.of(openBankToken));
        Mockito.when(openBankService.findOpenBankUserInfo(Mockito.any(OpenBankUserInfoRequestDto.class))).thenReturn(userInfoResponseDto);
        //when//then
        assertThrows(NoSuchElementException.class, ()->memberService.addOpenBankInfo(1L));
    }

    @Test
    @DisplayName("오픈뱅킹에서 유저 ci 정보 가져와서 저장하기 실패 - openBankId가 없다")
    void 유저_ci_가져오기_실패_오픈뱅킹_id_존재하지않는다() {
        //given
        Long memberId = 1L;
        Member expect = Member.builder().openBankCi("test").id(memberId).email("test").password("test").build();
        Mockito.when(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expect));
        //when//then
        assertThrows(RuntimeException.class, ()->memberService.addOpenBankInfo(1L));
    }

    @Test
    @DisplayName("오픈뱅킹에서 유저 ci 정보 가져와서 저장하기 실패 - 오픈뱅킹 사용자 토큰이 없는경우")
    void 유저_ci_가져오기_실패_오픈뱅킹사용자토큰없음() {
        //given
        OpenBankUserInfoResponseDto userInfoResponseDto = OpenBankUserInfoResponseDto.builder().user_ci("test").build();
        Long memberId = 1L;
        String openBankId = "1234";
        Member expect = Member.builder().id(memberId).email("test").password("test").openBankId("1234").build();
        OpenBankToken openBankToken = null;
        Mockito.when(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expect));
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.ofNullable(openBankToken));
        Mockito.when(openBankService.findOpenBankUserInfo(Mockito.any(OpenBankUserInfoRequestDto.class))).thenReturn(userInfoResponseDto);
        //when//then
        assertThrows(NoSuchElementException.class, ()->memberService.addOpenBankInfo(1L));
    }
}