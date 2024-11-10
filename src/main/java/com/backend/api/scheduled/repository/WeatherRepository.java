package com.backend.api.scheduled.repository;

import com.backend.api.scheduled.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, String> {
    void deleteByForecastId(Long forecastId);

    Weather findByForecastId(Long forecastId);
}
