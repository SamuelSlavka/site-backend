package com.backend.api.smart.service;

import com.backend.api.smart.entity.Device;
import com.backend.api.smart.model.DeviceCreationDto;
import com.backend.api.smart.model.SmallDeviceProjection;
import com.backend.api.smart.repository.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Objects;

@Service
public class DeviceServiceImpl implements DeviceService {
    private final Logger logger = LoggerFactory.getLogger((DeviceServiceImpl.class));

    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public List<Device> getDevices() {
        return deviceRepository.findAll();
    }

    @Override
    public List<SmallDeviceProjection> getSmallDevices() {
        return deviceRepository.findSmallDevices();
    }

    @Override
    public Device createDevice(DeviceCreationDto dto) {
        logger.info("Creating device");
        Device device;
        if (Objects.isNull(dto.getIsMain())) {
            device = new Device(dto.getName());
        } else {
            device = new Device(dto.getName(), dto.getIsMain());
        }
        deviceRepository.save(device);
        return device;
    }

    @Override
    public void deleteDevice(String id) {
        Device device = deviceRepository.findById(id).orElseThrow(() -> new NotFoundException("Device not found"));
        logger.info("Deleting device");
        deviceRepository.delete(device);
    }
}
