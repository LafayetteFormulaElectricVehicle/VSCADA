package VSCADAComp;

public class Sensor {
  
  private String sName;
  private String sUnits;
  private String sVal;
  
  public Sensor(String name, String units){
    sName = name;
    sUnits = units;
  }
  
  public void setName(String newName){
    sName = newName;
  }
  
  public void setUnits(String newUnits){
    sUnits = newUnits;
  }
  
  public void setValue(String newValue){
    sVal = newValue;
  }
  
}