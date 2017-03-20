package cockpit.database;

/**
 * Code based on code created by Shiv Modi on Sunday, 8 July 2012
 */

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

public class DynamicLineAndTimeSeriesChart {

    private TimeSeries series;

    private double lastValue = 100.0;

    private JPanel content;

    private Timer timer = new Timer(250, new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            updateChart();
        }
    });

    public DynamicLineAndTimeSeriesChart(String title) {

        series = new TimeSeries("Random Data");

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = createChart(dataset);

        chart.setBackgroundPaint(Color.WHITE);

        content = new JPanel(new BorderLayout());

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));

        content.add(chartPanel);

        timer.start();

    }

    public static void main(String[] args) {

        JFrame window = new JFrame("Test");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 400);
        window.setMinimumSize(new Dimension(300, 300));

        DynamicLineAndTimeSeriesChart demo = new DynamicLineAndTimeSeriesChart("Test");
        window.getContentPane().add(demo.getPanel(), BorderLayout.CENTER);

        window.setVisible(true);

    }

    public JPanel getPanel() {
        return content;
    }

    private JFreeChart createChart(TimeSeriesCollection dataset) {
        JFreeChart result = ChartFactory.createTimeSeriesChart(
                "UNITS x Time",
                "Time",
                "UNITS",
                dataset,
                true,
                true,
                false
        );

        XYPlot plot = result.getXYPlot();

        plot.setBackgroundPaint(new Color(0xFFFFFF));
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

    public void updateChart() {

        double factor = 0.9 + 0.2 * Math.random();
        lastValue = lastValue * factor;

        Millisecond now = new Millisecond();
        series.add(new Millisecond(), lastValue);
    }

}
