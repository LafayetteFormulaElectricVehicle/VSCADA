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
    Boolean endReached;
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
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));

            String line2;
            while (true) {
                line2 = in.readLine();
                if (line2 == null) endReached = true;
                else if (endReached) {
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
        try {
            sc = new Scanner(line);
            sc.next();
            int id = new BigInteger(sc.next(), 16).intValue();
            sc.next();

            while (sc.hasNext()) out.add(sc.next());
            sys.updateData(id, out);
        } catch (Exception e) {
            System.out.println("Bad format");
        }

    }
}