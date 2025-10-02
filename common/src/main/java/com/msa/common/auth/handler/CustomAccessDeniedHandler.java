package com.msa.common.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.common.dto.ErrorDto;
import com.msa.common.dto.ResponseDto;
import com.msa.common.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 인증된 사용자가 권한이 없는 리소스에 접근할 때 처리하는 핸들러
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 응답 상태 코드 설정
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 에러 응답 생성
        ErrorDto errorDto = ErrorDto.builder()
                .code(ErrorCode.ACCESS_DENIED.getCode())
                .message("접근이 거부되었습니다.")
                .build();

        ResponseDto<Object> responseDto = ResponseDto.builder()
                .success(false)
                .message(ErrorCode.ACCESS_DENIED.getMessage())
                .error(errorDto)
                .timestamp(LocalDateTime.now())
                .build();

        // JSON 응답 작성
        objectMapper.findAndRegisterModules(); // LocalDateTime 직렬화를 위해 필요
        objectMapper.writeValue(response.getWriter(), responseDto);
    }
}
