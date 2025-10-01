package com.msa.common.exception;

public class InvalidValueException extends BusinessException {
    
    public InvalidValueException(String message) {
        super(ErrorCode.INVALID_INPUT_VALUE, message);
    }
    
    public InvalidValueException(String fieldName, String value) {
        super(ErrorCode.INVALID_INPUT_VALUE, String.format("%s: '%s' 값이 유효하지 않습니다.", fieldName, value));
    }
}
