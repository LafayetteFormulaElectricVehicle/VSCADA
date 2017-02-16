import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;
//import java.util.ArrayList;

//.read example.sql 

public class DBHandler {
  
  private Connection c;
  private String dbName;
  
  private String sysHeader;
  private String itemHeader;
  private String valueHeader;
  private String timeHeader;
  
  public DBHandler() {
    c = null;
    
    
    dbName = "SCADA.db";
    connectDB();
  }
  
  public DBHandler(String name) {
    c = null;
    
    dbName = name;
    connectDB();
  }
  
  public void insert(String sys, String item, float value) {
    String sql = "INSERT INTO System" +
      "(System, Item, Value) VALUES (\"" +
      sys + "\", \"" + item + "\", \"" + value + "\") ";
    
    runSQL(sql);
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
      readSQLFile("../SQLSchema/SensorLabels.sql");
      readSQLFile("../SQLSchema/Config.sql");
    }
    readSQLFile("../SQLSchema/ErrorMessages.sql");
  }
  
  public Boolean checkExists(String table){
    return getTable(table) != null;
  }
  
  public void readSQLFile(String fileName){
    Statement stmt = null;
    String sql = "";

    try{
      Scanner sc = new Scanner(new File(fileName));
      stmt = c.createStatement();
      
//      BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
      
      while (sc.hasNextLine())
      {
        sql += sc.nextLine();
      }
      
//      bufferedReader.close();
      stmt.executeUpdate(sql);
    } catch (SQLException e ) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    catch(Exception e){
    }
  }
  
//  public ArrayList<String> getSchema(String fileName){
//    ArrayList<String> out = new ArrayList<String>();
//    try{
//      Scanner sc = new Scanner(new File(fileName));
//      sc.nextLine();
//      while (sc.hasNextLine())
//      {
//        out.add(sc.next());
//        sc.nextLine();
//      }
//    }
//    catch(Exception e){
//    }
//    return out;
//  }
  
  public void closeDB(){
    try {
      if (c.isClosed()) System.out.println("No open DB");
      else c.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }
  
  public String getTable(String table){
    String query = "select * from " + table;
    return runQuery(query);
  }
  
  //date in the form of 2017-02-05
  public String getDate(String table, String date){
    String query = "select * from " + table + " where DATE(TimeStamp) = \'" + date + "\'";
    return runQuery(query);
  }
  
  public String getSystem(String table, String sys){
    String query = "select * from " + table + " where System = \'" + sys + "\'";
    return runQuery(query);
  }
  
  private void runSQL(String sql){
    Statement stmt = null;
    
    try {
      stmt = c.createStatement();
      stmt.execute(sql);
      stmt.close();
    } catch (SQLException e ) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }
  
  private String runQuery(String query){
    
    Statement stmt = null;
    
    try {
      stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      stmt.close();
      return getResults(rs);
    } catch (SQLException e ) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return null;
    }
  }
  
  private String getResults(ResultSet rs){
    String output = sysHeader + "," + itemHeader +
      "," + valueHeader + "," + timeHeader + "\n";
    try{
      while (rs.next()) {
        String sys = rs.getString("System");
        String item = rs.getString("Item");
        float value = rs.getFloat("Value");
        String time = rs.getString("TimeStamp");
        output = output + sys + "," + item +
          "," + value + "," + time + "\n";
      }
    }
    catch (SQLException e ){
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    return output;
  }
  
  public void writeFile(String myString, String file){
    Scanner scanner = new Scanner(myString);
    
    try{
      PrintWriter writer = new PrintWriter(file, "UTF-8");
      while (scanner.hasNextLine()) {
        writer.println(scanner.nextLine());
      }
      
      writer.close();
    } catch (IOException e){}
    scanner.close();
  }
  
}