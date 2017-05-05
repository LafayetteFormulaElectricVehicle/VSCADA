package GUI;

import cockpit.database.DBHandler;
import cockpit.database.SCADASystem;
import cockpit.database.Sensor;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import com.google.gson.JsonObject;
import server.HTTPRequest;
import com.google.gson.JsonElement;

/**
 * <h1>Maintenance View</h1>
 * This class will serve as a means to view all data from the car in one location/ list view
 *
 * @author Craig Lombardo
 * @version 1.0
 * @since 2017-03-12
 */

public class MaintenanceView {

    private HTTPRequest request;
    private boolean singleColumn;
    private GridBagLayout innerLayout;
    private GridBagConstraints innerConstraints;
    private JPanel innerPanel;
    private JPanel pane;
    private JScrollPane scrollPanel;
    private DBHandler handler;
    private SCADASystem sys;
    private HashMap<Integer, JLabel> sensors;
    private Viewer viewer;
    private int view;

    private String ip;
    private boolean server;

    /**
     * This creates a new view with connected to the DB and minor aesthetic options
     * @param dbHandler The DBHandler associated with SCADA.db
     * @param scadaSys The SCADASystem linked with the DBHandler
     * @param viewer The The viewer which will display views
     * @param ipAddr The IP Address of the server
     * @param singleColumn True if single column (geared for cockpit) false for 2
     * @param viewNumber The view number on the viewer (0 if first, 1 if second, etc.)
     */
    public MaintenanceView(DBHandler dbHandler, SCADASystem scadaSys, Viewer viewer, String ipAddr, boolean singleColumn, int viewNumber) {
        request = new HTTPRequest();
        this.singleColumn = singleColumn;
        sys = scadaSys;
        handler = dbHandler;

        pane = new JPanel();
        sensors = new HashMap<>();

        ip = ipAddr;
        server = !ip.equals("");

        this.viewer = viewer;
        view = viewNumber;

        createMapping(handler.getIDDescUnitsTag(null));

        scrollPanel = new JScrollPane(innerPanel);
        pane.add(scrollPanel, BorderLayout.CENTER);
        scrollPanel.getVerticalScrollBar().setUnitIncrement(20);

        Timer displayTimer = new Timer(1000, null);
        displayTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                updateNow(sys.getMap());
            }
        });
        displayTimer.start();
    }

    /**
     * This method returns the panel that the view was drawn on
     * @return The panel
     */
    public JScrollPane getPane() {
        return scrollPanel;
    }

    private void updateNow(HashMap<Integer, Sensor> sysMap) {
        if (view == viewer.getCurrentView()) {
            if (server) {
                try {
                    JsonElement j = request.sendGet("http://" + ip + ":3000/map");

                    JsonObject obj = j.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {

                        sensors.get(Integer.parseInt(entry.getKey())).setText(getCalibValue(entry.getValue().toString()));
                    }

                } catch (Exception e) {
//                System.out.println("No Connection");
                }

            } else {
                for (Map.Entry<Integer, Sensor> entry : sysMap.entrySet()) {
                    sensors.get(entry.getKey()).setText(entry.getValue().getCalibValue());
                }

            }
        }
    }

    private String getCalibValue(String json) {
        int end = 0;

        for (int i = json.length() - 1; i >= 0; i--) {
            if (json.charAt(i) == '"') {
                if (end == 0) end = i;
                else return json.substring(i + 1, end);
            }
        }
        return "";
    }

    private void createHeader() {

        if (singleColumn) {
            addComp(0, 0, 30, new JLabel(""));
            addComp(1, 0, 10, new JLabel("<html><b>Sensor Tag</b></html>"));
            addComp(2, 0, 10, new JLabel("<html><b>Sensor Desc</b></html>"));
            addComp(3, 0, 10, new JLabel("<html><b>Sensor Value</b></html>"));
            addComp(4, 0, 10, new JLabel("<html><b>Sensor Units</b></html>"));
            addComp(5, 0, 30, new JLabel(""));
        } else {
            addComp(0, 0, 10, new JLabel("<html><b>Sensor Tag</b></html>"));
            addComp(1, 0, 10, new JLabel("<html><b>Sensor Desc</b></html>"));
            addComp(2, 0, 10, new JLabel("<html><b>Sensor Value</b></html>"));
            addComp(3, 0, 10, new JLabel("<html><b>Sensor Units</b></html>"));
            addComp(4, 0, 20, new JLabel(" "));
            addComp(5, 0, 10, new JLabel("<html><b>Sensor Tag</b></html>"));
            addComp(6, 0, 10, new JLabel("<html><b>Sensor Desc</b></html>"));
            addComp(7, 0, 10, new JLabel("<html><b>Sensor Value</b></html>"));
            addComp(8, 0, 10, new JLabel("<html><b>Sensor Units</b></html>"));
        }
    }

    private void createMapping(ArrayList<ArrayList<String>> info) {
        innerLayout = new GridBagLayout();
        innerConstraints = new GridBagConstraints();
        innerPanel = new JPanel(innerLayout);
        innerConstraints.fill = GridBagConstraints.NONE;
        innerConstraints.anchor = GridBagConstraints.CENTER;

        createHeader();

        String id;
        String name;
        String units;
        String tag;
        String hexVal;
        String hexString;

        ArrayList<String> r;

        int row = 1;
        int toggleVal = singleColumn ? 0 : 5;
        int align = singleColumn ? 1 : 0;

        for (int i = 0; i < info.size(); i++) {
            r = info.get(i);
            id = r.get(0);
            name = r.get(1);
            units = r.get(2);
            tag = r.get(3);

            JLabel valueField = new JLabel("");
            sensors.put(Integer.parseInt(id), valueField);

            addComp(align, row, 10, new JLabel(tag));
            addComp(align + 1, row, 10, new JLabel(name));
            addComp(align + 2, row, 10, valueField);
            addComp(align + 3, row, 10, new JLabel(units));

            if (!singleColumn && (i + 1 == info.size() / 2)) {
                row = 1;
                align += toggleVal;
            } else row++;
        }


        HashMap<Integer, Sensor> m = sys.getMap();

        updateNow(m);
    }

    private void addComp(int x, int y, int wx, Component comp) {
        innerConstraints.gridx = x;
        innerConstraints.gridy = y;

        innerConstraints.weightx = wx;

        innerConstraints.gridheight = 1;

        innerLayout.setConstraints(comp, innerConstraints);
        innerPanel.add(comp);
    }

}
