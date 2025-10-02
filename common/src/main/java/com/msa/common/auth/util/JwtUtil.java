package com.msa.common.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    // 기본값으로 사용할 시크릿 키 (실제 환경에서는 설정 파일에서 로드)
    @Value("${jwt.secret:your-secret-key-should-be-very-long-and-secure-for-production}")
    private String secretKey;
    
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * JWT 토큰에서 클레임을 추출합니다.
     * 서명 검증 없이 페이로드만 추출합니다.
     * 
     * @param token JWT 토큰
     * @return 클레임 맵
     */
    public Map<String, Object> parseClaims(String token) {
        try {
            // 토큰을 점(".")으로 분리
            String[] chunks = token.split("\\.");
            
            // 헤더와 페이로드만 필요하고 서명은 무시
            if (chunks.length < 2) {
                return java.util.Collections.emptyMap(); // 유효하지 않은 형식이면 빈 맵 반환
            }
            
            try {
                // Base64 디코딩을 통해 페이로드 추출
                String payload = new String(java.util.Base64.getUrlDecoder().decode(chunks[1]));
                
                // JSON 문자열을 Map으로 변환
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                return mapper.readValue(payload, Map.class);
            } catch (Exception e) {
                // 디코딩 오류는 조용히 무시하고 빈 맵 반환
                return java.util.Collections.emptyMap();
            }
        } catch (Exception e) {
            // 예외가 발생해도 시스템이 중단되지 않도록 빈 맵 반환
            return java.util.Collections.emptyMap();
        }
    }
    
    /**
     * 토큰이 만료되었는지 확인합니다.
     * 서명 검증 없이 페이로드만 추출하여 확인합니다.
     * 
     * @param token JWT 토큰
     * @return 만료 여부
     */
    public boolean isTokenExpired(String token) {
        try {
            Map<String, Object> claims = parseClaims(token);
            
            // 클레임이 비어있거나 exp 필드가 없으면 만료되지 않은 것으로 간주
            if (claims.isEmpty() || !claims.containsKey("exp")) {
                return false;
            }
            
            // exp는 초 단위의 타임스탬프
            Object expValue = claims.get("exp");
            if (expValue instanceof Number) {
                long expTime = ((Number) expValue).longValue() * 1000; // 밀리초로 변환
                return new Date(expTime).before(new Date());
            }
            return false;
        } catch (Exception e) {
            // 예외 발생 시 만료되지 않은 것으로 처리
            return false;
        }
    }
    
    /**
     * 토큰의 유효성을 검증합니다.
     * 서명 검증 없이 형식적으로만 검증합니다.
     * 
     * @param token JWT 토큰
     * @return 유효성 여부
     */
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        try {
            // JWT 형식이 맞는지 확인 (헤더.페이로드.서명 형식)
            String[] chunks = token.split("\\.");
            if (chunks.length != 3) {
                return false;
            }
            
            // 페이로드 추출
            Map<String, Object> claims = parseClaims(token);
            if (claims.isEmpty()) {
                return false;
            }
            
            // 만료 시간 확인은 선택적으로 수행
            // 만료 시간이 없는 토큰도 유효하게 처리
            if (claims.containsKey("exp")) {
                return !isTokenExpired(token);
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
