package VSCADAComp;

import java.util.ArrayList;

public class TSI {
  private String sysName = "TSI";
  
  private Double totalVoltage;
  private Double totalCurrent;
  
  public TSI(){}
  
  public void setAll(Double voltage, Double current){
    totalVoltage = voltage;
    totalCurrent = current;
  }
  
}
