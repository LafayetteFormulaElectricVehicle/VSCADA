select 
	l.dataName AS "Sensor",
	e.message AS "Err Msg.",
	e.TimeStamp AS "TimeStamp"
from SensorLabels AS l 
INNER JOIN ErrorMessages AS e ON l.ID=e.sensorID;

