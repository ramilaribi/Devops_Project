package com.example.backend_voltix.dto.Equipments;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EquipmentResponseDto {
    private Long id;
    private String name;
    private String designation;
    private int actAddress;
    private int actModule;
    private int actVal;
    private double power;
    private String brand;
    private int duration;
    private Long areaId;
    private String imageUrl;
    private Boolean state ;
}
