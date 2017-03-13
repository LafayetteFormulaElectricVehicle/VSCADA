package cockpit.database;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SCADAViewerNewOld extends JPanel {

    private GridBagLayout innerLayout;
    private GridBagConstraints innerConstraints;
    private JPanel innerPanel;
    private JFrame window;

    private DBHandler handler;

    private String file = "/Users/CraigLombardo/Desktop/output.txt";

    private HashMap<String, JLabel> sensors;

    private SCADASystem sys;
    ActionListener listener = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            updateNow(sys.getMap());
        }
    };

    public SCADAViewerNewOld() {
        window = new JFrame("Test");
        window.setTitle("SCADA Viewer");
        window.setSize(600, 500);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        init();
        window.setVisible(true);
    }

    public static void main(String[] args) {
        SCADAViewerNewOld test = new SCADAViewerNewOld();
    }

    public void init() {
        sensors = new HashMap<String, JLabel>();

        handler = new DBHandler("SCADA.db", "SQLSchema/");
        sys = new SCADASystem(handler, file);

        Thread thr = new Thread(sys);
        thr.start();

        createMapping(handler.getIDNames());

        JScrollPane scrollPanel = new JScrollPane(innerPanel);
        window.add(scrollPanel, BorderLayout.CENTER);
        scrollPanel.getVerticalScrollBar().setUnitIncrement(20);

        Timer displayTimer = new Timer(1000, listener);
        displayTimer.start();
    }

    private void updateNow(HashMap<String, String> sysMap) {
        for (Map.Entry<String, String> entry : sysMap.entrySet()) {
            sensors.get(entry.getKey()).setText(entry.getValue());
        }
    }

    public void createHeader() {

        JButton quit = new JButton("Quit");

        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        addComp(0, 0, new JLabel("Sensor ID:"));
        addComp(1, 0, new JLabel("Sensor Name:"));
        addComp(2, 0, new JLabel("Sensor Value:"));
        addComp(3, 0, new JLabel("       "));
        addComp(4, 0, quit);
    }

    public void createMapping(ArrayList<ArrayList<String>> info) {
        innerLayout = new GridBagLayout();
        innerConstraints = new GridBagConstraints();
        innerPanel = new JPanel(innerLayout);
        innerConstraints.fill = GridBagConstraints.NONE;
        innerConstraints.anchor = GridBagConstraints.CENTER;

        createHeader();

        String id;
        String name;
        int idVal;

        int row = 1;
        for (ArrayList<String> r : info) {
            id = r.get(0);
            name = r.get(1);
            JLabel tmp = new JLabel("");
            sensors.put(id, tmp);
            idVal = Integer.parseInt(id);
            addComp(0, row, new JLabel("0x" + (idVal <= 255 ? "0" : "") + Integer.toHexString(idVal)));
            addComp(1, row, new JLabel(name));
            addComp(2, row++, tmp);

        }
        updateNow(sys.getMap());
    }

    public void addComp(int x, int y, Component comp) {
        innerConstraints.gridx = x;
        innerConstraints.gridy = y;

        innerLayout.setConstraints(comp, innerConstraints);
        innerPanel.add(comp);
    }

}