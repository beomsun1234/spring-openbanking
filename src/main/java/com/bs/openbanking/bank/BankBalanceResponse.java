package com.bs.openbanking.bank;

import lombok.Data;

@Data
public class BankBalanceResponse {
    public String api_tran_id;
    public String api_tran_dtm;
    public String rsp_code;
    public String rsp_message;
    public String bank_tran_id;
    public String bank_tran_date;
    public String bank_code_tran;
    public String bank_rsp_code;
    public String bank_rsp_message;
    public String bank_name;
    public String savings_bank_name;
    public String fintech_use_num;
    public String balance_amt;
    public String available_amt;
    public String account_type;
    public String product_name;
    public String account_issue_date;
    public String maturity_date;
    public String last_tran_date;
}
