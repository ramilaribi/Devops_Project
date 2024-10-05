package com.example.backend_voltix.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    private String name;
    private String imageUrl;
    private int nbrFenetres;
    private int nbrPortes;
    private int numOfEquipments;
    private int numOfPeople;
    private double surface;
    @JsonIgnore
    private int level;
    @JsonIgnore
    @Column(name = "channel_id")
    private Long channelId;
    @JsonIgnore
    @Column(name = "parentId")
    private Long parentId;
    @JsonIgnore
    @Column(name = "siteId")
    private Long siteId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "deviceId")
    private Device device;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parentId", insertable = false, updatable = false)
    private Area parentArea;

    @JsonIgnore
    @OneToMany(mappedBy = "parentArea", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Area> childAreas;

    @JsonIgnore
    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Equipments> equipments;
}
