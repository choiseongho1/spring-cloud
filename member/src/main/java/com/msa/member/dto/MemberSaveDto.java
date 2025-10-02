package com.msa.member.dto;

import com.msa.member.domain.Member;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
                .role(Member.Role.valueOf(role))
                .build();
    }


}
