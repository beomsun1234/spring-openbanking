package com.bs.openbanking.bank.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class BankBalanceRequestDto {
    private String bank_tran_id;
    private String fintech_use_num;
    private String tran_dtime;

    public void setBankTransIdAndTransDateTime(String bank_tran_id,String tran_dtime){
        this.bank_tran_id = bank_tran_id;
        this.tran_dtime = tran_dtime;
    }

    @Builder
    public BankBalanceRequestDto(String fintech_use_num, String bank_tran_id, String tran_dtime){
        this.fintech_use_num = fintech_use_num;
        this.bank_tran_id = bank_tran_id;
        this.tran_dtime = tran_dtime;
    }
}
