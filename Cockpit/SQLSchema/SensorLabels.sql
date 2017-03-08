CREATE TABLE IF NOT EXISTS SensorLabels (
  ID INTEGER NOT NULL,
  sensorName VARCHAR(50) NOT NULL,
  sensorUnits VARCHAR(50) NOT NULL,
  dataType VARCHAR(50) NOT NULL,
  system VARCHAR(50) NOT NULL
  );

PRAGMA foreign_keys = ON;
