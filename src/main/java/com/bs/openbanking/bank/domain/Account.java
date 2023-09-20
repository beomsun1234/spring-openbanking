package com.bs.openbanking.bank.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;


import javax.persistence.*;

@Entity
@Getter
@Table(
        indexes = {
                @Index(name = "idx_member_id", columnList = "member_id", unique = false)
        }
)
@NoArgsConstructor
public class Account extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "member_id")
    private Long memberId;
    @Column(unique = true)
    private String fintechUseNum;
    private String bankName;
    private String accountNum;
    private String bankCode;
    private String accountSeq;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    private String holderName;

    @PrePersist
    public void prePersist() {
        this.accountType = this.accountType == null ? AccountType.SUB : this.accountType;
    }

    public boolean isMainAccount(){
        if (this.accountType == AccountType.MAIN) return true;
        return false;
    }

    public void updateAccountType(AccountType accountType){
        this.accountType = accountType;
    }

    @Builder
    public Account(Long id, Long memberId, String fintechUseNum, String bankName, String accountNum, String bankCode, String accountSeq, String holderName, AccountType accountType) {
        this.id = id;
        this.memberId = memberId;
        this.fintechUseNum = fintechUseNum;
        this.bankName = bankName;
        this.accountNum = accountNum;
        this.bankCode = bankCode;
        this.accountSeq = accountSeq;
        this.holderName = holderName;
        this.accountType = accountType;
    }
}
