package com.bs.openbanking.bank.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class BankRequestToken {
    private String code;
    private String client_id;
    private String client_secret;
    private String redirect_uri;
    private String grant_type; //고정값: authorization_code


    public void setBankRequestToken(String client_id,String client_secret, String redirect_uri, String grant_type){
        this.client_id= client_id;
        this.client_secret = client_secret;
        this.redirect_uri = redirect_uri;
        this.grant_type = grant_type;
    }
}
