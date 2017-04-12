CREATE TABLE IF NOT EXISTS SensorLabels (
  ID            INTEGER PRIMARY KEY AUTOINCREMENT,
  tag           VARCHAR(50) NOT NULL,
  address       INTEGER NOT NULL,
  offset        INTEGER NOT NULL,
  byteLength    INTEGER NOT NULL,
  description   VARCHAR(50) NOT NULL,
  system        VARCHAR(50) NOT NULL,
  units         VARCHAR(50) NOT NULL,
  store         INTEGER NOT NULL,
  valSlope      DOUBLE NOT NULL,
  valOffset     DOUBLE NOT NULL
  );

PRAGMA foreign_keys = ON;
