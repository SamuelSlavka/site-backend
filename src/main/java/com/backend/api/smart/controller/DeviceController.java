package com.backend.api.smart.controller;

import com.backend.api.smart.entity.Device;
import com.backend.api.smart.model.DeviceCreationDto;
import com.backend.api.smart.model.SmallDeviceProjection;
import com.backend.api.smart.service.DeviceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Device controller that provides crud actions on Device entity
 */
@RestController
@RequestMapping(path = "/api/v1/devices")
public class DeviceController {
    private final Logger logger = LoggerFactory.getLogger((DeviceController.class));

    @Autowired
    private DeviceService deviceService;

    /**
     * Get endpoint that fetches list of devices without their measurements
     *
     * @return returns body of HTTP response with list of devices
     */
    @GetMapping()
    public List<SmallDeviceProjection> getSmallDevices() {
        this.logger.info("Started fetching slim devices from");
        return deviceService.getSmallDevices();
    }

    /**
     * Post endpoint to create measuring device
     *
     * @param dto object with new name and flag to update empty
     * @return returns new Device
     */
    @PostMapping
    public Device createDevice(@Valid @RequestBody DeviceCreationDto dto) {
        this.logger.info("Started creating measurement created at {}", dto.getName());
        return deviceService.createDevice(dto);
    }


    /**
     * Delete endpoint to delete measuring device
     *
     * @param deviceId device id
     */
    @DeleteMapping(path = "/{deviceId}")
    public void deleteDevice(@Valid @PathVariable("deviceId") String deviceId) {
        this.logger.info("Started deleting device");
        deviceService.deleteDevice(deviceId);
    }
}
