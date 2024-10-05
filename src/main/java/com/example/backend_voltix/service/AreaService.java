package com.example.backend_voltix.service;

import com.example.backend_voltix.dto.Area.AreaDto;
import com.example.backend_voltix.model.Area;
import com.example.backend_voltix.model.Device;
import com.example.backend_voltix.repository.AreaRepository;
import com.example.backend_voltix.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Service
public class AreaService implements IAreaService {

    private static final Logger logger = LoggerFactory.getLogger(AreaService.class);
    @Value("${upload.path}"+"areas")
    private String uploadPath;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public List<Area> getAllAreas() {
        logger.info("Fetching all areas from the database");
        List<Area> areas = areaRepository.findAll();
        logger.info("Fetched {} areas", areas.size());
        return areas;
    }

    @Override
    public Area createAreaWithImage(MultipartFile file, AreaDto areaDto) {
        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path targetLocation = Paths.get(uploadPath).resolve(fileName);
            try {
                Files.copy(file.getInputStream(), targetLocation);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            imageUrl = "/" + fileName;
        }

        Area area = new Area();
        area.setName(areaDto.getName());
        area.setNbrFenetres(areaDto.getNbrFenetres());
        area.setNbrPortes(areaDto.getNbrPortes());
        area.setSurface(areaDto.getSurface());
        area.setImageUrl(imageUrl);

        if (areaDto.getDeviceId() != null) {
            Device device = deviceRepository.findById(areaDto.getDeviceId())
                    .orElseThrow(() -> new RuntimeException("Device not found"));
            area.setDevice(device);
        }

        return  areaRepository.save(area);
    }
    @Override
    @Transactional
    public Area updateAreaWithImage(Long id, MultipartFile file, AreaDto areaDto) {
        logger.info("Attempting to update area with ID: {}", id);

        Area area = areaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Area not found with ID: {}", id);
                    return new RuntimeException("Area not found");
                });

        if (file != null && !file.isEmpty()) {
            // Handle the new image file
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path targetLocation = Paths.get(uploadPath).resolve(fileName);
            try {
                Files.copy(file.getInputStream(), targetLocation);
                area.setImageUrl("/" + fileName);
                logger.info("Image uploaded successfully: {}", fileName);
            } catch (IOException e) {
                logger.error("Failed to upload image: {}", e.getMessage());
                throw new RuntimeException("Failed to upload image", e);
            }
        } else {
            logger.info("No image file provided for area update");
        }

        area.setName(areaDto.getName());
        area.setNbrFenetres(areaDto.getNbrFenetres());
        area.setNbrPortes(areaDto.getNbrPortes());
        area.setSurface(areaDto.getSurface());

        if (areaDto.getDeviceId() != null) {
            Device device = deviceRepository.findById(areaDto.getDeviceId())
                    .orElseThrow(() -> {
                        logger.error("Device not found with ID: {}", areaDto.getDeviceId());
                        return new RuntimeException("Device not found");
                    });
            area.setDevice(device);
            logger.info("Device associated with area: ID {}", areaDto.getDeviceId());
        }

        Area updatedArea = areaRepository.save(area);
        logger.info("Area updated successfully: {}", updatedArea);

        return updatedArea;
    }

    @Override
    @Transactional
    public void deleteArea(Long id) {
        logger.info("Deleting area with ID: {}", id);

        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Area not found"));

        areaRepository.delete(area);
        logger.info("Area with ID: {} deleted successfully", id);
    }
}
