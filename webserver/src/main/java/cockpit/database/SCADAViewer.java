package cockpit.database;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;


public class SCADAViewer extends JPanel {

    private JPanel cards;
    private Container pane;

    private DBHandler handler;

    private String file;

    private HashMap<String, JLabel> sensors;

    private SCADASystem sys;

//    private String[] comboBoxItems = {"Maintenance Mode", "Charging Mode", "stuff"};
//
//    private ArrayList<JComponent> components;

    private JComboBox<String> comboBox;

    public int currentView = 0;

    public SCADAViewer(DBHandler h, SCADASystem s, String f) {
        JFrame frame = new JFrame("SCADA Viewer");
        frame.setPreferredSize(new Dimension(600, 600));
        frame.setMinimumSize(new Dimension(300, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        sensors = new HashMap<String, JLabel>();
//        components = new ArrayList<JComponent>();
        handler = h;
        sys = s;

        file = f;

        cards = new JPanel(new CardLayout());

        Thread thr = new Thread(sys);
        thr.start();

        pane = frame.getContentPane();
        addComponentsToPane();

        frame.pack();
        frame.setVisible(true);
    }



    public void addComponentsToPane() {
        JPanel comboBoxPane = new JPanel();

            comboBox = new JComboBox<String>();

        comboBox.setMaximumSize(comboBox.getPreferredSize());

        comboBox.setEditable(false);

        comboBox.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        CardLayout cl = (CardLayout) (cards.getLayout());
                        cl.show(cards, comboBox.getSelectedItem().toString());
                        currentView = comboBox.getSelectedIndex();
                    }
                }
        );

//        comboBox.addItemListener(new ItemListener() {
//            public void itemStateChanged(ItemEvent evt) {
//                CardLayout cl = (CardLayout) (cards.getLayout());
//                cl.show(cards, (String) evt.getItem());
//            }
//        });

        comboBoxPane.add(comboBox);

        JButton quit = new JButton("Quit");

        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        comboBoxPane.add(quit);

//        components.add(new MaintenanceView(handler, sys, this, 0).getPane());
//        components.add(new JLabel("Hi"));
//        components.add(new JLabel("Hi"));
//
//        cards = new JPanel(new CardLayout());
//        for(int i=0; i<components.size(); i++){
//            cards.add(components.get(i), comboBoxItems[i]);
//        }

        pane.add(comboBoxPane, BorderLayout.PAGE_START);
        pane.add(cards, BorderLayout.CENTER);
    }

    public void addCard(JComponent card, String name){
        comboBox.addItem(name);
        cards.add(card, name);
    }

    public static void main(String[] args) {
        String file = "/Users/CraigLombardo/Desktop/output.txt";
        DBHandler handler = new DBHandler("SCADA.db", "SQLSchema/");
        SCADASystem sys = new SCADASystem(handler, file);
        SCADAViewer test = new SCADAViewer(handler, sys, file);

        test.addCard(new MaintenanceView(test.handler, sys, test, 0).getPane(), "Maintenance View");
        test.addCard(new JLabel("Hello"), "Hello");
        test.addCard(new JLabel("World"), "World");
        test.addCard(new CustomView(test.handler, sys, test, 3).getPane(), "Custom View");
    }
}