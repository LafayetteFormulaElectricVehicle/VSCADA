package VSCADAComp;

import java.util.ArrayList;
import VSCADAComp.Sensor;

public class Subsystem {
  
  private String subName;
  private ArrayList<Sensor> subSensors;
  
  public Subsystem(String name){
    subName = name;
    subSensors = new ArrayList<Sensor>();
  }
  
  public void addSensor(String name, String units){
    subSensors.add(new Sensor(name, units));
  }
  
}