package com.msa.common.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.common.auth.filter.GlobalSecurityContextFilter;
import com.msa.common.auth.handler.CustomAccessDeniedHandler;
import com.msa.common.auth.handler.CustomAuthenticationEntryPoint;
import com.msa.common.dto.ErrorDto;
import com.msa.common.dto.ResponseDto;
import com.msa.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final GlobalSecurityContextFilter globalSecurityContextFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable()) // 로그인 페이지 비활성화
                .httpBasic(httpBasic -> httpBasic.disable()) // HTTP 기본 인증 비활성화
                .logout(logout -> logout.disable()) // 로그아웃 기능 비활성화
                .exceptionHandling(exception -> exception
                        // 인증되지 않은 사용자가 보호된 리소스에 접근할 때
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        // 인증된 사용자가 권한이 없는 리소스에 접근할 때
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .authorizeHttpRequests((auth) -> auth
                        .anyRequest().permitAll())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(globalSecurityContextFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
