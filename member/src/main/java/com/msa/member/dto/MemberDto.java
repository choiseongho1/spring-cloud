package com.msa.member.dto;

import com.msa.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private Integer age;
    private String role;
    
    public static MemberDto from(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .password(member.getPassword())
                .name(member.getName())
                .email(member.getEmail())
                .age(member.getAge())
                .role(member.getRole().name())
                .build();
    }
}
