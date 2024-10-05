package com.example.backend_voltix.controller;

import com.example.backend_voltix.dto.Area.*;
import com.example.backend_voltix.model.Area;
import com.example.backend_voltix.service.IAreaService;
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

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/areas")
public class AreaController {

    @Autowired
    private IAreaService areaService;

    @Operation(summary = "Get All Areas", description = "Fetches all the areas from the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of areas")
    })
    @GetMapping("/getAll")
    public List<Area> getAllAreas() {
        return areaService.getAllAreas();
    }

    @Operation(summary = "Create Area", description = "Creates a new area and saves it to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created area"),
            @ApiResponse(responseCode = "400", description = "Invalid area data provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error while creating area")
    })
    @PostMapping("/add")
    public ResponseEntity<Object> addArea(
            @Parameter(description = "Image file of the area", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "Name of the area", required = true) @RequestParam("name") String name,
            @Parameter(description = "Number of windows in the area", required = true) @RequestParam("nbrFenetres") int nbrFenetres,
            @Parameter(description = "Number of doors in the area", required = true) @RequestParam("nbrPortes") int nbrPortes,
            @Parameter(description = "Surface area", required = true) @RequestParam("surface") double surface,
            @Parameter(description = "Device ID associated with the area", required = false) @RequestParam("deviceId") Long deviceId) {

        AreaDto areaDto = new AreaDto();
        areaDto.setName(name);
        areaDto.setNbrFenetres(nbrFenetres);
        areaDto.setNbrPortes(nbrPortes);
        areaDto.setSurface(surface);
        areaDto.setDeviceId(deviceId);
        try {
            Area createdArea = areaService.createAreaWithImage(file, areaDto);
            return ResponseEntity.ok(createdArea);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Update Area", description = "Updates an existing area with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated area"),
            @ApiResponse(responseCode = "400", description = "Invalid area data provided"),
            @ApiResponse(responseCode = "404", description = "Area not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while updating area")
    })
    @PutMapping("/update")
    public ResponseEntity<Object> updateArea(
            @Parameter(description = "Area ID", required = true) @RequestParam("id") Long id,
            @Parameter(description = "Image file of the area", required = false) @RequestParam(value = "file", required = false) MultipartFile file,
            @Parameter(description = "Name of the area", required = true) @RequestParam("name") String name,
            @Parameter(description = "Number of windows in the area", required = true) @RequestParam("nbrFenetres") int nbrFenetres,
            @Parameter(description = "Number of doors in the area", required = true) @RequestParam("nbrPortes") int nbrPortes,
            @Parameter(description = "Surface area", required = true) @RequestParam("surface") double surface,
            @Parameter(description = "Device ID associated with the area", required = false) @RequestParam("deviceId") Long deviceId) {

        AreaDto areaDto = new AreaDto();
        areaDto.setId(id);
        areaDto.setName(name);
        areaDto.setNbrFenetres(nbrFenetres);
        areaDto.setNbrPortes(nbrPortes);
        areaDto.setSurface(surface);
        areaDto.setDeviceId(deviceId);
        try {
            Area updatedArea = areaService.updateAreaWithImage(id, file, areaDto);

            return ResponseEntity.ok(updatedArea);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Area not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Delete Area", description = "Deletes an area from the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted area"),
            @ApiResponse(responseCode = "404", description = "Area not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while deleting area")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteArea(
            @Parameter(description = "Area deletion data transfer object containing the area ID", required = true)
            @RequestBody DeleteAreaDto deleteAreaDto) {
        areaService.deleteArea(deleteAreaDto.getId());
        return ResponseEntity.ok("Area deleted successfully");
    }
}
