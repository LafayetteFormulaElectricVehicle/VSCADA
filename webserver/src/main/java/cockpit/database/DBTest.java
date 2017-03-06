package cockpit.database;

//import VSCADAComp.Subsystem;
//import VSCADAComp.Parser;

/* Compiling
javac -cp ".:DBHandler/sqlite-jdbc-3.16.1.jar:VSCADAComp/gson-2.2.2.jar"
  Configuration/*.java DBHandler/DBHandler.java
  DBHandler/DBTest.java VSCADAComp/*.java -d exec/
*/
import java.util.ArrayList;

public class DBTest{

  public static void main(String[] args){
    DBHandler handler = new DBHandler("../SCADA.db","../SQLSchema/");
    SCADASystem scada = new SCADASystem(handler);
    scada.addSensor("1");
    scada.addSensor("2");
    scada.addSensor("3");
    scada.addSensor("4");
    scada.addSensor("5");
    scada.addSensor("6");
    scada.addSensor("7");
    scada.addSensor("8");
    scada.addSensor("9");
    scada.addSensor("10");

    scada.openCAN();
  }


//  public static void main(String[] args){
//    DBHandler handler = new DBHandler("SCADA.db","SQLSchema/");
//
//    SCADASystem s = new SCADASystem(handler);
//    s.addSensor("1");
//    s.addSensor("8");
//    s.addSensor("5");
//    s.addSensor("2");
//
//    s.parse();
//
//    s.updateValue("1","a");
//    s.updateValue("2,b");
//    s.updateMultValues("5,c,8,d");
//
//    s.parse();
//
//    ArrayList<ArrayList<String>> out; // = handler.getInfo("1,2,3", null, "2017-02-26", "2017-02-28");
//    out = handler.getInfo(null, null, null, null);
//    par(out);
//    out = handler.getInfo("1,2,5", null, null, null);
//    par(out);
//    out = handler.getInfo("1,2,5", "TSV", null, null);
//    par(out);
//    out = handler.getInfo("1,2,5", "TSV", "2017-03-05 22:46:50", null);
//    par(out);
//    out = handler.getInfo("1,2,5", "TSV", "2017-03-05 22:46:50", "2017-03-05 22:48:35");
//    par(out);
//
//    out = handler.getInfo(null, "TSV", null, null);
//    par(out);
//    out = handler.getInfo(null, "TSV", "2017-03-05 22:46:50", null);
//    par(out);
//    out = handler.getInfo(null, "TSV", "2017-03-05 22:46:50", "2017-03-05 22:48:35");
//    par(out);
//
////      par(out);
//
//    ArrayList<String> id = new ArrayList<String>();
//    ArrayList<String> val = new ArrayList<String>();
//    id.add("1");
//    id.add("2");
//    id.add("5");
//    id.add("8");
//
//    val.add("e");
//    val.add("f");
//    val.add("g");
//    val.add("h");
//
//    s.updateMultValues(id, val);
//
//    s.parse();
//
////      s.writeToDB();
//  }




  /*public static void main(String[] args){
   //    System.out.println("Hello World!");

   DBHandler handler = new DBHandler("../SCADA.db","../SQLSchema/");
   //
   ArrayList<ArrayList<String>> out; // = handler.getInfo("2017-02-26", "2017-02-28");

   //    out = handler.getInfo(null,"2017-02-26", "2017-02-28");
   //    par(out);
   //    out = handler.getInfo("TSV", "2017-02-26", "2017-02-28");
   //    par(out);
   //    out = handler.getInfo("TSV",null, "2017-02-28");
   //    par(out);
   //    out = handler.getInfo("TSV",null, null);
   //    par(out);
   //    out = handler.getInfo("TSV,DYNO",null, null);
   //    par(out);
   //    out = handler.getInfo("TSV,DYNO", "2017-02-27", null);
   //    par(out);
   out = handler.getInfo(null,null, null);
   par(out);


   //    String sql = "INSERT INTO Data (sensorID, value) VALUES (\"24\",\"9000\")";
   //    handler.runSQL(sql);
   //
   //    sql = "INSERT INTO Data (sensorID, value) VALUES (\"18\",\"bar\")";
   //    handler.runSQL(sql);
   //
   //    sql = "INSERT INTO Data (sensorID, value) VALUES (\"22\",\"900\")";
   //    handler.runSQL(sql);

   Subsystem s = new Subsystem("TSV");
   s.addSensor("1", "CellV1","V");
   s.addSensor("2", "CellV2","V");
   s.addSensor("5", "CellA2","A");
   s.addSensor("8", "CellT4","F");

   s.parse();

   s.updateValue("1","a");
   s.updateValue("2","b");
   s.updateValue("5","c");
   s.updateValue("8,d");

   System.out.println();

   s.parse();

   System.out.println(s.removeSensor("8"));

   s.updateMultValues("1,foo,2,bar,5,hello,8,world,9");

   System.out.println();

   s.parse();

   //    ArrayList<ArrayList<String>> out = handler.listSensors("cell");
   //
   //    for(ArrayList<String> inner : out){
   //      for(String s : inner){
   //        System.out.print(s + " - ");
   //      }
   //      System.out.println();
   //    }

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

   handler.closeDB();

   }*/

  public static void par(ArrayList<ArrayList<String>> out){
    if(out!=null) for(ArrayList<String> inner : out){
      for(String s : inner){
        System.out.print(s + " - ");
      }
      System.out.println();
    }
    System.out.println();
    System.out.println();
  }

}
