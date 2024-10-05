package com.example.backend_voltix.dto.Device;

import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDetailsResponseDto {
    private String imei;
    private String project;
    private String serialNumber;
    private String macAddress;
    private LocalDateTime softLastUpdate;
    private LocalDateTime lastConnection;
    private boolean enable;
    private LocalDateTime dateInstallation;
}