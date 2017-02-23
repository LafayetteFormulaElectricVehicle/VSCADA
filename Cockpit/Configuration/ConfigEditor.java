import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;

public class ConfigEditor{
  
  Scanner sc;
  private String[] commands = {"a", "add", "cname", "cunits", "d", "delete", "help", "l", "list", "q", "quit"};
  DBHandler handler;
  
  public ConfigEditor(){
    sc = new Scanner(System.in);
    handler = new DBHandler("../SCADA.db");
    run();
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
  
  //System.out.println("Invalid number of arguments");
  private boolean checkResponse(String input){
    Scanner s = new Scanner(input);
    String action = s.next();
    String arg1 = "";
    String arg2 = "";
    
    int cmd = checkCommand(action);
    if(cmd == -1) System.out.println("That was not a valid action, try typing help");
    else{
      
      try{arg1 = s.next();}
      catch(Exception e){arg1 = null;}
      
      try{arg2 = s.next();}
      catch(Exception e){arg2 = null;}
      
      switch(action){
        
        case "a":
        case "add":
          if(arg1 == null || arg2 == null){
          System.out.println("Invalid number of arguments");
          break;
        }
          handler.addSensor(arg1, arg2);
          break;
        case "cname": 
          if(arg1 == null || arg2 == null){
          System.out.println("Invalid number of arguments");
          break;
        }
          handler.updateSensorName(arg1, arg2);
          break;
        case "cunits":
          if(arg1 == null || arg2 == null){
          System.out.println("Invalid number of arguments");
          break;
        }
          handler.updateSensorUnits(arg1, arg2);
          break;
        case "d":
        case "delete":
          if(arg1 == null){
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
        case "q":
        case "quit":
          return false;
        default:
          System.out.println("Didn't catch that, sorry");
          break;
      }
    }
    return true;
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
    ArrayList<ArrayList<String>> result= handler.listSensors(arg == null ? "" : arg);
    for(ArrayList<String> inner : result){
      for(String s : inner){
        System.out.print(s + "\t");
      }
    }
    System.out.println();
  }
  
  private void getHelp(){
    System.out.println();
    try{
      Scanner help = new Scanner(new File("help.txt"));
      while(help.hasNextLine()) System.out.println(help.nextLine());
    }
    catch(java.io.FileNotFoundException e){
    }
  }
  
  public static void main(String[] args) {
    ConfigEditor config = new ConfigEditor();
    
//    DBHandler handler = new DBHandler("../SCADA.db");
//    System.out.println(handler.getAllSensors());
  }
  
}