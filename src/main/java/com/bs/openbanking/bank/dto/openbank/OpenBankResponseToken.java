package com.bs.openbanking.bank.dto.openbank;


import lombok.Data;

@Data
public class OpenBankResponseToken {
    private String rsp_code;
    private String rsp_message;
    private String access_token;
    private String token_type;
    private int expires_in;
    private String refresh_token;
    private String scope;
    private String user_seq_no;
}
