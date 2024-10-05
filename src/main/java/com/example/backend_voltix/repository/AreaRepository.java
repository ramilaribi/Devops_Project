package com.example.backend_voltix.repository;

import com.example.backend_voltix.model.Area;
import com.example.backend_voltix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AreaRepository extends JpaRepository<Area,Long> {
    Optional<Area> findById(Long id);


}
