package GUI;

import cockpit.database.DBHandler;
import cockpit.database.SCADASystem;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

public class SCADAViewer {

    public int currentView = 0;
    private JPanel cards;
    private Container pane;
    private JComboBox<String> comboBox;
    private int count = 0;

    private JLabel cTime;
    private JLabel temperature;
    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;

    private SCADASystem system;
    private boolean savingData = true;

    public SCADAViewer(SCADASystem sys) {
        system = sys;

        JFrame frame = new JFrame("SCADA Viewer");
        frame.setPreferredSize(new Dimension(850, 600));
        frame.setMinimumSize(new Dimension(300, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cards = new JPanel(new CardLayout());

        pane = frame.getContentPane();
        addComponentsToPane();

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);

    }

    public static void main(String[] args) {

        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        String file = System.getProperty("user.home") + "/Desktop/output.txt";
//        System.out.println(file);
        DBHandler handler = new DBHandler();
        SCADASystem sys = new SCADASystem(handler, file);

        Thread thr = new Thread(sys);
        thr.start();

        SCADAViewer test = new SCADAViewer(sys);

        test.addCard(new MaintenanceView(handler, sys, test, 0).getPane(), "Maintenance View");
        test.addCard(new QueryView(handler).getPane(), "Query View");
        test.addCard(new CustomView(handler, sys, test, 2).getPane(), "Custom View");
        test.addCard(new DynoView(handler, sys, test, 3).getPanel(), "Dyno Control");
        test.addCard(new ConfigurationView(handler).getPanel(), "Configuration View");
        test.addCard(new EquationView(handler, sys).getPanel(), "Equation Viewer");
        test.addCard(new ChargingView(sys).getPanel(), "Charging View");

//        handler.getLatestData(3);
//
//        System.out.println(handler.getInfo("BI", null, "2017-04-09 23:29:58", "2017-04-09 23:29:59"));

    }

    private void getTemperature(){
        Scanner sc;

        try {
            sc = new Scanner(Runtime.getRuntime().exec("vcgencmd measure_temp").getInputStream());
            temperature.setText("pi " + sc.nextLine());
        } catch (Exception e) {
            temperature.setText("pi temp=?");
        }
    }

    public void addComponentsToPane() {
        cTime = new JLabel("Runtime: 0:00:00");

        temperature = new JLabel();
        getTemperature();

        Timer t = new Timer(1000, null);
        t.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String text = "Runtime: ";
                text += ((hours >= 10) ? hours : "0" + hours) + ":";
                text += ((minutes >= 10) ? minutes : "0" + minutes) + ":";
                text += (seconds >= 10) ? seconds : "0" + seconds;

                cTime.setText(text);

                if (++seconds == 60) {
                    getTemperature();
                    seconds = 0;
                    minutes++;
                }
                if (minutes == 60) {
                    minutes = 0;
                    hours++;
                }
            }
        });
        t.start();

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


        JButton quit = new JButton("Quit");

        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        Scanner sc;
        String ip = "IP: ";


        try {
            sc = new Scanner(Runtime.getRuntime().exec("hostname -I").getInputStream());
            ip += sc.nextLine();
        } catch (Exception e) {
        }


        JButton dataAq = new JButton("Saving Data: " + savingData);

        dataAq.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savingData = !savingData;
                system.toggleDataSave(savingData);
                dataAq.setText("Saving Data: " + savingData);
            }
        });


        comboBoxPane.add(new JLabel(ip));
        comboBoxPane.add(new JLabel("      "));
        comboBoxPane.add(temperature);
        comboBoxPane.add(new JLabel("      "));
        comboBoxPane.add(cTime);
        comboBoxPane.add(new JLabel("      "));
        comboBoxPane.add(comboBox);
        comboBoxPane.add(new JLabel("      "));
        comboBoxPane.add(dataAq);
        comboBoxPane.add(new JLabel("      "));
        comboBoxPane.add(quit);
        comboBoxPane.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));

        pane.add(comboBoxPane, BorderLayout.PAGE_START);
        pane.add(cards, BorderLayout.CENTER);
    }

    public void addCard(JComponent card, String name) {
        comboBox.addItem(name);
        cards.add(card, name);
        comboBox.setMaximumRowCount(++count);
    }
}



//vcgencmd measure_temp //get temperature of PI

