package com.msa.member.dto;

import com.msa.member.domain.Member;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSaveDto {

    @NotNull(message = "username은 필수 입력 사항입니다.")
    private String username;

    @NotNull(message = "password는 필수 입력 사항입니다.")
    private String password;

    @NotNull(message = "name은 필수 입력 사항입니다.")
    private String name;

    @NotNull(message = "email은 필수 입력 사항입니다.")
    private String email;

    @NotNull(message = "age는 필수 입력 사항입니다.")
    private Integer age;

    private String role;


    public Member toEntity() {
        return Member.builder()
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .age(age)
                .role(convertRole(role))
                .build();
    }
    
    private Member.Role convertRole(String role) {
        if (StringUtils.isEmpty(role)) {
            return Member.Role.ROLE_USER;
        }
        
        try {
            return Member.Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            // 잘못된 role 값이 들어온 경우 기본값 반환
            return Member.Role.ROLE_USER;
        }
    }


}
