CREATE TABLE IF NOT EXISTS TSI (
  TimeStamp DATE DEFAULT(datetime('now', 'localtime')),
  totalVoltage FLOAT NOT NULL,
  totalCurrent FLOAT NOT NULL);

