package com.bs.openbanking.bank.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class SignInDto {
    private String email;
    private String password;

    @Builder
    public SignInDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
