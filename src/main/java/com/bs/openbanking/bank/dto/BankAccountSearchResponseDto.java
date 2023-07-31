package com.bs.openbanking.bank.dto;

import lombok.Data;

import java.util.List;

@Data
public class BankAccountSearchResponseDto {

    private String api_tran_id;
    private String rsp_code;
    private String rsp_message;
    private String api_tran_dtm;
    private String user_name;
    private String res_cnt;
    private List<AccountDto> res_list;


}
