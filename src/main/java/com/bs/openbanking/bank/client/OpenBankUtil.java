package com.bs.openbanking.bank.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class OpenBankUtil {


    /**
     * 은행 거래 고유번호 랜덤 생성
     */

    public static String getRandomNumber(String bank_tran_id){

        Random rand = new Random();
        String rst = Integer.toString(rand.nextInt(8) + 1);
        for(int i=0; i < 8; i++){
            rst += Integer.toString(rand.nextInt(9));
        }
        return bank_tran_id+rst;
    }
    /**
     * 거래시간 
     */
    public static String getTransTime(){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
        String now = localDateTime.format(dateTimeFormatter);
        return now;
    }

    /**
     * 
     * 마스킹된 계좌 자르기
     */
    public static String trimAccountNum(String accountNum, int length){
        String account = accountNum.substring(0, length - 3);
        return account;
    }

    public static ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

}
