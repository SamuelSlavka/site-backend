package com.backend.api.smart.entity;

import com.backend.api.smart.model.MeasurementCreationDto;
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
    private String device;

    @Column(name = "measured_at")
    private LocalDateTime measuredAt;

    public Measurement(MeasurementCreationDto dto) {
        this.temperature = dto.getTemperature();
        this.humidity = dto.getHumidity();
        this.measuredAt = dto.getMeasuredAt();
        this.device = dto.getDevice();
    }
}
