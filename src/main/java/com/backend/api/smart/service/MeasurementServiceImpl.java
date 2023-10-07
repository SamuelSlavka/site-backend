package com.backend.api.smart.service;

import com.backend.api.smart.entity.Measurement;
import com.backend.api.smart.model.MeasurementCreationDto;
import com.backend.api.smart.repository.MeasurementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MeasurementServiceImpl implements MeasurementService {
    private final Logger logger = LoggerFactory.getLogger((MeasurementServiceImpl.class));

    @Autowired
    private MeasurementRepository measurementRepository;

    @Override
    public List<Measurement> getMeasurements() {
        return measurementRepository.findAllOrdered();
    }

    @Override
    public List<Measurement> getMeasurementsFromDate(LocalDateTime start) {
        return measurementRepository.findAllOrderedFromDate(start);
    }


    @Override
    public Measurement getLatestMeasurement() {
        return measurementRepository.findFirst();
    }

    @Override
    public Measurement createMeasurement(MeasurementCreationDto dto) {
        logger.info("Creating measurement");
        Measurement measurement = new Measurement(dto);
        measurementRepository.save(measurement);
        return measurement;
    }
}
