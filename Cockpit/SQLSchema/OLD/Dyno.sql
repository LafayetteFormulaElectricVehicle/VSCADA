CREATE TABLE IF NOT EXISTS Dyno (
  TimeStamp DATE DEFAULT(datetime('now', 'localtime')),
  torque FLOAT NOT NULL,
  oilTemp FLOAT NOT NULL,
  coolantTemp FLOAT NOT NULL,
  coolantFR FLOAT NOT NULL,
  RPM FLOAT NOT NULL);
