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
 * <h1>SCADA Cockpit</h1>
 * This class will serve as a means to view all data as if one were in the cockpit/launch a server
 *
 * @author Craig Lombardo
 * @version 1.0
 * @since 2017-03-20
 */

public class SCADACockpit implements Viewer {


    /**
     * The height of the screen
     */
    public int frameHeight;
    /**
     * The width of the screen
     */
    public int frameWidth;
    private JPanel cards;
    private int currentView = 0;
    private Container pane;
    private JComboBox<String> comboBox;
    private int count = 0;
    private JLabel cTime;
    private JLabel temperature;
    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;

    private JLabel ipAddress;

    /**
     * This constructor creates a new SCADA System and launches a server
     */
    public SCADACockpit() {
        JFrame frame = new JFrame("SCADA Viewer");
        frame.setPreferredSize(new Dimension(800, 480));
        frame.setMinimumSize(new Dimension(300, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cards = new JPanel(new CardLayout());

        pane = frame.getContentPane();
        addComponentsToPane();

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frameWidth = dim.width;
        frameHeight = dim.height;

        frame.pack();
        frame.setVisible(true);

        DBHandler handler = new DBHandler();
        SCADASystem sys = new SCADASystem(handler, false);

        SparkServer sparkServer = new SparkServer(handler, sys);

        Thread thr = new Thread(sys);
        thr.start();
        addCard(new DriveView(sys, this, frameWidth, frameHeight, 0), "Drive View");
        addCard(new ChargingView(sys, this, frameWidth, "", 1), "Charging View");

        addCard(new MaintenanceView(handler, sys, this, "", true, 2).getPane(), "Maintenance View");

        addCard(new QueryView(handler).getPane(), "Query View");
        addCard(new ConfigurationView(handler).getPanel(), "Configuration View");
        addCard(new EquationView(handler, sys).getPanel(), "Equation Viewer");

    }

    public static void main(String[] args) {

        SCADACockpit test = new SCADACockpit();

    }

    /**
     * Returns the index of the currently selected view
     *
     * @return The integer index of the current view (0 is first, 1 is second etc)
     */
    public int getCurrentView() {
        return currentView;
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

        ipAddress = new JLabel(ip);

        comboBoxPane.add(ipAddress);
        comboBoxPane.add(new JLabel("      "));
        comboBoxPane.add(temperature);
        comboBoxPane.add(new JLabel("      "));
        comboBoxPane.add(cTime);
        comboBoxPane.add(new JLabel("      "));
        comboBoxPane.add(comboBox);
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
}


//vcgencmd measure_temp //get temperature of PI

