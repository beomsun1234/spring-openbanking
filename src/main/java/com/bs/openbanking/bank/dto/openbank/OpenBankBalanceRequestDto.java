package com.bs.openbanking.bank.dto.openbank;

import lombok.Builder;
import lombok.Data;

@Data
public class OpenBankBalanceRequestDto {
    private String access_token;
    private String bank_tran_id;
    private String fintech_use_num;
    private String tran_dtime;

    @Builder
    public OpenBankBalanceRequestDto(String fintech_use_num, String bank_tran_id, String tran_dtime, String accessToken){
        this.fintech_use_num = fintech_use_num;
        this.bank_tran_id = bank_tran_id;
        this.tran_dtime = tran_dtime;
        this.access_token = accessToken;
    }
}
