package com.example.backend_voltix.dto.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConsumptionDataResponseDto {
    private double consumption;
    private String timestamp;
}
