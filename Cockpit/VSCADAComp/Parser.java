package VSCADAComp;

import com.google.gson.Gson;

public class Parser {
  
  public static String parse(VSCADAComp.Subsystem obj){
    Gson g = new Gson();
    return g.toJson(obj);
  }
  
}