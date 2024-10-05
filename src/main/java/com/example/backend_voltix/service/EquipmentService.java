package com.example.backend_voltix.service;

import com.example.backend_voltix.dto.Equipments.EquipmentRequestDto;
import com.example.backend_voltix.dto.Equipments.EquipmentResponseDto;
import com.example.backend_voltix.model.Area;
import com.example.backend_voltix.model.Equipments;
import com.example.backend_voltix.repository.AreaRepository;
import com.example.backend_voltix.repository.EquipmentsRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Service
public class EquipmentService implements IEquipmentService {

    private static final Logger logger = LoggerFactory.getLogger(EquipmentService.class);

    @Value("${upload.path}"+"equipments")
    private String uploadPath;

    @Autowired
    private EquipmentsRepository equipmentsRepository;

    @Autowired
    private AreaRepository areaRepository;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.info("Created directory for storing files: " + uploadPath);
            }
        } catch (IOException e) {
            logger.error("Could not create directory for storing files: " + uploadPath, e);
        }
    }

    @Override
    public EquipmentResponseDto addEquipment(MultipartFile file, EquipmentRequestDto equipmentRequestDto) throws IOException {
        logger.info("Starting to add equipment with request: {}", equipmentRequestDto);

        Area area = areaRepository.findById(equipmentRequestDto.getAreaId())
                .orElseThrow(() -> new RuntimeException("Area not found"));

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path targetLocation = Paths.get(uploadPath).resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);
            imageUrl = "/" + fileName;
        }

        Equipments equipments = new Equipments();
        equipments.setName(equipmentRequestDto.getName());
        equipments.setDesignation(equipmentRequestDto.getDesignation());
        equipments.setPower(equipmentRequestDto.getPower());
        equipments.setBrand(equipmentRequestDto.getBrand());
        equipments.setActAddress(0); // Default value
        equipments.setActModule(0);  // Default value
        equipments.setActVal(0);     // Default value
        equipments.setDuration(0);   // Default value
        equipments.setArea(area);
        equipments.setImageUrl(imageUrl);

        Equipments savedEquipment = equipmentsRepository.save(equipments);

        // Increment the number of equipments in the area
        area.setNumOfEquipments(area.getNumOfEquipments() + 1);
        areaRepository.save(area);

        EquipmentResponseDto responseDto = mapToResponseDto(savedEquipment);

        logger.info("Successfully added equipment: {}", responseDto);

        return responseDto;
    }

    @Override
    public void deleteEquipment(Long id) {
        logger.info("Starting to delete equipment with ID: {}", id);
        Equipments equipments = equipmentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        Area area = equipments.getArea();
        equipmentsRepository.delete(equipments);

        // Decrement the number of equipments in the area
        if (area != null) {
            area.setNumOfEquipments(area.getNumOfEquipments() - 1);
            areaRepository.save(area);
        }

        logger.info("Successfully deleted equipment with ID: {}", id);
    }

    @Override
    public EquipmentResponseDto updateEquipment(Long id, MultipartFile file, EquipmentRequestDto equipmentRequestDto) throws IOException {
        logger.info("Starting to update equipment with ID: {}", id);
        Equipments existingEquipment = equipmentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        Area oldArea = existingEquipment.getArea();
        Area newArea = areaRepository.findById(equipmentRequestDto.getAreaId())
                .orElseThrow(() -> new RuntimeException("Area not found"));

        String imageUrl = existingEquipment.getImageUrl();
        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path targetLocation = Paths.get(uploadPath).resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);
            imageUrl = "/" + fileName;
            logger.info("hedhaaaa l path", imageUrl);

        }

        existingEquipment.setName(equipmentRequestDto.getName());
        existingEquipment.setDesignation(equipmentRequestDto.getDesignation());
        existingEquipment.setPower(equipmentRequestDto.getPower());
        existingEquipment.setBrand(equipmentRequestDto.getBrand());
        existingEquipment.setActAddress(0); // Default value
        existingEquipment.setActModule(0);  // Default value
        existingEquipment.setActVal(0);     // Default value
        existingEquipment.setDuration(0);   // Default value
        existingEquipment.setArea(newArea);
        existingEquipment.setImageUrl(imageUrl);

        Equipments updatedEquipment = equipmentsRepository.save(existingEquipment);

        // Update numOfEquipments in areas if they are different
        if (!oldArea.equals(newArea)) {
            oldArea.setNumOfEquipments(oldArea.getNumOfEquipments() - 1);
            newArea.setNumOfEquipments(newArea.getNumOfEquipments() + 1);
            areaRepository.save(oldArea);
            areaRepository.save(newArea);
        }

        EquipmentResponseDto responseDto = mapToResponseDto(updatedEquipment);

        logger.info("Successfully updated equipment: {}", responseDto);

        return responseDto;
    }

    @Override
    public List<EquipmentResponseDto> getAllEquipmentsInArea(Long areaId) {
        logger.info("Starting to get all equipments in area with ID: {}", areaId);
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Area not found"));
        List<Equipments> equipmentsList = equipmentsRepository.findByArea(area);
        return equipmentsList.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Equipments> toggleEquipmentState(Long id) {
        Optional<Equipments> equipmentOptional = equipmentsRepository.findById(id);

        if (equipmentOptional.isPresent()) {
            Equipments equipment = equipmentOptional.get();
            // Toggle the state based on its current value
            if (equipment.getState() != null && equipment.getState()) {
                equipment.setState(false);  // Turn off if it's currently on
                log.info("Equipment with id {} was ON, turning it OFF", id);
            } else {
                equipment.setState(true);  // Turn on if it's currently off or null
                log.info("Equipment with id {} was OFF, turning it ON", id);
            }
            equipmentsRepository.save(equipment);
            return Optional.of(equipment);
        } else {
            log.warn("Equipment with id {} not found", id);
            return Optional.empty();
        }
    }

    private EquipmentResponseDto mapToResponseDto(Equipments equipments) {
        EquipmentResponseDto responseDto = new EquipmentResponseDto();
        responseDto.setId(equipments.getId());
        responseDto.setName(equipments.getName());
        responseDto.setDesignation(equipments.getDesignation());
        responseDto.setPower(equipments.getPower());
        responseDto.setBrand(equipments.getBrand());
        responseDto.setActAddress(equipments.getActAddress());
        responseDto.setActModule(equipments.getActModule());
        responseDto.setActVal(equipments.getActVal());
        responseDto.setDuration(equipments.getDuration());
        responseDto.setAreaId(equipments.getArea().getId());
        responseDto.setImageUrl(equipments.getImageUrl());
        responseDto.setState(equipments.getState());
        return responseDto;
    }
}
