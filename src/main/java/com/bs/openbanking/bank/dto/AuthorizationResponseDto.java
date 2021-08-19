package com.bs.openbanking.bank.dto;

import lombok.Data;

@Data
public class AuthorizationResponseDto {
    private String code;
    private String scope;
    private String client_info;
    private String state;
}
