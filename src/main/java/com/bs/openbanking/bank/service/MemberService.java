package com.bs.openbanking.bank.service;

import com.bs.openbanking.bank.domain.Member;
import com.bs.openbanking.bank.dto.MemberDto;
import com.bs.openbanking.bank.dto.SignInDto;
import com.bs.openbanking.bank.dto.SignUpDto;
import com.bs.openbanking.bank.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Long signUp(SignUpDto signUpDto){
        Member member = memberRepository.save(signUpDto.toEntity());
        return member.getId();
    }


    public MemberDto findMemberById(Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow();
        return MemberDto.of(member);
    }

    public MemberDto signIn(SignInDto signInDto){
        Member member = memberRepository.findByEmail(signInDto.getEmail()).orElseThrow();

        if (!member.isVaildPassword(signInDto.getPassword())){
            throw new IllegalArgumentException("로그인실패");
        }

        return MemberDto.of(member);
    }

}
