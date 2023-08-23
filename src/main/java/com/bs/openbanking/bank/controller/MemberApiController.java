package com.bs.openbanking.bank.controller;

import com.bs.openbanking.bank.dto.MemberDto;
import com.bs.openbanking.bank.dto.SignInDto;
import com.bs.openbanking.bank.dto.SignUpDto;
import com.bs.openbanking.bank.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@RequestMapping("api")
public class MemberApiController {
    private final MemberService memberService;

    @GetMapping("members/{id}")
    public ResponseEntity<MemberDto> findByMemberId(@PathVariable Long id){
        MemberDto member = memberService.findMemberById(id);
        return ResponseEntity.ok().body(member);
    }

    @PostMapping("members")
    public ResponseEntity<Long> signUp(@RequestBody SignUpDto signUpDto){
        Long memberId = memberService.signUp(signUpDto);
        return ResponseEntity.ok().body(memberId);
    }

    @PostMapping("members/login")
    public ResponseEntity<MemberDto> signIn(@RequestBody SignInDto signInDto){
        MemberDto member = memberService.signIn(signInDto);
        return ResponseEntity.ok().body(member);
    }

}
