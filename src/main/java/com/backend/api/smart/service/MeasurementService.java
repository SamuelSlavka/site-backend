package com.backend.api.smart.service;

import com.backend.api.smart.entity.Measurement;
import com.backend.api.smart.model.MeasurementCreationDto;
import com.backend.api.wiki.error.NotFoundException;

/**
 * Service used for measurement manipulation
 */
public interface MeasurementService {

    Measurement getLatestMeasurement();

    Measurement createMeasurement(MeasurementCreationDto measurement) throws NotFoundException;
}
