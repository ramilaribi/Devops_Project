package com.example.backend_voltix.model.mongo;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "consommation")
@Data
public class Consommation {
    @Id
    private String id;
    private double date;
    private double consommation_diff;
}
