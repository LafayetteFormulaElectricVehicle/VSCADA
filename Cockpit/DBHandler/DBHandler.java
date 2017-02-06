import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
//      System.exit(0);
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
//      System.exit(0);
    }
  }
  
  public void connectDB(){
    c = null;
    
    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:"+ dbName);
      System.out.println("Opened " + dbName + " successfully");
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
//      System.exit(0);
    }
  }
  
  public void viewTable(){
    
    Statement stmt = null;
    
    String query = "select * from System";
    
    try {
      stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      printResults(rs);
    } catch (SQLException e ) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    } 
  }
  
  //date in the form of 2017-02-05
  public void viewDate(String date){
    
    Statement stmt = null;
    
    String query =
      "select * from System where DATE(TimeStamp) = \'" + date + "\'";
    
    try {
      stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      printResults(rs);
    } catch (SQLException e ) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    } 
  }
  
  public void printResults(ResultSet rs){
    System.out.println(sysHeader + "\t" + itemHeader +
                       "\t" + valueHeader + "\t" + timeHeader);
    try{
      while (rs.next()) {
        String sys = rs.getString("System");
        String item = rs.getString("Item");
        float value = rs.getFloat("Value");
        String time = rs.getString("TimeStamp");
        
        System.out.println(sys + "\t" + item +
                           "\t" + value + "\t" + time);
      }
    }
    catch (SQLException e ) {}
  }
  
  public void setHeaders(String sys, String item, String value, String time){
    sysHeader = sys;
    itemHeader = item;
    valueHeader = value;
    timeHeader = time;
  }
  
}