package com.msa.common.dto;

import jakarta.validation.constraints.Min;

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
public class PageRequestDto {
    
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
    @Builder.Default
    private Integer page = 0;
    
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Builder.Default
    private Integer size = 10;
    
    private String sortBy;
    private String sortDirection;
    
    public String getSort() {
        return sortBy != null && sortDirection != null ? sortBy + "," + sortDirection : null;
    }
}
