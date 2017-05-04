package GUI;

import cockpit.database.DBHandler;
import cockpit.database.SCADASystem;
import cockpit.database.Sensor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.MatteBorder;
import javax.swing.event.*;
/**
 * <h1>Dyno View</h1>
 * This class will serve as a means to view what the dyno is doing
 *
 * @author Craig Lombardo
 * @version 1.0
 * @since 2017-03-30
 */

public class DynoView {

    //Top Control Panel
    private GridBagLayout topControlLayout;
    private GridBagConstraints topControlConstraints;
    private JPanel topControlPanel;

    //Bottom Control Panel
    private GridBagLayout bottomControlLayout;
    private GridBagConstraints bottomControlConstraints;
    private JPanel bottomControlPanel;

    //Overall Control Panel
    private GridBagLayout controlLayout;
    private GridBagConstraints controlConstraints;
    private JPanel controlPanel;

    private ChartPanel graphPanel;
    private JPanel panelMain;

    //Value from 1-100 representing % of respective field
    private int throttlePos;
    private int torquePos;

    //The quick fields for viewing <tag, field>
    private HashMap<String, Field> fields;

    private TimeSeries dataset;
    private String dataUnits = "Testing";

    private String[] tags = {"BI", "RMSC", "SF", "TI", "CFP", "CV", "CT", "MT"};

    private DBHandler handler;

    private Viewer viewer;
    private int view;

    /**
     *
     * @param DBH The DBHandler that will interact with SCADA.db
     * @param scadaSys The SCADASystem that is linked with the DBHandler
     * @param sViewer The viewer which will display views
     * @param viewNumber The view number on the viewer (0 if first, 1 if second, etc.)
     */
    public DynoView(DBHandler DBH, SCADASystem scadaSys, Viewer sViewer, int viewNumber) {
        handler = DBH;
        viewer = sViewer;
        view = viewNumber;

        panelMain = new JPanel(new BorderLayout());
        dataset = new TimeSeries("");
        createControlPanel();
        createGraphPanel();

        Timer displayTimer = new Timer(1000, null);
        displayTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dataset.add(new Millisecond(), throttlePos + torquePos);
                updateNow(scadaSys.getMap());
            }
        });
        displayTimer.start();
    }

    /**
     * This method returns the panel that the view was drawn on
     * @return The panel
     */
    public JPanel getPanel() {
        return panelMain;
    }

    private void createControlPanel() {
        //Overall Panel
        controlLayout = new GridBagLayout();
        controlConstraints = new GridBagConstraints();
        controlPanel = new JPanel(controlLayout);

        controlConstraints.fill = GridBagConstraints.BOTH;
        controlConstraints.anchor = GridBagConstraints.CENTER;
        controlConstraints.weighty = 1;
        controlConstraints.weightx = 1;

        //Top panel
        topControlLayout = new GridBagLayout();
        topControlConstraints = new GridBagConstraints();
        topControlPanel = new JPanel(topControlLayout);

        topControlConstraints.fill = GridBagConstraints.BOTH;
        topControlConstraints.anchor = GridBagConstraints.CENTER;
        topControlConstraints.weighty = 1;
        topControlConstraints.weightx = 1;

        //Bottom Panel
//        bottomControlPanel = new JPanel();
//        bottomControlPanel.setLayout(new GridLayout(4, 4));
        bottomControlLayout = new GridBagLayout();
        bottomControlConstraints = new GridBagConstraints();
        bottomControlPanel = new JPanel(bottomControlLayout);

        bottomControlConstraints.fill = GridBagConstraints.BOTH;
        bottomControlConstraints.anchor = GridBagConstraints.CENTER;
        bottomControlConstraints.weighty = 1;
        bottomControlConstraints.weightx = 1;
        bottomControlPanel.setBorder(new MatteBorder(1, 0, 1, 0, Color.black));

        createHeader();
    }

    private void createHeader() {
        createTopControls();
        createBottomControls();

        addControlsComp(0, 0, 100, 35, true, topControlPanel);
        addControlsComp(0, 1, 100, 65, true, bottomControlPanel);

        panelMain.add(controlPanel, BorderLayout.PAGE_START);
    }

    private void createTopControls() {
        JSlider throttleSlider = new JSlider(0, 100, 0);
        JSpinner throttleSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));

        throttleSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int value = (int) throttleSpinner.getValue();
                throttlePos = value;
                throttleSlider.setValue(value);
            }
        });

        throttleSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                throttleSpinner.setValue(throttleSlider.getValue());
                throttlePos = throttleSlider.getValue();
                if (!throttleSlider.getValueIsAdjusting()) changeSystemVars();
            }
        });


        JSlider torqueSlider = new JSlider(0, 100, 0);
        JSpinner torqueSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));

        torqueSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int value = (int) torqueSpinner.getValue();
                torqueSlider.setValue(value);
                torquePos = value;
            }
        });

        torqueSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                torqueSpinner.setValue(torqueSlider.getValue());
                torquePos = torqueSlider.getValue();
                if (!torqueSlider.getValueIsAdjusting()) changeSystemVars();
            }
        });

        JButton resetButton = new JButton("Reset Graph");
        resetButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        resetGraphView();
                    }
                }
        );

        addTopControlsComp(0, 0, 40, 10, true, new JLabel("Throttle (%)", SwingConstants.CENTER));
        addTopControlsComp(1, 0, 10, 10, false, new JLabel("", SwingConstants.CENTER));
        addTopControlsComp(2, 0, 40, 10, true, new JLabel("Torque (%)", SwingConstants.CENTER));

        addTopControlsComp(0, 1, 35, 10, true, throttleSlider);
        addTopControlsComp(1, 1, 10, 10, false, throttleSpinner);
        addTopControlsComp(2, 1, 35, 10, true, torqueSlider);
        addTopControlsComp(3, 1, 10, 10, false, torqueSpinner);
        addTopControlsComp(4, 0, 10, 10, false, resetButton);
    }

    private void createBottomControls() {

        createFields();

        Field f;

        int half = fields.size() / 2;
        int xPos = 0;

        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            f = fields.get(entry.getKey());

            addBottomControlsComp(xPos % half, xPos >= half ? 3 : 0, true, new JLabel(f.title, SwingConstants.CENTER));
            addBottomControlsComp(xPos % half, xPos >= half ? 4 : 1, true, f.value);
            xPos++;
        }
        addBottomControlsComp(0, 2, true, new JLabel(" "));

    }

    private void createFields() {

        fields = new HashMap<>();

        for (ArrayList<String> arr : handler.getIDDescUnitsTag(tags)) {
            fields.put(arr.get(0), new Field(arr.get(1), arr.get(2)));
        }

//        fields.put("1", new Field("Torque", "lb*ft"));
//        fields.put("2", new Field("Power", "hp"));
//        fields.put("3", new Field("Dyno Speed", "rpm"));
//        fields.put("4", new Field("MC Speed", "rpm"));
//        fields.put("5", new Field("Current", "A"));
//        fields.put("6", new Field("Voltage", "V"));
//        fields.put("7", new Field("MC Temp", "C"));
//        fields.put("8", new Field("Motor Temp", "C"));
    }

    public void addControlsComp(int x, int y, int wx, int wy, boolean resize, Component comp) {
        controlConstraints.gridx = x;
        controlConstraints.gridy = y;

        controlConstraints.weightx = wx;
        controlConstraints.weighty = wy;

        controlConstraints.fill = resize ? GridBagConstraints.BOTH : GridBagConstraints.NONE;

        controlLayout.setConstraints(comp, controlConstraints);
        controlPanel.add(comp);
    }

    private void addTopControlsComp(int x, int y, int wx, int wy, boolean resize, Component comp) {
        topControlConstraints.gridx = x;
        topControlConstraints.gridy = y;

        topControlConstraints.weightx = wx;
        topControlConstraints.weighty = wy;

        topControlConstraints.fill = resize ? GridBagConstraints.BOTH : GridBagConstraints.NONE;

        topControlLayout.setConstraints(comp, topControlConstraints);
        topControlPanel.add(comp);
    }

    private void addBottomControlsComp(int x, int y, boolean resize, Component comp) {
        bottomControlConstraints.gridx = x;
        bottomControlConstraints.gridy = y;

        bottomControlConstraints.fill = resize ? GridBagConstraints.BOTH : GridBagConstraints.NONE;

        bottomControlLayout.setConstraints(comp, bottomControlConstraints);
        bottomControlPanel.add(comp);
    }

    private void changeSystemVars() {
        System.out.println("Throttle: " + throttlePos + "%  -  Torque: " + torquePos + "%");
    }

    private void createGraphPanel() {
        JFreeChart chart = createChart(dataset, dataUnits);
        chart.setBackgroundPaint(Color.WHITE);

        graphPanel = new ChartPanel(chart);

        panelMain.add(graphPanel);
    }

    private JFreeChart createChart(TimeSeries dataSeries, String units) {
        TimeSeriesCollection set = new TimeSeriesCollection(dataSeries);

        JFreeChart result = ChartFactory.createTimeSeriesChart(
                units + " x Time",
                "Time",
                units,
                set,
                false,
                true,
                false
        );

        XYPlot plot = result.getXYPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        ValueAxis xaxis = plot.getDomainAxis();
        xaxis.setAutoRange(true);

        xaxis.setVerticalTickLabels(true);

        ValueAxis yaxis = plot.getRangeAxis();
        yaxis.setRange(0.0, 100.0);
        yaxis.setAutoRange(true);
        return result;
    }

    private void resetGraphView() {
        panelMain.remove(graphPanel);

        dataset = new TimeSeries("");
        createGraphPanel();

        panelMain.validate();
        panelMain.repaint();
    }

    private void updateNow(HashMap<Integer, Sensor> sysMap) {
        Field f;
        if (view == viewer.getCurrentView()) {
            for (Map.Entry<Integer, Sensor> entry : sysMap.entrySet()) {
//                System.out.println(entry.getKey());
                f = fields.get("" + entry.getKey());
                if (f != null) {
                    f.value.setText(entry.getValue().getCalibValue());
                }
            }
        }
    }

    private class Field {

        public String title;
        public JTextField value;

        private Field(String t, String u) {
            title = t + " (" + u + ")";
            value = new JTextField("NaN?");
            value.setHorizontalAlignment(JTextField.CENTER);
            value.setEditable(false);
        }

    }

}