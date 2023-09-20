package com.bs.openbanking.bank.controller;

import com.bs.openbanking.bank.dto.AccountDto;
import com.bs.openbanking.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@RequestMapping("api")
public class AccountApiController {
    private final AccountService accountService;

    @PostMapping("members/{id}/account")
    public ResponseEntity<Long> saveAccounts(@PathVariable Long id){
        Long size = accountService.saveAccounts(id);
        return ResponseEntity.ok().body(size);
    }

    @GetMapping("members/{id}/account")
    public ResponseEntity<List<AccountDto>> findAccounts(@PathVariable Long id){
        List<AccountDto> accounts = accountService.findAccountsByMemberId(id);
        return ResponseEntity.ok().body(accounts);
    }

    @PutMapping("members/{id}/accounts/{accountId}")
    public ResponseEntity updateAccountType(@PathVariable Long id, @PathVariable("accountId") Long accountId) {
        accountService.updateAccountType(id, accountId);
        return ResponseEntity.status(200).build();
    }
}
