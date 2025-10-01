package com.msa.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {
    
    @NotBlank(message = "리프레시 토큰은 필수입니다")
    private String refreshToken;
}
