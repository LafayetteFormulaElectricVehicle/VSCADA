package VSCADAComp;

import com.google.gson.Gson;
import VSCADAComp.Sensor;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * <h1>Subsystem Class</h1>
 * This class will serve as the data container for a generic subsystem in the SCADA cockpit.
 * Each subsystem will have a map of <ID,Sensors> and functions for adding, removing and 
 *
 * @author  Craig Lombardo
 * @version 1.0
 * @since   2017-03-01
 */
public class Subsystem {
  
  private String subName;
  private HashMap<String, Sensor> subSensors;
  
  /**
   * This Constructor serves as the creation of a new subsystem.
   * @param name The name of the desired subsystem.
   */
  public Subsystem(String name){
    subName = name;
    subSensors = new HashMap<String, Sensor>();
  }
  
  /**
   * This method will add a new sensor to the subsystem.
   * @param ID The ID of the sensor.
   * @param name The name of the sensor.
   * @param units The units of the sensor.
   */
  public void addSensor(String ID, String name, String units){
    subSensors.put(ID, new Sensor(name, units, ID));
  }
  
  /**
   * This method will remove a current sensor from the subsystem.
   * @param ID The ID of the sensor to remove.
   * @return True if a valid sensor ID was input.
   */
  public Boolean removeSensor(String ID){
    return (subSensors.remove(ID) != null);
  }
  
  /**
   * This method will update the current value of a given sensor.
   * @param ID The ID of the sensor.
   * @param value The new value of the sensor.
   * @return True if a valid sensor ID was input.
   */
  public Boolean updateValue(String ID, String value){
    Sensor s = subSensors.get(ID);
    if(s == null) return false;
    else{
      s.setValue(value);
      return true;
    }
  }
  
  /**
   * This method will update the current value of a given sensor.
   * @param IDVal The ID and new value of the sensor separated by a comma(,).
   * @return True if a valid sensor ID was input.
   */
  public Boolean updateValue(String IDVal){
    int index = IDVal.indexOf(",");
    Sensor s = subSensors.get(IDVal.substring(0,index));
    if(s == null) return false;
    else{
      s.setValue(IDVal.substring(index + 1));
      return true;
    }
  }
  
  /**
   * This method will update the current value of multiple sensors.
   * @param IDs An ArrayList of IDs of sensors.
   * @param values An ArrayList of new values of sensors.
   * @return True if all sensor IDs input were valid.
   */
  public Boolean updateMultValues(ArrayList<String> IDs, ArrayList<String> values){
    if(IDs == null || values == null) return false;
    int maxLen = IDs.size() >= values.size() ? IDs.size() : values.size();
    Boolean allValid = true;
    Sensor s;
    
    for(int i=0; i<maxLen; i++){
      s = subSensors.get(IDs.get(i));
      if(s == null) allValid = false;
      else s.setValue(values.get(i));
    }
    return allValid;
  }
  
  /**
   * This method will update the current value of multiple sensors.
   * @param infp A comma separated list of ID,value pairs.
   * @return True if all sensor IDs input were valid.
   */
  public Boolean updateMultValues(String info){
    if(info == null) return false;
    Boolean allValid = true;
    Scanner sc = new Scanner(info);
    sc.useDelimiter(",");
    Boolean onID = true;
    Sensor s = null;
    
    while(sc.hasNext()){
      if(onID) s = subSensors.get(sc.next());
      else{
        if(s == null) allValid = false;
        else s.setValue(sc.next());
      }
      onID = !onID;
    }
    return allValid;
  }
  
  /**
   * This method will take all sensor data for the subsystem and convert it to a JSON.
   * @return A String of all data in JSON format.
   */
  public String parse(){
    Gson g = new Gson();
    return g.toJson(this);
  }
  
}
