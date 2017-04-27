package interfaces.can;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.math.BigInteger;

import cockpit.database.DBHandler;
import cockpit.database.SCADASystem;

public class CANReader implements Runnable {

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
        Can.open_port("vcan0");
        String data;
        while(true) {
            data = Can.read_port();
            //System.out.println(data);
            parseLine(data);
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
            id = new BigInteger(sc.next(), 16).intValue();
            String len_str = sc.next();
            length = new BigInteger(len_str.replace("[","").replace("]",""), 16).intValue();

            System.out.println("interface: " + iface);
            System.out.println("id: "+ id);
            System.out.println("length: " + length);

            while (sc.hasNext()) {
                out.add(sc.next());
                System.out.println(out.get(out.size()-1));
            }
            sys.updateData(id, out);
        } catch (Exception e) {
            System.out.println(line);
            System.out.println("Bad format");
        }

    }
}