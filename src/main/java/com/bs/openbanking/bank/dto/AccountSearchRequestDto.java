package com.bs.openbanking.bank.dto;

import lombok.Data;

@Data
public class AccountSearchRequestDto {
    private String access_token;
    private String user_seq_no;
    private String include_cancel_yn;
    private String sort_order;
    private String model;
}
