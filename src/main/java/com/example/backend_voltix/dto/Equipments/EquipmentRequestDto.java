package com.example.backend_voltix.dto.Equipments;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EquipmentRequestDto {
    private String name;
    private String designation;
    private double power;
    private String brand;
    private Long areaId;
}
