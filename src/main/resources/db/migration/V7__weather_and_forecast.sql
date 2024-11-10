
CREATE TABLE forecast (
    id SERIAL PRIMARY KEY
);

CREATE TABLE weather (
    id SERIAL PRIMARY KEY,
    forecast_id BIGINT REFERENCES forecast(id),
    temp DOUBLE PRECISION,
    feels_like DOUBLE PRECISION,
    temp_max DOUBLE PRECISION,
    temp_min DOUBLE PRECISION,
    humidity DOUBLE PRECISION,
    sunrise VARCHAR(255),
    sunset VARCHAR(255),
    dt_txt TIMESTAMP
);

CREATE TABLE weather_description (
    id SERIAL PRIMARY KEY,
    weather_id BIGINT REFERENCES weather(id),
    main VARCHAR(255),
    description VARCHAR(255),
    icon VARCHAR(255)
);

INSERT INTO forecast (id) VALUES (0);
INSERT INTO forecast (id) VALUES (1);
