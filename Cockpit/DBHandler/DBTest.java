public class DBTest{

  public static void main(String[] args){
    System.out.println("Hello World!");
    
    DBHandler handler = new DBHandler();
    
    handler.createTable();
//    handler.insert("Christer", "Not SCADA", (float) 234.25);
  }

}