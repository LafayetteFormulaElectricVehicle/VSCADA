select
	l.sensorName AS "Sensor",
	e.value AS "Value",
	e.TimeStamp AS "TimeStamp"
from SensorLabels AS l
INNER JOIN Data AS e ON l.ID=e.sensorID;


select
	labels.ID AS "Sensor ID",
	labels.sensorName AS "Sensor",
	labels.sensorUnits AS "Units",
	labels.dataType AS "Data Type",
	data.value AS "Value",
	data.TimeStamp AS "TimeStamp"
from SensorLabels AS labels
INNER JOIN Data AS data ON labels.ID=data.sensorID;

select
	*
from SensorLabels AS labels
INNER JOIN Data AS data ON labels.ID=data.sensorID;
