package com.example.backend_voltix.controller;

import com.example.backend_voltix.dto.Equipments.*;
import com.example.backend_voltix.model.Equipments;
import com.example.backend_voltix.service.IEquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/equipments")
public class EquipmentController {

    private static final Logger logger = LoggerFactory.getLogger(EquipmentController.class);

    @Autowired
    private IEquipmentService equipmentService;

    @Operation(summary = "Add Equipment", description = "Adds a new equipment to the system, including an image file upload.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment added successfully"),
            @ApiResponse(responseCode = "404", description = "Area not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error or file upload error")
    })
    @PostMapping("/add")
    public ResponseEntity<Object> addEquipment(
            @Parameter(description = "Image file of the equipment", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "Name of the equipment", required = true) @RequestParam("name") String name,
            @Parameter(description = "Designation of the equipment", required = true) @RequestParam("designation") String designation,
            @Parameter(description = "Power consumption of the equipment", required = true) @RequestParam("power") double power,
            @Parameter(description = "Brand of the equipment", required = true) @RequestParam("brand") String brand,
            @Parameter(description = "ID of the area where the equipment will be located", required = true) @RequestParam("areaId") Long areaId) {

        EquipmentRequestDto equipmentRequestDto = new EquipmentRequestDto();
        equipmentRequestDto.setName(name);
        equipmentRequestDto.setDesignation(designation);
        equipmentRequestDto.setPower(power);
        equipmentRequestDto.setBrand(brand);
        equipmentRequestDto.setAreaId(areaId);

        logger.info("Received request to add equipment: {}", equipmentRequestDto);
        try {
            EquipmentResponseDto savedEquipments = equipmentService.addEquipment(file, equipmentRequestDto);
            logger.info("Successfully added equipment: {}", savedEquipments);
            return ResponseEntity.ok(savedEquipments);
        } catch (RuntimeException e) {
            logger.error("Error adding equipment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Area not found");
        } catch (IOException e) {
            logger.error("Error saving file: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload error");
        } catch (Exception e) {
            logger.error("Unexpected error adding equipment: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Delete Equipment", description = "Deletes an equipment by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Equipment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while deleting equipment")
    })
    @PostMapping("/delete")
    public ResponseEntity<Object> deleteEquipment(
            @Parameter(description = "Delete equipment request containing the equipment ID", required = true)
            @RequestBody DeleteEquipmentRequestDto deleteEquipmentRequestDto) {

        Long id = deleteEquipmentRequestDto.getId();
        logger.info("Received request to delete equipment with ID: {}", id);
        try {
            equipmentService.deleteEquipment(id);
            logger.info("Successfully deleted equipment with ID: {}", id);
            return ResponseEntity.ok("Equipment deleted successfully");
        } catch (RuntimeException e) {
            logger.error("Error deleting equipment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Equipment not found");
        } catch (Exception e) {
            logger.error("Unexpected error deleting equipment: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Update Equipment", description = "Updates an existing equipment's details, including the option to upload a new image file.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment updated successfully"),
            @ApiResponse(responseCode = "404", description = "Equipment or area not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error or file upload error")
    })
    @PostMapping("/update")
    public ResponseEntity<Object> updateEquipment(
            @Parameter(description = "New image file for the equipment", required = false) @RequestParam(value = "file", required = false) MultipartFile file,
            @Parameter(description = "ID of the equipment to be updated", required = true) @RequestParam("id") Long id,
            @Parameter(description = "New name of the equipment", required = true) @RequestParam("name") String name,
            @Parameter(description = "New designation of the equipment", required = true) @RequestParam("designation") String designation,
            @Parameter(description = "New power consumption of the equipment", required = true) @RequestParam("power") double power,
            @Parameter(description = "New brand of the equipment", required = true) @RequestParam("brand") String brand,
            @Parameter(description = "ID of the new area where the equipment will be located", required = true) @RequestParam("areaId") Long areaId) {

        EquipmentRequestDto equipmentRequestDto = new EquipmentRequestDto();
        equipmentRequestDto.setName(name);
        equipmentRequestDto.setDesignation(designation);
        equipmentRequestDto.setPower(power);
        equipmentRequestDto.setBrand(brand);
        equipmentRequestDto.setAreaId(areaId);

        logger.info("Received request to update equipment with ID: {}", id);
        try {
            EquipmentResponseDto updatedEquipments = equipmentService.updateEquipment(id, file, equipmentRequestDto);
            logger.info("Successfully updated equipment: {}", updatedEquipments);
            return ResponseEntity.ok(updatedEquipments);
        } catch (RuntimeException e) {
            logger.error("Error updating equipment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Equipment or area not found");
        } catch (IOException e) {
            logger.error("Error saving file: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload error");
        } catch (Exception e) {
            logger.error("Unexpected error updating equipment: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Get All Equipments in Area", description = "Retrieves all equipments located in a specific area.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved equipments in the area"),
            @ApiResponse(responseCode = "404", description = "Area not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while retrieving equipments")
    })
    @PostMapping("/area")
    public ResponseEntity<Object> getAllEquipmentsInArea(
            @Parameter(description = "Request containing the area ID to fetch all equipments", required = true)
            @RequestBody AreaRequestDto areaRequestDto) {

        logger.info("Received request to get all equipments in area with ID: {}", areaRequestDto.getAreaId());
        try {
            List<EquipmentResponseDto> equipments = equipmentService.getAllEquipmentsInArea(areaRequestDto.getAreaId());
            logger.info("Successfully retrieved equipments in area with ID: {}", areaRequestDto.getAreaId());
            return ResponseEntity.ok(equipments);
        } catch (RuntimeException e) {
            logger.error("Error retrieving equipments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Area not found");
        } catch (Exception e) {
            logger.error("Unexpected error retrieving equipments: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Toggle Equipment State", description = "Toggles the operational state of an equipment by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipment state toggled successfully"),
            @ApiResponse(responseCode = "404", description = "Equipment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while toggling equipment state")
    })
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Equipments> toggleEquipmentState(
            @Parameter(description = "ID of the equipment to toggle the state for", required = true)
            @PathVariable Long id) {
        log.info("Received request to toggle state of equipment with id {}", id);
        return equipmentService.toggleEquipmentState(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
