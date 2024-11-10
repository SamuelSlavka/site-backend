package com.backend.api.scheduled.tasks;

import com.backend.api.scheduled.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {
    @Autowired
    private ApiService apiService;

    // Runs every 30 minutes
    @Scheduled(fixedRate = 1800000)
    public void performTask() {
        apiService.callStocksApi();
        apiService.callWeatherApi();
        apiService.callForecastApi();
    }
}
