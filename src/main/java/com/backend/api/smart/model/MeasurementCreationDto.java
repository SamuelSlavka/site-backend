package com.backend.api.smart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MeasurementCreationDto {
    @NonNull
    private Double temperature;
    @NonNull
    private Double humidity;
    @NonNull
    private LocalDateTime measuredAt;
    @NonNull
    private String device;
}
