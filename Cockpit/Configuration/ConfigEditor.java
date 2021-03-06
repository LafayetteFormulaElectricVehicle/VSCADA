import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.math.BigInteger;
import DBHandler.DBHandler;

public class ConfigEditor{
  
  Scanner sc;
  private String[] commands = {"a", "add", "cname", "ctype", "cunits", "d", "delete", "exit", "help", "l", "list", "q", "quit"};
  private String[] dataTypes = {"FLOAT", "INT", "INTEGER", "STRING"};
  private String[] sysTypes = {"DYNO", "TSI", "TSV", "MISC"};
  DBHandler handler;
  
  private ArrayList<String> ids;
  
  public ConfigEditor(String dbPath, String sPath){
    sc = new Scanner(System.in);
    handler = new DBHandler(dbPath, sPath);
    ids = new ArrayList<String>();
    getTakenIDs();
    run();
  }
  
  private void getTakenIDs(){
    ArrayList<ArrayList<String>> result = handler.getIDs();
    if(result!=null) for(ArrayList<String> inner : result){
      for(String s : inner){
        ids.add(s);
      }
    }
  }
  
  private void run(){
    String resp = "";
    do{
      resp = prompt("What would you like to do? Type help for additional information!\n");
    }
    while(checkResponse(resp));
  }
  
  private int checkCommand(String cmd){
    for(int i=0; i<commands.length; i++) if(cmd.equals(commands[i])) return i;
    return -1;
  }
  
  private Boolean checkDataType(String type){
    for(int i=0; i<dataTypes.length; i++) if(type.equals(dataTypes[i])) return true;
    System.out.println("Please enter a valid data type:");
    for(int i=0; i<dataTypes.length; i++){
      System.out.print(dataTypes[i]);
      if(i<dataTypes.length-1) System.out.print(" or ");
    }
    System.out.println();
    return false;
  }
  
  private Boolean checkID(String id){
    if(id == null) return false;
    for(String s : ids){
      if(s.equals(id)){
        System.out.println("That ID is taken, sorry, try entering another hex ID");
        return false;
      }
    }
    return true;
  }
  
  private Boolean checkSysType(String type){
    for(int i=0; i<sysTypes.length; i++) if(type.equals(sysTypes[i])) return true;
    System.out.println("Please enter a valid system type:");
    for(int i=0; i<sysTypes.length; i++){
      System.out.print(sysTypes[i]);
      if(i<sysTypes.length-1) System.out.print(" or ");
    }
    System.out.println();
    return false;
  }
  
  //System.out.println("Invalid number of arguments");
  private Boolean checkResponse(String input){
    Scanner s = new Scanner(input);
    String action = s.next();
    String arg0 = "";
    String arg1 = "";
    String arg2 = "";
    String arg3 = "";
    String arg4 = "";
    
    int cmd = checkCommand(action);
    if(cmd == -1) System.out.println("That was not a valid action, try typing help");
    else{
      
      try{arg0 = s.next();}
      catch(Exception e){arg0 = null;}
      
      try{arg1 = s.next();}
      catch(Exception e){arg1 = null;}
      
      try{arg2 = s.next();}
      catch(Exception e){arg2 = null;}
      
      try{arg3 = s.next();}
      catch(Exception e){arg3 = null;}
      
      try{arg4 = s.next();}
      catch(Exception e){arg4 = null;}
      
      return act(action, arg0, arg1, arg2, arg3, arg4);
      
    }
    return true;
  }
  
  private Boolean act(String action, String arg0, String arg1, String arg2, String arg3, String arg4){
    switch(action){
      
      case "a":
      case "add":
        if(arg0 == null || arg1 == null || arg2 == null || arg3 == null || arg4 == null){
        System.out.println("Invalid number of arguments");
        break;
      }
        add(arg0, arg1, arg2, arg3, arg4);
        break;
      case "cname":
        if(arg0 == null || arg1 == null){
        System.out.println("Invalid number of arguments");
        break;
      }
        handler.updateSensorName(arg0, arg1);
        break;
      case "ctype":
        if(arg0 == null || arg1 == null){
        System.out.println("Invalid number of arguments");
        break;
      }
        handler.updateSensorDataType(arg0, arg1);
        break;
      case "cunits":
        if(arg0 == null || arg1 == null){
        System.out.println("Invalid number of arguments");
        break;
      }
        handler.updateSensorUnits(arg0, arg1);
        break;
      case "d":
      case "delete":
        if(arg0 == null){
        System.out.println("Invalid number of arguments");
        break;
      }
        handler.removeSensor(arg1);
        break;
      case "help":
        getHelp();
        break;
      case "l":
      case "list":
        list(arg1);
        break;
      case "exit":
      case "q":
      case "quit":
        return false;
      default:
        System.out.println("Didn't catch that, sorry");
        break;
    }
    return true;
  }
  
  private String checkHex(String input){
    try{
      return "" + new BigInteger(input, 16).intValue();
    }
    catch(java.lang.NumberFormatException e){
      System.out.println("What you entered is not in hex format");
      return null;
    }
  }
  
  private void add(String arg0, String arg1, String arg2, String arg3, String arg4){
    String sys = arg3.toUpperCase();
    String type = arg4.toUpperCase();
    String id = checkHex(arg0);
    
    while(!checkID(id)) id = checkHex(prompt(""));
    while(!checkSysType(sys)) sys = prompt("").toUpperCase();
    while(!checkDataType(type)) type = prompt("").toUpperCase();
    
    if(type.equals("STRING")) type = "VARCHAR(50)";
    else if(type.equals("INT")) type = "INTEGER";
    ids.add(id);
    handler.addSensor(id, arg1, arg2, type, sys);
  }
  
  private String prompt(String prompt){
    System.out.print(prompt);
    String output = "";
    String resp = "";
    while(resp.equals("")) resp = sc.nextLine();
    
    Scanner check = new Scanner(resp);
    check.useDelimiter("");
    String ch = "";
    while (check.hasNext()) {
      ch = check.next();
      output += (ch.equals("\'") ? "" : ch);
    }
    return output;
  }
  
  private void list(String arg){
    String line = "";
    ArrayList<ArrayList<String>> result= handler.listSensors(arg == null ? "" : arg);
    for(ArrayList<String> inner : result){
      System.out.print(inner.get(0) + "(" + inner.get(1) + ")" + " \t");
    }
    System.out.println();
  }
  
  private void getHelp(){
    System.out.println();
    try{
      Scanner help = new Scanner(new File("../Configuration/help.txt"));
      while(help.hasNextLine()) System.out.println(help.nextLine());
    }
    catch(java.io.FileNotFoundException e){
    }
  }
  
  public static void main(String[] args) {
    ConfigEditor config = new ConfigEditor("../SCADA.db", "../SQLSchema/");
    
//    DBHandler handler = new DBHandler("../SCADA.db");
//    System.out.println(handler.getAllSensors());
  }
  
}
