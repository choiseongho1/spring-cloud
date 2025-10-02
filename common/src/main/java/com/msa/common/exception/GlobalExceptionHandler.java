package com.msa.common.exception;

import com.msa.common.dto.ErrorDto;
import com.msa.common.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseDto<Object>> handleBaseException(BaseException e) {
        log.error("BaseException: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        ErrorDto errorDto = ErrorDto.builder()
                .code(errorCode.getCode())
                .message(e.getMessage())
                .build();
        
        ResponseDto<Object> response = ResponseDto.fail(errorCode.getMessage(), errorDto);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }
    
    /**
     * 유효성 검증 실패 시 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        log.error("유효성 검증 실패: {}", ex.getMessage());

        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();

        bindingResult.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ResponseDto<Map<String, String>> responseDto = ResponseDto.error(
                "입력값 유효성 검증 실패",
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }
    
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResponseDto<Object>> handleBindException(BindException e) {
        log.error("BindException: {}", e.getMessage());
        ErrorDto errorDto = createFieldErrorDto(e.getBindingResult());
        ResponseDto<Object> response = ResponseDto.fail(ErrorCode.INVALID_INPUT_VALUE.getMessage(), errorDto);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseDto<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException: {}", e.getMessage());
        ErrorDto errorDto = ErrorDto.builder()
                .code(ErrorCode.INVALID_TYPE_VALUE.getCode())
                .message(String.format("%s 값의 타입이 잘못되었습니다. 요청 값: '%s', 필요한 타입: %s", 
                        e.getName(), e.getValue(), e.getRequiredType().getSimpleName()))
                .build();
        
        ResponseDto<Object> response = ResponseDto.fail(ErrorCode.INVALID_TYPE_VALUE.getMessage(), errorDto);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Spring Security의 AuthorizationDeniedException 처리
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ResponseDto<Object>> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        log.warn("권한 없음: {}", e.getMessage());
        ErrorDto errorDto = ErrorDto.builder()
                .code(ErrorCode.PERMISSION_DENIED.getCode())
                .message("요청한 리소스에 접근할 권한이 없습니다.")
                .build();
        
        ResponseDto<Object> response = ResponseDto.fail(ErrorCode.PERMISSION_DENIED.getMessage(), errorDto);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Spring Security의 AccessDeniedException 처리
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseDto<Object>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("접근 거부: {}", e.getMessage());
        ErrorDto errorDto = ErrorDto.builder()
                .code(ErrorCode.ACCESS_DENIED.getCode())
                .message("접근이 거부되었습니다.")
                .build();
        
        ResponseDto<Object> response = ResponseDto.fail(ErrorCode.ACCESS_DENIED.getMessage(), errorDto);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Object>> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage(), e);
        ErrorDto errorDto = ErrorDto.builder()
                .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message(e.getMessage())
                .build();
        
        ResponseDto<Object> response = ResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), errorDto);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private ErrorDto createFieldErrorDto(BindingResult bindingResult) {
        ErrorDto errorDto = ErrorDto.builder()
                .code(ErrorCode.INVALID_INPUT_VALUE.getCode())
                .message(ErrorCode.INVALID_INPUT_VALUE.getMessage())
                .build();
        
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            errorDto.addFieldError(
                    fieldError.getField(),
                    String.valueOf(fieldError.getRejectedValue()),
                    fieldError.getDefaultMessage()
            );
        }
        
        return errorDto;
    }
}
