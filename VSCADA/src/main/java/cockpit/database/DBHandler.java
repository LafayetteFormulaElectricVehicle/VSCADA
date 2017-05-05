package cockpit.database;

import javax.swing.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * <h1>DBHandler</h1>
 * This class will serve as the best means for accessing the DB without the need to know SQL
 *
 * @author Craig Lombardo
 * @version 1.0
 * @since 2017-02-11
 */
public class DBHandler {

    private Connection c;
    private String dbName = "SCADA.db";

    private String schemaPath = "SQLSchema/";

    /**
     * The general constructor for the DBHandler, looks for (or creates) SCADA.db
     */
    public DBHandler() {
        c = null;
        connectDB();
    }

    private void connectDB() {
        c = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbName);
            checkDB();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private void checkDB() {
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

    private Boolean checkExists(String table) {
        return getTable(table) != null;
    }

    private ArrayList<ArrayList<String>> getTable(String table) {
        String query = "select * from " + table;
        return runQuery(query);
    }

    private void readSQLFile(String fileName) {
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

    private ArrayList<String> getSchema(String fileName) {
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

    private void closeDB() {
        try {
            if (c.isClosed()) System.out.println("No open DB");
            else c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private void runSQL(String sql) {
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

    /**
     * This function will allow for insertion of a single data entry
     * @param ID of the sensor
     * @param value of the sensor
     */
    public void insertData(String ID, String value) {
        String sql = "INSERT INTO Data (sensorID, value) VALUES (\"" + ID + "\",\"" + value + "\");";

        runSQL(sql);
    }

    /**
     * This function will allow for insertion of a multiple data entries. The format is as follows:
     * (id1,val1), (id2, val2), ... (idn, valn)
     * @param IDVals The data to be inserted into the DB
     */
    public void insertData(String IDVals) {
        if (IDVals == null) return;
        String sql = "INSERT INTO Data (sensorID, value) VALUES " + IDVals + ";";
        runSQL(sql);
    }

    /*************************************************************************************************
     * Configuration Functions
     *************************************************************************************************/

    /**
     * This function gets all information from the SensorLabels table.
     * @return ArrayList&lt;ArrayList&lt;String&gt;&gt; All the information from the table.
     */
    public ArrayList<ArrayList<String>> getSensorInfo() {
        String sql = "select * from SensorLabels ORDER BY tag ASC;";
        return runQuery(sql);
    }

    /**
     * This method returns imformation on tags that have previously been entered into the DB.
     * @param tags the human readable tags that you would like to search for
     * @return ID, description, units, tag in ascending order
     */
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

    /**
     * This method gets a list of all ID's in the DB
     * @return ArrayList&lt;ArrayList&lt;String&gt;&gt; containing the ID's of all sensors (just IDs)
     */
    public ArrayList<ArrayList<String>> getIDs() {
        String sql = "select ID from SensorLabels;";
        return runQuery(sql);
    }

    /**
     * This method returns all information from the SensorLabels table.
     * If you change the database this method will fail you as it was used directly by SCADASystem.createAllSensors()
     * @return ArrayList&lt;ArrayList&lt;String&gt;&gt; of all info in SensorLabels table
     */
    public ArrayList<ArrayList<String>> getSensorcharacterization() {
        String sql = "select * from SensorLabels;";
        return runQuery(sql);
    }

    /**
     * This method will remove a sensor from the DB.
     * @param tag The tag of the sensor you would like to remove.
     */
    public void removeSensor(String tag) {
        String sql = "DELETE FROM SensorLabels WHERE tag=\'" + tag + "\'";
        runSQL(sql);
    }

    /**
     * This method will update the information of the sensor selected. Format is as follows, data[0]-&gt;data[8] respectively:
     * (tag, address, offset, byteLength, description, system, units, store, correction)
     * @param isNew Whether or not this is a new entry or one to edit
     * @param data THe data in the aforementioned format.
     */
    public void updateSensor(Boolean isNew, String[] data) {
//        System.out.println(data.length);
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
//        System.out.println(sql1);

        runSQL(sql1);

    }

    /**
     * This method will return the most recent data put into the DB. The seconds parameter will allow a user to select
     * the amount of time they want to go back for data. i.e. using 5 seconds will yield the last 5 seconds of data.
     * @param seconds The number of seconds before the last data time.
     * @return ArrayList&lt;ArrayList&lt;String&gt;&gt; tag, description, system, units, value, timestamp
     */
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

    /**
     * This method will return information from a date range (or all if you don't specify a start/end time. All
     * parameters of the same type are OR based and those of different types are AND based so be cautious if
     * you are looking for things across multiple criteria ranges. i.e. it returns items from any tag in the list
     * which are from any of the selected systems which are within the date range. To specify you want <b>ANY</b>
     * tag/system or date, simply input null.
     * @param tags The tag(s) of the sensors we desire
     * @param systems The system(s) of the sensors we want
     * @param date1 Start date in same format as 2017-04-09 23:29:58
     * @param date2 End date in same format as 2017-04-09 23:29:58
     * @return ArrayList&lt;ArrayList&lt;String&gt;&gt; tag, description, system, units, value, timestamp
     */
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

    /**
     * This method returns all of the equations/destinations from the DB.
     * @return ArrayList&lt;ArrayList&lt;String&gt;&gt; Each ArrayList is formattion as such: (destination,equation)
     */
    public ArrayList<ArrayList<String>> getEquations() {
        String sql = "SELECT destination, equation FROM Equations ORDER BY equationOrder ASC;";
        return runQuery(sql);
    }

    /**
     * This method updates equations in the DB. REMEMBER, the equations follow a left to right order of
     * operations <b>NOT PEMDAS</b>. You may use + - / * for normal functions and equations may contain
     * a mixture of valid tags and numbers (decimals are cool).
     * @param destinations The destination of the equation
     * @param equations The equation to be used when evaluation
     */
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
        runSQL(sql);
    }

}
