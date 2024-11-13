package com.backend.api.scheduled.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "weather")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forecast_id")
    @JsonIgnore
    private Forecast forecast;

    @Embedded
    @JsonProperty("main")
    private Main main;

    @Embedded
    @JsonProperty("sys")
    private Sys sys;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "weather_id")
    @JsonProperty("weather")
    private List<WeatherDescription> weatherInner;

    @Setter
    @Getter
    @JsonProperty("pop")
    @Column(name = "pop")
    private Integer pop;

    @Setter
    @Getter
    @JsonProperty("dt_txt")
    @Column(name = "dt_txt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dtTxt;

    @Embeddable
    @Setter
    @Getter
    public static class Main {
        @JsonProperty("temp")
        private Double temp;

        @JsonProperty("temp_max")
        private Double temp_max;

        @JsonProperty("temp_min")
        private Double temp_min;

        @JsonProperty("feels_like")
        private Double feels_like;

        @JsonProperty("humidity")
        private Double humidity;
    }


    @Embeddable
    @Setter
    @Getter
    public static class Sys {
        @JsonProperty("sunrise")
        private String sunrise;

        @JsonProperty("sunset")
        private String sunset;
    }
}