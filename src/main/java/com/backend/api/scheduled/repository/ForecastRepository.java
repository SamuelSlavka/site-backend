package com.backend.api.scheduled.repository;

import com.backend.api.scheduled.entity.Forecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast, String> {
    Optional<Forecast> findById(Long s);
}
