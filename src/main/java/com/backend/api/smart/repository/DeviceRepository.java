package com.backend.api.smart.repository;

import com.backend.api.smart.entity.Device;
import com.backend.api.smart.model.SmallDeviceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    Device findFirstByIsMainTrue();

    @Query("SELECT d.id as id, d.name as name, d.isMain as isMain FROM Device d")
    List<SmallDeviceProjection> findSmallDevices();

}
