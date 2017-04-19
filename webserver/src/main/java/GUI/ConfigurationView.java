package GUI;

/**
 * Created by CraigLombardo on 3/14/17.
 */

import cockpit.database.DBHandler;
import cockpit.database.Sensor;
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class ConfigurationView {

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

    //Search Panel
    private GridBagLayout searchLayout;
    private GridBagConstraints searchConstraints;
    private JPanel searchPanel;

    //Fields
    private JTextField tag;
    private JTextField address;
    private JTextField offset;
    private JTextField byteLength;
    private JTextField description;
    private JComboBox<String> system;
    private JTextField units;
    private JComboBox<String> store;

    private JTextField correction;

    private JButton duplicate;

    private JButton editButton;

    private JTextField searchBox;

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
        createSearchBar();
        panelMain.add(innerPanel);
    }

    public static void main(String[] args) {

        DBHandler handler = new DBHandler();

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
        createSearchConstraints();
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

    private void createSearchConstraints() {
        searchLayout = new GridBagLayout();
        searchConstraints = new GridBagConstraints();

        searchConstraints.fill = GridBagConstraints.BOTH;
        searchConstraints.anchor = GridBagConstraints.CENTER;
        searchConstraints.weighty = 1;
        searchPanel = new JPanel(searchLayout);
    }

    private void addSearchComp(int x, int y, int wx, Component comp) {
        searchConstraints.gridx = x;
        searchConstraints.gridy = y;

        searchConstraints.weightx = wx;

        searchLayout.setConstraints(comp, searchConstraints);
        searchPanel.add(comp);
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
        addExistingItemsComp(0, 0, new JLabel(" Tag "));
        addExistingItemsComp(1, 0, new JLabel("  Name  "));

        ArrayList<ArrayList<String>> info = handler.getSensorInfo();

        JLabel sensorLabel;
        Sensor sensor;

        int thisID;
        cRow = ROW_START;

        for (ArrayList<String> s : info) {
            thisID = Integer.parseInt(s.get(0));
            if (cID < thisID) cID = thisID;
            sensor = new Sensor(thisID, s.get(1), Integer.parseInt(s.get(2)),
                    Integer.parseInt(s.get(3)), Integer.parseInt(s.get(4)), s.get(5), s.get(6),
                    s.get(7), Integer.parseInt(s.get(8)), Double.parseDouble(s.get(9)));

            JButton sensorButton = new JButton(sensor.getTag());
            sensorButton.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            buttonPushed(sensorButton.getText());
                        }
                    }
            );

            sensorLabel = new JLabel(sensor.getDescription());

            addExistingItemsComp(0, 1, new JLabel(""));
            addExistingItemsComp(0, cRow, sensorButton);
            addExistingItemsComp(1, cRow++, sensorLabel);

            sensors.put(sensor.getTag(), new SensorTuple(sensorButton, sensorLabel, sensor));

        }

//        cRow = ROW_START;
//        SortedSet<String> keys = new TreeSet<>(sensors.keySet());
//        for (String key : keys) {
//            SensorTuple s = sensors.get(key);
//
//        }
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

        correction.setText("");

        duplicate.setEnabled(false);
        editButton.setText("Add Record");

        if (searchBox != null) searchBox.setText("");
    }

    private void buttonPushed(String name) {
        Sensor s;
        try {
            s = sensors.get(name).sensor;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panelMain, "This tag does not exist.");
            return;
        }

        tag.setText(s.getTag());
        tag.setEditable(false);
        address.setText("" + s.getAddress());
        offset.setText("" + s.getOffset());
        byteLength.setText("" + s.getByteLength());
        description.setText(s.getDescription());
        system.setSelectedItem(s.getSystem());
        units.setText(s.getUnits());
        store.setSelectedItem("" + s.getStore());

        correction.setText("" + s.getCorrection());

        duplicate.setEnabled(true);
        editButton.setText("Update Record");

        searchBox.setText(name);
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

        correction = new JTextField(12);
        correction.setHorizontalAlignment(JTextField.CENTER);

        duplicate = new JButton("Duplicate Record");
        duplicate.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        duplicateRecord();
                    }
                }
        );

        editButton = new JButton("");
        editButton.addActionListener(
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

        addNewItemsComp(1, 0, new JLabel(""));
        addNewItemsComp(1, 1, new JLabel(" "));

        addNewItemsComp(0, 2, new JLabel("  Tag  "));
        addNewItemsComp(0, 3, tag);
//        addNewItemsComp(0, 4, new JLabel(" "));

        addNewItemsComp(1, 2, new JLabel("  Address  "));
        addNewItemsComp(1, 3, address);
//        addNewItemsComp(1, 4, new JLabel(" "));

        addNewItemsComp(2, 2, new JLabel("  Offset  "));
        addNewItemsComp(2, 3, offset);
//        addNewItemsComp(2, 4, new JLabel(" "));

        addNewItemsComp(0, 5, new JLabel("  Bytes  "));
        addNewItemsComp(0, 6, byteLength);
//        addNewItemsComp(0, 7, new JLabel(" "));

        addNewItemsComp(1, 5, new JLabel("  Description  "));
        addNewItemsComp(1, 6, description);
//        addNewItemsComp(1, 7, new JLabel(" "));

        addNewItemsComp(2, 5, new JLabel("  Units  "));
        addNewItemsComp(2, 6, units);
//        addNewItemsComp(2, 7, new JLabel(" "));

        addNewItemsComp(0, 8, new JLabel("  Correction  "));
        addNewItemsComp(0, 9, correction);
//        addNewItemsComp(0, 10, new JLabel(" "));

        addNewItemsComp(1, 8, new JLabel("  Store  "));
        addNewItemsComp(1, 9, store);

        addNewItemsComp(2, 8, new JLabel("  System  "));
        addNewItemsComp(2, 9, system);

        addNewItemsComp(0, 10, new JLabel(" "));
        addNewItemsComp(0, 11, new JLabel(" "));
        addNewItemsComp(2, 12, duplicate);

        addNewItemsComp(0, 15, new JLabel(" "));
        addNewItemsComp(0, 16, new JLabel(" "));
        addNewItemsComp(0, 17, newButton);
        addNewItemsComp(1, 17, editButton);
        addNewItemsComp(2, 17, deleteButton);

        cleanItemInfo();
    }

    private void duplicateRecord() {
        if (duplicate.isEnabled()) {
            tag.setText("");
            tag.setEditable(true);
            editButton.setText("Add Record");
            duplicate.setEnabled(false);
        }
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

                String[] data = {"", "", "", "", "", "", "", "", ""};
                data[0] = tag.getText().toUpperCase();
                data[1] = address.getText();
                data[2] = offset.getText();
                data[3] = byteLength.getText();
                data[4] = description.getText();
                data[5] = (String) system.getSelectedItem();
                data[6] = units.getText();
                data[7] = (String) store.getSelectedItem();
                data[8] = correction.getText();

                handler.updateSensor(tag.isEditable(), data);

                if (tag.isEditable()) {

                    Sensor newSensor = new Sensor(cID++, data[0], Integer.parseInt(data[1]),
                            Integer.parseInt(data[2]), Integer.parseInt(data[3]), data[4],
                            data[5], data[6], data[7].equals("true") ? 1 : 0,
                            Double.parseDouble(data[8]));

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

                    SortedSet<String> keys = new TreeSet<>(sensors.keySet());

                    cRow = ROW_START;

                    for (String key : keys) {
                        SensorTuple s = sensors.get(key);

                        addExistingItemsComp(0, cRow, s.button);
                        addExistingItemsComp(1, cRow++, s.label);
                    }

                    tag.setText(data[0]);

                    panelMain.validate();
                    panelMain.repaint();

                    duplicate.setEnabled(true);

                } else {
                    SensorTuple tup = sensors.get(tag.getText());
                    Sensor s = tup.sensor;

                    s.setTag(tag.getText());
                    s.setAddress(Integer.parseInt(address.getText()));
                    s.setOffset(Integer.parseInt(offset.getText()));
                    s.setByteLength(Integer.parseInt(byteLength.getText()));
                    s.setDescription(description.getText());
                    s.setUnits(units.getText());
                    s.setSystem((String) system.getSelectedItem());
                    s.setStore(store.getSelectedItem().equals("true"));

                    s.setCorrection(Double.parseDouble(correction.getText()));

                    tup.label.setText(description.getText());
                }
            } else System.out.println("NOT EDITING");
        } else {

        }
    }

    private Boolean checkName(String name, Boolean print) {
        for (Map.Entry<String, SensorTuple> entry : sensors.entrySet()) {
            if (entry.getKey().equals(name)) {
                if (print) JOptionPane.showMessageDialog(panelMain, "This tag has already been taken, select another!");
                return false;
            }
        }
        return true;
    }

    private Boolean checkAllFields() {
        String cTag = tag.getText();
        if (!cTag.isEmpty()) {
            if (tag.isEditable()) {
                if (!checkName(cTag, true)) {
                    return false;
                }
            }
        }

        Boolean addressCheck = isNumber(address.getText());
        Boolean offsetCheck = isNumber(offset.getText());
        Boolean byteCheck = isNumber(byteLength.getText());
        Boolean descriptionCheck = !description.getText().isEmpty();
        Boolean unitsCheck = !units.getText().isEmpty();
        Boolean correctionCheck = !correction.getText().isEmpty();

        return (addressCheck && offsetCheck && byteCheck && descriptionCheck &&
                unitsCheck && correctionCheck);
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

    private void createSearchBar() {

        JLabel title = new JLabel("Tag Lookup");
        title.setHorizontalAlignment(SwingConstants.CENTER);

        searchBox = new JTextField("");
        searchBox.setHorizontalAlignment(SwingConstants.CENTER);

        searchBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonPushed(searchBox.getText().toUpperCase());
            }
        });

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        buttonPushed(searchBox.getText().toUpperCase());
                    }
                }
        );

        addSearchComp(0, 0, 40, new JLabel(" "));
        addSearchComp(1, 0, 10, title);
        addSearchComp(2, 0, 10, new JLabel(""));
        addSearchComp(3, 0, 40, new JLabel(" "));

        addSearchComp(0, 1, 40, new JLabel(" "));
        addSearchComp(1, 1, 10, searchBox);
        addSearchComp(2, 1, 10, searchButton);
        addSearchComp(3, 1, 40, new JLabel(" "));

        panelMain.add(searchPanel, BorderLayout.PAGE_START);
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
