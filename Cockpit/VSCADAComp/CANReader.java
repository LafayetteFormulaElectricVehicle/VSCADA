package VSCADAComp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.math.BigInteger;

import DBHandler.DBHandler;

public class CANReader implements Runnable{
  
  String file;
  Boolean endReached;
  SCADASystem sys;
  
  ArrayList<String> ids;
  ArrayList<String> values;
  Scanner sc;
  
  public CANReader(String fileName, SCADASystem system){
    file = fileName;
    endReached = false;
    sys = system;
    values = new ArrayList<String>();
    ids = new ArrayList<String>();
    sc = null;
  }
  
  public void run(){
    try{
      BufferedReader in = new BufferedReader(new FileReader(file));
      
      String line2;
      while (true){
        line2 = in.readLine();
        if(line2 == null) endReached = true;
        else if(endReached){
          parseLine(line2);
        }
      }
    }
    catch(java.io.FileNotFoundException e){
      System.out.println("File not found!");
    }
    catch(java.io.IOException ex){
      System.out.println("IO Error!");
    }
  }
  
  public void parseLine(String line){
    try{
      sc = new Scanner(line);
      sc.next();
      String id = sc.next();
      sc.next();
      String totalVal = "";
      while(sc.hasNext()) totalVal+= sc.next();
      String value = "" + new BigInteger(totalVal, 16).intValue();
      sys.updateValue(id, value);
    }
    catch(Exception e){
      System.out.println("Bad format");
    }
    
  }
//  
//  public void writeToDB(){
//    int len = values.size();
//    if(len == 0) return;
//    
//    String data = "";
//    
//    for(int i=0; i<len; i++){
//      data += "(" + ids.remove(0) + ", " + values.remove(0) + ")";
//      if(i != len-1) data += ", ";
//    }
//    handler.insertData(data);
//  }
  
//  public void update(){
//    int len = values.size();
//    if(len == 0) return;
//    System.out.println("chirp");
//    String data = "";
//    
//    for(int i=0; i<len; i++){
//      data += ids.remove(0) + "," + values.remove(0);
//      if(i != len-1) data += ",";
//    }
////    System.out.println(data);
//    //HERE
//    sys.updateValue(data);
//  }
}