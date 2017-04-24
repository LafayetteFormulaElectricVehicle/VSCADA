package GUI;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.StandardGradientPaintTransformer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A simple demonstration application showing how to create a stacked bar chart
 * using data from a {@link CategoryDataset}.
 */
public class DriveView extends ApplicationFrame implements ChangeListener {

    ChartPanel batteryPanel;
    ChartPanel tempPanel;
    ChartPanel speedPanel;
    JFreeChart batteryChart;
    JFreeChart speedChart;
    JFreeChart tempChart;

    JSlider s1;
    JSlider s2;

    private DefaultValueDataset speedDataset;
    private CategoryDataset batteryDataset;
    private CategoryDataset tempDataset;
    private int value = 50000;

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public DriveView(final String title) {
        super(title);
//        batteryDataset = createBatteryDataset();
        speedDataset = new DefaultValueDataset(value);
//        tempDataset = createTemperatureDataset();

        speedChart = createSpeedChart(speedDataset);
//        batteryChart = createBatteryChart(batteryDataset);
//        tempChart = createTemperatureChart(tempDataset);

//        batteryPanel = new ChartPanel(batteryChart);
//        tempPanel = new ChartPanel(tempChart);
        speedPanel = new ChartPanel(speedChart);

        speedPanel.setPreferredSize(new Dimension(300, 380));
//        batteryPanel.setPreferredSize(new java.awt.Dimension(220, 380));
//        tempPanel.setPreferredSize(new java.awt.Dimension(220, 380));
//        batteryPanel1.setMaximumDrawHeight(100);

        //set_battery_0_10();

//        getContentPane().add(batteryPanel, BorderLayout.WEST);
//        getContentPane().add(tempPanel, BorderLayout.EAST);
        getContentPane().add(speedPanel, BorderLayout.CENTER);


        JPanel jpanel = new JPanel(new GridLayout(2, 2));
        jpanel.add(new JLabel("Battery %"));
        jpanel.add(new JLabel("Temp %"));

        s1 = new JSlider(0,100);
        s1.setMajorTickSpacing(20);
        s1.setPaintTicks(true);
        s1.setPaintLabels(true);
        s1.addChangeListener(this);

        s2 = new JSlider(0,200);
        s2.setMajorTickSpacing(20);
        s2.setPaintTicks(true);
        s2.setPaintLabels(true);
        s2.addChangeListener(this);

        getContentPane().add(s1, BorderLayout.NORTH);
        getContentPane().add(s2, BorderLayout.SOUTH);

        update_battery(50);
        update_temperature(s2.getValue());
        refresh();


        Timer t = new Timer(250, new ActionListener() {
            Random r = new Random();
            @Override
            public void actionPerformed(ActionEvent e) {
                value -= (int) (Math.abs(100 * r.nextGaussian()));
                update(value);
            }
        });
        t.start();

    }

    public void update(int data) {
        speedDataset.setValue(data);
    }

    public void stateChanged(ChangeEvent changeEvent) {
        update_battery(s1.getValue());
        update_temperature(s2.getValue());
        refresh();
    }

    public void refresh() {
        getContentPane().removeAll();
        getContentPane().revalidate();
        getContentPane().add(batteryPanel, BorderLayout.WEST);
        getContentPane().add(tempPanel, BorderLayout.EAST);
        getContentPane().add(speedPanel, BorderLayout.CENTER);
        getContentPane().add(s1, BorderLayout.NORTH);
        getContentPane().add(s2, BorderLayout.SOUTH);
        getContentPane().repaint();
    }

    private JFreeChart createSpeedChart(ValueDataset valuedataset) {
        MeterPlot meterplot = new MeterPlot(valuedataset);
        meterplot.setRange(new Range(0.0D, 100000D));
        meterplot.addInterval(new MeterInterval("Turtle speed", new Range(0.0D, 20000D),
                Color.red, new BasicStroke(2.0F), new Color(255, 0, 0, 128)));
        meterplot.addInterval(new MeterInterval("Meh", new Range(10000D, 70000D),
                Color.yellow, new BasicStroke(2.0F), new Color(255, 255, 0, 64)));
        meterplot.addInterval(new MeterInterval("FAST!!!", new Range(90000D, 100000D),
                Color.green, new BasicStroke(2.0F), new Color(0, 255, 0, 64)));

        meterplot.setNeedlePaint(Color.darkGray);
        meterplot.setDialBackgroundPaint(Color.white);
        meterplot.setDialOutlinePaint(Color.black);
        meterplot.setDialShape(DialShape.CHORD);
        meterplot.setMeterAngle(180);
        meterplot.setTickLabelsVisible(true);
        meterplot.setTickLabelFont(new Font("Arial", 1, 14));
        meterplot.setTickLabelPaint(Color.black);
        meterplot.setTickSize(5D);
        meterplot.setTickPaint(Color.gray);
        meterplot.setValuePaint(Color.black);
        meterplot.setValueFont(new Font("Arial", 1, 14));
        JFreeChart jfreechart = new JFreeChart("RPM",
                JFreeChart.DEFAULT_TITLE_FONT, meterplot, true);
        return jfreechart;
    }


    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private CategoryDataset createBatteryDataset() {
        DefaultCategoryDataset result = new DefaultCategoryDataset();

        result.addValue(20.0, "Product 1 (US)", "Battery");
        result.addValue(15.0, "Product 1 (Europe)", "Battery");
        result.addValue(25.0, "Product 1 (Asia)", "Battery");
        result.addValue(40.0, "Product 1 (Middle East)", "Battery");

        return result;
    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private CategoryDataset createTemperatureDataset() {
        DefaultCategoryDataset result = new DefaultCategoryDataset();

        result.addValue(60.0, "Product 1 (US)", " ");
        result.addValue(20.0, "Product 1 (Europe)", " ");
        result.addValue(20.0, "Product 1 (Asia)", " ");
//        result.addValue(40.0, "Product 1 (Middle East)", "Battery");

        return result;
    }

    /**
     * Creates a sample chart.
     *
     * @param dataset  the dataset for the chart.
     *
     * @return A sample chart.
     */
    private JFreeChart createBatteryChart(final CategoryDataset dataset) {

        final JFreeChart chart = ChartFactory.createStackedBarChart(
                "Battery",  // chart title
                "Category",                  // domain axis label
                "Percentage",                     // range axis label
                dataset,                     // data
                PlotOrientation.VERTICAL,    // the plot orientation
                true,                        // legend
                true,                        // tooltips
                false                        // urls
        );

        GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
        KeyToGroupMap map = new KeyToGroupMap("G1");
        map.mapKeyToGroup("Product 1 (US)", "G1");
        map.mapKeyToGroup("Product 1 (Europe)", "G1");
        map.mapKeyToGroup("Product 1 (Asia)", "G1");
        map.mapKeyToGroup("Product 1 (Middle East)", "G1");

        renderer.setSeriesToGroupMap(map);

        renderer.setItemMargin(0.0);
        Paint p1 = new GradientPaint(
                0.0f, 0.0f, new Color(0xFF, 0x22, 0x22), 0.0f, 0.0f, new Color(0xFF, 0x22, 0x22)
        );
        renderer.setSeriesPaint(0, p1);
        renderer.setSeriesPaint(5, p1);
        renderer.setSeriesPaint(10, p1);

        Paint p2 = new GradientPaint(
                0.0f, 0.0f, new Color(0xFF, 0xAA, 0x22), 0.0f, 0.0f, new Color(0xFF, 0xAA, 0x22)
        );
        renderer.setSeriesPaint(1, p2);
        renderer.setSeriesPaint(6, p2);
        renderer.setSeriesPaint(11, p2);

        Paint p3 = new GradientPaint(
                0.0f, 0.0f, new Color(0xFF, 0xFF, 0x22), 0.0f, 0.0f, new Color(0xFF, 0xFF, 0x88)
        );
        renderer.setSeriesPaint(2, p3);
        renderer.setSeriesPaint(7, p3);
        renderer.setSeriesPaint(12, p3);

        Paint p4 = new GradientPaint(
                0.0f, 0.0f, new Color(0x22, 0xFF, 0x22), 0.0f, 0.0f, new Color(0x88, 0xFF, 0x88)
        );
        renderer.setSeriesPaint(3, p4);
        renderer.setSeriesPaint(8, p4);
        renderer.setSeriesPaint(13, p4);

        Paint p5 = new GradientPaint(
                0.0f, 0.0f, new Color(0xFF, 0xFF, 0xFF), 0.0f, 0.0f, new Color(0xFF, 0xFF, 0xFF)
        );
        renderer.setSeriesPaint(4, p5);
        renderer.setSeriesPaint(9, p5);
        renderer.setSeriesPaint(14, p5);

        renderer.setGradientPaintTransformer(
                new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL)
        );



        SubCategoryAxis domainAxis = new SubCategoryAxis("Pack Voltage");
        domainAxis.setCategoryMargin(0.05);
        domainAxis.addSubCategory("");
        //domainAxis.addSubCategory("Product 2");
        //domainAxis.addSubCategory("Product 3");

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainAxis(domainAxis);
        //plot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
        plot.setRenderer(renderer);
        plot.setFixedLegendItems(createLegendItems());
        return chart;

    }

    private JFreeChart createTemperatureChart(final CategoryDataset dataset) {

        final JFreeChart chart = ChartFactory.createStackedBarChart(
                "Temperature",  // chart title
                "Category",                  // domain axis label
                "Degrees Celsius",                     // range axis label
                dataset,                     // data
                PlotOrientation.VERTICAL,    // the plot orientation
                true,                        // legend
                true,                        // tooltips
                false                        // urls
        );

        GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
        KeyToGroupMap map = new KeyToGroupMap("G1");
        map.mapKeyToGroup("Product 1 (US)", "G1");
        map.mapKeyToGroup("Product 1 (Europe)", "G1");
        map.mapKeyToGroup("Product 1 (Asia)", "G1");
        map.mapKeyToGroup("Product 1 (Middle East)", "G1");

        renderer.setSeriesToGroupMap(map);

        renderer.setItemMargin(0.0);

        Paint p1 = new GradientPaint(
                0.0f, 0.0f, new Color(0x22, 0xFF, 0x22), 0.0f, 0.0f, new Color(0x88, 0xFF, 0x88)
        );
        renderer.setSeriesPaint(0, p1);
        renderer.setSeriesPaint(5, p1);
        renderer.setSeriesPaint(10, p1);

        Paint p2 = new GradientPaint(
                0.0f, 0.0f, new Color(0xFF, 0xFF, 0x22), 0.0f, 0.0f, new Color(0xFF, 0xFF, 0x88)
        );
        renderer.setSeriesPaint(1, p2);
        renderer.setSeriesPaint(6, p2);
        renderer.setSeriesPaint(11, p2);

        Paint p3 = new GradientPaint(
                0.0f, 0.0f, new Color(0xFF, 0xAA, 0x22), 0.0f, 0.0f, new Color(0xFF, 0xAA, 0x22)
        );
        renderer.setSeriesPaint(2, p3);
        renderer.setSeriesPaint(7, p3);
        renderer.setSeriesPaint(12, p3);

        Paint p4 = new GradientPaint(
                0.0f, 0.0f, new Color(0xFF, 0x22, 0x22), 0.0f, 0.0f, new Color(0xFF, 0x22, 0x22)
        );
        renderer.setSeriesPaint(3, p4);
        renderer.setSeriesPaint(8, p4);
        renderer.setSeriesPaint(13, p4);

        Paint p5 = new GradientPaint(
                0.0f, 0.0f, new Color(0xFF, 0xFF, 0xFF), 0.0f, 0.0f, new Color(0xFF, 0xFF, 0xFF)
        );
        renderer.setSeriesPaint(4, p5);
        renderer.setSeriesPaint(9, p5);
        renderer.setSeriesPaint(14, p5);

        renderer.setGradientPaintTransformer(
                new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL)
        );

        SubCategoryAxis domainAxis = new SubCategoryAxis(" ");
        domainAxis.setCategoryMargin(0.05);
        domainAxis.addSubCategory(" ");
        //domainAxis.addSubCategory("Product 2");
        //domainAxis.addSubCategory("Product 3");

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainAxis(domainAxis);
        //plot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
        plot.setRenderer(renderer);
        plot.setFixedLegendItems(createLegendItems());
        return chart;

    }

    /**
     * Creates the legend items for the chart.  In this case, we set them manually because we
     * only want legend items for a subset of the data series.
     *
     * @return The legend items.
     */
    private LegendItemCollection createLegendItems() {
        LegendItemCollection result = new LegendItemCollection();
//        LegendItem item1 = new LegendItem("US", new Color(0x22, 0x22, 0xFF));
        //      LegendItem item2 = new LegendItem("Europe", new Color(0x22, 0xFF, 0x22));
        //    LegendItem item3 = new LegendItem("Asia", new Color(0xFF, 0x22, 0x22));
        //  LegendItem item4 = new LegendItem("Middle East", new Color(0xFF, 0xFF, 0x22));
//        result.add(item1);
        //      result.add(item2);
        //    result.add(item3);
        //  result.add(item4);
        return result;
    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    *
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {
        final DriveView demo = new DriveView("Stacked Bar Chart Demo 4");
        demo.setPreferredSize(new java.awt.Dimension(800, 480));
        demo.pack();

        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

    public void update_battery(int value) {
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        if(value < 10) {
            result.addValue(5, "Product 1 (US)", "Battery");
            result.addValue(0, "Product 1 (Europe)", "Battery");
            result.addValue(0, "Product 1 (Asia)", "Battery");
            result.addValue(0, "Product 1 (Middle East)", "Battery");
            result.addValue(95, "Product 1 (Austin)", "Battery");
        } else if (value < 20) {
            result.addValue(15, "Product 1 (US)", "Battery");
            result.addValue(0, "Product 1 (Europe)", "Battery");
            result.addValue(0, "Product 1 (Asia)", "Battery");
            result.addValue(0, "Product 1 (Middle East)", "Battery");
            result.addValue(85, "Product 1 (Austin)", "Battery");
        } else if (value < 30) {
            result.addValue(0, "Product 1 (US)", "Battery");
            result.addValue(25, "Product 1 (Europe)", "Battery");
            result.addValue(0, "Product 1 (Asia)", "Battery");
            result.addValue(0, "Product 1 (Middle East)", "Battery");
            result.addValue(75, "Product 1 (Austin)", "Battery");
        } else if (value < 40) {
            result.addValue(0, "Product 1 (US)", "Battery");
            result.addValue(35, "Product 1 (Europe)", "Battery");
            result.addValue(0, "Product 1 (Asia)", "Battery");
            result.addValue(0, "Product 1 (Middle East)", "Battery");
            result.addValue(65, "Product 1 (Austin)", "Battery");
        } else if (value < 50) {
            result.addValue(0, "Product 1 (US)", "Battery");
            result.addValue(0, "Product 1 (Europe)", "Battery");
            result.addValue(45, "Product 1 (Asia)", "Battery");
            result.addValue(0, "Product 1 (Middle East)", "Battery");
            result.addValue(55, "Product 1 (Austin)", "Battery");
        } else if (value < 60) {
            result.addValue(0, "Product 1 (US)", "Battery");
            result.addValue(0, "Product 1 (Europe)", "Battery");
            result.addValue(55, "Product 1 (Asia)", "Battery");
            result.addValue(0, "Product 1 (Middle East)", "Battery");
            result.addValue(45, "Product 1 (Austin)", "Battery");
        } else if (value < 70) {
            result.addValue(0, "Product 1 (US)", "Battery");
            result.addValue(0, "Product 1 (Europe)", "Battery");
            result.addValue(65, "Product 1 (Asia)", "Battery");
            result.addValue(0, "Product 1 (Middle East)", "Battery");
            result.addValue(35, "Product 1 (Austin)", "Battery");
        } else if (value < 80) {
            result.addValue(0, "Product 1 (US)", "Battery");
            result.addValue(0, "Product 1 (Europe)", "Battery");
            result.addValue(0, "Product 1 (Asia)", "Battery");
            result.addValue(75, "Product 1 (Middle East)", "Battery");
            result.addValue(25, "Product 1 (Austin)", "Battery");
        } else if (value < 90) {
            result.addValue(0, "Product 1 (US)", "Battery");
            result.addValue(0, "Product 1 (Europe)", "Battery");
            result.addValue(0, "Product 1 (Asia)", "Battery");
            result.addValue(85, "Product 1 (Middle East)", "Battery");
            result.addValue(15, "Product 1 (Austin)", "Battery");
        } else {
            result.addValue(0, "Product 1 (US)", "Battery");
            result.addValue(0, "Product 1 (Europe)", "Battery");
            result.addValue(0, "Product 1 (Asia)", "Battery");
            result.addValue(95, "Product 1 (Middle East)", "Battery");
            result.addValue(5, "Product 1 (Austin)", "Battery");
        }

        batteryDataset = result;
        batteryChart = createBatteryChart(batteryDataset);
        batteryChart.removeLegend();
        batteryPanel = new ChartPanel(batteryChart);
        batteryPanel.setPreferredSize(new java.awt.Dimension(220, 380));
    }

    public void update_temperature(int value) {
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        if(value < 20) {
            result.addValue(20, "Product 1 (US)", " ");
            result.addValue(0, "Product 1 (Europe)", " ");
            result.addValue(0, "Product 1 (Asia)", " ");
            result.addValue(0, "Product 1 (Middle East)", " ");
            result.addValue(180, "Product 1 (Austin)", " ");
        } else if (value < 40) {
            result.addValue(40, "Product 1 (US)", " ");
            result.addValue(0, "Product 1 (Europe)", " ");
            result.addValue(0, "Product 1 (Asia)", " ");
            result.addValue(0, "Product 1 (Middle East)", " ");
            result.addValue(160, "Product 1 (Austin)", " ");
        } else if (value < 60) {
            result.addValue(0, "Product 1 (US)", " ");
            result.addValue(60, "Product 1 (Europe)", " ");
            result.addValue(0, "Product 1 (Asia)", " ");
            result.addValue(0, "Product 1 (Middle East)", " ");
            result.addValue(140, "Product 1 (Austin)", " ");
        } else if (value < 80) {
            result.addValue(0, "Product 1 (US)", " ");
            result.addValue(80, "Product 1 (Europe)", " ");
            result.addValue(0, "Product 1 (Asia)", " ");
            result.addValue(0, "Product 1 (Middle East)", " ");
            result.addValue(120, "Product 1 (Austin)", " ");
        } else if (value < 100) {
            result.addValue(0, "Product 1 (US)", " ");
            result.addValue(0, "Product 1 (Europe)", " ");
            result.addValue(100, "Product 1 (Asia)", " ");
            result.addValue(0, "Product 1 (Middle East)", " ");
            result.addValue(100, "Product 1 (Austin)", " ");
        } else if (value < 120) {
            result.addValue(0, "Product 1 (US)", " ");
            result.addValue(0, "Product 1 (Europe)", " ");
            result.addValue(120, "Product 1 (Asia)", " ");
            result.addValue(0, "Product 1 (Middle East)", " ");
            result.addValue(80, "Product 1 (Austin)", " ");
        } else if (value < 140) {
            result.addValue(0, "Product 1 (US)", " ");
            result.addValue(0, "Product 1 (Europe)", " ");
            result.addValue(140, "Product 1 (Asia)", " ");
            result.addValue(0, "Product 1 (Middle East)", " ");
            result.addValue(60, "Product 1 (Austin)", " ");
        } else if (value < 160) {
            result.addValue(0, "Product 1 (US)", " ");
            result.addValue(0, "Product 1 (Europe)", " ");
            result.addValue(0, "Product 1 (Asia)", " ");
            result.addValue(160, "Product 1 (Middle East)", " ");
            result.addValue(40, "Product 1 (Austin)", " ");
        } else if (value < 180) {
            result.addValue(0, "Product 1 (US)", " ");
            result.addValue(0, "Product 1 (Europe)", " ");
            result.addValue(0, "Product 1 (Asia)", " ");
            result.addValue(180, "Product 1 (Middle East)", " ");
            result.addValue(20, "Product 1 (Austin)", " ");
        } else {
            result.addValue(0, "Product 1 (US)", " ");
            result.addValue(0, "Product 1 (Europe)", " ");
            result.addValue(0, "Product 1 (Asia)", " ");
            result.addValue(200, "Product 1 (Middle East)", " ");
            result.addValue(0, "Product 1 (Austin)", " ");
        }

        tempDataset = result;
        tempChart = createTemperatureChart(tempDataset);
        tempChart.removeLegend();
        tempPanel = new ChartPanel(tempChart);
        tempPanel.setPreferredSize(new java.awt.Dimension(220, 380));
    }



}
