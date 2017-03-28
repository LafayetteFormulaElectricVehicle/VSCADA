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
 * Each subsystem will have a map of <ID,Sensors> and functions for adding, removing and
 *
 * @author Craig Lombardo
 * @version 1.0
 * @since 2017-03-01
 */
public class SCADASystem implements Runnable {
    //ID, Sensor --- Contains actual data
    private HashMap<Integer, Sensor> sensors;
    //ADDR, <IDS> -- Purely for mapping purposes
    private HashMap<Integer, ArrayList<Integer>> mappings;
    private DBHandler handler;
    private String file;

    public SCADASystem(DBHandler dbhandler, String CANfile) {
        handler = dbhandler;
        createAllSensors();
        file = CANfile;
    }

    public void createAllSensors() {
        sensors = new HashMap<>();
        mappings = new HashMap<>();
        ArrayList<ArrayList<String>> info = handler.getSensorcharacterization();
        for (ArrayList<String> arr : info) {
            Sensor s = new Sensor(Integer.parseInt(arr.get(0)), arr.get(1), Integer.parseInt(arr.get(2)),
                    Integer.parseInt(arr.get(3)), Integer.parseInt(arr.get(4)), arr.get(5),
                    arr.get(6), arr.get(7), Integer.parseInt(arr.get(8)));

            tryInsert(Integer.parseInt(arr.get(2)), Integer.parseInt(arr.get(0)));
            sensors.put(Integer.parseInt(arr.get(0)), s);
        }
    }

    public void tryInsert(Integer address, Integer id) {
        ArrayList<Integer> result = mappings.get(address);
        if (result == null) {
            ArrayList<Integer> newArr = new ArrayList<>();
            newArr.add(id);
            mappings.put(address, newArr);
        } else result.add(id);
    }

    public void updateData(Integer address, ArrayList<String> packets) {
        for (Integer i : mappings.get(address)) {
            Sensor s = sensors.get(i);
            String bytes = "";

            for (int j = s.getOffset(); j < s.getOffset() + s.getByteLength(); j++) {
                bytes += packets.get(j);
            }

            s.setValue("" + new BigInteger(bytes, 16).intValue());
        }
    }

    public void writeToDB() {
        String data = "";
        String val = "";
        for (Map.Entry<Integer, Sensor> entry : sensors.entrySet()) {
            val = entry.getValue().getValue();
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

    public HashMap<Integer, Sensor> getMap() {
        return sensors;
    }

    public void run() {
        CANReader tmp = new CANReader(file, this);
        Thread thr = new Thread(tmp);


        Timer t = new Timer(1000, null);
        t.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                writeToDB();
            }
        });
        t.start();

        thr.start();
        while (true) {
        }
    }
}
