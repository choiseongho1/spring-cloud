package com.msa.common.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDto {
    
    private String code;
    private String message;
    
    @Builder.Default
    private List<FieldError> fieldErrors = new ArrayList<>();
    
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String value;
        private String reason;
    }
    
    public void addFieldError(String field, String value, String reason) {
        fieldErrors.add(new FieldError(field, value, reason));
    }
}
