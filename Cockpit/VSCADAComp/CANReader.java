package VSCADAComp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import DBHandler.DBHandler;

public class CANReader implements Runnable{
  
  String file;
  Boolean endReached;
  DBHandler handler;
  
  ArrayList<String> ids;
  ArrayList<String> values;
  
  public CANReader(String fileName, DBHandler dbhandler){
    file = fileName;
    endReached = false;
    handler = dbhandler;
    values = new ArrayList<String>();
    ids = new ArrayList<String>();
  }
  
  public void run(){
    try{
      BufferedReader in = new BufferedReader(new FileReader(file));
      
      String line2;
      while (true){
        line2 = in.readLine();
        if(line2 == null) endReached = true;
        else if(endReached) parseLine(line2);
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
    int index = line.indexOf(" ");
    String id = line.substring(0, index);
    String value = line.substring(index + 1);
    ids.add(id);
    values.add(value);
  }
  
  public void writeToDB(){
    int len = values.size();
    if(len == 0) return;
    
    String data = "";
    
    for(int i=0; i<len; i++){
      data += "(" + ids.remove(0) + ", " + values.remove(0) + ")";
      if(i != len-1) data += ", ";
    }
    handler.insertData(data);
  }
}