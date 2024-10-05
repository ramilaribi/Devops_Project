package com.example.backend_voltix.repository;

import com.example.backend_voltix.model.Phones;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<Phones, Long> {
    void deleteAllByUserId(Long userId);
}