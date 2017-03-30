package cockpit.database;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by CraigLombardo on 3/12/17.
 */

public class MaintenanceView {

    private GridBagLayout innerLayout;
    private GridBagConstraints innerConstraints;
    private JPanel innerPanel;
    private JPanel pane;
    private JScrollPane scrollPanel;

    private DBHandler handler;
    private SCADASystem sys;

    private HashMap<Integer, JLabel> sensors;

    private SCADAViewer viewer;
    private int view;

    public MaintenanceView(DBHandler dbHandler, SCADASystem scadaSys, SCADAViewer viewer, int viewNumber) {
        sys = scadaSys;
        handler = dbHandler;

        pane = new JPanel();
        sensors = new HashMap<>();

        this.viewer = viewer;
        view = viewNumber;

        createMapping(handler.getIDDescUnitsTag());

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

    public JScrollPane getPane() {
        return scrollPanel;
    }

    private void updateNow(HashMap<Integer, Sensor> sysMap) {
        if (view == viewer.currentView) {
            for (Map.Entry<Integer, Sensor> entry : sysMap.entrySet()) {
                sensors.get(entry.getKey()).setText(entry.getValue().getValue());
            }
        }
    }

    public void createHeader() {
        addComp(0, 0, new JLabel("  Sensor Tag  "));
        addComp(1, 0, new JLabel("  Sensor Desc  "));
        addComp(2, 0, new JLabel("  Sensor Value  "));
        addComp(3, 0, new JLabel("  Sensor Units  "));
    }

    public void createMapping(ArrayList<ArrayList<String>> info) {
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

        int row = 1;
        for (ArrayList<String> r : info) {
            id = r.get(0);
            name = r.get(1);
            units = r.get(2);
            tag = r.get(3);

            addComp(0, row, new JLabel(tag));
            addComp(1, row, new JLabel(name));

            JLabel valueField = new JLabel("");
            sensors.put(Integer.parseInt(id), valueField);
            addComp(2, row, valueField);

            addComp(3, row++, new JLabel(units));
        }

        HashMap<Integer, Sensor> m = sys.getMap();

        updateNow(m);
    }

    public void addComp(int x, int y, Component comp) {
        innerConstraints.gridx = x;
        innerConstraints.gridy = y;

        innerLayout.setConstraints(comp, innerConstraints);
        innerPanel.add(comp);
    }

}
