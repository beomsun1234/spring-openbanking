package com.bs.openbanking.bank.dto;

import lombok.Data;

@Data
public class OpenBankFailureResponseDto {
    private String rsp_code;
    private String rsp_message;
}
