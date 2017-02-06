import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.IOException;

public class DBHandler {
  
  private Statement stmt;
  private Connection c;
  private String dbName;
  
  private String sysHeader;
  private String itemHeader;
  private String valueHeader;
  private String timeHeader;
  
  public DBHandler() {
    stmt = null;
    c = null;
    
    setHeaders("System", "Item", "Value", "TimeStamp");
    
    dbName = "SCADA.db";
    connectDB();
  }
  
  public DBHandler(String name) {
    stmt = null;
    c = null;
    
    setHeaders("System", "Item", "Value", "TimeStamp");
    
    dbName = name;
    connectDB();
  }
  
  public void createTable() {
    stmt = null;
    try {
      stmt = c.createStatement();
      
      String sql = "CREATE TABLE IF NOT EXISTS System(" +
        "ID Integer PRIMARY KEY AUTOINCREMENT," +
        "System varchar(255)NOT NULL," +
        "Item varchar(255)NOT NULL," +
        "Value FLOAT NOT NULL," +
        "TimeStamp DATE DEFAULT(datetime('now', 'localtime')))";
      
      stmt.executeUpdate(sql);
      stmt.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }
  
  public void insert(String sys, String item, float value) {
    stmt = null;
    try {
      stmt = c.createStatement();
      
      String sql = "INSERT INTO System" +
        "(System, Item, Value) VALUES (\"" +
        sys + "\", \"" + item + "\", \"" + value + "\") ";
      stmt.executeUpdate(sql);
      stmt.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }
  
  public void connectDB(){
    c = null;
    
    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:"+ dbName);
//      System.out.println("Opened " + dbName + " successfully");
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
//      System.exit(0);
    }
  }
  
  public void closeDB() {
    try {
      if (c.isClosed()) System.out.println("No open DB");
      else c.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }
  
  public String getTable(){
    String query = "select * from System";
    return runQuery(query);
  }
  
  //date in the form of 2017-02-05
  public String getDate(String date){
    String query = "select * from System where DATE(TimeStamp) = \'" + date + "\'";
    return runQuery(query);
  }
  
  public String getSystem(String sys){
    String query = "select * from System where System = \'" + sys + "\'";
    return runQuery(query);
  }
  
  private String runQuery(String query){
    
    Statement stmt = null;
    
    try {
      stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery(query);
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
    catch (SQLException e ) {
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
  
  public void setHeaders(String sys, String item, String value, String time){
    if(sys != null) sysHeader = sys;
    if(item != null) itemHeader = item;
    if(value != null) valueHeader = value;
    if(time != null) timeHeader = time;
  }
  
}