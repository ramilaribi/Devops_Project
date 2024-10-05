package com.example.backend_voltix.dto.Equipments;

import com.example.backend_voltix.dto.Equipments.EquipmentRequestDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class UpdateEquipmentRequestDto {
    private Long id;
    private EquipmentRequestDto equipmentRequestDto;
}
