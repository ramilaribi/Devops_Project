package com.example.backend_voltix.service;

import com.example.backend_voltix.model.Area;
import com.example.backend_voltix.model.Client;
import com.example.backend_voltix.model.Device;
import com.example.backend_voltix.repository.AreaRepository;
import com.example.backend_voltix.repository.ClientRepository;
import com.example.backend_voltix.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DeviceService implements IDeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ClientRepository  clientRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Transactional
    public Device addDevice(Device device) {
        return deviceRepository.save(device);
    }

    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id).orElseThrow(() -> new RuntimeException("Device not found"));
    }


    public Optional<Device> getDevicesByAreaId(Long areaId) {
        return deviceRepository.findByAreaId(areaId);
    }


    public Optional<Device> getDevicesByClientId(Long clientId) {
        return deviceRepository.findByClientId(clientId);
    }



    @Transactional
    public Device updateDevice(Long id, Device device) {
        Device existingDevice = getDeviceById(id);
        // Update properties
        existingDevice.setProject(device.getProject());
        existingDevice.setSerialNumber(device.getSerialNumber());
        existingDevice.setMacAddress(device.getMacAddress());
        existingDevice.setImei(device.getImei());
        existingDevice.setCanOta(device.isCanOta());
        existingDevice.setDateInstallation(device.getDateInstallation());
        return deviceRepository.save(existingDevice);
    }

    @Transactional
    public void deleteDevice(Long id) {
        deviceRepository.deleteById(id);
    }


    @Transactional
    public Device assignDeviceToClient(Long deviceId, Long clientId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        device.setClient(client);
        return deviceRepository.save(device);
    }

    @Transactional
    public Device assignDeviceToArea(Long deviceId, Long areaId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Area not found"));

        device.setArea(area);
        return deviceRepository.save(device);
    }


//Still Testing
    //**********************************************************************************************
    public Device updateDeviceStatus(Long deviceId, boolean enable, boolean ota) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        device.setEnable(enable);
        device.setOta(ota);
        return deviceRepository.save(device);
    }


    /*
    public Device updateLastConnection(Long deviceId, LocalDateTime lastConnection) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        device.setLastConnection(lastConnection);
        return deviceRepository.save(device);
    }

    public Device updateFirmware(Long deviceId, boolean canOta, LocalDateTime softLastUpdate) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        device.setCanOta(canOta);
        device.setSoftLastUpdate(softLastUpdate);
        return deviceRepository.save(device);
    } */

    public Optional<Device> getDeviceBySerialNumber(String serialNumber) {
        return deviceRepository.findDeviceBySerialNumber(serialNumber);
    }
}
