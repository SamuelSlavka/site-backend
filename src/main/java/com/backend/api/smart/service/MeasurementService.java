package com.backend.api.smart.service;

import com.backend.api.smart.entity.Measurement;
import com.backend.api.smart.model.MeasurementCreationDto;
import com.backend.api.wiki.error.NotFoundException;

import java.util.List;

/**
 * Service used for measurement manipulation
 */
public interface MeasurementService {
    List<Measurement> getMeasurementsFrom(long offset, String deviceId) throws NotFoundException;

    Measurement getLatestMeasurement();

    Measurement createMeasurement(MeasurementCreationDto measurement) throws NotFoundException;
}
