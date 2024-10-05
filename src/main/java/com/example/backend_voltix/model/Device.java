package com.example.backend_voltix.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String project;
    private String deviceName;
    private String serialNumber;
    private String macAddress;
    private String imei;
    private boolean ota;
    private boolean canOta;
    private LocalDateTime softLastUpdate;
    private boolean enable;
    private LocalDateTime lastConnection;
    private LocalDateTime dateInstallation;
    @Enumerated(EnumType.STRING)
    private Action action;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "area_id")
    private Area area;


}
