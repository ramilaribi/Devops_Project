package com.example.backend_voltix.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String designation;
    @Builder.Default
    private int actAddress = 0;
    @Builder.Default
    private int actModule = 0;
    @Builder.Default
    private int actVal = 0;
    private double power;
    private String brand;
    @Builder.Default
    private int duration = 0;

    @ManyToOne
    @JoinColumn(name = "area_id")
    private Area area;

    private String imageUrl;

    private Boolean state ;
}
