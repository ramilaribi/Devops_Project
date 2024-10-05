package com.example.backend_voltix.repository;

import com.example.backend_voltix.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findDeviceBySerialNumber(String serialNumber);
    Optional<Device> findByClientId(Long clientId);
    Optional<Device> findByAreaId(Long areaId);

}
