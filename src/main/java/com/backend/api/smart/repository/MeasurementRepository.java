package com.backend.api.smart.repository;

import com.backend.api.smart.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, String> {

    @Query("SELECT m FROM Measurement m ORDER BY m.measuredAt DESC")
    List<Measurement> findAllOrdered();

    @Query("SELECT m FROM Measurement m WHERE m.measuredAt >= ?1 ORDER BY m.measuredAt DESC")
    List<Measurement> findAllOrderedFromDate(LocalDateTime start);

    @Query("SELECT m FROM Measurement m ORDER BY m.measuredAt DESC LIMIT 1")
    Measurement findFirst();
}
