/*
package cockpit.database;

*/
/**
 * Created by CraigLombardo on 3/18/17.
 *//*


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

public class ConfigurationView extends JPanel {

    private static int ROW_START = 2;
    private JScrollPane scrollPanelExistingItems;
    private GridBagLayout existingItemsLayout;
    private GridBagConstraints existingItemsConstraints;
    private JPanel existingItemsPanel;
    private JScrollPane scrollPanelNewItems;
    private GridBagLayout newItemsLayout;
    private GridBagConstraints newItemsConstraints;
    private JPanel newItemsPanel;
    private GridBagLayout innerLayout;
    private GridBagConstraints innerConstraints;
    private JPanel innerPanel;
    private JPanel panelMain;
    private JTextField id;
    private JTextField name;
    private JComboBox<String> system;
    private JTextField units;
    private JTextField slope;
    private JTextField offset;
    private JTextField criticality;
    private JTextField stableLow;
    private JTextField stableHigh;
    private JTextField criticalLow;
    private JTextField criticalHigh;
    private HashMap<String, SensorTuple> sensors;
    private DBHandler handler;
    private int cRow = ROW_START;

    public ConfigurationView(DBHandler h) {
        handler = h;
        panelMain = new JPanel(new BorderLayout());

        sensors = new HashMap<String, SensorTuple>();

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

//        JButton sensorButton;
        JLabel sensorLabel;
        Sensor sensor;

        for (ArrayList<String> s : info) {
            sensor = new Sensor(s.get(0), s.get(1), s.get(3), s.get(2), s.get(10),
                    s.get(11), s.get(9), s.get(5), s.get(6), s.get(7), s.get(8));

            JButton sensorButton = new JButton(sensor.hexID);
            sensorButton.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            buttonPushed(sensorButton.getText());
                        }
                    }
            );

            sensorLabel = new JLabel(sensor.name);

            sensors.put(sensor.hexID, new SensorTuple(sensorButton, sensorLabel, sensor));

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
        id.setText("");
        id.setEditable(true);
        name.setText("");
        system.setSelectedIndex(0);
        units.setText("");
        slope.setText("");
        offset.setText("");
        criticality.setText("");
        stableLow.setText("");
        stableHigh.setText("");
        criticalLow.setText("");
        criticalHigh.setText("");
    }

    private void buttonPushed(String e) {
        SensorInfo s = sensors.get(e).info;

        id.setText(s.hexID);
        id.setEditable(false);
        name.setText(s.name);
        system.setSelectedItem(s.system);
        units.setText(s.units);
        slope.setText(s.slope);
        offset.setText(s.offset);
        criticality.setText(s.criticality);
        stableLow.setText(s.stableLow);
        stableHigh.setText(s.stableHigh);
        criticalLow.setText(s.criticalLow);
        criticalHigh.setText(s.criticalHigh);
    }

    private void createNewItemsPane() {
        id = new JTextField("", 4);
        id.setHorizontalAlignment(JTextField.CENTER);
        id.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (id.getText().length() == 5) {
                    e.consume();
                }
            }
        });

        name = new JTextField(12);
        name.setHorizontalAlignment(JTextField.CENTER);

        system = new JComboBox<String>();
        system.setMaximumSize(system.getPreferredSize());
        system.setEditable(false);
        ((JLabel) system.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        setSystemTypes();

        units = new JTextField(12);
        units.setHorizontalAlignment(JTextField.CENTER);

        slope = new JTextField(12);
        slope.setHorizontalAlignment(JTextField.CENTER);

        offset = new JTextField(12);
        offset.setHorizontalAlignment(JTextField.CENTER);

        criticality = new JTextField(12);
        criticality.setHorizontalAlignment(JTextField.CENTER);

        stableLow = new JTextField(12);
        stableLow.setHorizontalAlignment(JTextField.CENTER);

        stableHigh = new JTextField(12);
        stableHigh.setHorizontalAlignment(JTextField.CENTER);

        criticalLow = new JTextField(12);
        criticalLow.setHorizontalAlignment(JTextField.CENTER);

        criticalHigh = new JTextField(12);
        criticalHigh.setHorizontalAlignment(JTextField.CENTER);

        addNewItemsComp(1, 0, new JLabel(""));
        addNewItemsComp(1, 1, new JLabel(" "));

        addNewItemsComp(0, 2, new JLabel("  ID  "));
        addNewItemsComp(0, 3, id);
        addNewItemsComp(0, 4, new JLabel(" "));

        addNewItemsComp(1, 2, new JLabel("  Name  "));
        addNewItemsComp(1, 3, name);
        addNewItemsComp(1, 4, new JLabel(" "));

        addNewItemsComp(2, 2, new JLabel("  System  "));
        addNewItemsComp(2, 3, system);
        addNewItemsComp(2, 4, new JLabel(" "));

        addNewItemsComp(0, 5, new JLabel("  Units  "));
        addNewItemsComp(0, 6, units);
        addNewItemsComp(0, 7, new JLabel(" "));

        addNewItemsComp(1, 5, new JLabel("  Slope  "));
        addNewItemsComp(1, 6, slope);
        addNewItemsComp(1, 7, new JLabel(" "));

        addNewItemsComp(2, 5, new JLabel("  Offset  "));
        addNewItemsComp(2, 6, offset);
        addNewItemsComp(2, 7, new JLabel(" "));

        addNewItemsComp(0, 8, new JLabel("  Criticality  "));
        addNewItemsComp(0, 9, criticality);
        addNewItemsComp(0, 10, new JLabel(" "));

        addNewItemsComp(1, 8, new JLabel("  Stable Low  "));
        addNewItemsComp(1, 9, stableLow);
        addNewItemsComp(1, 10, new JLabel(" "));

        addNewItemsComp(2, 8, new JLabel("  Stable High  "));
        addNewItemsComp(2, 9, stableHigh);
        addNewItemsComp(2, 10, new JLabel(" "));

        addNewItemsComp(0, 11, new JLabel("  Critical Low  "));
        addNewItemsComp(0, 12, criticalLow);
        addNewItemsComp(0, 13, new JLabel(" "));

        addNewItemsComp(1, 11, new JLabel("  Critical High  "));
        addNewItemsComp(1, 12, criticalHigh);
        addNewItemsComp(1, 13, new JLabel(" "));

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

        addNewItemsComp(0, 14, new JLabel(" "));
        addNewItemsComp(0, 15, newButton);
        addNewItemsComp(1, 15, submit);
        addNewItemsComp(2, 15, deleteButton);

        cleanItemInfo();
    }

    private void deleteItem() {
        if (!id.isEditable()) {
            int dialogButton = JOptionPane.OK_CANCEL_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you would like to delete this entry permanently from the DB?", "Warning", dialogButton);
            if (dialogResult == JOptionPane.OK_OPTION) {
                SensorTuple s = sensors.get(id.getText());
                existingItemsPanel.remove(s.button);
                existingItemsPanel.remove(s.label);
                sensors.remove(id.getText());
                handler.removeSensor("" + Integer.parseInt(id.getText().substring(2, 5), 16));
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
                String intID = "" + Integer.parseInt(id.getText().substring(2, 5), 16);

                handler.updateSensor(id.isEditable(), intID, name.getText(),
                        units.getText(), (String) system.getSelectedItem(), stableLow.getText(),
                        stableHigh.getText(), criticalLow.getText(), criticalHigh.getText(),
                        criticality.getText(), slope.getText(), offset.getText());

                SensorInfo newSensor = new SensorInfo(intID, name.getText(), (String) system.getSelectedItem(),
                        units.getText(), slope.getText(), offset.getText(), criticality.getText(),
                        stableLow.getText(), stableHigh.getText(), criticalLow.getText(), criticalHigh.getText());

                if (id.isEditable()) {

                    for (Map.Entry<String, SensorTuple> entry : sensors.entrySet()) {
                        existingItemsPanel.remove(entry.getValue().button);
                        existingItemsPanel.remove(entry.getValue().label);
                    }

                    JButton sensorButton = new JButton(newSensor.hexID);
                    sensorButton.addActionListener(
                            new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    buttonPushed(sensorButton.getText());
                                }
                            }
                    );
                    JLabel newLabel = new JLabel(newSensor.name);
                    sensors.put(newSensor.hexID, new SensorTuple(sensorButton, newLabel, newSensor));

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
                    String buff = "" + Integer.toHexString(Integer.parseInt(intID));
                    String hID = "0x000".substring(0, 5 - buff.length()) + buff;
                    SensorTuple t = sensors.get(hID);
                    t.label.setText(name.getText());

                    sensors.remove(hID);
                    sensors.put(id.getText(), new SensorTuple(t.button, t.label, newSensor));
                }
            } else System.out.println("NOT EDITING");
        } else {

        }
    }

    private Boolean checkAllFields() {
        Boolean idCheck = !id.getText().isEmpty();
        Boolean nameCheck = !name.getText().isEmpty();
        Boolean unitsCheck = !units.getText().isEmpty();
        Boolean sLowCheck = isNumber(stableLow.getText());
        Boolean sHighCheck = isNumber(stableHigh.getText());
        Boolean critLowCheck = isNumber(criticalLow.getText());
        Boolean critHighCheck = isNumber(criticalHigh.getText());
        Boolean critCheck = isNumber(criticality.getText());
        Boolean slopeCheck = isNumber(slope.getText());
        Boolean offsetCheck = isNumber(offset.getText());

        return (idCheck && nameCheck && unitsCheck &&
                sLowCheck && sHighCheck && critLowCheck &&
                critHighCheck && critCheck && slopeCheck &&
                offsetCheck);
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
*/
