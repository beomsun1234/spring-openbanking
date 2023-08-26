package com.bs.openbanking.bank.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class AccountRequestDto {
    private String accessToken;
    private String openBankId;

    @Builder
    public AccountRequestDto(String accessToken, String openBankId) {
        this.accessToken = accessToken;
        this.openBankId = openBankId;
    }
}
