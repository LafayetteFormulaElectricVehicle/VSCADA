package interfaces.can;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.math.BigInteger;

import cockpit.database.DBHandler;
import cockpit.database.SCADASystem;

public class CANReader implements Runnable {

    private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CANReader.class.getName());

    public boolean endReached;
    public volatile boolean newData = false;
    String file;
    SCADASystem sys;
    ArrayList<String> ids;
    ArrayList<String> values;
    Scanner sc;
    public CANReader(String fileName, SCADASystem system) {
        file = fileName;
        endReached = false;
        sys = system;
        values = new ArrayList<String>();
        ids = new ArrayList<String>();
        sc = null;

    }

    public void run() {
        String iface = "can0";
        try {
            LOG.info("Opening port on interface: " +iface);
            if(Can.open_port(iface)!=0) throw new Exception();
        } catch (Exception e) {
            LOG.error("Could not open interface: " + iface);
            e.printStackTrace();
        }
        try {
            String data;
            while (true) {
                data = Can.read_port();
                //System.out.println(data);
                parseLine(data);
            }
        } catch (Exception e) {
            LOG.error("Could not read port or parse data");
            LOG.error("Closing port on interface: " + iface);
            Can.close_port();
            e.printStackTrace();
        }
//        try {
//            BufferedReader in = new BufferedReader(new FileReader(file));
//
//            String line2;
//            while (true) {
//                line2 = in.readLine();
//                if (line2 == null || line2.equals("")) {
//                    endReached = true;
//                } else if (endReached) {
//                    newData = true;
//                    parseLine(line2);
//                }
//            }
//        } catch (java.io.FileNotFoundException e) {
//            System.out.println("File not found!");
//        } catch (java.io.IOException ex) {
//            System.out.println("IO Error!");
//        }
    }

    public void parseLine(String line) {
        ArrayList<String> out = new ArrayList<>();
        int id = -33;
        int length = -1;
        String iface = "";
        try {
            sc = new Scanner(line);
            iface = sc.next();
            String id_hex = sc.next();
            id = new BigInteger(id_hex, 16).intValue();
            String len_str = sc.next();
            length = new BigInteger(len_str.replace("[","").replace("]",""), 16).intValue();
            while (sc.hasNext()) out.add(sc.next());

//            System.out.print("interface: " + iface);
//            System.out.print("\t\tid: "+ id +" (0x"+id_hex+")");
//            System.out.print("\t\tlength: [" + length+"]\t\tdata: ");
//            for(String s : out) System.out.print(s+" ");
//            System.out.println();

            sys.updateData(id, out);
        } catch (Exception e) {
            LOG.error("Bad format: " + line);
            e.printStackTrace();
        }

    }
}