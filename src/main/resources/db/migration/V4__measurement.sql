CREATE TABLE measurement (
   id VARCHAR(255) NOT NULL,
   measured_at TIMESTAMP WITHOUT TIME ZONE,
   temperature NUMERIC,
   humidity NUMERIC,
   device VARCHAR(255)
);