package com.bs.openbanking.bank.domain;


import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Member extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String email;
    @Column(unique = true)
    private String openBankCi;
    @NotNull
    private String password;
    @Column(unique = true)
    private String openBankId;

    @Builder
    public Member(Long id, String email, String password, String openBankId, String openBankCi) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.openBankId = openBankId;
        this.openBankCi = openBankCi;
    }

    public void updateOpenBankId(String openBankId){
        this.openBankId = openBankId;
    }
    public void updateOpenBankCi(String openBankCi){
        this.openBankCi = openBankCi;
    }

    public boolean isVaildPassword(String password){
        if (!this.password.equals(password)){
            return false;
        }
        return true;
    }

    public boolean hasOpenBankCi(){
        if (this.openBankCi == null || this.openBankCi.isBlank()){
            return false;
        }
        return true;
    }
}
