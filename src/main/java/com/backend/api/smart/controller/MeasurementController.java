package com.backend.api.smart.controller;

import com.backend.api.smart.entity.Measurement;
import com.backend.api.smart.model.MeasurementCreationDto;
import com.backend.api.smart.service.MeasurementService;
import com.backend.api.wiki.error.NotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Measurement controller that provides crud actions on Measurement entity
 */
@RestController
@RequestMapping(path = "/api/v1/measurements")
public class MeasurementController {

    private final Logger logger = LoggerFactory.getLogger((MeasurementController.class));

    @Autowired
    private MeasurementService measurementService;

    /**
     * Get endpoint that fetches latest measurement
     *
     * @return returns body of HTTP response with list of measurements
     */
    @GetMapping(path = "/latest")
    public Measurement getLatestMeasurement() {
        this.logger.info("Started fetching measurements");
        return measurementService.getLatestMeasurement();

    }

    /**
     * Post endpoint that creates an measurement
     *
     * @param request object in HTTP request body containing new measurement
     * @return returns newly created measurement
     */
    @PostMapping
    public Measurement createMeasurement(@Valid @RequestBody MeasurementCreationDto request) throws NotFoundException {
        this.logger.info("Started creating measurement created at {}", request.getMeasuredAt());
        return measurementService.createMeasurement(request);
    }
}
