package com.bs.openbanking.bank.controller;

import com.bs.openbanking.bank.dto.OpenBankTokenDto;
import com.bs.openbanking.bank.dto.TokenRequestDto;
import com.bs.openbanking.bank.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("api")
public class TokenApiController {
    private final TokenService tokenService;

    /**
     * 토큰저장 플로우 -> 프론트에서 오픈뱅킹 인증 끝나고 받은 code를 member id와 함께 백으로 요청
     * redirect_uri 은 프론트엔드 주소
     * @param tokenRequestDto
     * @return
     */
    @PostMapping("members/openbank/token")
    public ResponseEntity saveOpenBankToken(@RequestBody TokenRequestDto tokenRequestDto){
        tokenService.saveOpenBankUserToken(tokenRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("members/{id}/openbank/token")
    public ResponseEntity<OpenBankTokenDto> findOpenBankToken(@PathVariable Long id){
        OpenBankTokenDto token = tokenService.findOpenBankTokenByMemberId(id);
        return ResponseEntity.ok().body(token);
    }

}
