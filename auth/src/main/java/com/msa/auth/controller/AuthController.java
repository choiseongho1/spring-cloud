package com.msa.auth.controller;

import com.msa.auth.dto.LoginRequest;
import com.msa.auth.dto.LogoutRequest;
import com.msa.auth.dto.RefreshTokenRequest;
import com.msa.auth.dto.TokenDto;
import com.msa.auth.service.AuthService;
import com.msa.common.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/ping")
    public ResponseDto<?> ping() {
        return ResponseDto.success("Auth Service Ping Success");
    }


    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<TokenDto>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("[컨트롤러] 로그인 요청: {}", loginRequest.getUsername());
        
        try {
            TokenDto tokenDto = authService.login(loginRequest);
            log.info("[컨트롤러] 로그인 성공: {}", loginRequest.getUsername());
            
            return ResponseEntity.ok(ResponseDto.success("로그인 성공", tokenDto));
        } catch (Exception e) {
            log.error("[컨트롤러] 로그인 실패: {}, 오류: {}", loginRequest.getUsername(), e.getMessage());
            throw e;
        }
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<ResponseDto<TokenDto>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenDto tokenDto = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ResponseDto.success("토큰 갱신 성공", tokenDto));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<?>> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ResponseDto.success("로그아웃 성공"));
    }
}
