package com.bs.openbanking.bank;

import lombok.Data;

@Data
public class BankRequestToken {
    private String code;
    private String client_id;
    private String client_secret;
    private String redirect_uri;
    private String grant_type;
}
