package cockpit.database;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class BackupDBHandler {

    private Connection c;
    private String dbName;

    private String schemaPath = "../SQLSchema/";

    public BackupDBHandler() {
        c = null;
        dbName = "SCADA.db";
        connectDB();
    }

    public BackupDBHandler(String name, String sPath) {
        c = null;
        dbName = name;
        if (sPath != null) schemaPath = sPath;
        connectDB();
    }

    public void connectDB() {
        c = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbName);
            checkDB();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void checkDB() {
        if (!checkExists("SensorLabels")) {
            readSQLFile(schemaPath + "SensorLabels.sql");
            readSQLFile(schemaPath + "SensorSetup.sql");
        }
//        if (!checkExists("Configurations")) {
//            readSQLFile(schemaPath + "Configurations.sql");
//            readSQLFile(schemaPath + "ConfigurationSetup.sql");
//        }
        readSQLFile(schemaPath + "Data.sql");
        readSQLFile(schemaPath + "ErrorMessages.sql");
    }

    public Boolean checkExists(String table) {
        return getTable(table) != null;
    }

    public ArrayList<ArrayList<String>> getTable(String table) {
        String query = "select * from " + table;
        return runQuery(query);
    }

    public void readSQLFile(String fileName) {
        Statement stmt = null;
        String sql = "";

        try {
            Scanner sc = new Scanner(new File(fileName));
            stmt = c.createStatement();
            while (sc.hasNextLine()) {
                sql += sc.nextLine();
            }
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (Exception e) {
        }
    }

    public ArrayList<String> getSchema(String fileName) {
        ArrayList<String> out = new ArrayList<String>();
        try {
            Scanner sc = new Scanner(new File(fileName));
            sc.nextLine();
            while (sc.hasNextLine()) {
                out.add(sc.next());
                sc.nextLine();
            }
        } catch (Exception e) {
        }
        return out;
    }

    public void closeDB() {
        try {
            if (c.isClosed()) System.out.println("No open DB");
            else c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void runSQL(String sql) {
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            stmt.execute(sql);
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private ArrayList<ArrayList<String>> runQuery(String query) {
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return getResultsTable(rs);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }

    private ArrayList<ArrayList<String>> getResultsTable(ResultSet rs) {
        ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();
        ArrayList<String> inner;

        try {
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();
            while (rs.next()) {
                inner = new ArrayList<String>();
                for (int i = 1; i <= columns; i++) {
                    inner.add(rs.getString(i));
                }
                output.add(inner);
            }
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        return output;
    }

    //date in the form of 2017-02-05
    public ArrayList<ArrayList<String>> getInfo(String IDs, String systems, String date1, String date2) {

        String whereC;
        String andC;
        String and2C;

        String sys;
        String range;
        String idRange;

        whereC = (systems != null || date1 != null || date2 != null || IDs != null) ? " WHERE " : "";
        andC = (IDs != null && systems != null) ? " AND " : "";
        // andC = (IDs != null && (systems != null || date1 != null || date2 != null)) ? " AND " : "";
        and2C = ((IDs != null || systems != null) && (date1 != null || date2 != null)) ? " AND " : "";

        sys = parseSystems(systems);

        if (IDs != null) idRange = "labels.ID IN (" + parseCSV(IDs) + ")";
        else idRange = "";

        if (date1 == null) {
            if (date2 == null) range = "";
            else range = "DATETIME(TimeStamp) = \"" + date2 + "\"";
        } else {
            if (date2 == null) range = "DATETIME(TimeStamp) = \"" + date1 + "\"";
            else range = "DATETIME(TimeStamp) >= \"" + date1 + "\" " +
                    "AND DATETIME(TimeStamp) <= \"" + date2 + "\"";
        }

        String query = "select " +
                "labels.ID AS \"Sensor ID\", " +
                "labels.sensorName AS \"Sensor\", " +
                "labels.sensorUnits AS \"Units\", " +
                "labels.dataType AS \"Data Type\", " +
                "labels.system AS \"System\", " +
                "data.value AS \"Value\", " +
                "data.TimeStamp AS \"TimeStamp\" " +
                "from SensorLabels AS labels " +
                "INNER JOIN Data AS data ON labels.ID=data.sensorID" +
                whereC + idRange + andC + sys + and2C + range + ";";

        System.out.println(query + "\n\n");
//    return null;
        return runQuery(query);
    }

    private String parseCSV(String csv) {
        String out = "";
        Scanner sc = new Scanner(csv);
        sc.useDelimiter(",");
        while (sc.hasNext()) {
            out += "\"" + sc.next() + "\"";
            if (sc.hasNext()) out += ", ";
        }
        return out;
    }

    private String parseSystems(String systems) {
        if (systems == null) return "";
        Scanner sc = new Scanner(systems);
        sc.useDelimiter(",");
        String out = "labels.system IN (";
        while (sc.hasNext()) {
            out += "\"" + sc.next() + "\"";
            if (sc.hasNext()) out += ", ";
        }
        return out + ")";
    }

    public void insertData(String ID, String value) {
        String sql = "INSERT INTO Data (sensorID, value) VALUES (\"" + ID + "\",\"" + value + "\");";

        runSQL(sql);
    }

    public void insertData(String IDVals) {
        if (IDVals == null) return;
        String sql = "INSERT INTO Data (sensorID, value) VALUES " + IDVals + ";";
        runSQL(sql);
    }

    /*************************************************************************************************
     * Configuration Functions
     *************************************************************************************************/

    public ArrayList<ArrayList<String>> getIDNames() {
        String sql = "select ID, sensorName from SensorLabels;";
        return runQuery(sql);
    }

    public ArrayList<ArrayList<String>> getIDs() {
        String sql = "select ID from SensorLabels;";
        return runQuery(sql);
    }

    public ArrayList<ArrayList<String>> getSensorInfo() {
        String sql = "select l.*, c.* from SensorLabels AS l INNER JOIN Configurations AS c ON l.ID=c.sensorID;";
        return runQuery(sql);
    }

    public void updateSensor(Boolean isNew, String cID, String sName, String sUnits, String sys,
                             String sLow, String sHigh, String cLow, String cHigh,
                             String crit, String slope, String off) {

        String sql1 = "";
        String sql2 = "";

        if (isNew) {
            sql1 = "INSERT INTO SensorLabels " +
                    "(ID, sensorName, sensorUnits, system) VALUES " +
                    "('" + cID + "', '" + sName + "', '" + sUnits + "', '" + sys + "');";

            sql2 = "INSERT INTO Configurations " +
                    "(sensorID, stableLow, stableHigh, criticalLow, criticalHigh, criticality, slope, offset) VALUES " +
                    "('" + cID + "', '" + sLow + "', '" + sHigh + "', '" + cLow + "', '" +
                    cHigh + "', '" + crit + "', '" + slope + "', '" + off + "');";
        } else {
            sql1 = "UPDATE SensorLabels SET " +
                    "sensorName='" + sName + "', sensorUnits='" + sUnits + "', system='" + sys + "' " +
                    "WHERE ID='" + cID + "';";

            sql2 = "UPDATE Configurations SET " +
                    "stableLow='" + sLow + "', stableHigh='" + sHigh + "', " +
                    "criticalLow='" + cLow + "', criticalHigh='" + cHigh + "', criticality='" + crit + "', " +
                    "slope='" + slope + "', offset='" + off + "' WHERE sensorID='" + cID + "';";
        }

        runSQL(sql1);
        runSQL(sql2);
    }

    public void removeSensor(String sensorID){
        String sql = "DELETE FROM SensorLabels WHERE ID=\'" + sensorID + "\'";
        runSQL(sql);
    }

/*
  public ArrayList<ArrayList<String>> listSensors(String expr){
    String sql = "select sensorName, sensorUnits from SensorLabels " +
            "WHERE sensorName LIKE \'" + expr + "%\' " +
            "ORDER BY sensorName COLLATE NOCASE";

    return runQuery(sql);
  }

  public Boolean checkSensorName(String name){
    String sql = "select sensorName from SensorLabels " +
            "WHERE sensorName = \"" + name + "\"";
    ArrayList<ArrayList<String>> result = runQuery(sql);
    for(ArrayList<String> inner : result){
      if(inner.size() != 0) return false;
    }
    return true;
  }

  public void addSensor(String id, String sensorName, String sensorUnits, String sensorDataType, String sensorSys){
//    if(checkSensorName(sensorName)){

    String sql = "INSERT INTO SensorLabels " +
            "(id, sensorName, sensorUnits, dataType, system) VALUES (\"" + id + "\", \"" +
            sensorName + "\", \"" + sensorUnits + "\", \"" + sensorDataType + "\", \"" + sensorSys + "\") ";
    runSQL(sql);
//    }
//    else{
//      System.out.println("Sorry, a sensor with this name already exists!");
//    }
  }

  public void updateSensorName(String sensorName, String newName){
    String sql = "UPDATE SensorLabels " +
            "SET sensorName=\'" + newName + "\' " +
            "WHERE sensorName=\'" + sensorName + "\';";

    runSQL(sql);
  }

  public void updateSensorUnits(String sensorName, String newUnits){
    String sql = "UPDATE SensorLabels " +
            "SET sensorUnits=\'" + newUnits + "\' " +
            "WHERE sensorName=\'" + sensorName + "\';";

    runSQL(sql);
  }

  public void updateSensorDataType(String sensorName, String newType){
    String sql = "UPDATE SensorLabels " +
            "SET dataType=\'" + newType + "\' " +
            "WHERE sensorName=\'" + sensorName + "\';";

    runSQL(sql);
  }

  public void removeSensor(String sensorName){
    String sql = "DELETE FROM SensorLabels WHERE sensorName=\'" + sensorName + "\'";
    runSQL(sql);
  }*/

}
