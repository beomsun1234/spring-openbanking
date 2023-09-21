package com.bs.openbanking.bank.dto.openbank;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpenBankUserInfoResponseDto {
    private String api_tran_id;
    private String api_tran_dtm;
    private String rsp_code;
    private String rsp_message;
    private String user_seq_no;
    private String user_ci;

    @Builder
    public OpenBankUserInfoResponseDto(String api_tran_id, String api_tran_dtm, String rsp_code, String rsp_message, String user_seq_no, String user_ci) {
        this.api_tran_id = api_tran_id;
        this.api_tran_dtm = api_tran_dtm;
        this.rsp_code = rsp_code;
        this.rsp_message = rsp_message;
        this.user_seq_no = user_seq_no;
        this.user_ci = user_ci;
    }
}
