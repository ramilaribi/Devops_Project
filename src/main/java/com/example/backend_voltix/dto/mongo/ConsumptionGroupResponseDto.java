package com.example.backend_voltix.dto.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
public class ConsumptionGroupResponseDto {
    @Field("_id")
    private Integer channelId;
    private Double totalConsumption;
    private String areaName;
}
