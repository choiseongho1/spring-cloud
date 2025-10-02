package com.msa.auth.controller;

import com.msa.auth.config.JwtTokenProvider;
import com.msa.auth.dto.TokenDto;
import com.msa.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * 토큰 테스트용 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/auth/test")
@RequiredArgsConstructor
public class TestTokenController {

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/token")
    public ResponseEntity<ResponseDto<TokenDto>> testToken() {
        // 테스트용 인증 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // TokenDto 생성
        TokenDto tokenDto = TokenDto.of(accessToken, refreshToken, 1800L);

        return ResponseEntity.ok(ResponseDto.success("테스트 토큰 생성 성공", tokenDto));
    }
}
