package com.bs.openbanking.bank.service;

import com.bs.openbanking.bank.domain.Member;
import com.bs.openbanking.bank.domain.OpenBankToken;
import com.bs.openbanking.bank.dto.OpenBankReponseToken;
import com.bs.openbanking.bank.dto.OpenBankTokenDto;
import com.bs.openbanking.bank.dto.TokenRequestDto;
import com.bs.openbanking.bank.repository.MemberRepository;
import com.bs.openbanking.bank.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.NotActiveException;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final OpenBankService openBankService;

    @Transactional
    public void saveOpenBankUserToken(TokenRequestDto tokenRequestDto){

        Member member = memberRepository.findById(tokenRequestDto.getMemberId()).orElseThrow();

        if (tokenRepository.existsOpenBankTokenByMemberId(tokenRequestDto.getMemberId())){
            throw new RuntimeException("이미 토큰이 존재함");
        }

        OpenBankReponseToken openBankReponseToken = openBankService.requestToken(tokenRequestDto);

        OpenBankToken openBankToken = OpenBankToken.builder()
                .accessToken(openBankReponseToken.getAccess_token())
                .refreshToken(openBankReponseToken.getRefresh_token())
                .expiresIn((long) openBankReponseToken.getExpires_in())
                .openBankId(openBankReponseToken.getUser_seq_no())
                .memberId(tokenRequestDto.getMemberId())
                .build();

        OpenBankToken bankToken = tokenRepository.save(openBankToken);

        member.updateOpenBankId(bankToken.getAccessToken());
    }

    public OpenBankTokenDto findOpenBankTokenByMemberId(Long memberId){
        OpenBankToken openBankToken = tokenRepository.findOpenBankTokenByMemberId(memberId).orElseThrow();
        return OpenBankTokenDto.of(openBankToken);
    }
}
