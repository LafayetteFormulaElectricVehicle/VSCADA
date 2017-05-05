package GUI;

import cockpit.database.DBHandler;
import cockpit.database.SCADASystem;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import server.SparkServer;

/**
 * <h1>SCADA Viewer</h1>
 * This class will serve as a means to view all data from a desktop, via a wireless connection
 *
 * @author Craig Lombardo
 * @version 1.0
 * @since 2017-03-25
 */
public class SCADAViewer implements Viewer {

    private int frameWidth;
    private int frameHeight;
    private int currentView = 0;
    private JPanel cards;
    private Container pane;
    private JComboBox<String> comboBox;
    private int count = 0;
    private JLabel cTime;
    private JLabel temperature;
    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;
    private SCADASystem sys;
    private boolean savingData = true;

    /**
     * This constructor creates a new SCADAViewer for use as a desktop application.
     */
    public SCADAViewer() {

        JFrame frame = new JFrame("SCADA Viewer");
        frame.setPreferredSize(new Dimension(850, 600));
        frame.setMinimumSize(new Dimension(300, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cards = new JPanel(new CardLayout());

        pane = frame.getContentPane();
        addComponentsToPane();

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        Dimension dims = Toolkit.getDefaultToolkit().getScreenSize();
        frameWidth = dims.width;
        frameHeight = dims.height;
        frame.pack();
        frame.setVisible(true);

        String ip;

        int dialogButton = JOptionPane.OK_CANCEL_OPTION;
        ip = JOptionPane.showInputDialog(null, "What is the IP of the server?", "IP Info", dialogButton);
        if (ip == null || ip.equals("")) {
            System.exit(0);
        }

        DBHandler handler = new DBHandler();
        sys = new SCADASystem(handler, true);

        SparkServer sparkServer;
        if (ip.equals("")) sparkServer = new SparkServer(handler, sys);

        Thread thr = new Thread(sys);
        thr.start();


        addCard(new MaintenanceView(handler, sys, this, ip, false, 0).getPane(), "Maintenance View");
        addCard(new QueryView(handler).getPane(), "Query View");
        addCard(new CustomView(handler, sys, this, ip, 2).getPane(), "Custom View");
        addCard(new DynoView(handler, sys, this, 3).getPanel(), "Dyno Control");
        addCard(new ConfigurationView(handler).getPanel(), "Configuration View");
        addCard(new EquationView(handler, sys).getPanel(), "Equation Viewer");
        addCard(new ChargingView(sys, this, frameWidth, ip, 6), "Charging View");

    }

    public static void main(String[] args) {

        SCADAViewer test = new SCADAViewer();

    }

    private void getTemperature() {
        Scanner sc;

        try {
            sc = new Scanner(Runtime.getRuntime().exec("vcgencmd measure_temp").getInputStream());
            temperature.setText("pi " + sc.nextLine());
        } catch (Exception e) {
            temperature.setText("pi temp=?");
        }
    }

    private void addComponentsToPane() {
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
                sys.toggleDataSave(savingData);
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

    private void addCard(JComponent card, String name) {
        comboBox.addItem(name);
        cards.add(card, name);
        comboBox.setMaximumRowCount(++count);
    }

    /**
     * This method returns the index of the current view
     * @return 0 if the first view, 1 if second etc.
     */
    public int getCurrentView() {
        return currentView;
    }
}


//vcgencmd measure_temp //get temperature of PI

