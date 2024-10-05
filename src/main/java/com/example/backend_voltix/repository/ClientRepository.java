package com.example.backend_voltix.repository;

import com.example.backend_voltix.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ClientRepository extends JpaRepository<Client, Long> {
}
