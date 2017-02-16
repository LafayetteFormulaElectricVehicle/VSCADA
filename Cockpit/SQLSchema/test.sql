CREATE TABLE IF NOT EXISTS Dyno (
  torque FLOAT NOT NULL,
  oilTemp FLOAT NOT NULL,
  coolantTemp FLOAT NOT NULL,
  coolantFR FLOAT NOT NULL,
  RPM FLOAT NOT NULL,
  TimeStamp DATE DEFAULT(datetime('now', 'localtime')));
