package VSCADAComp;

public class Sensor {
  
  private String sName;
  private String sVal;
  private String sUnits;
  private String sID;
  
  public Sensor(String name, String units, String ID){
    sName = name;
    sUnits = units;
    sID = ID;
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
  
  public void setID(String newID){
    sID = newID;
  }
  
}