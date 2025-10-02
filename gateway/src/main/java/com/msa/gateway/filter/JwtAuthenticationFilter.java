package com.msa.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    
    @Value("${jwt.secret}")
    private String secret;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();
            
            // 인증이 필요 없는 공개 경로 확인
            if (isPublicPath(path)) {
                return chain.filter(exchange);
            }
            
            // 헤더에서 토큰 추출
            List<String> authHeaders = request.getHeaders().get(AUTH_HEADER);
            if (authHeaders == null || authHeaders.isEmpty()) {
                log.warn("[게이트웨이 필터] 인증 토큰 없음: {}", path);
                return onError(exchange, "인증 토큰이 없습니다", HttpStatus.UNAUTHORIZED);
            }
            
            String token = authHeaders.get(0);
            if (!token.startsWith(TOKEN_PREFIX)) {
                log.warn("[게이트웨이 필터] 유효하지 않은 토큰 형식: {}", path);
                return onError(exchange, "유효하지 않은 토큰 형식입니다", HttpStatus.UNAUTHORIZED);
            }
            
            token = token.substring(TOKEN_PREFIX.length());
            
            // 토큰이 중복되어 있는지 확인
            if (token.contains(".") && token.split("\\.").length > 3) {
                // JWT는 헤더.페이로드.서명 형태로 구성되어 있으므로 첫 번째 토큰만 추출
                String[] parts = token.split("\\.");
                if (parts.length >= 3) {
                    token = parts[0] + "." + parts[1] + "." + parts[2];
                }
            }
            
            // 토큰 검증
            try {
                if (!isValidToken(token)) {
                    log.warn("[게이트웨이 필터] 유효하지 않은 토큰: {}", path);
                    return onError(exchange, "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED);
                }
                log.info("[게이트웨이 필터] 토큰 검증 성공: {}", path);
            } catch (Exception e) {
                log.error("[게이트웨이 필터] 토큰 검증 오류: {} - {}", path, e.getMessage());
                return onError(exchange, "토큰 검증 오류: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
            
            // 토큰에서 사용자 정보 추출
            try {
                Claims claims = extractClaims(token);
                
                // 요청에 사용자 정보 추가
                String userId = claims.get("userId", String.class);
                String username = claims.getSubject();
                
                if (userId == null) {
                    log.warn("[게이트웨이 필터] userId 클레임이 없습니다: {}", path);
                    return onError(exchange, "userId 클레임이 없습니다", HttpStatus.FORBIDDEN);
                }
                
                ServerHttpRequest enrichedRequest = request.mutate()
                    .header("X-Auth-UserId", userId)
                    .header("X-Auth-Username", username)
                    .build();
                
                // 검증 성공 시 요청 전달
                return chain.filter(exchange.mutate().request(enrichedRequest).build());
            } catch (Exception e) {
                log.error("[게이트웨이 필터] 사용자 정보 추출 오류: {}", e.getMessage());
                return onError(exchange, "사용자 정보 추출 오류", HttpStatus.FORBIDDEN);
            }
        };
    }

    private boolean isPublicPath(String path) {
        return path.contains("/api/auth/") || 
               path.contains("/api/public/") ||
               path.contains("/actuator/");
    }

    private boolean isValidToken(String token) {
        try {
            Claims claims = extractClaims(token);
            
            // 토큰 만료 확인
            Date expiration = claims.getExpiration();
            Date now = new Date();
            boolean isValid = !expiration.before(now);
            
            if (!isValid) {
                log.warn("[게이트웨이 필터] 토큰 만료");
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("[게이트웨이 필터] 토큰 검증 오류: {}", e.getMessage());
            return false;
        }
    }
    
    private Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("[게이트웨이 필터] 토큰 클레임 추출 오류: {}", e.getMessage());
            throw e;
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        
        log.error("[게이트웨이 필터] 접근 거부: {} - 경로: {}", message, path);
        
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        String errorJson = String.format("{\"error\":\"%s\", \"path\":\"%s\", \"status\":%d}", 
                message, path, status.value());
        byte[] bytes = errorJson.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {
        // 필요한 설정이 있다면 여기에 추가
    }
}
