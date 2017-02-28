select
	l.sensorName AS "Sensor",
	e.value AS "Value",
	e.TimeStamp AS "TimeStamp"
from SensorLabels AS l
INNER JOIN Data AS e ON l.ID=e.sensorID;

-- Get all crucial data
select
	labels.ID AS "Sensor ID",
	labels.sensorName AS "Sensor",
	labels.sensorUnits AS "Units",
	labels.dataType AS "Data Type",
	data.value AS "Value",
	data.TimeStamp AS "TimeStamp"
from SensorLabels AS labels
INNER JOIN Data AS data ON labels.ID=data.sensorID;

-- Get everything
select
	*
from SensorLabels AS labels
INNER JOIN Data AS data ON labels.ID=data.sensorID;

-- Get date range
select
	labels.ID AS "Sensor ID",
	labels.sensorName AS "Sensor",
	labels.sensorUnits AS "Units",
	labels.dataType AS "Data Type",
	labels.system AS "System",
	data.value AS "Value",
	data.TimeStamp AS "TimeStamp"
from SensorLabels AS labels
INNER JOIN Data AS data ON labels.ID=data.sensorID
WHERE DATE(TimeStamp) >= "2017-02-26"
AND DATE(TimeStamp) <= "2017-02-27";

-- System search
select
	labels.ID AS "Sensor ID",
	labels.sensorName AS "Sensor",
	labels.sensorUnits AS "Units",
	labels.dataType AS "Data Type",
	data.value AS "Value",
	data.TimeStamp AS "TimeStamp"
from SensorLabels AS labels
INNER JOIN Data AS data ON labels.ID=data.sensorID
WHERE labels.system="TSV";
