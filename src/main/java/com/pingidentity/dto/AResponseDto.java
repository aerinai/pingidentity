package com.pingidentity.dto;

import lombok.Data;

@Data
public class AResponseDto {
    private Integer result;
    private String errorType;
    private String errorDescription;

}
