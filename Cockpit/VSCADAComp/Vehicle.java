package VSCADAComp;

public class Vehicle {
  
  private String sysName = "Vehicle";
  private Double distTraveled;
  private Double cSpeed;
  
  public Vehicle(){}
  
  public void addDist(Double dist){
    distTraveled += dist;
  }
  
}