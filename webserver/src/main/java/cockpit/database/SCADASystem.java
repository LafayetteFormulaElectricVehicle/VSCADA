package cockpit.database;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Map;

/**
 * <h1>SCADA Class</h1>
 * This class will serve as the data container for a generic subsystem in the SCADA cockpit.
 * Each subsystem will have a map of <ID,Sensors> and functions for adding, removing and
 *
 * @author  Craig Lombardo
 * @version 1.0
 * @since   2017-03-01
 */
public class SCADASystem{

    private HashMap<String, String> sensors;
    private DBHandler handler;

    /**
     * This Constructor serves as the creation of a new subsystem.
     * @param name The name of the desired subsystem.
     */
    public SCADASystem(DBHandler dbhandler){
        handler = dbhandler;
        sensors = new HashMap<String, String>();
    }

    /**
     * This method will add a new sensor to the subsystem.
     * @param ID The ID of the sensor.
     * @param name The name of the sensor.
     * @param units The units of the sensor.
     */
    public void addSensor(String ID){
        sensors.put(ID, "NaN?");
    }

    /**
     * This method will remove a current sensor from the subsystem.
     * @param ID The ID of the sensor to remove.
     * @return True if a valid sensor ID was input.
     */
    public Boolean removeSensor(String ID){
        return (sensors.remove(ID) != null);
    }

    /**
     * This method will update the current value of a given sensor.
     * @param ID The ID of the sensor.
     * @param value The new value of the sensor.
     * @return True if a valid sensor ID was input.
     */
    public Boolean updateValue(String ID, String value){
        return sensors.put(ID, value) != null;
    }

    /**
     * This method will update the current value of a given sensor.
     * @param IDVal The ID and new value of the sensor separated by a comma(,).
     * @return True if a valid sensor ID was input.
     */
    public Boolean updateValue(String IDVal){
        int index = IDVal.indexOf(",");
        String ID = IDVal.substring(0,index);
        String value = IDVal.substring(index + 1);
        return sensors.put(ID, value) != null;
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

        for(int i=0; i<maxLen; i++){
            if(sensors.put(IDs.get(i), values.get(i)) == null) allValid = false;
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
        while(sc.hasNext()){
            if(sensors.put(sc.next(),sc.next()) == null) allValid = false;
        }
        return allValid;
    }

    /**
     * This method will take all sensor data for the subsystem and convert it to a JSON.
     * @return A String of all data in JSON format.
     */
    public void parse(){
        String data = "";

        for (Map.Entry<String, String> entry : sensors.entrySet()){
            data += "(\"" + entry.getKey() + "\", \"" + entry.getValue() + "\")";
            data += ", ";
        }
        data = data.substring(0, data.length() - 2);
        System.out.println(data);
    }

    public void writeToDB(){
        String data = "";
        String val = "";
        for (Map.Entry<String, String> entry : sensors.entrySet()){
            val = entry.getValue();
            if(!val.equals("NaN?")){
                data += "(\"" + entry.getKey() + "\", \"" + val + "\")";
                data += ", ";
            }
        }
        if(data.equals("")) handler.insertData(null);
        else{
            data = data.substring(0, data.length() - 2);
            handler.insertData(data);
        }
    }

    public void openCAN(){
        String file = "/home/lombardc/Desktop/output.txt";
        interfaces.can.Can tmp = new interfaces.can.Can(file, this);
        Thread thr = new Thread(tmp);
        SCADATimer t = new SCADATimer(1000, this);
        Thread thr2 = new Thread(t);
        thr.start();
        thr2.start();
        while(true){}
    }

}
