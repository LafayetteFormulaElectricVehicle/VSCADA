package interfaces.can;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.math.BigInteger;

import cockpit.database.DBHandler;
import cockpit.database.SCADASystem;

public class CANReader implements Runnable {

    String file;
    public boolean endReached;
    SCADASystem sys;

    ArrayList<String> ids;
    ArrayList<String> values;
    Scanner sc;

    public volatile boolean newData = false;

    public CANReader(String fileName, SCADASystem system) {
        file = fileName;
        endReached = false;
        sys = system;
        values = new ArrayList<String>();
        ids = new ArrayList<String>();
        sc = null;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));

            String line2;
            while (true) {
                line2 = in.readLine();
                if (line2 == null || line2.equals("")) {
                    endReached = true;
                }
                else if (endReached) {
                    newData = true;
                    parseLine(line2);
                }
            }
        } catch (java.io.FileNotFoundException e) {
            System.out.println("File not found!");
        } catch (java.io.IOException ex) {
            System.out.println("IO Error!");
        }
    }

    public void parseLine(String line) {
        ArrayList<String> out = new ArrayList<>();
        int id = -33;
        try {
            sc = new Scanner(line);
            sc.next();
            id = new BigInteger(sc.next(), 16).intValue();
            sc.next();

            while (sc.hasNext()) out.add(sc.next());
            sys.updateData(id, out);
        } catch (Exception e) {
            System.out.println(line);
            System.out.println("Bad format");
        }

    }
}