package com.bs.openbanking.bank.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class AccountTransferRequestDto {
    private String bank_tran_id; //M202111920+U+랜덤숫자 9 자리
    private String cntr_account_type; //n  약정 계좌/계정 구분주2)  n:계좌, c:계정
    private String cntr_account_num;  //약정 계좌/계정 번호주2) 내계좌
    private String dps_print_content; //입금계좌인자내역
    private String fintech_use_num;   //출금계좌핀테크이용번호
    private String tran_amt;   //거래금액
    private String tran_dtime;
    private String req_client_name;
    private String req_client_bank_code;
    private String req_client_account_num; //내계좌
    private String req_client_num; //임의값
    private String transfer_purpose; //TR
    private String recv_client_name;
    private String recv_client_bank_code;
    private String recv_client_account_num;


    public AccountTransferRequestDto(String bank_tran_id,String fintech_use_num, String req_client_name, String cntr_account_num, String req_client_account_num){
        this.bank_tran_id = bank_tran_id;
        this.fintech_use_num = fintech_use_num;
        this.req_client_name = req_client_name;
        this.cntr_account_num = cntr_account_num;
        this.req_client_account_num = req_client_account_num;
    }
}
