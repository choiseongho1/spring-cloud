package com.msa.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    // 공통 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "지원하지 않는 HTTP 메서드입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "요청한 엔티티를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "서버 내부 오류가 발생했습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", "잘못된 타입의 값입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "접근이 거부되었습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C007", "요청한 리소스를 찾을 수 없습니다."),
    
    // 인증 및 인가 관련 에러
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A003", "만료된 토큰입니다."),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "A004", "권한이 없습니다."),
    
    // 회원 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "M002", "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "M003", "잘못된 비밀번호입니다."),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "M004", "계정이 잠겼습니다."),
    
    // 외부 서비스 통신 관련 에러
    API_CALL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "외부 API 호출 중 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "E002", "서비스를 일시적으로 사용할 수 없습니다."),
    TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "E003", "요청 시간이 초과되었습니다.");
    
    private final HttpStatus status;
    private final String code;
    private final String message;
}
