package com.bs.openbanking.bank.domain;


import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @Column(unique = true)
    private String openBankId;

    @Builder
    public Member(Long id, String email, String password, String openBankId) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.openBankId = openBankId;
    }

    public void updateOpenBankId(String openBankId){
        this.openBankId = openBankId;
    }

    public Boolean isVaildPassword(String password){
        if (!this.password.equals(password)){
            return false;
        }
        return true;
    }


}
