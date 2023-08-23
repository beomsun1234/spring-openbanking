package com.bs.openbanking.bank.dto;


import com.bs.openbanking.bank.domain.Member;
import lombok.Builder;
import lombok.Data;

@Data
public class MemberDto {
    private Long id;
    private String email;
    private String openBankId;

    @Builder
    public MemberDto(Long id, String email, String openBankId) {
        this.id = id;
        this.email = email;
        this.openBankId = openBankId;
    }

    public static MemberDto of(Member member){
        return MemberDto
                .builder()
                .email(member.getEmail())
                .id(member.getId())
                .openBankId(member.getOpenBankId())
                .build();
    }
}
