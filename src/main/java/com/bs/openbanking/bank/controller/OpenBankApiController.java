package com.bs.openbanking.bank.controller;

import com.bs.openbanking.bank.dto.*;
import com.bs.openbanking.bank.service.OpenBankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
public class OpenBankApiController {

    private final OpenBankService openBankService;

    @GetMapping("/api/account/list")
    public List<OpenBankAccountResponseDto> getAccountWithBalance(@RequestBody AccountRequestDto accountRequestDto) {
        return openBankService.getAccountWithBalance(accountRequestDto);
    }
}


