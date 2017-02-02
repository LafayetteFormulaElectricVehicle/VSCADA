import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHandler {
  
  private Statement stmt;
  private Connection c;
  private String dbName;
  
  public DBHandler() {
    stmt = null;
    c = null;
    dbName = "SCADA.db";
    connectDB();
  }
  
  public DBHandler(String name) {
    stmt = null;
    c = null;
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







  public void viewTable() throws SQLException {
      
      Statement stmt = null;
      
      String query =
        "select System, Item, Value, " +
        "from System";
      
      try {
        stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
          String coffeeName = rs.getString("COF_NAME");
          int supplierID = rs.getInt("SUP_ID");
          float price = rs.getFloat("PRICE");
          int sales = rs.getInt("SALES");
          int total = rs.getInt("TOTAL");
          System.out.println(coffeeName + "\t" + supplierID +
                             "\t" + price + "\t" + sales +
                             "\t" + total);
        }
      } catch (SQLException e ) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
      } finally {
        if (stmt != null) { stmt.close(); }
      }
    }
  }