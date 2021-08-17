package com.bs.openbanking.bank.dto;


import lombok.Data;

@Data
public class BankReponseToken {
    private String access_token;
    private String token_type;
    private int expires_in;
    private String refresh_token;
    private String scope;
    private String user_seq_no;
}
