package VSCADAComp;

import java.util.ArrayList;

/**
 * The PackMan shall communicate with the VSCADA system. 
 * VSCADA shall acquire data from all sensors and AMS boards 
 * in the pack (cell voltage, pack voltage, cell temperatures, 
 * pack internal temperatures, pack current) as well as 
 * acquiring SOC estimates and charge algorithm state. A 
 * Calibration and Error Analysis must be performed on the 
 * measurement process. This analysis shall calculate estimates 
 * the uncertainties associated with all AMS measurands, especially SOC.
 */

public class TSV {
  private String sysName = "TSV";
  
  private Integer numCells = 4;
  private Integer numPacks = 4;
  
  private ArrayList<Double> cellVoltage;
  private ArrayList<Double> packCurrent;
  
  public TSV(){
    cellVoltage = new ArrayList<Double>(numPacks);
    packCurrent = new ArrayList<Double>(numPacks);
  }
  
  public Double getCellVoltage(int cell){
    if(cell < 0 || cell > numCells) return -1.0;
    else return cellVoltage.get(cell);
  }
  public void setCellVoltage(int cell, Double val){
    cellVoltage.set(cell, val);
  }
  
  public Double getPackCurrent(int pack){
    if(pack < 0 || pack > numPacks) return -1.0;
    else return packCurrent.get(pack);
  }
  public void setPackCurrent(int pack, Double val){
    packCurrent.set(pack, val);
  }
  
  public Double getTotalVoltage(){
    Double ans = 0.0;
    for(int i=0; i< numPacks; i++) ans += cellVoltage.get(i);
    return ans;
  }
  
  public Double getTotalCurrent(){
    Double ans = 0.0;
    for(int i=0; i< numPacks; i++) ans += packCurrent.get(i);
    return ans;
  }
  
  public Double getTotalPower(){
    return getTotalVoltage() * getTotalCurrent();
  }
}
