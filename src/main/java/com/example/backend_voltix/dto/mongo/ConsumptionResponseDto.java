package com.example.backend_voltix.dto.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConsumptionResponseDto {
    private String dailyConsumption;
    private String monthlyConsumption;
}
