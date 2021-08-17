package com.bs.openbanking.bank.dto;

import com.bs.openbanking.bank.dto.AcountDto;
import lombok.Data;

import java.util.List;

@Data
public class BankAcountSearchResponseDto {

    private String api_tran_id;
    private String rsp_code;
    private String rsp_message;
    private String api_tran_dtm;
    private String user_name;
    private String res_cnt;
    private List<AcountDto> res_list;


}
