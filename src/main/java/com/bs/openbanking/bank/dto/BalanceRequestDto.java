package com.bs.openbanking.bank.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class BalanceRequestDto {
    private String accessToken;
    private Long memberId;
    private String fintechUseNum;

    @Builder
    public BalanceRequestDto(String accessToken, Long memberId, String fintechUseNum) {
        this.accessToken = accessToken;
        this.memberId = memberId;
        this.fintechUseNum = fintechUseNum;
    }
}
