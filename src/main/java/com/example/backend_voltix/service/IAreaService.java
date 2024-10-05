package com.example.backend_voltix.service;

import com.example.backend_voltix.dto.Area.AreaDto;
import com.example.backend_voltix.model.Area;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IAreaService {
    List<Area> getAllAreas();
    Area createAreaWithImage(MultipartFile file, AreaDto areaDto);
    Area updateAreaWithImage(Long id, MultipartFile file, AreaDto areaDto);

    void deleteArea(Long id);

}
