package com.msa.auth.client.dto;

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
}
