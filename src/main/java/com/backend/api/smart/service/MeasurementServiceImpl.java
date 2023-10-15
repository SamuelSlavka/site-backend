package com.backend.api.smart.service;

import com.backend.api.smart.entity.Device;
import com.backend.api.smart.entity.Measurement;
import com.backend.api.smart.model.MeasurementCreationDto;
import com.backend.api.smart.repository.DeviceRepository;
import com.backend.api.smart.repository.MeasurementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class MeasurementServiceImpl implements MeasurementService {
    private final Logger logger = LoggerFactory.getLogger((MeasurementServiceImpl.class));

    @Autowired
    private MeasurementRepository measurementRepository;
    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public Measurement getLatestMeasurement() {
        Device device = deviceRepository.findFirstByIsMainTrue();
        if (Objects.nonNull(device)) {
            return measurementRepository.findFirst(device);
        }
        return null;
    }

    @Override
    public Measurement createMeasurement(MeasurementCreationDto dto) {
        logger.info("Creating measurement");
        Optional<Device> device = deviceRepository.findById(dto.getDevice());

        Measurement measurement = new Measurement(dto);

        device.ifPresent(dev -> {
            logger.info("Adding to device");
            dev.getMeasurements().add(measurement);
        });

        measurementRepository.save(measurement);
        return measurement;
    }
}
