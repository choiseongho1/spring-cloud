package com.msa.member.dto;

import com.msa.member.domain.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class MemberPageDto {
    
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private Integer age;
    private Member.Role role;
    
    @QueryProjection
    public MemberPageDto(Long id, String username, String password, String name, String email, Integer age, Member.Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.age = age;
        this.role = role;
    }
}
