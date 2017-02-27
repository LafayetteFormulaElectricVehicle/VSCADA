package cockpit.database;

//import java.util.ArrayList;
//        import VSCADAComp.Sensor;

        import java.util.HashMap;

public class Subsystem {

    private String subName;
    //  private ArrayList<Sensor> subSensors;
    private HashMap<String, Sensor> subSensors;

    public Subsystem(String name){
        subName = name;
//    subSensors = new ArrayList<Sensor>();
        subSensors = new HashMap<String, Sensor>();
    }

    public void addSensor(String ID, String name, String units){
//    subSensors.add(new Sensor(name, units));
        subSensors.put(ID, new Sensor(name, units, ID));
    }

}
