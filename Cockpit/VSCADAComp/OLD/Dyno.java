package VSCADAComp;

public class Dyno {
  
  private Double torque;
  private Double oilTemp;
  private Double coolantTemp;
  private Double coolantFR;
  private Double RPM;
  
  public Dyno(){}
  
  public void setTorque(Double val){
    torque = val;
  }
  
  public void setOilTemp(Double val){
    oilTemp = val;
  }
  
  public void setCoolantTemp(Double val){
    coolantTemp = val;
  }
  
  public void setCoolantFR(Double val){
    coolantFR = val;
  }
  
  public void setRPM(Double val){
    RPM = val;
  }
  
  public Double getTorque(){
    return torque;
  }
  
  public Double setOilTemp(){
    return oilTemp;
  }
  
  public Double setCoolantTemp(){
    return coolantTemp;
  }
  
  public Double getCoolantFR(){
    return coolantFR;
  }
  
  public Double getRPM(){
    return RPM;
  }
  
  public void setAll(Double tor, Double oil, Double coolT, Double coolFR, Double rpm){
    torque = tor;
    oilTemp = oil;
    coolantTemp = coolT;
    coolantFR = coolFR;
    RPM = rpm;
  }
  
}
