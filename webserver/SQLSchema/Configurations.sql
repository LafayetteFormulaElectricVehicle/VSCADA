CREATE TABLE IF NOT EXISTS Configurations (
  sensorID INTEGER,
  stableLow VARCHAR(150) NOT NULL,
  stableHigh VARCHAR(150) NOT NULL,
  criticalLow VARCHAR(150) NOT NULL,
  criticalHigh VARCHAR(150) NOT NULL,
  criticality VARCHAR(150) NOT NULL,
  slope VARCHAR(150) NOT NULL,
  offset VARCHAR(150) NOT NULL,
  FOREIGN KEY(sensorID) REFERENCES SensorLabels(ID) ON DELETE CASCADE
  );

PRAGMA foreign_keys = ON;
