//import VSCADAComp.*;
import java.util.ArrayList;

public class DBTest{
  
  public static void main(String[] args){
//    System.out.println("Hello World!");
    
    DBHandler handler = new DBHandler("../SCADA.db");
    
    ArrayList<ArrayList<String>> out = handler.listSensors("cell");
    
    for(ArrayList<String> inner : out){
      for(String s : inner){
        System.out.print(s + " - ");
      }
      System.out.println();
    }
    
//    handler.addSensor("Hello", "World");
//    handler.updateSensorName("Hello", "Goodbye");
//    handler.updateSensorUnits("Goodbye", "foo");
//    handler.removeSensor("Goodbye");
    
//    System.out.println(handler.getAllSensors());
//    
//    handler.addSensor("Hello", "World!");
//    
//    System.out.println(handler.getAllSensors());
    
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
    
//    System.out.println(handler.getDate("2017-02-09"));
    
//    handler.closeDB();
    
  }
  
}