//import VSCADAComp.*;
//import java.util.ArrayList;

public class DBTest{

  public static void main(String[] args){
//    System.out.println("Hello World!");
    
    DBHandler handler = new DBHandler();
    handler.readSQLFile("../SQLSchema/test.sql");
//    
//    handler.createTable("AustinTable");
//    handler.insert("Christer", "Not SCADA", (float) 234.25);
//    handler.insert("Craig", "SCADA", (float) 1728);
//    for(int i=0; i<1000; i++) handler.insert("Austin", "SCADA", (float) 1.56);
//    
//    System.out.println(handler.getSystem("Craig"));
//    handler.writeFile(handlrm er.getTable(),"tmp.csv");
//    
//    handler.setHeaders("Sysasdasd", null, "Val", null);//"TimeStamp");
//    handler.viewTable();
//    
//    System.out.println();
//    handler.viewDate("2017-02-05");
    
    
    /*TSV test1 = new TSV();
     TSI test2 = new TSI();
     Dyno test3 = new Dyno();
     Vehicle test4 = new Vehicle();
     
     System.out.println(Parser.parse(test1));cd 
     System.out.println(Parser.parse(test2));
     System.out.println(Parser.parse(test3));
     System.out.println(Parser.parse(test4));
     
     */
    
//    System.out.println(handler.getDate("2017-02-09"));
    
//    handler.closeDB();
    
  }
  
}