package cockpit.database;

/**
 * Created by CraigLombardo on 3/14/17.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomView extends JPanel {

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

    private HashMap<String, JLabel> sensors;
    private ArrayList<JCheckBox> items;
    private ArrayList<CustomTuple> itemTuples;

    private SCADAViewer viewer;
    private int view;

    private JPanel panelMain;
    private Boolean createLabels = true;


    public CustomView(DBHandler dbHandler, SCADASystem scadaSys, SCADAViewer viewer, int viewNumber) {
        panelMain = new JPanel(new BorderLayout());
        sensors = new HashMap<String, JLabel>();

        sys = scadaSys;
        handler = dbHandler;
        this.viewer = viewer;
        view = viewNumber;

        items = new ArrayList<JCheckBox>();
        itemTuples = new ArrayList<CustomTuple>();

        createConstraints();

        createButtons();
        addComp(0, 1, new JLabel("  "), itemsPanel, itemsConstraints, itemsLayout);
        addComp(1, 1, new JLabel("  Sensor Tag:  "), itemsPanel, itemsConstraints, itemsLayout);
        addComp(2, 1, new JLabel("  Sensor Desc:  "), itemsPanel, itemsConstraints, itemsLayout);
        addComp(3, 1, new JLabel("  Sensor Units:  "), itemsPanel, itemsConstraints, itemsLayout);

        createItems(handler.getIDDescUnitsTag());

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
        if (view == viewer.currentView) {
            for (Map.Entry<Integer, Sensor> entry : sysMap.entrySet()) {
//                System.out.println("sys "+entry.getKey());
                sensors.get("" + entry.getKey()).setText(entry.getValue().getValue());
            }
        }
    }

    public void createItems(ArrayList<ArrayList<String>> info) {

        int row = 2;
        String id;
        String name;
        String units;
        String hexVal;
        String idString;

        String tag;

        for (ArrayList<String> r : info) {
            id = r.get(0);
            name = r.get(1);
            units = r.get(2);
            tag = r.get(3);

            hexVal = Integer.toHexString(Integer.parseInt(id));
            idString = "0x000".substring(0, 5 - hexVal.length()) + hexVal;

            JCheckBox check = new JCheckBox();
            items.add(check);
            itemTuples.add(new CustomTuple(id, idString, name, units, tag));
            addComp(0, row, check, itemsPanel, itemsConstraints, itemsLayout);
            addComp(1, row, new JLabel(tag), itemsPanel, itemsConstraints, itemsLayout);
            addComp(2, row, new JLabel(name), itemsPanel, itemsConstraints, itemsLayout);
            addComp(3, row++, new JLabel(units), itemsPanel, itemsConstraints, itemsLayout);

        }
    }

    public void createButtons() {
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

    public void createNewSelectedInfo() {
        CustomTuple checkedTuple;

        for (int i = 0; i < items.size(); i++) {
            if (createLabels) {
                JLabel tmp = new JLabel("");
                sensors.put("" + itemTuples.get(i).tID, tmp);
            }
            if (items.get(i).isSelected()) {
                checkedTuple = itemTuples.get(i);
                addComp(0, i + 1, new JLabel(checkedTuple.tTag), selectedPanel, selectedConstraints, selectedLayout);
                addComp(1, i + 1, new JLabel(checkedTuple.tName), selectedPanel, selectedConstraints, selectedLayout);
                addComp(2, i + 1, sensors.get(checkedTuple.tID), selectedPanel, selectedConstraints, selectedLayout);
                addComp(3, i + 1, new JLabel(checkedTuple.tUnits), selectedPanel, selectedConstraints, selectedLayout);
            }
        }

        createLabels = false;
        scrollPanelSelect = new JScrollPane(selectedPanel);
        scrollPanelSelect.getVerticalScrollBar().setUnitIncrement(20);
    }

    public void updateSelection() {
        innerPanel.remove(scrollPanelSelect);
        createNewSelectedPane();

        createNewSelectedInfo();

        addComp(1, 0, scrollPanelSelect, innerPanel, innerConstraints, innerLayout);
        panelMain.add(innerPanel);
        panelMain.validate();
        panelMain.repaint();
    }

    public JPanel getPane() {
        return panelMain;
    }

    public void createNewSelectedPane() {
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

    public void createConstraints() {
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

    public void addComp(int x, int y, Component comp, JPanel panel, GridBagConstraints constraints, GridBagLayout layout) {
        constraints.gridx = x;
        constraints.gridy = y;

        layout.setConstraints(comp, constraints);
        panel.add(comp);
    }

    private class CustomTuple {

        private String tID;
        private String tIDString;
        private String tName;
        private String tUnits;
        private String tTag;

        private CustomTuple(String i, String iS, String n, String u, String t) {
            tID = i;
            tIDString = iS;
            tName = n;
            tUnits = u;
            tTag = t;
        }
    }

}

