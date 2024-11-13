package com.backend.api.scheduled.service;

import com.backend.api.scheduled.cosntants.Constants;
import com.backend.api.scheduled.entity.Forecast;
import com.backend.api.scheduled.entity.Weather;
import com.backend.api.scheduled.repository.ForecastRepository;
import com.backend.api.scheduled.repository.WeatherRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ApiServiceImpl implements ApiService {

    private static final Logger logger = LoggerFactory.getLogger(ApiServiceImpl.class);

    @Value("${OPEN_WEATHER_API}")
    private String weatherApiKey;

    @Value("${ALPHA_VANTAGE_API}")
    private String alphaApiKey;

    @Autowired
    private ForecastRepository forecastRepository;

    @Autowired
    private WeatherRepository weatherRepository;

    @Override
    @Transactional
    public void callStocksApi() {
        logger.info("StocksTask executed");
    }

    public String getLatestStocks() {
        return "";
    }

    @Override
    @Transactional
    public void callWeatherApi() {
        String weatherUrl = String.format("%s&appid=%s&units=metric", Constants.weatherBaseUrl, weatherApiKey);
        RestTemplate restTemplate = new RestTemplate();
        Weather weatherResponse = restTemplate.getForObject(weatherUrl, Weather.class);
        Optional<Forecast> forecast = forecastRepository.findById(Constants.weatherId);
        if (weatherResponse != null && forecast.isPresent()) {
            weatherRepository.deleteByForecastId(Constants.weatherId);
            weatherResponse.setForecast(forecast.get());
            weatherRepository.save(weatherResponse);
            logger.info("Weather updated");
        }
        logger.info("Weather Task executed");
    }

    public Weather getLatestWeather() {
        Optional<Forecast> fr = forecastRepository.findById(Constants.weatherId);
        return fr.map(forecast -> forecast.getList().get(0)).orElse(null);
    }

    @Override
    @Transactional
    public void callForecastApi() {
        String forecastUrl = String.format("%s&appid=%s&units=metric&cnt=9", Constants.forecastBaseUrl, weatherApiKey);
        RestTemplate restTemplate = new RestTemplate();
        Forecast forecastResponse = restTemplate.getForObject(forecastUrl, Forecast.class);
        if (forecastResponse != null) {
            forecastResponse.setId(Constants.forecastId);
            weatherRepository.deleteByForecastId(Constants.forecastId);
            forecastRepository.save(forecastResponse);
            logger.info("Forecast updated");
        }
        logger.info("Forecast Task executed");
    }

    public Forecast getLatestForecast() {
        Optional<Forecast> fr = forecastRepository.findById(Constants.forecastId);
        return fr.orElse(null);
    }
}
