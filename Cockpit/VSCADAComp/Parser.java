package VSCADAComp;

import com.google.gson.Gson;

public class Parser {
  
  public static String parse(VSCADAComp.Subsystem obj){
    Gson g = new Gson();
    return g.toJson(obj);
  }
  
//  public static String parse(VSCADAComp.TSI obj){
//    Gson g = new Gson();
//    return g.toJson(obj);
//  }
//  
//  public static String parse(VSCADAComp.TSV obj){
//    Gson g = new Gson();
//    return g.toJson(obj);
//  }
//  
//  public static String parse(VSCADAComp.Dyno obj){
//    Gson g = new Gson();
//    return g.toJson(obj);
//  }
//  
//  public static String parse(VSCADAComp.Vehicle obj){
//    Gson g = new Gson();
//    return g.toJson(obj);
//  }
}