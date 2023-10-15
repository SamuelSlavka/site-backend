package com.backend.api.smart.service;

import com.backend.api.smart.entity.Device;
import com.backend.api.smart.model.DeviceCreationDto;
import com.backend.api.smart.model.SmallDeviceProjection;

import java.util.List;

/**
 * Service used for device manipulation
 */
public interface DeviceService {
    List<Device> getDevices();

    List<SmallDeviceProjection> getSmallDevices();

    Device createDevice(DeviceCreationDto device);

    void deleteDevice(String device);
}
