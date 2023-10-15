package com.backend.api.smart.repository;

import com.backend.api.smart.entity.Device;
import com.backend.api.smart.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, String> {
    @Query("SELECT m FROM Measurement m WHERE m.device = ?1 ORDER BY m.measuredAt DESC LIMIT 1")
    Measurement findFirst(Device device);
}
