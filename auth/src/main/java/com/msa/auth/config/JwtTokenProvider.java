package com.msa.auth.config;

import com.msa.auth.client.dto.MemberDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

//    @Value("${jwt.secret:defaultSecretKeyForDevelopmentEnvironmentOnly}")
    private String secret = "ce9a4a38b29c53ea18eac9c03d6e55eb246de75f9c584564d246b367f28490fe";

    @Value("${jwt.access-token-validity:1800000}") // 30분 (밀리초)
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity:604800000}") // 7일 (밀리초)
    private long refreshTokenValidity;

    // JWT 토큰에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // JWT 토큰에서 만료일 추출
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // JWT 토큰에서 사용자 ID 추출
    public Long getUserIdFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return Long.parseLong(claims.get("userId", String.class));
    }

    // JWT 토큰에서 권한 정보 추출
    public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        String authorities = claims.get("authorities", String.class);
        return Arrays.stream(authorities.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // JWT 토큰에서 특정 클레임 추출
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // JWT 토큰에서 모든 클레임 추출
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰 만료 여부 확인
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // 액세스 토큰 생성 (Member 정보 포함)
    public String generateAccessToken(Authentication authentication, MemberDto memberDto) {
        Map<String, Object> claims = new HashMap<>();
        
        // 사용자 권한 정보 추가
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("authorities", authorities);
        
        // Member 서비스에서 가져온 정보 추가
        claims.put("userId", memberDto.getId().toString());
        claims.put("name", memberDto.getName());
        claims.put("email", memberDto.getEmail());
        
        return doGenerateToken(claims, authentication.getName(), accessTokenValidity);
    }

    // 액세스 토큰 생성 (기본)
    public String generateAccessToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        
        // 사용자 권한 정보 추가
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("authorities", authorities);
        
        // 사용자 정보가 UserDetails 타입인 경우 userId 추출
        if (authentication.getPrincipal() instanceof UserDetails) {
            User user = (User) authentication.getPrincipal();
            // userId는 별도로 저장해야 함 (예: SecurityContext에서 가져오거나 다른 방법으로)
            // 여기서는 예시로 username을 userId로 사용
            claims.put("userId", user.getUsername());
        }
        
        return doGenerateToken(claims, authentication.getName(), accessTokenValidity);
    }

    // 리프레시 토큰 생성
    public String generateRefreshToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "refresh");
        return doGenerateToken(claims, authentication.getName(), refreshTokenValidity);
    }

    // 서비스 간 통신용 토큰 생성
    public String generateServiceToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "service");
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 60000)) // 1분
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 생성 (내부 메서드)
    private String doGenerateToken(Map<String, Object> claims, String subject, long validity) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰으로부터 인증 객체 생성
    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        Collection<? extends GrantedAuthority> authorities = getAuthoritiesFromToken(token);
        
        UserDetails principal = new User(username, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 토큰 검증
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // 서비스 토큰 검증
    public boolean isServiceToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return "service".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    // 서명 키 생성
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
