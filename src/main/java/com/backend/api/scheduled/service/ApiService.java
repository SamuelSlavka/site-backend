package com.backend.api.scheduled.service;

import com.backend.api.scheduled.entity.Forecast;
import com.backend.api.scheduled.entity.Weather;

/**
 * Service used for calling external apis
 */
public interface ApiService {
    void callStocksApi();

    String getLatestStocks();

    void callWeatherApi();

    Weather getLatestWeather();

    void callForecastApi();

    Forecast getLatestForecast();
}
