package com.msa.auth.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    
    public static TokenDto of(String accessToken, String refreshToken, Long expiresIn) {
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .build();
    }
}
