package cockpit.database;

/**
 * Created by CraigLombardo on 3/30/17.
 */

import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.Border;
import javax.swing.event.*;

public class DynoView {

    private GridBagLayout controlLayout;
    private GridBagConstraints controlConstraints;
    private JPanel controlPanel;

    private GridBagLayout graphLayout;
    private GridBagConstraints graphConstraints;
    private JPanel graphPanel;

    private JPanel panelMain;

    public DynoView() {
        panelMain = new JPanel(new BorderLayout());

        createControlPanel();
//        createGraphPanel();

        JTextArea text = new JTextArea();
        JScrollPane scroll = new JScrollPane(text); //place the JTextArea in a scroll pane

//        panelMain.add(scroll, BorderLayout.CENTER);
    }

    public static void main(String[] args) {

        JFrame window = new JFrame("Test");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(600, 600);
        window.setMinimumSize(new Dimension(300, 300));

        DynoView test1 = new DynoView();
        window.getContentPane().add(test1.getPanel(), BorderLayout.CENTER);

        window.setVisible(true);
    }

    public JPanel getPanel() {
        return panelMain;
    }

    public void createControlPanel() {
        controlLayout = new GridBagLayout();
        controlConstraints = new GridBagConstraints();
        controlPanel = new JPanel(controlLayout);

        controlConstraints.fill = GridBagConstraints.NONE;
        controlConstraints.anchor = GridBagConstraints.CENTER;
        controlConstraints.weighty = 1;

        createHeader();
    }

    public void createHeader() {
        JSlider slider = new JSlider(0, 100, 0);
        JSpinner spinner = new JSpinner();

        spinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int value = (int) spinner.getValue();
                if (value < 0) {
                    value = 0;
                    spinner.setValue(0);
                } else if (value > 100) {
                    value = 100;
                    spinner.setValue(100);
                }
                slider.setValue(value);
            }
        });

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                spinner.setValue(slider.getValue());
            }
        });


        JPanel one = new JPanel();
        JPanel two = new JPanel();
        JPanel three = new JPanel();
        JPanel four = new JPanel();

        Border eBorder = BorderFactory.createEtchedBorder();

        one.setBorder(BorderFactory.createTitledBorder(eBorder, "80pct"));
        two.setBorder(BorderFactory.createTitledBorder(eBorder, "80pct"));
        three.setBorder(BorderFactory.createTitledBorder(eBorder, "80pct"));
        four.setBorder(BorderFactory.createTitledBorder(eBorder, "80pct"));

        one.add(slider);
        two.add(spinner);
        three.add(new JButton());
        four.add(new JTextArea());

        addControlsComp(0, 0, 60, 50, one);
        addControlsComp(1, 0, 10, 50, two);
        addControlsComp(2, 0, 90,500, three);
        addControlsComp(0, 1, 100, 50,four);
//        addControlsComp(1, 1, 0.5, 1, new JLabel("test"));
//        addControlsComp(0, 2, 1, 1, new JLabel("Hello"));
//        addControlsComp(0, 3, 1, 1, new JLabel("Hello"));

//        panelMain.add(controlPanel, BorderLayout.PAGE_START);
        panelMain.add(controlPanel);
    }

    public void addControlsComp(int x, int y, double wX, double wY, Component comp) {
        controlConstraints.gridx = x;
        controlConstraints.gridy = y;

        controlConstraints.weightx = wX;
        controlConstraints.weighty = wY;

        controlLayout.setConstraints(comp, controlConstraints);
        controlPanel.add(comp);
    }

}