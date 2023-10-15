package com.backend.api.smart.repository;

import com.backend.api.smart.entity.Device;
import com.backend.api.smart.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, String> {
    @Query("SELECT m FROM Measurement m WHERE m.device = ?1 ORDER BY m.measuredAt DESC LIMIT 1")
    Measurement findFirst(Device device);

    @Query("SELECT m FROM Measurement m WHERE m.device = ?1 AND m.measuredAt > ?2 ORDER BY m.measuredAt DESC")
    List<Measurement> findAllWithOffset(Device device, LocalDateTime start);

    @Query(nativeQuery = true, value = """ 
            SELECT t.*
            FROM (
                SELECT *, row_number() OVER(ORDER BY measured_at ASC) AS row
                FROM Measurement
            ) t WHERE
             t.row % ?3 = 0 AND
             t.measured_at > ?2 AND
             t.device_id = ?1
             ORDER BY t.measured_at DESC
            """)
    List<Measurement> findAllWithOffsetAndFilter(String device, LocalDateTime start, Integer filter);

    @Query(nativeQuery = true, value = """ 
            SELECT t.*
            FROM (
                SELECT *, row_number() OVER(ORDER BY measured_at ASC) AS row
                FROM Measurement
            ) t WHERE
             t.row % ?2 = 0 AND
             t.device_id = ?1
             ORDER BY t.measured_at DESC
            """)
    List<Measurement> findAllWithoutOffset(String device, Integer filter);
}
