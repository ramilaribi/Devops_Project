package com.example.backend_voltix.dto.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ConsumptionPeriodResponseDto {
    private List<Double> weeklyConsumption;
}
