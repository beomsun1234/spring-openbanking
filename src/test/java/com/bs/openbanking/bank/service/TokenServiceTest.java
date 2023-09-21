package com.bs.openbanking.bank.service;

import com.bs.openbanking.bank.domain.Member;
import com.bs.openbanking.bank.domain.OpenBankToken;
import com.bs.openbanking.bank.dto.openbank.OpenBankResponseToken;
import com.bs.openbanking.bank.dto.OpenBankTokenDto;
import com.bs.openbanking.bank.dto.TokenRequestDto;
import com.bs.openbanking.bank.repository.MemberRepository;
import com.bs.openbanking.bank.repository.TokenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

@ExtendWith({SpringExtension.class})
@ActiveProfiles("openbank")
class TokenServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private OpenBankService openBankService;
    @InjectMocks
    private TokenService tokenService;

    @Test
    @DisplayName("토큰저장 성공")
    void 토큰저장성공() {
        //given
        Member member = Member.builder().id(1L).email("test").build();
        OpenBankToken openBankToken = OpenBankToken.builder().accessToken("test").openBankId("test").refreshToken("test").memberId(1L).build();
        OpenBankResponseToken openBankResponseToken = new OpenBankResponseToken();
        openBankResponseToken.setAccess_token("test");
        openBankResponseToken.setRefresh_token("test");
        openBankResponseToken.setUser_seq_no("1234");
        openBankResponseToken.setExpires_in(100);

        TokenRequestDto tokenRequestDto = TokenRequestDto.builder().memberId(1L).code("test").build();

        Mockito.when(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(member));
        Mockito.when(tokenRepository.existsOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(false);
        Mockito.when(openBankService.requestToken(Mockito.any(TokenRequestDto.class))).thenReturn(openBankResponseToken);
        Mockito.when(tokenRepository.save(Mockito.any(OpenBankToken.class))).thenReturn(openBankToken);
        //when, then
        tokenService.saveOpenBankUserToken(tokenRequestDto);
    }

    @Test
    @DisplayName("토큰저장 실패 - 이미 토큰이 존재할 경우")
    void 토큰저장실패_1() {
        //given
        Member member = Member.builder().id(1L).email("test").build();
        OpenBankToken openBankToken = OpenBankToken.builder().memberId(1L).accessToken("test").build();

        OpenBankResponseToken openBankResponseToken = new OpenBankResponseToken();
        openBankResponseToken.setAccess_token("test");
        openBankResponseToken.setRefresh_token("test");
        openBankResponseToken.setUser_seq_no("1234");
        openBankResponseToken.setExpires_in(100);

        TokenRequestDto tokenRequestDto = TokenRequestDto.builder().memberId(1L).code("test").build();

        Mockito.when(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(member));
        Mockito.when(tokenRepository.existsOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(openBankService.requestToken(Mockito.any(TokenRequestDto.class))).thenReturn(openBankResponseToken);
        Mockito.when(tokenRepository.save(Mockito.any(OpenBankToken.class))).thenReturn(openBankToken);
        //when, then
        Assertions.assertThrows(RuntimeException.class, () -> tokenService.saveOpenBankUserToken(tokenRequestDto));
    }
    @Test
    @DisplayName("토큰저장 실패 - 회원이 없을경우")
    void 토큰저장실패_2() {
        //given
        OpenBankToken openBankToken = OpenBankToken.builder().memberId(1L).accessToken("test").build();

        OpenBankResponseToken openBankResponseToken = new OpenBankResponseToken();
        openBankResponseToken.setAccess_token("test");
        openBankResponseToken.setRefresh_token("test");
        openBankResponseToken.setUser_seq_no("1234");
        openBankResponseToken.setExpires_in(100);

        TokenRequestDto tokenRequestDto = TokenRequestDto.builder().memberId(1L).code("test").build();

        Mockito.when(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));
        Mockito.when(tokenRepository.existsOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(false);
        Mockito.when(openBankService.requestToken(Mockito.any(TokenRequestDto.class))).thenReturn(openBankResponseToken);
        Mockito.when(tokenRepository.save(Mockito.any(OpenBankToken.class))).thenReturn(openBankToken);
        //when, then
        Assertions.assertThrows(NoSuchElementException.class, () -> tokenService.saveOpenBankUserToken(tokenRequestDto));
    }
    @Test
    @DisplayName("오픈뱅킹상요자토큰조회 성공")
    void 오픈뱅킹상요자토큰조회() {
        //given
        OpenBankToken openBankToken = OpenBankToken.builder().accessToken("test").openBankId("test").refreshToken("test").expiresIn(1L).memberId(1L).id(1L).build();
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.of(openBankToken));
        //when
        OpenBankTokenDto token = tokenService.findOpenBankTokenByMemberId(1L);
        //then
        Assertions.assertEquals(openBankToken.getAccessToken(), token.getAccessToken());
    }
    @Test
    @DisplayName("오픈뱅킹상요자토큰조회 실패 - 토큰이 존재하지 않을경우")
    void 오픈뱅킹상요자토큰조회_실패() {
        //given
        Mockito.when(tokenRepository.findOpenBankTokenByMemberId(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));
        //when, then
        Assertions.assertThrows(NoSuchElementException.class, ()-> tokenService.findOpenBankTokenByMemberId(1L));
    }


}