package com.bs.openbanking.bank.dto;


import lombok.Data;

@Data
public class AccountTransferResponseDto {
    private String api_tran_id;
    private String api_tran_dtm;
    private String rsp_code;
    private String rsp_message;
    private String dps_bank_code_std;
    private String dps_bank_code_sub;
    private String dps_bank_name;
    private String dps_account_num_masked;
    private String dps_print_content;
    private String dps_account_holder_name;
    private String bank_tran_id;
    private String bank_tran_date;
    private String bank_code_tran;
    private String bank_rsp_code;
    private String bank_rsp_message;
    private String fintech_use_num;
    private String account_alias;
    private String bank_code_std;
    private String bank_code_sub;
    private String bank_name;
    private String account_num_masked;
    private String print_content;
    private String account_holder_name;
    private String tran_amt;
    private String wd_limit_remain_amt;
}
