package GUI;

import cockpit.database.DBHandler;
import cockpit.database.SCADASystem;
import cockpit.database.Sensor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import server.HTTPRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1>Custom View</h1>
 * This class will serve as a means to view the sensors you want rather than all of them
 *
 * @author Craig Lombardo
 * @version 1.0
 * @since 2017-03-14
 */


public class CustomView {

    private JScrollPane scrollPanelSelect;
    private GridBagLayout selectedLayout;
    private GridBagConstraints selectedConstraints;
    private JPanel selectedPanel;
    //
    private JScrollPane scrollPanelItems;
    private GridBagLayout itemsLayout;
    private GridBagConstraints itemsConstraints;
    private JPanel itemsPanel;
    //

    private JScrollPane innerPanelItems;
    private GridBagLayout innerLayout;
    private GridBagConstraints innerConstraints;
    private JPanel innerPanel;

    private DBHandler handler;
    private SCADASystem sys;

    private HashMap<Integer, JLabel> sensors;
    private ArrayList<JCheckBox> items;
    private ArrayList<Sensor> itemSensors;

    private Viewer viewer;
    private int view;

    private JPanel panelMain;
    private Boolean createLabels = true;

    private boolean server;
    private String ip;
    private HTTPRequest request;

    /**
     * This creates a new instance of CustomView
     * @param dbHandler The DBHandler to be used with the SCADA.db
     * @param scadaSys The SCADASystem that is linked with the same DBHandler
     * @param viewer The viewer which will display views
     * @param ipAddr The IP address of the server ("" if not applicable)
     * @param viewNumber The view number on the viewer (0 if first, 1 if second, etc.)
     */
    public CustomView(DBHandler dbHandler, SCADASystem scadaSys, Viewer viewer, String ipAddr, int viewNumber) {
        panelMain = new JPanel(new BorderLayout());
        sensors = new HashMap<>();

        sys = scadaSys;
        handler = dbHandler;
        this.viewer = viewer;
        view = viewNumber;

        request = new HTTPRequest();
        ip = ipAddr;
        server = !ip.equals("");

        items = new ArrayList<JCheckBox>();
        itemSensors = new ArrayList<Sensor>();

        createConstraints();

        createButtons();
        addComp(0, 1, new JLabel("  "), itemsPanel, itemsConstraints, itemsLayout);
        addComp(1, 1, new JLabel("  Sensor Tag:  "), itemsPanel, itemsConstraints, itemsLayout);
        addComp(2, 1, new JLabel("  Sensor Desc:  "), itemsPanel, itemsConstraints, itemsLayout);
        addComp(3, 1, new JLabel("  Sensor Units:  "), itemsPanel, itemsConstraints, itemsLayout);

        createItems(handler.getIDDescUnitsTag(null));

        createNewSelectedInfo();

        scrollPanelItems = new JScrollPane(itemsPanel);
        scrollPanelItems.getVerticalScrollBar().setUnitIncrement(20);

        scrollPanelSelect = new JScrollPane(selectedPanel);
        scrollPanelSelect.getVerticalScrollBar().setUnitIncrement(20);

        innerConstraints.weighty = 1;
        innerConstraints.weightx = 0.4;
        addComp(0, 0, scrollPanelItems, innerPanel, innerConstraints, innerLayout);
        innerConstraints.weightx = 0.6;
        addComp(1, 0, scrollPanelSelect, innerPanel, innerConstraints, innerLayout);

        panelMain.add(innerPanel);

        Timer displayTimer = new Timer(1000, null);
        displayTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                updateNow(sys.getMap());
            }
        });
        displayTimer.start();
    }

    private void updateNow(HashMap<Integer, Sensor> sysMap) {
        if (view == viewer.getCurrentView()) {
//            for (Map.Entry<Integer, Sensor> entry : sysMap.entrySet()) {
////                System.out.println("sys "+entry.getKey());
//                sensors.get(entry.getKey()).setText(entry.getValue().getCalibValue());
//            }

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

    private void createItems(ArrayList<ArrayList<String>> info) {

        int row = 2;
        String id;
        String name;
        String units;
        String hexVal;
        String idString;

        String tag;

        HashMap<Integer, Sensor> allSensors = sys.getMap();

        for (ArrayList<String> r : info) {
            id = r.get(0);
            name = r.get(1);
            units = r.get(2);
            tag = r.get(3);

            hexVal = Integer.toHexString(Integer.parseInt(id));
            idString = "0x000".substring(0, 5 - hexVal.length()) + hexVal;

            JCheckBox check = new JCheckBox();
            items.add(check);

            itemSensors.add(allSensors.get(Integer.parseInt(r.get(0))));
            addComp(0, row, check, itemsPanel, itemsConstraints, itemsLayout);
            addComp(1, row, new JLabel(tag), itemsPanel, itemsConstraints, itemsLayout);
            addComp(2, row, new JLabel(name), itemsPanel, itemsConstraints, itemsLayout);
            addComp(3, row++, new JLabel(units), itemsPanel, itemsConstraints, itemsLayout);

        }
    }

    private void createButtons() {
        JPanel buttons = new JPanel(new GridLayout());

        JButton sAll = new JButton("Select All");
        sAll.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //Do stuff
                        for (JCheckBox c : items) c.setSelected(true);
                    }
                }
        );

        JButton uAll = new JButton("Unselect All");
        uAll.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //Do stuff
                        for (JCheckBox c : items) c.setSelected(false);
                    }
                }
        );

        JButton update = new JButton("Update View");
        update.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //Do stuff
                        updateSelection();
                    }
                }
        );

        buttons.add(sAll);
        buttons.add(uAll);
        buttons.add(update);
        panelMain.add(buttons, BorderLayout.PAGE_START);
    }

    private void createNewSelectedInfo() {
        Sensor s;

        for (int i = 0; i < items.size(); i++) {
            if (createLabels) {
                JLabel tmp = new JLabel("");
                sensors.put(itemSensors.get(i).getID(), tmp);
            }
            if (items.get(i).isSelected()) {
                s = itemSensors.get(i);
                addComp(0, i + 1, new JLabel(s.getTag()), selectedPanel, selectedConstraints, selectedLayout);
                addComp(1, i + 1, new JLabel(s.getDescription()), selectedPanel, selectedConstraints, selectedLayout);
                addComp(2, i + 1, sensors.get(s.getID()), selectedPanel, selectedConstraints, selectedLayout);
                addComp(3, i + 1, new JLabel(s.getUnits()), selectedPanel, selectedConstraints, selectedLayout);
            }
        }

        createLabels = false;
        scrollPanelSelect = new JScrollPane(selectedPanel);
        scrollPanelSelect.getVerticalScrollBar().setUnitIncrement(20);
    }

    private void updateSelection() {
        innerPanel.remove(scrollPanelSelect);
        createNewSelectedPane();

        createNewSelectedInfo();

        addComp(1, 0, scrollPanelSelect, innerPanel, innerConstraints, innerLayout);
        panelMain.add(innerPanel);
        panelMain.validate();
        panelMain.repaint();
    }

    /**
     * This method returns the panel that the view was drawn on
     * @return The panel
     */
    public JPanel getPane() {
        return panelMain;
    }

    private void createNewSelectedPane() {
        selectedLayout = new GridBagLayout();
        selectedConstraints = new GridBagConstraints();

        selectedConstraints.fill = GridBagConstraints.NONE;
        selectedConstraints.anchor = GridBagConstraints.CENTER;
        //    selectedConstraints.weighty = 1;
        selectedPanel = new JPanel(selectedLayout);

        addComp(0, 0, new JLabel("  Sensor Tag:  "), selectedPanel, selectedConstraints, selectedLayout);
        addComp(1, 0, new JLabel("  Sensor Desc.:  "), selectedPanel, selectedConstraints, selectedLayout);
        addComp(2, 0, new JLabel("  Sensor Value:  "), selectedPanel, selectedConstraints, selectedLayout);
        addComp(3, 0, new JLabel("  Sensor Units:  "), selectedPanel, selectedConstraints, selectedLayout);

    }

    private void createConstraints() {
        createNewSelectedPane();
        ////
        itemsLayout = new GridBagLayout();
        itemsConstraints = new GridBagConstraints();
        itemsPanel = new JPanel(itemsLayout);

        itemsConstraints.fill = GridBagConstraints.NONE;
        itemsConstraints.anchor = GridBagConstraints.CENTER;
        itemsConstraints.weighty = 1;
        ////

        innerLayout = new GridBagLayout();
        innerConstraints = new GridBagConstraints();


        innerConstraints.fill = GridBagConstraints.BOTH;
        innerConstraints.anchor = GridBagConstraints.CENTER;
        innerConstraints.weighty = 1;
        innerPanel = new JPanel(innerLayout);
        ////
    }

    private void addComp(int x, int y, Component comp, JPanel panel, GridBagConstraints constraints, GridBagLayout layout) {
        constraints.gridx = x;
        constraints.gridy = y;

        layout.setConstraints(comp, constraints);
        panel.add(comp);
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

}

