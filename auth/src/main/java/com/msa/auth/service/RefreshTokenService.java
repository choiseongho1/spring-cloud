package com.msa.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    @Value("${jwt.refresh-token-validity}") // 7일 (밀리초)
    private long refreshTokenValidity;
    
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    
    /**
     * 리프레시 토큰 저장
     */
    public void saveRefreshToken(String username, String token) {
        try {
            // Redis에 저장 (키: "refresh_token:username", 값: 토큰)
            String key = REFRESH_TOKEN_PREFIX + username;
            redisTemplate.opsForValue().set(key, token);
            redisTemplate.expire(key, refreshTokenValidity, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("[리프레시 토큰] 저장 실패: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 사용자명으로 리프레시 토큰 조회
     */
    public Optional<String> findByUsername(String username) {
        String key = REFRESH_TOKEN_PREFIX + username;
        String token = (String) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(token);
    }
    
    /**
     * 사용자명으로 리프레시 토큰 삭제
     */
    public void deleteByUsername(String username) {
        String key = REFRESH_TOKEN_PREFIX + username;
        redisTemplate.delete(key);
    }
    
    /**
     * 토큰 검증
     */
    public boolean validateToken(String token, String username) {
        String savedToken = (String) redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + username);
        return token.equals(savedToken);
    }
}
