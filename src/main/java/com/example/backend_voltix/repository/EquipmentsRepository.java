package com.example.backend_voltix.repository;

import com.example.backend_voltix.model.Area;
import com.example.backend_voltix.model.Equipments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentsRepository extends JpaRepository<Equipments, Long> {
    List<Equipments> findByArea(Area area);


}

