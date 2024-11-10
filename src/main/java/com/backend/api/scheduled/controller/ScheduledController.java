package com.backend.api.scheduled.controller;

import com.backend.api.scheduled.entity.Forecast;
import com.backend.api.scheduled.entity.Weather;
import com.backend.api.scheduled.service.ApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for weather and stocks
 */
@RestController
@RequestMapping(path = "/api/v1/scheduled")
public class ScheduledController {
    private final Logger logger = LoggerFactory.getLogger((ScheduledController.class));

    @Autowired
    private ApiService apiService;

    /**
     * Get endpoint that fetches current weather
     *
     * @return returns body of HTTP response with Weather
     */
    @GetMapping(path = "/weather")
    public Weather getWeather() {
        this.logger.info("Fetching weather");
        return apiService.getLatestWeather();
    }

    /**
     * Get endpoint that fetches current forecast
     *
     * @return returns body of HTTP response with Forecast
     */
    @GetMapping(path = "/forecast")
    public Forecast getForecast() {
        this.logger.info("Fetching forecast");
        return apiService.getLatestForecast();
    }

    /**
     * Get endpoint that fetches current stocks
     *
     * @return returns body of HTTP response with StocksTicker
     */
    @GetMapping(path = "/stocks")
    public String getStocks() {
        this.logger.info("Fetching stocks");
        return apiService.getLatestStocks();
    }
}
