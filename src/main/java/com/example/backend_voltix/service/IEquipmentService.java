package com.example.backend_voltix.service;

import com.example.backend_voltix.dto.Equipments.EquipmentRequestDto;
import com.example.backend_voltix.dto.Equipments.EquipmentResponseDto;
import com.example.backend_voltix.model.Equipments;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IEquipmentService {
    EquipmentResponseDto addEquipment(MultipartFile file, EquipmentRequestDto equipmentRequestDto) throws IOException;

    void deleteEquipment(Long id);
    EquipmentResponseDto updateEquipment(Long id, MultipartFile file, EquipmentRequestDto equipmentRequestDto) throws IOException;
    List<EquipmentResponseDto> getAllEquipmentsInArea(Long areaId);

    public Optional<Equipments> toggleEquipmentState(Long id);
}
