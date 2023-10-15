package com.backend.api.smart.entity;

import com.backend.api.smart.model.MeasurementCreationDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Double temperature;
    private Double humidity;

    @Column(name = "measured_at")
    private LocalDateTime measuredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    @JsonIgnore
    private Device device;


    public Measurement(MeasurementCreationDto dto) {
        this.temperature = dto.getTemperature();
        this.humidity = dto.getHumidity();
        this.measuredAt = dto.getMeasuredAt();
    }
}
