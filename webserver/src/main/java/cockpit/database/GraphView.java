package cockpit.database;

/**
 * Created by CraigLombardo on 3/14/17.
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

public class GraphView {

    int graphCount = 0;
    private JPanel graphsPanel;
    private JComboBox<String>[] graphOptions;
    private String[] selection = {"", "", "", ""};
    private TimeSeries[] series = {null, null, null, null};
    private Integer[] seriesMove = {4, 4, 4, 4};
    private double lastValue1 = 100.0;
    private double lastValue2 = 100.0;
    private double lastValue3 = 100.0;
    private double lastValue4 = 100.0;
    private JPanel content;
    private Timer timer = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            if (graphCount != 0) updateChart();
        }
    });

    public GraphView() {

        content = new JPanel(new BorderLayout());
        graphOptions = new JComboBox[4];

        createHeader();

        series[0] = new TimeSeries("");
        series[1] = new TimeSeries("");
        series[2] = new TimeSeries("");
        series[3] = new TimeSeries("");

        graphsPanel = new JPanel(new GridLayout(2, 2));

        timer.start();

    }

    public static void main(String[] args) {

        JFrame window = new JFrame("Test");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 400);
        window.setMinimumSize(new Dimension(300, 300));

        GraphView demo = new GraphView();
        window.getContentPane().add(demo.getPanel(), BorderLayout.CENTER);

        window.setVisible(true);

    }

    public JPanel getPanel() {
        return content;
    }

    private JFreeChart createChart(TimeSeriesCollection dataset, String units) {
        JFreeChart result = ChartFactory.createTimeSeriesChart(
                units + " x Time",
                "Time",
                units,
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

    public void createHeader() {
        JPanel boxes = new JPanel(new GridLayout(1, 3));

        JButton update = new JButton("Update Graph View");
        update.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        updateGraphsPane();
                    }
                }
        );

        createGraphBoxes();

        boxes.add(graphOptions[0]);
        boxes.add(graphOptions[1]);
        boxes.add(graphOptions[2]);
        boxes.add(graphOptions[3]);
        boxes.add(update);
        content.add(boxes, BorderLayout.PAGE_START);
    }

    public void updateChart() {

        lastValue1 = lastValue1 * (0.9 + 0.2 * Math.random());
        lastValue2 = lastValue1 * (0.9 + 0.2 * Math.random());
        lastValue3 = lastValue1 * (0.9 + 0.2 * Math.random());
        lastValue4 = lastValue1 * (0.9 + 0.2 * Math.random());

        Millisecond now = new Millisecond();
        series[0].add(now, lastValue1);
        series[1].add(now, lastValue2);
        series[2].add(now, lastValue3);
        series[3].add(now, lastValue4);

    }

    public int getGraphCount() {
        int out = 0;
        out += graphOptions[0].getSelectedIndex() != 0 ? 1 : 0;
        out += graphOptions[1].getSelectedIndex() != 0 ? 1 : 0;
        out += graphOptions[2].getSelectedIndex() != 0 ? 1 : 0;
        out += graphOptions[3].getSelectedIndex() != 0 ? 1 : 0;
        return out;
    }

    public void updateGraphsPane() {
        content.remove(graphsPanel);
        graphsPanel = new JPanel(new GridLayout());

        checkOptionBoxes();

        createNewGraphPane();

        graphCount = getGraphCount();

        content.add(graphsPanel);
        content.validate();
        content.repaint();
    }

    public void createNewGraphPane() {

        for (int i = 0; i < 4; i++) {
            System.out.println(seriesMove[i]);
            if (seriesMove[i] != 4){
                series[i] = series[seriesMove[i]];
                series[seriesMove[i]] = new TimeSeries("");
                seriesMove[i] = 4;
            }
            else if (!selection[i].equals((String) graphOptions[i].getSelectedItem())) {
                series[i] = new TimeSeries("");
            }
        }

        int graphCount = getGraphCount();

        graphsPanel.setLayout(new GridLayout(graphCount <= 2 ? 1 : 2, graphCount <= 1 ? 1 : 2));

        String cSelection;

        for (int i = 0; i < graphCount; i++) {
            cSelection = (String) graphOptions[i].getSelectedItem();
            selection[i] = cSelection;

            TimeSeriesCollection dataset = new TimeSeriesCollection(series[i]);
            JFreeChart chart = createChart(dataset, cSelection);

            chart.setBackgroundPaint(Color.WHITE);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            graphsPanel.add(chartPanel);
        }
    }

    private void checkOptionBoxes() {
        int indexes[] = {0, 0, 0, 0};
        int option;

        for (int i = 0; i < 4; i++) {
            for (int j = i; j < 4; j++) {
                option = graphOptions[j].getSelectedIndex();
                if (option != 0) {
                    indexes[i] = option;
                    if(i != j) seriesMove[i] = j;
                    graphOptions[j].setSelectedIndex(0);
                    break;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            graphOptions[i].setSelectedIndex(indexes[i]);
        }
    }

    private void createGraphBoxes() {

        for (int i = 0; i < 4; i++) {
            graphOptions[i] = new JComboBox<String>();
            graphOptions[i].setMaximumSize(graphOptions[0].getPreferredSize());
            graphOptions[i].setEditable(false);
            ((JLabel) graphOptions[i].getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        }

        setGraphOptions();
    }

    private void setGraphOptions() {
        for (int i = 0; i < 4; i++) {
            graphOptions[i].addItem("(Select Sensor)");
            graphOptions[i].addItem("COOLING");
            graphOptions[i].addItem("DYNO");
            graphOptions[i].addItem("TSI");
            graphOptions[i].addItem("TSV");
        }
    }
}