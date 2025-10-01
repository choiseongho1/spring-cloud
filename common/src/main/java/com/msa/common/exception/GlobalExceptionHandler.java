package com.msa.common.exception;

import com.msa.common.dto.ErrorDto;
import com.msa.common.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

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
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage());
        ErrorDto errorDto = createFieldErrorDto(e.getBindingResult());
        ResponseDto<Object> response = ResponseDto.fail(ErrorCode.INVALID_INPUT_VALUE.getMessage(), errorDto);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
