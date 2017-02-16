CREATE TABLE IF NOT EXISTS ErrorMessages (
  ID INTEGER PRIMARY KEY AUTOINCREMENT,
  message VARCHAR(150) NOT NULL,
  TimeStamp DATE DEFAULT(datetime('now', 'localtime')),
  sensorID INTEGER,
  FOREIGN KEY(sensorID) REFERENCES SensorLabels(ID)
  );

PRAGMA foreign_keys = ON;
