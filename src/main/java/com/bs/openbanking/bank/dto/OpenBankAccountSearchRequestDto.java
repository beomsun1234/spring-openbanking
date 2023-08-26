package com.bs.openbanking.bank.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class OpenBankAccountSearchRequestDto {
    private String access_token;
    private String user_seq_no;
    private String include_cancel_yn;
    private String sort_order;

    @Builder
    public OpenBankAccountSearchRequestDto(String access_token, String user_seq_no, String include_cancel_yn, String sort_order) {
        this.access_token = access_token;
        this.user_seq_no = user_seq_no;
        this.include_cancel_yn = include_cancel_yn;
        this.sort_order = sort_order;
    }
}
