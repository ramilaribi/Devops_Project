package com.example.backend_voltix.dto.Device;

import java.time.LocalDateTime;

public class DeviceResponse {
    private Long id;
    private String project;
    private Long serialNumber;
    private String macAddress;
    private String imei;
    private boolean ota;
    private boolean canOta;
    private LocalDateTime softLastUpdate;
    private boolean enable;
    private LocalDateTime lastConnection;
    private LocalDateTime dateInstallation;

}