package com.backend.api.smart.service;

import com.backend.api.smart.entity.Measurement;
import com.backend.api.smart.model.MeasurementCreationDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service used for measurement manipulation
 */
public interface MeasurementService {
    List<Measurement> getMeasurements();

    List<Measurement> getMeasurementsFromDate(LocalDateTime start);

    Measurement getLatestMeasurement();

    Measurement createMeasurement(MeasurementCreationDto measurement);
}
