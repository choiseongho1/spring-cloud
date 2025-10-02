package com.msa.common.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.common.dto.ErrorDto;
import com.msa.common.dto.ResponseDto;
import com.msa.common.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 인증되지 않은 사용자가 보호된 리소스에 접근할 때 처리하는 진입점
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 응답 상태 코드 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 에러 응답 생성
        ErrorDto errorDto = ErrorDto.builder()
                .code(ErrorCode.UNAUTHORIZED.getCode())
                .message("인증이 필요합니다.")
                .build();

        ResponseDto<Object> responseDto = ResponseDto.builder()
                .success(false)
                .message(ErrorCode.UNAUTHORIZED.getMessage())
                .error(errorDto)
                .timestamp(LocalDateTime.now())
                .build();

        // JSON 응답 작성
        objectMapper.findAndRegisterModules(); // LocalDateTime 직렬화를 위해 필요
        objectMapper.writeValue(response.getWriter(), responseDto);
    }
}
