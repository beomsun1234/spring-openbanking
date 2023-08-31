package com.bs.openbanking.bank.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class OpenBankUserInfoRequestDto {
    private String openBankId;
    private String accessToken;

    @Builder
    public OpenBankUserInfoRequestDto(String openBankId, String accessToken) {
        this.openBankId = openBankId;
        this.accessToken = accessToken;
    }
}
