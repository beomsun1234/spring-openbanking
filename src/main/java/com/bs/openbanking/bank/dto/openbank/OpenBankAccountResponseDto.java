package com.bs.openbanking.bank.dto.openbank;

import lombok.Builder;
import lombok.Data;

@Data
public class OpenBankAccountResponseDto {
    private String fintech_use_num;
    private String bank_name;
    private String account_num;
    private String account_num_masked;
    private String account_seq;
    private String balance_amt;

    @Builder
    public OpenBankAccountResponseDto(String fintech_use_num, String bank_name, String account_num, String account_num_masked, String account_seq, String balance_amt){
        this.fintech_use_num = fintech_use_num;
        this.balance_amt = balance_amt;
        this.account_num = account_num;
        this.account_seq = account_seq;
        this.bank_name = bank_name;
        this.account_num_masked = account_num_masked;
    }
}
