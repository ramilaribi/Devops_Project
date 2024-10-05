package com.example.backend_voltix.controller;

import com.example.backend_voltix.dto.Device.DeviceDetailsResponseDto;
import com.example.backend_voltix.model.Area;
import com.example.backend_voltix.model.Device;
import com.example.backend_voltix.repository.AreaRepository;
import com.example.backend_voltix.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/device")
public class DeviceController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private AreaRepository areaRepository;

    @Operation(summary = "Add Device", description = "Adds a new device to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device added successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error while adding device")
    })
    @PostMapping("/add")
    public ResponseEntity<Device> addDevice(
            @Parameter(description = "Device object to be added", required = true)
            @RequestBody Device device) {
        try {
            logger.info("Received request to add device: {}", device);
            Device savedDevice = deviceService.addDevice(device);
            logger.info("Device added successfully with ID: {}", savedDevice.getId());
            return ResponseEntity.ok(savedDevice);
        } catch (Exception e) {
            logger.error("Error occurred while adding device", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(summary = "Get Device", description = "Fetches device details by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device found"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Device> getDevice(
            @Parameter(description = "ID of the device to be fetched", required = true)
            @PathVariable Long id) {
        try {
            logger.info("Fetching device with ID: {}", id);
            Device device = deviceService.getDeviceById(id);
            return ResponseEntity.ok(device);
        } catch (Exception e) {
            logger.error("Device not found", e);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update Device", description = "Updates an existing device's details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device updated successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while updating device")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<Device> updateDevice(
            @Parameter(description = "ID of the device to be updated", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated device object", required = true)
            @RequestBody Device device) {
        try {
            logger.info("Updating device with ID: {}", id);
            Device updatedDevice = deviceService.updateDevice(id, device);
            return ResponseEntity.ok(updatedDevice);
        } catch (Exception e) {
            logger.error("Error occurred while updating device", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(summary = "Delete Device", description = "Deletes a device from the system by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while deleting device")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDevice(
            @Parameter(description = "ID of the device to be deleted", required = true)
            @PathVariable Long id) {
        try {
            logger.info("Deleting device with ID: {}", id);
            deviceService.deleteDevice(id);
            return ResponseEntity.ok("Device deleted successfully");
        } catch (Exception e) {
            logger.error("Error occurred while deleting device", e);
            return ResponseEntity.status(500).body("Failed to delete device");
        }
    }

    @Operation(summary = "Get Devices by Area ID", description = "Fetches devices associated with a specific area.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devices found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while fetching devices")
    })
    @GetMapping("/area/{areaId}")
    public ResponseEntity<Optional<Device>> getDevicesByAreaId(
            @Parameter(description = "ID of the area to fetch devices for", required = true)
            @PathVariable Long areaId) {
        try {
            Optional<Device> devices = deviceService.getDevicesByAreaId(areaId);
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get Devices by Client ID", description = "Fetches devices associated with a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devices found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while fetching devices")
    })
    @GetMapping("/client/{clientId}")
    public ResponseEntity<Optional<Device>> getDevicesByClientId(
            @Parameter(description = "ID of the client to fetch devices for", required = true)
            @PathVariable Long clientId) {
        try {
            Optional<Device> devices = deviceService.getDevicesByClientId(clientId);
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Update Device Status", description = "Updates the status of a device, including enabling and OTA status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while updating device status")
    })
    @PutMapping("/status/{deviceId}")
    public ResponseEntity<Device> updateDeviceStatus(
            @Parameter(description = "ID of the device to update status for", required = true)
            @PathVariable Long deviceId,
            @Parameter(description = "Enable status of the device", required = true) @RequestParam boolean enable,
            @Parameter(description = "OTA status of the device", required = true) @RequestParam boolean ota) {
        try {
            Device updatedDevice = deviceService.updateDeviceStatus(deviceId, enable, ota);
            return ResponseEntity.ok(updatedDevice);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Assign Device to Client", description = "Assigns a device to a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device assigned to client successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error while assigning device")
    })
    @PostMapping("/assign/client")
    public ResponseEntity<Device> assignDeviceToClient(
            @Parameter(description = "ID of the device to be assigned", required = true) @RequestParam Long deviceId,
            @Parameter(description = "ID of the client to assign the device to", required = true) @RequestParam Long clientId) {
        try {
            Device device = deviceService.assignDeviceToClient(deviceId, clientId);
            return ResponseEntity.ok(device);
        } catch (Exception e) {
            logger.error("Error assigning device to client", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(summary = "Assign Device to Area", description = "Assigns a device to a specific area.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device assigned to area successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error while assigning device")
    })
    @PostMapping("/assign/area")
    public ResponseEntity<Device> assignDeviceToArea(
            @Parameter(description = "ID of the device to be assigned", required = true) @RequestParam Long deviceId,
            @Parameter(description = "ID of the area to assign the device to", required = true) @RequestParam Long areaId) {
        try {
            Device device = deviceService.assignDeviceToArea(deviceId, areaId);
            return ResponseEntity.ok(device);
        } catch (Exception e) {
            logger.error("Error assigning device to area", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(summary = "Create Area", description = "Creates a new area and saves it to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Area created successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error while creating area")
    })
    @PostMapping("/area")
    public ResponseEntity<Area> createArea(
            @Parameter(description = "Area object to be created", required = true)
            @RequestBody Area area) {
        try {
            Area savedArea = areaRepository.save(area);
            return ResponseEntity.ok(savedArea);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(summary = "Get Device by Serial Number", description = "Fetches device details by serial number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device found"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @GetMapping("/serialNbr/{serialNumber}")
    public ResponseEntity<DeviceDetailsResponseDto> getDeviceBySerialNumber(
            @Parameter(description = "Serial number of the device to be fetched", required = true)
            @PathVariable String serialNumber) {
        Optional<Device> deviceOpt = deviceService.getDeviceBySerialNumber(serialNumber);
        if (deviceOpt.isPresent()) {
            Device device = deviceOpt.get();
            DeviceDetailsResponseDto responseDto = new DeviceDetailsResponseDto();
            responseDto.setImei(device.getImei());
            responseDto.setProject(device.getProject());
            responseDto.setSerialNumber(device.getSerialNumber());
            responseDto.setMacAddress(device.getMacAddress());
            responseDto.setSoftLastUpdate(device.getSoftLastUpdate());
            responseDto.setLastConnection(device.getLastConnection());
            responseDto.setEnable(device.isEnable());
            responseDto.setDateInstallation(device.getDateInstallation());
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
