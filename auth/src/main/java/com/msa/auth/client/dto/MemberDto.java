package com.msa.auth.client.dto;

import lombok.*;

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
