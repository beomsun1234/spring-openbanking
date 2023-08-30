package com.bs.openbanking.bank.dto;

import com.bs.openbanking.bank.domain.OpenBankToken;
import lombok.Builder;
import lombok.Data;

@Data
public class OpenBankTokenDto {
    private Long memberId;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String openBankId;

    @Builder
    public OpenBankTokenDto(Long memberId, String accessToken, String refreshToken, Long expiresIn, String openBankId) {
        this.memberId = memberId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.openBankId = openBankId;
    }

    public static OpenBankTokenDto of(OpenBankToken openBankToken){
        return OpenBankTokenDto.builder().accessToken(openBankToken.getAccessToken())
                .expiresIn(openBankToken.getExpiresIn())
                .refreshToken(openBankToken.getRefreshToken())
                .memberId(openBankToken.getMemberId())
                .openBankId(openBankToken.getOpenBankId())
                .build();
    }
}
