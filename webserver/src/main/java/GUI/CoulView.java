package GUI;


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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * <h1>CoulView</h1>
 * This class will serve as the best means for accessing the DB without the need to know SQL
 *
 * @author Craig Lombardo
 * @version 1.0
 * @since 2017-04-17
 */

public class CoulView {

    private JPanel graphsPanel;
    private String[] selection = {"PCOUL1", "PCOUL2", "PCOUL3", "PCOUL4"};
    private TimeSeries[] series = {null, null, null, null};
    private double[] lastValues = {0d, 0d, 0d, 0d};
    private double lastValue1 = 100.0;
    private double lastValue2 = 100.0;
    private double lastValue3 = 100.0;
    private double lastValue4 = 100.0;
    private JPanel content;

    private SCADASystem system;

    private Timer timer = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            updateChart();
        }
    });

    /**
     * This method creates a new Coul View for direct graph views
     * @param sys The SCADASystem containing all of the information about sensors
     */
    public CoulView(SCADASystem sys) {

        system = sys;

        content = new JPanel(new BorderLayout());

        series[0] = new TimeSeries("Pack 1");
        series[1] = new TimeSeries("Pack 2");
        series[2] = new TimeSeries("Pack 3");
        series[3] = new TimeSeries("Pack 4");

        graphsPanel = new JPanel(new GridLayout(2, 2));
        updateGraphsPane();
        timer.start();

    }

    /**
     * This method returns the panel which the view was put on
     * @return THe panel with the view
     */
    public JPanel getPanel() {
        return content;
    }

    private JFreeChart createChart(TimeSeriesCollection dataset, String units) {
        JFreeChart result = ChartFactory.createTimeSeriesChart(
                units,
                "Time",
                "Couloumbs",
                dataset,
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

    private void updateChart() {

        HashMap<String, Sensor> cMap = system.getCustomMapping();

        for (int i = 0; i < 4; i++) {
            try {
                lastValues[i] = Double.parseDouble(cMap.get(selection[i]).getCalibValue());
            } catch (Exception e) {
                lastValues[i] = 0d;
            }
            series[i].add(new Millisecond(), lastValues[i]);
        }

    }

    private void updateGraphsPane() {
        content.remove(graphsPanel);
        graphsPanel = new JPanel(new GridLayout());

        createNewGraphPane();

        content.add(graphsPanel);
        content.validate();
        content.repaint();
    }

    private void createNewGraphPane() {

        for (int i = 0; i < 4; i++) {
            series[i] = new TimeSeries("");
        }

        graphsPanel.setLayout(new GridLayout(2, 2));


        for (int i = 0; i < 4; i++) {

            TimeSeriesCollection dataset = new TimeSeriesCollection(series[i]);
            JFreeChart chart = createChart(dataset, selection[i]);

            chart.setBackgroundPaint(Color.WHITE);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            graphsPanel.add(chartPanel);
        }
    }

}