package com.bs.openbanking.bank.dto;

import lombok.Builder;
import lombok.Data;


@Data
public class AccountDto {

    private Long id;
    private Long memberId;
    private String fintechUseNum;
    private String bankName;
    private String accountNum;
    private String bankCode;
    private String accountSeq;
    private String balanceAmt;
    @Builder
    public AccountDto(Long id, Long memberId, String fintechUseNum, String bankName, String accountNum, String bankCode, String accountSeq, String balanceAmt) {
        this.id = id;
        this.memberId = memberId;
        this.fintechUseNum = fintechUseNum;
        this.bankName = bankName;
        this.accountNum = accountNum;
        this.bankCode = bankCode;
        this.accountSeq = accountSeq;
        this.balanceAmt = balanceAmt;
    }
}
