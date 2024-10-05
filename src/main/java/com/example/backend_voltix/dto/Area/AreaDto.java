package com.example.backend_voltix.dto.Area;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AreaDto {
    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    private int nbrFenetres;
    private int nbrPortes;
    private int numOfEquipments;
    private int numOfPeople;
    private double surface;
    private Long deviceId;
}
