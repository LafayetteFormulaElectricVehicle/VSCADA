package cockpit.database;

import interfaces.can.CANReader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1>SCADA Class</h1>
 * This class will serve as the data container for a generic subsystem in the SCADA cockpit.
 * Each subsystem will have a map of &lt;ID,Sensors&gt; and functions for adding, removing and
 *
 * @author Craig Lombardo
 * @version 1.0
 * @since 2017-03-01
 */
public class SCADASystem implements Runnable {
    //sensorID, Sensor --- Contains actual data
    private HashMap<Integer, Sensor> sensors;
    //ADDR, <sensorIDS> -- Purely for mapping purposes
    private HashMap<Integer, ArrayList<Integer>> mappings;

    //Tag, sensorID
    private HashMap<String, Sensor> customMapping;

    private DBHandler handler;
    private ArrayList<Equation> equations;

    //Timeout
    private static final int MAX_TIME = 3;
    private int seconds = MAX_TIME;

    private boolean storeData = true;

    /**
     * The constructor will create a new instance of a SCADASystem
     * @param dbhandler The database handler that is connected to the SCADA.db
     */
    public SCADASystem(DBHandler dbhandler) {
        handler = dbhandler;
        createAllSensors();
        createAllEquations();
    }

    private void createAllSensors() {
        sensors = new HashMap<>();
        mappings = new HashMap<>();
        customMapping = new HashMap<>();
        ArrayList<ArrayList<String>> info = handler.getSensorcharacterization();
        for (ArrayList<String> arr : info) {
            Sensor s = new Sensor(Integer.parseInt(arr.get(0)), arr.get(1), Integer.parseInt(arr.get(2)),
                    Integer.parseInt(arr.get(3)), Integer.parseInt(arr.get(4)), arr.get(5),
                    arr.get(6), arr.get(7), Integer.parseInt(arr.get(8)), Double.parseDouble(arr.get(9)));

            tryInsert(Integer.parseInt(arr.get(2)), Integer.parseInt(arr.get(0)));
            sensors.put(Integer.parseInt(arr.get(0)), s);
            customMapping.put(arr.get(1), s);
        }
    }

    /**
     * This method returns all of the equations found in the DB
     * @return Arraylist containing all of the Equations (class Equation)
     */
    public ArrayList<Equation> getEquations(){
        return equations;
    }

    private void createAllEquations() {
        equations = new ArrayList<>();
        ArrayList<ArrayList<String>> output = handler.getEquations();

        Equation e;
        for (ArrayList<String> arr : output) {
            equations.add(new Equation(arr.get(0), arr.get(1)));
        }
    }

    private void tryInsert(Integer address, Integer id) {
        ArrayList<Integer> result = mappings.get(address);
        if (result == null) {
            ArrayList<Integer> newArr = new ArrayList<>();
            newArr.add(id);
            mappings.put(address, newArr);
        } else result.add(id);
    }

    /**
     * This method updates the values of all of the sensors in the system. Typical can formats are like:
     * can0  500   [8]  06 00 B3 00 00 00 3F 31 where in this case the input params would be:
     * (500, {06, 00, B3, 00, 00, 00, 3F, 31})
     * @param address The can address that was received
     * @param packets The data received over the can
     */
    public void updateData(Integer address, ArrayList<String> packets) {
        ArrayList<Integer> map = mappings.get(address);
        if (map == null) return;
        for (Integer i : map) {
            Sensor s = sensors.get(i);
            String bytes = "";
//            System.out.println(s.getDescription());

            for (int j = s.getOffset(); j < s.getOffset() + s.getByteLength(); j++) {
                bytes += packets.get(j);
//                System.out.println(j);
            }
//            System.out.println(bytes);
            s.setCalibValue("" + new BigInteger(bytes, 16).intValue());
            customMapping.get(s.getTag()).setCalibValue("" + new BigInteger(bytes, 16).intValue());
//            System.out.println(s.getTag() + " " + new BigInteger(bytes, 16).intValue());
        }

    }

    private void writeToDB() {
        if(!storeData) return;
        String data = "";
        String val = "";

        updateCustomSensors();

        for (Map.Entry<Integer, Sensor> entry : sensors.entrySet()) {
            val = entry.getValue().getCalibValue();
            if (!val.equals("NaN?")) {
                data += "(\"" + entry.getKey() + "\", \"" + val + "\")";
                data += ", ";
            }
        }
        if (!data.equals("")) {
            data = data.substring(0, data.length() - 2);
            handler.insertData(data);
        }
    }

    /**
     * This method returns the mapping of the sensors for use in other applications
     * @return The Hashmap containing all key value pairs of Integer and Sensor
     */
    public HashMap<Integer, Sensor> getMap() {
        return sensors;
    }

    /**
     * This method returns the mapping of the sensors for use in other applications, effectively the same as getMap
     * however this method returns tag based instead of backend ID based.
     * @return The Hashmap containing all key value pairs of Integer and Sensor
     */
    public HashMap<String, Sensor> getCustomMapping(){ return customMapping;}

    /**
     * Used by the thread runners.
     */
    public void run() {
        CANReader tmp = new CANReader(this);
        Thread thr = new Thread(tmp);

        Timer t = new Timer(250, null);
        t.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(tmp.newData){
                    seconds = MAX_TIME;
                    tmp.newData = false;
                    writeToDB();
                }
                else if(seconds > 0) {
                    seconds--;
                    writeToDB();
                }
            }
        });
        t.start();

        thr.start();
        while (true) {
        }
    }

    private void updateCustomSensors(){
        for(Equation e : equations){
            String destTag = e.destination;
            Sensor destSensor = customMapping.get(destTag);
            Double value = e.evaluate(customMapping);

//            System.out.println(destTag + " : " + value);
//            System.out.println(sensors.get(destSensor.id).getDescription());
//            if(value != null) destSensor.setRawValue("" + e.evaluate(customMapping));

            if(value != null){
                sensors.get(destSensor.getID()).setCalibValue("" + value);
                customMapping.get(destTag).setCalibValue("" + value);
            }
        }
    }

    /**
     * Sets whether or not the system shoulk store data into the db
     * @param bool True for store false for view only
     */
    public void toggleDataSave(Boolean bool){
        storeData = bool;
    }

}
