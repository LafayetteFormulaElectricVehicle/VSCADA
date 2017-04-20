package cockpit.database;

import javax.swing.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DBHandler {

    private Connection c;
    private String dbName = "SCADA.db";

    private String schemaPath = "SQLSchema/";

    public DBHandler() {
        c = null;
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
            readSQLFile(schemaPath + "CustomSensorSetup.sql");
        }
        readSQLFile(schemaPath + "Data.sql");
        readSQLFile(schemaPath + "ErrorMessages.sql");
        if (!checkExists("Equations")) {
            readSQLFile(schemaPath + "Equations.sql");
            readSQLFile(schemaPath + "EquationSetup.sql");
        }
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
//            System.err.println(e.getClass().getName() + ": " + e.getMessage());
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
        } catch (java.sql.SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        return output;
    }

    //date in the form of 2017-02-05
   /* public ArrayList<ArrayList<String>> getInfo(String IDs, String systems, String date1, String date2) {

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
    }*/

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

    public void insertDataTimes(String data) {
        if (data == null) return;
        String sql = "INSERT INTO Data (sensorID, value, TimeStamp) VALUES " + data + ";";
        runSQL(sql);
    }

    /*************************************************************************************************
     * Configuration Functions
     *************************************************************************************************/

    public ArrayList<ArrayList<String>> getSensorInfo() {
        String sql = "select * from SensorLabels ORDER BY tag ASC;";
        return runQuery(sql);
    }

    public ArrayList<ArrayList<String>> getIDDescUnitsTag(String[] tags) {
        String tagSearch = "";

        if (tags != null) {
            tagSearch += "WHERE tag in (";
            for (int i = 0; i < tags.length; i++) {
                tagSearch += "'" + tags[i] + "'";
                if (i < tags.length - 1) tagSearch += ", ";
            }
            tagSearch += ")";
        }
        String sql = "select ID, description, units, tag from SensorLabels " + tagSearch + " ORDER BY tag ASC;";
        return runQuery(sql);
    }

    public ArrayList<ArrayList<String>> getIDs() {
        String sql = "select ID from SensorLabels;";
        return runQuery(sql);
    }

    //If you change the database this method will fail you
    public ArrayList<ArrayList<String>> getSensorcharacterization() {
        String sql = "select * from SensorLabels;";
        return runQuery(sql);
    }

    public void removeSensor(String tag) {
        String sql = "DELETE FROM SensorLabels WHERE tag=\'" + tag + "\'";
        runSQL(sql);
    }

    public void updateSensor(Boolean isNew, String[] data) {
        System.out.println(data.length);
        for (String s : data) System.out.println("'" + s + "'");

        if (data.length != 9) return;
        String sql1 = "";

        if (isNew) {
            sql1 = "INSERT INTO SensorLabels (tag, address, offset, byteLength, description, " +
                    "system, units, store, correction) VALUES " +
                    "('" + data[0] + "', '" + data[1] + "', '" + data[2] + "', '" + data[3] + "', '" +
                    data[4] + "', '" + data[5] + "', '" + data[6] + "', '" +
                    (data[7].equals("true") ? 1 : 0) + "', '" + data[8] + "');";
        } else {
            sql1 = "UPDATE SensorLabels SET " +
                    "tag='" + data[0] + "', " +
                    "address='" + data[1] + "', " +
                    "offset='" + data[2] + "', " +
                    "byteLength='" + data[3] + "', " +
                    "description='" + data[4] + "', " +
                    "system='" + data[5] + "', " +
                    "units='" + data[6] + "', " +
                    "store='" + (data[7].equals("true") ? 1 : 0) + "', " +
                    "correction='" + data[8] + "', " +
                    "WHERE tag='" + data[0] + "';";
        }

//        System.out.println(sql1);
        System.out.println(sql1);

        runSQL(sql1);

    }

    public ArrayList<ArrayList<String>> getLatestData(int seconds) {
/*      tag
        description
        system
        units
        value
        timestamp
*/
        String selects = "labels.tag, labels.description, labels.system, labels.units, data.value, data.TimeStamp";
        String tables = "SensorLabels AS labels " +
                "INNER JOIN Data AS data ON labels.ID=data.sensorID";

        String query = "";
        if (seconds == 1)
            query = "select " + selects + " from " + tables + " where TimeStamp = (select MAX(TimeStamp) from Data);";
        else {
            query = "select " + selects + " from " + tables + " where TimeStamp <= (select MAX(TimeStamp) from Data)" +
                    "AND TimeStamp >= datetime((select MAX(TimeStamp) from Data), '-" + seconds + " seconds');";
        }

//        System.out.println(query + "\n\n");
        return runQuery(query);
    }

    //Dates in format of 2017-04-09 23:29:58
    public ArrayList<ArrayList<String>> getInfo(String tags, String systems, String date1, String date2) {

        String whereC;
        String andC;
        String and2C;

        String sys;
        String range;
        String idRange;

        whereC = (systems != null || date1 != null || date2 != null || tags != null) ? " WHERE " : "";
        andC = (tags != null && systems != null) ? " AND " : "";
        // andC = (IDs != null && (systems != null || date1 != null || date2 != null)) ? " AND " : "";
        and2C = ((tags != null || systems != null) && (date1 != null || date2 != null)) ? " AND " : "";

        sys = parseSystems(systems);

        if (tags != null) idRange = "labels.tag IN (" + parseCSV(tags) + ")";
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
                "labels.tag, " +
                "labels.description, " +
                "labels.system, " +
                "labels.units, " +
                "data.value, " +
                "data.TimeStamp " +
                "from SensorLabels AS labels " +
                "INNER JOIN Data AS data ON labels.ID=data.sensorID" +
                whereC + idRange + andC + sys + and2C + range + " ORDER BY labels.tag ASC;";

//        System.out.println(query + "\n\n");
        return runQuery(query);
    }

    public ArrayList<ArrayList<String>> getEquations() {
        String sql = "SELECT destination, equation FROM Equations ORDER BY equationOrder ASC;";
        return runQuery(sql);
    }

    public void updateEquations(ArrayList<JTextField> destinations, ArrayList<JTextField> equations) {
        String sql = "delete from Equations;";
        runSQL(sql);

        sql = "INSERT INTO Equations (equationOrder, destination, equation) VALUES ";

        int size = destinations.size();

        String dest;
        String eqs;

        for (int i = 0; i < size; i++) {
            dest = destinations.get(i).getText();
            eqs = equations.get(i).getText();
            if (!dest.equals("") && !eqs.equals("")) {
                sql += "('" + i + "', '" + dest + "', '" + eqs + "')";
                if (i < size - 1) sql += ",";
                else sql += ";";
            }
        }
//        System.out.println(sql);
        runSQL(sql);
    }

}
