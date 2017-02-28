CREATE TABLE IF NOT EXISTS ErrorMessages (
  ID INTEGER PRIMARY KEY AUTOINCREMENT,
  sensorID INTEGER,
  message VARCHAR(150) NOT NULL,
  TimeStamp DATE DEFAULT(datetime('now', 'localtime')),
  FOREIGN KEY(sensorID) REFERENCES SensorLabels(ID) ON DELETE CASCADE
  );

PRAGMA foreign_keys = ON;
