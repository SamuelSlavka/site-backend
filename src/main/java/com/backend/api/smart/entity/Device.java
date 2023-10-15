package com.backend.api.smart.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private Boolean isMain = false;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "device")
    private Set<Measurement> measurements = new HashSet<>();

    public Device(String name) {
        this.name = name;
        this.isMain = false;
    }

    public Device(String name, Boolean isMain) {
        this.name = name;
        this.isMain = isMain;
    }
}
