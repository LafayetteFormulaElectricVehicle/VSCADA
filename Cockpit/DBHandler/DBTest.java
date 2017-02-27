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

//    Subsystem s = new Subsystem("TSV");
//    s.addSensor("1", "CellV1","V");
//    s.addSensor("2", "CellV2","V");
//    s.addSensor("5", "CellA2","A");
//    s.addSensor("8", "CellT4","F");
//    Parser p = new Parser();
//    System.out.println(p.parse(s));

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

  }

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
