CREATE TABLE IF NOT EXISTS Data (
  sensorID INTEGER,
  value VARCHAR(150) NOT NULL,
  TimeStamp DATE DEFAULT(datetime('now', 'localtime')),
  FOREIGN KEY(sensorID) REFERENCES SensorLabels(ID) ON DELETE CASCADE
  );

PRAGMA foreign_keys = ON;
