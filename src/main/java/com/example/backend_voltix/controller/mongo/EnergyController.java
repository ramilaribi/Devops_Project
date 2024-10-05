package com.example.backend_voltix.controller.mongo;

import com.example.backend_voltix.dto.mongo.ConsumptionDataResponseDto;
import com.example.backend_voltix.dto.mongo.ConsumptionGroupResponseDto;
import com.example.backend_voltix.dto.mongo.ConsumptionPeriodResponseDto;
import com.example.backend_voltix.dto.mongo.ConsumptionResponseDto;
import com.example.backend_voltix.service.mongo.ConsommationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    @Autowired
    private ConsommationService consommationService;

    @Operation(summary = "Get Daily Consumption", description = "Retrieves the daily energy consumption for a given date and serial number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved daily consumption"),
            @ApiResponse(responseCode = "500", description = "Internal server error while retrieving daily consumption")
    })
    @GetMapping("/daily-consumption")
    public ConsumptionResponseDto getDailyConsumption(
            @Parameter(description = "Date for which to retrieve the consumption, in ISO format (optional)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Serial number of the device", required = true)
            @RequestParam(required = true) String serialNumber) {
        return consommationService.calculateConsumptions(date, serialNumber);
    }

    @Operation(summary = "Get Weekly Consumption", description = "Retrieves the energy consumption data for the past week based on the serial number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved weekly consumption"),
            @ApiResponse(responseCode = "500", description = "Internal server error while retrieving weekly consumption")
    })
    @GetMapping("/daily")
    public ResponseEntity<ConsumptionPeriodResponseDto> getWeeklyConsumption(
            @Parameter(description = "Serial number of the device", required = true)
            @RequestParam String serialNumber) {
        try {
            ConsumptionPeriodResponseDto consumption = consommationService.getWeeklyConsumption(serialNumber);
            return ResponseEntity.ok(consumption);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Get Current Month Consumption", description = "Retrieves the energy consumption data for the current month based on the serial number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved current month consumption"),
            @ApiResponse(responseCode = "500", description = "Internal server error while retrieving current month consumption")
    })
    @GetMapping("/weekly")
    public ConsumptionPeriodResponseDto getCurrentMonthConsumption(
            @Parameter(description = "Serial number of the device", required = true)
            @RequestParam String serialNumber) {
        return consommationService.getCurrentMonthConsumption(serialNumber);
    }

    @Operation(summary = "Get Yearly Consumption", description = "Retrieves the energy consumption data for the current year based on the serial number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved yearly consumption"),
            @ApiResponse(responseCode = "500", description = "Internal server error while retrieving yearly consumption")
    })
    @GetMapping("/monthly")
    public List<Double> getYearlyConsumption(
            @Parameter(description = "Serial number of the device", required = true)
            @RequestParam String serialNumber) {
        return consommationService.getYearlyConsumption(serialNumber);
    }

    @Operation(summary = "Get Grouped Consumptions by Area", description = "Retrieves grouped energy consumption data by area for a given serial number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved grouped consumptions by area"),
            @ApiResponse(responseCode = "500", description = "Internal server error while retrieving grouped consumptions")
    })
    @GetMapping("/most-area-consumption/{sn}")
    public ResponseEntity<ConsumptionGroupResponseDto> getGroupedConsumptions(
            @Parameter(description = "Serial number of the device", required = true)
            @PathVariable("sn") String serialNumber) {
        ConsumptionGroupResponseDto consumptionByChannel = consommationService.calculateGroupedConsumptions(serialNumber);
        return ResponseEntity.ok(consumptionByChannel);
    }

    @Operation(summary = "Get Total Consumption", description = "Retrieves the total energy consumption for a device based on its serial number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved total consumption"),
            @ApiResponse(responseCode = "500", description = "Internal server error while retrieving total consumption")
    })
    @GetMapping("/total")
    public ResponseEntity<Double> getTotalConsumption(
            @Parameter(description = "Serial number of the device", required = true)
            @RequestParam String serialNumber) {
        double totalConsumption = consommationService.getTotalConsumption(serialNumber);
        return ResponseEntity.ok(totalConsumption);
    }

    @Operation(summary = "Get Historic Daily Consumptions", description = "Retrieves historical daily consumption data for a device based on its serial number and channel ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved historical daily consumptions"),
            @ApiResponse(responseCode = "500", description = "Internal server error while retrieving daily consumptions")
    })
    @GetMapping("/consumptions/historic-daily")
    public ResponseEntity<List<ConsumptionDataResponseDto>> getDailyConsumptions(
            @Parameter(description = "Serial number of the device", required = true)
            @RequestParam String serialNumber,
            @Parameter(description = "Channel ID for which to retrieve the data", required = true)
            @RequestParam int channelId) {
        List<ConsumptionDataResponseDto> consumptions = consommationService.getHistoricDailyConsumptions(serialNumber, channelId);
        return ResponseEntity.ok(consumptions);
    }
}
