package cockpit.database;

/**
 * Created by CraigLombardo on 3/14/17.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class ConfigurationView extends JPanel {

    private static int ROW_START = 2;
    private int cRow = ROW_START;

    //Existing Items
    private JScrollPane scrollPanelExistingItems;
    private GridBagLayout existingItemsLayout;
    private GridBagConstraints existingItemsConstraints;
    private JPanel existingItemsPanel;

    //New Items
    private JScrollPane scrollPanelNewItems;
    private GridBagLayout newItemsLayout;
    private GridBagConstraints newItemsConstraints;
    private JPanel newItemsPanel;

    //Inner Panel
    private GridBagLayout innerLayout;
    private GridBagConstraints innerConstraints;
    private JPanel innerPanel;
    private JPanel panelMain;

    //Fields
    private JTextField tag;
    private JTextField address;
    private JTextField offset;
    private JTextField byteLength;
    private JTextField description;
    private JComboBox<String> system;
    private JTextField units;
    private JComboBox<String> store;

    private HashMap<String, SensorTuple> sensors;
    private DBHandler handler;

    private int cID;

    public ConfigurationView(DBHandler h) {
        handler = h;
        panelMain = new JPanel(new BorderLayout());

        sensors = new HashMap<>();

        createConstraints();

        createExistingItems();
        createNewItemsPane();


        createScrollPanes();
        panelMain.add(innerPanel);
    }

    public static void main(String[] args) {

        DBHandler handler = new DBHandler("SCADA.db", "SQLSchema/");

        JFrame window = new JFrame("Test");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 400);
        window.setMinimumSize(new Dimension(300, 300));

        ConfigurationView test1 = new ConfigurationView(handler);
        window.getContentPane().add(test1.getPanel(), BorderLayout.CENTER);

        window.setVisible(true);
    }

    private void createScrollPanes() {
        scrollPanelExistingItems = new JScrollPane(existingItemsPanel);
        scrollPanelExistingItems.getVerticalScrollBar().setUnitIncrement(20);

        scrollPanelNewItems = new JScrollPane(newItemsPanel);
        scrollPanelNewItems.getVerticalScrollBar().setUnitIncrement(20);

        innerConstraints.weighty = 1;
        innerConstraints.weightx = 0.4;
        addInnerComp(0, 0, scrollPanelExistingItems);
        innerConstraints.weightx = 0.6;
        addInnerComp(1, 0, scrollPanelNewItems);
    }

    public JPanel getPanel() {
        return panelMain;
    }

    private void createConstraints() {
        createExistingConstraints();
        createNewItemConstraints();
        createInnerConstraints();
    }

    private void createExistingConstraints() {
        existingItemsLayout = new GridBagLayout();
        existingItemsConstraints = new GridBagConstraints();
        existingItemsPanel = new JPanel(existingItemsLayout);

        existingItemsConstraints.fill = GridBagConstraints.NONE;
        existingItemsConstraints.anchor = GridBagConstraints.CENTER;

    }

    private void createNewItemConstraints() {
        newItemsLayout = new GridBagLayout();
        newItemsConstraints = new GridBagConstraints();
        newItemsPanel = new JPanel(newItemsLayout);

        newItemsConstraints.fill = GridBagConstraints.NONE;
        newItemsConstraints.anchor = GridBagConstraints.CENTER;
    }

    private void createInnerConstraints() {
        innerLayout = new GridBagLayout();
        innerConstraints = new GridBagConstraints();

        innerConstraints.fill = GridBagConstraints.BOTH;
        innerConstraints.anchor = GridBagConstraints.CENTER;
        innerConstraints.weighty = 1;
        innerPanel = new JPanel(innerLayout);
    }

    private void addExistingItemsComp(int x, int y, Component comp) {
        existingItemsConstraints.gridx = x;
        existingItemsConstraints.gridy = y;

        existingItemsLayout.setConstraints(comp, existingItemsConstraints);
        existingItemsPanel.add(comp);
    }

    private void addNewItemsComp(int x, int y, Component comp) {
        newItemsConstraints.gridx = x;
        newItemsConstraints.gridy = y;

        newItemsLayout.setConstraints(comp, newItemsConstraints);
        newItemsPanel.add(comp);
    }

    private void addInnerComp(int x, int y, Component comp) {
        innerConstraints.gridx = x;
        innerConstraints.gridy = y;

        innerLayout.setConstraints(comp, innerConstraints);
        innerPanel.add(comp);
    }

    private void createExistingItems() {
        addExistingItemsComp(0, 0, new JLabel(" ID "));
        addExistingItemsComp(1, 0, new JLabel("  Name  "));

        ArrayList<ArrayList<String>> info = handler.getSensorInfo();

        JLabel sensorLabel;
        Sensor sensor;

        int thisID;

        for (ArrayList<String> s : info) {
            thisID = Integer.parseInt(s.get(0));
            if (cID < thisID) cID = thisID;

//                public Sensor(int i, String t, int a, int o, int b, String d, String s, String u, int st) {


                sensor = new Sensor(thisID, s.get(1), Integer.parseInt(s.get(2)),
                    Integer.parseInt(s.get(3)), Integer.parseInt(s.get(4)), s.get(5), s.get(6),
                    s.get(7), Integer.parseInt(s.get(8)));

            JButton sensorButton = new JButton(sensor.getTag());
            sensorButton.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            buttonPushed(sensorButton.getText());
                        }
                    }
            );

            sensorLabel = new JLabel(sensor.getDescription());

            sensors.put(sensor.getTag(), new SensorTuple(sensorButton, sensorLabel, sensor));

        }

        addExistingItemsComp(0, 1, new JLabel(""));
        cRow = ROW_START;
        SortedSet<String> keys = new TreeSet<String>(sensors.keySet());
        for (String key : keys) {
            SensorTuple s = sensors.get(key);

            addExistingItemsComp(0, cRow, s.button);
            addExistingItemsComp(1, cRow++, s.label);
        }
    }

    private void cleanItemInfo() {
        tag.setText("");
        tag.setEditable(true);
        address.setText("");
        offset.setText("");
        byteLength.setText("");
        description.setText("");
        system.setSelectedIndex(0);
        units.setText("");
        store.setSelectedIndex(0);
    }

    private void buttonPushed(String e) {
        Sensor s = sensors.get(e).sensor;

        tag.setText(s.getTag());
        tag.setEditable(false);
        address.setText("" + s.getAddress());
        offset.setText("" + s.getOffset());
        byteLength.setText("" + s.getByteLength());
        description.setText(s.getDescription());
        system.setSelectedItem(s.getSystem());
        units.setText(s.getUnits());
        store.setSelectedItem("" + s.getStore());
    }

    private void createNewItemsPane() {

        tag = new JTextField(12);
        tag.setHorizontalAlignment(JTextField.CENTER);

        address = new JTextField(12);
        address.setHorizontalAlignment(JTextField.CENTER);

        offset = new JTextField(12);
        offset.setHorizontalAlignment(JTextField.CENTER);

        byteLength = new JTextField(12);
        byteLength.setHorizontalAlignment(JTextField.CENTER);

        description = new JTextField(12);
        description.setHorizontalAlignment(JTextField.CENTER);

        system = new JComboBox<>();
        system.setMaximumSize(system.getPreferredSize());
        system.setEditable(false);
        ((JLabel) system.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        setSystemTypes();

        units = new JTextField(12);
        units.setHorizontalAlignment(JTextField.CENTER);

        store = new JComboBox<>();
        store.setMaximumSize(store.getPreferredSize());
        store.setEditable(false);
        ((JLabel) store.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        setStoreTypes();

        addNewItemsComp(1, 0, new JLabel(""));
        addNewItemsComp(1, 1, new JLabel(" "));

        addNewItemsComp(0, 2, new JLabel("  Tag  "));
        addNewItemsComp(0, 3, tag);
        addNewItemsComp(0, 4, new JLabel(" "));

        addNewItemsComp(1, 2, new JLabel("  Address  "));
        addNewItemsComp(1, 3, address);
        addNewItemsComp(1, 4, new JLabel(" "));

        addNewItemsComp(2, 2, new JLabel("  Offset  "));
        addNewItemsComp(2, 3, offset);
        addNewItemsComp(2, 4, new JLabel(" "));

        addNewItemsComp(0, 5, new JLabel("  Bytes  "));
        addNewItemsComp(0, 6, byteLength);
        addNewItemsComp(0, 7, new JLabel(" "));

        addNewItemsComp(1, 5, new JLabel("  Description  "));
        addNewItemsComp(1, 6, description);
        addNewItemsComp(1, 7, new JLabel(" "));

        addNewItemsComp(2, 5, new JLabel("  System  "));
        addNewItemsComp(2, 6, system);
        addNewItemsComp(2, 7, new JLabel(" "));

        addNewItemsComp(0, 8, new JLabel("  Units  "));
        addNewItemsComp(0, 9, units);
        addNewItemsComp(0, 10, new JLabel(" "));

        addNewItemsComp(1, 8, new JLabel("  Store  "));
        addNewItemsComp(1, 9, store);
        addNewItemsComp(1, 10, new JLabel(" "));


        JButton submit = new JButton("Add/Update");
        submit.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        editItem();
                    }
                }
        );

        JButton newButton = new JButton("New Item");
        newButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cleanItemInfo();
                    }
                }
        );

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        deleteItem();
                    }
                }
        );

        addNewItemsComp(0, 11, new JLabel(" "));
        addNewItemsComp(0, 12, newButton);
        addNewItemsComp(1, 12, submit);
        addNewItemsComp(2, 12, deleteButton);

        cleanItemInfo();
    }

    private void deleteItem() {
        if (!tag.isEditable()) {
            int dialogButton = JOptionPane.OK_CANCEL_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you would like to delete this entry permanently from the DB?", "Warning", dialogButton);
            if (dialogResult == JOptionPane.OK_OPTION) {
                SensorTuple s = sensors.get(tag.getText());
                existingItemsPanel.remove(s.button);
                existingItemsPanel.remove(s.label);
                sensors.remove(tag.getText());
                handler.removeSensor(tag.getText());
                panelMain.validate();
                panelMain.repaint();
                cleanItemInfo();
            } else {
            }
        }
    }

    private void editItem() {
        if (checkAllFields()) {
            int dialogButton = JOptionPane.OK_CANCEL_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you would like to make these changes to the DB?", "Warning", dialogButton);
            if (dialogResult == JOptionPane.OK_OPTION) {

                String[] data = {"", "", "", "", "", "", "", ""};
                data[0] = tag.getText();
                data[1] = address.getText();
                data[2] = offset.getText();
                data[3] = byteLength.getText();
                data[4] = description.getText();
                data[5] = (String) system.getSelectedItem();
                data[6] = units.getText();
                data[7] = (String) store.getSelectedItem();

                handler.updateSensor(tag.isEditable(), data);

                if (tag.isEditable()) {

                Sensor newSensor = new Sensor(cID++, data[0], Integer.parseInt(data[1]),
                        Integer.parseInt(data[2]), Integer.parseInt(data[3]), data[4],
                        data[5], data[6], data[7].equals("true") ? 1 : 0);

                    for (Map.Entry<String, SensorTuple> entry : sensors.entrySet()) {
                        existingItemsPanel.remove(entry.getValue().button);
                        existingItemsPanel.remove(entry.getValue().label);
                    }

                    JButton sensorButton = new JButton(newSensor.getTag());
                    sensorButton.addActionListener(
                            new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    buttonPushed(sensorButton.getText());
                                }
                            }
                    );
                    JLabel newLabel = new JLabel(newSensor.getDescription());
                    sensors.put(newSensor.getTag(), new SensorTuple(sensorButton, newLabel, newSensor));

                    SortedSet<String> keys = new TreeSet<String>(sensors.keySet());

                    cRow = ROW_START;

                    for (String key : keys) {
                        SensorTuple s = sensors.get(key);

                        addExistingItemsComp(0, cRow, s.button);
                        addExistingItemsComp(1, cRow++, s.label);
                    }

                    panelMain.validate();
                    panelMain.repaint();

                } else {
                    Sensor s = sensors.get(tag.getText()).sensor;

                    s.setTag(tag.getText());
                    s.setAddress(Integer.parseInt(address.getText()));
                    s.setOffset(Integer.parseInt(offset.getText()));
                    s.setByteLength(Integer.parseInt(byteLength.getText()));
                    s.setDescription(description.getText());
                    s.setUnits(units.getText());
                    s.setSystem((String) system.getSelectedItem());
                    s.setStore(store.getSelectedItem().equals("true"));
                }
            } else System.out.println("NOT EDITING");
        } else {

        }
    }

    private Boolean checkAllFields() {
    return true;
//        int i, String t, int a, int o, int b, String d, String s, String u, int st
//
//        Boolean tagCheck = isNumber(tag.getText());
//        Boolean nameCheck = !name.getText().isEmpty();
//        Boolean unitsCheck = !units.getText().isEmpty();
//        Boolean sLowCheck = isNumber(stableLow.getText());
//        Boolean sHighCheck = isNumber(stableHigh.getText());
//        Boolean critLowCheck = isNumber(criticalLow.getText());
//        Boolean critHighCheck = isNumber(criticalHigh.getText());
//        Boolean critCheck = isNumber(criticality.getText());
//        Boolean slopeCheck = isNumber(slope.getText());
//        Boolean offsetCheck = isNumber(offset.getText());
//
//        return (idCheck && nameCheck && unitsCheck &&
//                sLowCheck && sHighCheck && critLowCheck &&
//                critHighCheck && critCheck && slopeCheck &&
//                offsetCheck);
    }

    private Boolean isNumber(String s) {
        if (s.equals("")) return false;
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void setStoreTypes() {
        store.addItem("true");
        store.addItem("false");
    }

    private void setSystemTypes() {
        system.addItem("COOLING");
        system.addItem("DYNO");
        system.addItem("TSI");
        system.addItem("TSV");
    }

    private class SensorTuple {

        JButton button;
        JLabel label;
        Sensor sensor;

        public SensorTuple(JButton btn, JLabel lbl, Sensor s) {
            button = btn;
            label = lbl;
            sensor = s;
        }
    }
}
