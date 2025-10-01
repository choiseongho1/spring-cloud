package com.msa.auth.service;

import com.msa.auth.client.MemberServiceClient;
import com.msa.auth.client.dto.MemberDto;
import com.msa.auth.config.JwtTokenProvider;
import com.msa.auth.domain.RefreshToken;
import com.msa.auth.dto.LoginRequest;
import com.msa.auth.dto.TokenDto;
import com.msa.auth.repository.RefreshTokenRepository;

import com.msa.common.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
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
        log.info("[로그인] 로그인 시도: {}", loginRequest.getUsername());
        
        Authentication authentication;
        try {
            // 인증 시도
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            log.info("[로그인] 인증 성공: {}", loginRequest.getUsername());

            // SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("[로그인] 인증 실패: {}, 오류: {}", loginRequest.getUsername(), e.getMessage());
            log.error("[로그인] 오류 상세", e); // 스택 트레이스 출력
            throw e; // 인증 실패 시 예외 재발생
        }
        
        try {
            // Member 서비스에서 추가 사용자 정보 조회
            log.debug("[로그인] Member 서비스에서 사용자 정보 조회 시도: {}", loginRequest.getUsername());
            MemberDto memberDto = memberServiceClient.getMemberByUsername(loginRequest.getUsername());
            log.info("[로그인] Member 서비스에서 사용자 정보 조회 성공: {}", memberDto.getUsername());
            
            // 토큰 생성 (Member 서비스에서 가져온 정보 포함)
            log.debug("[로그인] 토큰 생성 시도 (Member 정보 포함)");
            String accessToken = jwtTokenProvider.generateAccessToken(authentication, memberDto);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
            log.info("[로그인] 토큰 생성 성공: {}", loginRequest.getUsername());
            
            // 리프레시 토큰 저장
            saveRefreshToken(authentication.getName(), refreshToken);
            log.debug("[로그인] 리프레시 토큰 저장 완료");
            
            return TokenDto.of(accessToken, refreshToken, accessTokenValidity / 1000);
        } catch (Exception e) {
            log.error("[로그인] Member 서비스 호출 중 오류 발생: {}, 오류: {}", loginRequest.getUsername(), e.getMessage());
            log.error("[로그인] 오류 상세", e); // 스택 트레이스 출력
            
            // 기본 토큰 생성 (Member 서비스 정보 없이)
            log.debug("[로그인] 기본 토큰 생성 시도");
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
            log.info("[로그인] 기본 토큰 생성 성공");
            
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

        // DB에 저장된 리프레시 토큰 조회
        RefreshToken savedToken = refreshTokenRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("저장된 리프레시 토큰이 없습니다"));

        // 토큰 일치 확인
        if (!savedToken.getToken().equals(refreshToken)) {
            throw new RuntimeException("리프레시 토큰이 일치하지 않습니다");
        }

        // 토큰 만료 확인
        if (savedToken.isExpired()) {
            refreshTokenRepository.delete(savedToken);
            throw new RuntimeException("리프레시 토큰이 만료되었습니다");
        }

        // 새 토큰 생성을 위한 인증 객체 생성
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        
        try {
            // Member 서비스에서 최신 사용자 정보 조회
            MemberDto memberDto = memberServiceClient.getMemberByUsername(username);
            log.info("Member 서비스에서 사용자 정보 조회 성공 (refreshToken): {}", memberDto.getUsername());
            
            // 최신 정보로 토큰 생성
            String newAccessToken = jwtTokenProvider.generateAccessToken(authentication, memberDto);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);
            
            // 새 리프레시 토큰 저장
            saveRefreshToken(username, newRefreshToken);
            
            return TokenDto.of(newAccessToken, newRefreshToken, accessTokenValidity / 1000);
        } catch (Exception e) {
            log.error("Member 서비스 호출 중 오류 발생 (refreshToken): {}", e.getMessage());
            
            // 기본 토큰 생성 (Member 서비스 정보 없이)
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
            refreshTokenRepository.deleteByUsername(username);
        }
    }

    /**
     * 리프레시 토큰 저장
     */
    private void saveRefreshToken(String username, String token) {
        log.debug("[리프레시 토큰] 저장 시도: {}", username);
        
        try {
            Instant expiryDate = Instant.now().plusMillis(refreshTokenValidity);
            log.debug("[리프레시 토큰] 만료 시간 설정: {}", expiryDate);

            RefreshToken refreshToken = RefreshToken.builder()
                    .token(token)
                    .username(username)
                    .expiryDate(expiryDate)
                    .build();

            refreshTokenRepository.save(refreshToken);
            log.info("[리프레시 토큰] 저장 성공: {}", username);
        } catch (Exception e) {
            log.error("[리프레시 토큰] 저장 실패: {}, 오류: {}", username, e.getMessage());
            log.error("[리프레시 토큰] 오류 상세", e);
            throw e; // 예외 재발생
        }
    }
}
