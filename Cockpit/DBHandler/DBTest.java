public class DBTest{
  
  public static void main(String[] args){
//    System.out.println("Hello World!");
    
    DBHandler handler = new DBHandler();
    
    handler.createTable();
//    handler.insert("Christer", "Not SCADA", (float) 234.25);
//    handler.insert("Craig", "SCADA", (float) 1728);
//    handler.insert("Austin", "SCADA", (float) 1.56);
    
    System.out.println(handler.getSystem("Craigwq"));
//    handler.writeFile(handler.getTable(),"tmp.csv");
//    
//    handler.setHeaders("Sysasdasd", null, "Val", null);//"TimeStamp");
//    handler.viewTable();
//    
//    System.out.println();
//    handler.viewDate("2017-02-05");
    handler.closeDB();
  }
  
}