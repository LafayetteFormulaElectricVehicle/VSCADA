package cockpit.database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SCADAViewer extends JPanel {

    public int currentView = 0;
    private JPanel cards;
    private Container pane;
    private JComboBox<String> comboBox;

    public SCADAViewer() {
        JFrame frame = new JFrame("SCADA Viewer");
        frame.setPreferredSize(new Dimension(850, 600));
        frame.setMinimumSize(new Dimension(300, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cards = new JPanel(new CardLayout());

        pane = frame.getContentPane();
        addComponentsToPane();

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        String file = "/Users/CraigLombardo/Desktop/output.txt";
        DBHandler handler = new DBHandler("SCADA.db", "SQLSchema/");
        SCADASystem sys = new SCADASystem(handler, file);

        Thread thr = new Thread(sys);
        thr.start();

        SCADAViewer test = new SCADAViewer();

        test.addCard(new MaintenanceView(handler, sys, test, 0).getPane(), "Maintenance View");
        test.addCard(new QueryView().getPane(), "Query View");
        test.addCard(new CustomView(handler, sys, test, 2).getPane(), "Custom View");
        test.addCard(new ConfigurationView(handler).getPanel(), "Configuration View");
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

        comboBoxPane.add(comboBox);

        JButton quit = new JButton("Quit");

        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        comboBoxPane.add(quit);

        pane.add(comboBoxPane, BorderLayout.PAGE_START);
        pane.add(cards, BorderLayout.CENTER);
    }

    public void addCard(JComponent card, String name) {
        comboBox.addItem(name);
        cards.add(card, name);
    }
}