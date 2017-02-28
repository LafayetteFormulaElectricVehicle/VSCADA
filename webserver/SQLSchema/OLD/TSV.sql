CREATE TABLE IF NOT EXISTS TSV (
  TimeStamp DATE DEFAULT(datetime('now', 'localtime')),

  cellV1 FLOAT NOT NULL,
  cellV2 FLOAT NOT NULL,
  cellV3 FLOAT NOT NULL,
  cellV4 FLOAT NOT NULL,

  cellT1 FLOAT NOT NULL,
  cellT2 FLOAT NOT NULL,
  cellT3 FLOAT NOT NULL,
  cellT4 FLOAT NOT NULL,

  packA1 FLOAT NOT NULL,
  packA2 FLOAT NOT NULL,
  packA3 FLOAT NOT NULL,
  packA4 FLOAT NOT NULL,

  packT1 FLOAT NOT NULL,
  packT2 FLOAT NOT NULL,
  packT3 FLOAT NOT NULL,
  packT4 FLOAT NOT NULL);
