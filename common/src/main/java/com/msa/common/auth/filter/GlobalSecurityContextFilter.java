package com.msa.common.auth.filter;

import com.msa.common.auth.model.GlobalCustomUserDetails;
import com.msa.common.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class GlobalSecurityContextFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        // 1. Authorization 헤더에서 JWT 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                // 2. "Bearer " 접두사 제거하여 토큰 추출
                String token = authorizationHeader.substring(7);
                
                // 3. 토큰 유효성 검증 및 클레임 추출
                Map<String, Object> claims = jwtUtil.parseClaims(token);
                if (claims.isEmpty()) {
                    // 클레임이 비어있으면 처리하지 않고 다음 필터로 진행
                    return;
                }
                
                // 4. 클레임에서 필요한 정보 추출
                // 클레임에서 userId 추출 (여러 필드 시도)
                String userId = null;
                if (claims.containsKey("userId")) {
                    userId = claims.get("userId").toString();
                } else if (claims.containsKey("user_id")) {
                    userId = claims.get("user_id").toString();
                } else if (claims.containsKey("sub")) {
                    userId = claims.get("sub").toString();
                }
                
                // 클레임에서 role 추출 (여러 필드 시도)
                String role = null;
                // 클레임에서 role 추출
                if (claims.containsKey("authorities")) {
                    role = claims.get("authorities").toString();
                } else if (claims.containsKey("roles")) {
                    role = claims.get("roles").toString();
                } else if (claims.containsKey("role")) {
                    role = claims.get("role").toString();
                } else {
                    // 기본 권한 설정
                    role = "USER";
                    log.debug("[필터] 클레임에 권한 정보 없음, 기본값 USER 사용");
                }
                
                log.debug("[필터] 추출된 역할: {}", role);
                
                // ROLE_ 접두사 제거
                if (role != null && role.startsWith("ROLE_")) {
                    String originalRole = role;
                    role = role.substring(5); // "ROLE_" 접두사 제거
                    log.debug("[필터] ROLE_ 접두사 제거: {} -> {}", originalRole, role);
                }
                
                // userId가 유효한지 확인
                if (userId == null || userId.isEmpty()) {
                    return;
                }
                
                // 5. SecurityContext에 인증 정보 설정
                GlobalCustomUserDetails customUserDetails = new GlobalCustomUserDetails(role, userId);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        customUserDetails, null, customUserDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // 예외 발생 시 조용히 무시하고 다음 필터로 진행
            }
        } else {
            // 기존 헤더 기반 인증도 유지 (게이트웨이에서 헤더 전달 방식 사용 시)
            String role = request.getHeader("X-USER-ROLE");
            String userId = request.getHeader("X-USER-ID");
            
            if (role != null && userId != null) {
                // 문자열 형태의 userId를 직접 사용
                GlobalCustomUserDetails customUserDetails = new GlobalCustomUserDetails(role, userId);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        customUserDetails, null, customUserDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
