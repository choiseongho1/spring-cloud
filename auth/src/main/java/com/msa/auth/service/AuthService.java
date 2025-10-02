package com.msa.auth.service;

import com.msa.auth.client.MemberServiceClient;
import com.msa.auth.client.dto.MemberDto;
import com.msa.auth.config.JwtTokenProvider;
import com.msa.auth.dto.LoginRequest;
import com.msa.auth.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberServiceClient memberServiceClient;
    
    @Value("${jwt.refresh-token-validity:604800000}") // 7일 (밀리초)
    private long refreshTokenValidity;
    
    @Value("${jwt.access-token-validity:1800000}") // 30분 (밀리초)
    private long accessTokenValidity;

    /**
     * 로그인
     */
    @Transactional
    public TokenDto login(LoginRequest loginRequest) {
        log.info("[로그인] 시도: {}", loginRequest.getUsername());
        
        Authentication authentication;
        try {
            // 인증 시도
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            
            // SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("[로그인] 인증 실패: {}, 오류: {}", loginRequest.getUsername(), e.getMessage());
            throw e;
        }
        
        try {
            // Member 서비스에서 사용자 정보 조회
            MemberDto memberDto = memberServiceClient.getMemberByUsername(loginRequest.getUsername());
            
            // 토큰 생성 (Member 서비스에서 가져온 정보 포함)
            String accessToken = jwtTokenProvider.generateAccessToken(authentication, memberDto);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
            
            // 리프레시 토큰 저장
            saveRefreshToken(authentication.getName(), refreshToken);
            
            return TokenDto.of(accessToken, refreshToken, accessTokenValidity / 1000);
        } catch (Exception e) {
            log.error("[로그인] Member 서비스 호출 오류: {}", e.getMessage());
            
            // 기본 토큰 생성 (Member 서비스 정보 없이)
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
            
            // 리프레시 토큰 저장
            saveRefreshToken(authentication.getName(), refreshToken);
            
            return TokenDto.of(accessToken, refreshToken, accessTokenValidity / 1000);
        }
    }

    /**
     * 리프레시 토큰으로 새 액세스 토큰 발급
     */
    @Transactional
    public TokenDto refreshToken(String refreshToken) {
        // 리프레시 토큰 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다");
        }

        // 토큰에서 사용자 이름 추출
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        // Redis에 저장된 리프레시 토큰 조회
        String savedToken = refreshTokenService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("저장된 리프레시 토큰이 없습니다"));

        // 토큰 일치 확인
        if (!savedToken.equals(refreshToken)) {
            throw new RuntimeException("리프레시 토큰이 일치하지 않습니다");
        }

        // 새 토큰 생성을 위한 인증 객체 생성
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        
        try {
            // Member 서비스에서 최신 사용자 정보 조회
            MemberDto memberDto = memberServiceClient.getMemberByUsername(username);
            
            // 최신 정보로 토큰 생성
            String newAccessToken = jwtTokenProvider.generateAccessToken(authentication, memberDto);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);
            
            // 새 리프레시 토큰 저장
            saveRefreshToken(username, newRefreshToken);
            
            return TokenDto.of(newAccessToken, newRefreshToken, accessTokenValidity / 1000);
        } catch (Exception e) {
            log.error("[토큰 갱신] Member 서비스 호출 오류: {}", e.getMessage());
            
            // 기본 토큰 생성
            String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);
            
            // 새 리프레시 토큰 저장
            saveRefreshToken(username, newRefreshToken);
            
            return TokenDto.of(newAccessToken, newRefreshToken, accessTokenValidity / 1000);
        }
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(String refreshToken) {
        if (jwtTokenProvider.validateToken(refreshToken)) {
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            refreshTokenService.deleteByUsername(username);
        }
    }

    /**
     * 리프레시 토큰 저장
     */
    private void saveRefreshToken(String username, String token) {
        refreshTokenService.saveRefreshToken(username, token);
    }
}
