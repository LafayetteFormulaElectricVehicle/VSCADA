package cockpit.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

public class DBHandler {

  private Connection c;
  private String dbName;

  private String sysHeader;
  private String itemHeader;
  private String valueHeader;
  private String timeHeader;

  private String schemaPath = "SQLSchema/";

  public DBHandler(){
    c = null;
    dbName = "SCADA.db";
    connectDB();
  }

  public DBHandler(String name, String sPath){
    c = null;
    dbName = name;
    if(sPath!= null) schemaPath = sPath;
    connectDB();
  }

  public void connectDB(){
    c = null;

    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:"+ dbName);
      checkDB();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  public void checkDB(){
    if(!checkExists("SensorLabels")){
      readSQLFile(schemaPath + "SensorLabels.sql");
      readSQLFile(schemaPath + "Config.sql");
    }
    readSQLFile(schemaPath + "Data.sql");
    readSQLFile(schemaPath + "ErrorMessages.sql");
  }

  public Boolean checkExists(String table){
    return getTable(table) != null;
  }

  public ArrayList<ArrayList<String>> getTable(String table){
    String query = "select * from " + table;
    return runQuery(query);
  }

  public void readSQLFile(String fileName){
    Statement stmt = null;
    String sql = "";

    try{
      Scanner sc = new Scanner(new File(fileName));
      stmt = c.createStatement();
      while (sc.hasNextLine())
      {
        sql += sc.nextLine();
      }
      stmt.executeUpdate(sql);
    } catch (SQLException e ) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    catch(Exception e){
    }
  }

  public ArrayList<String> getSchema(String fileName){
    ArrayList<String> out = new ArrayList<String>();
    try{
      Scanner sc = new Scanner(new File(fileName));
      sc.nextLine();
      while (sc.hasNextLine())
      {
        out.add(sc.next());
        sc.nextLine();
      }
    }
    catch(Exception e){
    }
    return out;
  }

  public void closeDB(){
    try {
      if (c.isClosed()) System.out.println("No open DB");
      else c.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  public void runSQL(String sql){
    Statement stmt = null;

    try {
      stmt = c.createStatement();
      stmt.execute(sql);
      stmt.close();
    } catch (SQLException e ) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  private ArrayList<ArrayList<String>> runQuery(String query){
    Statement stmt = null;
    try {
      stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      return getResultsTable(rs);
    }
    catch (SQLException e ) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return null;
    }
  }

  private ArrayList<ArrayList<String>> getResultsTable(ResultSet rs){
    ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();
    ArrayList<String> inner;

    try{
      ResultSetMetaData md = rs.getMetaData();
      int columns = md.getColumnCount();
      while (rs.next()) {
        inner = new ArrayList<String>();
        for (int i = 1; i <= columns; i++) {
          inner.add(rs.getString(i));
        }
        output.add(inner);
      }
    }
    catch(java.sql.SQLException e){
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return null;
    }
    return output;
  }

  //date in the form of 2017-02-05
  public ArrayList<ArrayList<String>> getInfo(String systems, String date1, String date2){

    String whereC;
    String andC;
    String sys;
    String range;

    whereC = (systems != null || date1 != null || date2 != null) ? "WHERE " : "";
    andC = (systems != null && (date1 != null || date2 != null)) ? " AND " : "";

    sys = parseSystems(systems);

    if(date1 == null){
      if(date2 == null) range = "";
      else range = "DATE(TimeStamp) = \"" + date2 + "\"";
    }
    else{
      if(date2 == null) range = "DATE(TimeStamp) = \"" + date1 + "\"";
      else range = "DATE(TimeStamp) >= \"" + date1 + "\" " +
        "AND DATE(TimeStamp) <= \"" + date2 + "\"";
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
      "INNER JOIN Data AS data ON labels.ID=data.sensorID " +
      whereC + sys + andC + range + ";";

    // System.out.println(query);
    return runQuery(query);
  }

  private String parseSystems(String systems){
    if(systems == null) return "";
    Scanner sc = new Scanner(systems);
    sc.useDelimiter(",");
    String out = "labels.system IN (";
    while(sc.hasNext()){
      out += "\"" + sc.next() + "\"";
      if(sc.hasNext()) out += ", ";
    }
    return out + ")";
  }

  /*************************************************************************************************
    * Configuration Functions
    *************************************************************************************************/

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

  public void addSensor(String sensorName, String sensorUnits, String sensorDataType, String sensorSys){
    if(checkSensorName(sensorName)){

      String sql = "INSERT INTO SensorLabels " +
        "(sensorName, sensorUnits, dataType, system) VALUES (\"" +
        sensorName + "\", \"" + sensorUnits + "\", \"" + sensorDataType + "\", \"" + sensorSys + "\") ";

      runSQL(sql);
    }
    else{
      System.out.println("Sorry, a sensor with this name already exists!");
    }
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
  }


  /*************************************************************************************************
    * Old Functions
    *************************************************************************************************/

//  private String getResults(ResultSet rs){
//
//    String output = "";
//    try{
//      while (rs.next()) {
//        output += rs.getString("sensorName") + "\n";
//      }
//    }
//    catch (SQLException e ){
//      System.err.println(e.getClass().getName() + ": " + e.getMessage());
//    }
//    return output;
//  }

//  public void writeFile(String myString, String file){
//    Scanner scanner = new Scanner(myString);
//
//    try{
//      PrintWriter writer = new PrintWriter(file, "UTF-8");
//      while (scanner.hasNextLine()) {
//        writer.println(scanner.nextLine());
//      }
//
//      writer.close();
//    } catch (IOException e){}
//    scanner.close();
//  }

}
