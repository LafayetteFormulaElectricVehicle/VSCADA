package GUI;


import cockpit.database.SCADASystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import cockpit.database.Sensor;

/**
 * <h1>Drive View</h1>
 * This class will serve as a means to view what the driver will see in the cockpit
 *
 * @author Craig Lombardo
 * @version 1.0
 * @since 2017-03-14
 */

public class DriveView extends JPanel {

    private SCADASystem system;

    private String[] temperatures = {"PCT1AVG", "PCT2AVG", "PCT3AVG", "PCT4AVG"};
    private String[] socs = {"PSOC1", "PSOC1", "PSOC3", "PSOC4"};
    private String[] currents = {"PA1", "PA2", "PA3", "PA4"};

    private ProgressBar temperatureBar;
    private ProgressBar socBar;

    private Font myFont = new Font("DialogInput", Font.BOLD, 24);

    private int percentLabelOffset = getFontMetrics(myFont).stringWidth("100%") / 3;

    private int viewNumber;
    private Viewer viewer;

    private CustomDial currentDial;
    private CustomDial RPMDial;

    private int panelWidth;
    private int segmentWidth;
    private int segmentHeight;

    private Timer timer = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            updateDisplay();
        }
    });

    /**
     * The constructor creates a new DriveView
     * @param sys The SCADASystem that is linked with the DBHandler
     * @param viewer The viewer which will display views
     * @param screenWidth Width of the display screen for auto-formatting
     * @param screenHeight Height of the display screen for auto-formatting
     * @param viewNumber The view number on the viewer (0 if first, 1 if second, etc.)
     */
    public DriveView(SCADASystem sys, Viewer viewer, int screenWidth, int screenHeight, int viewNumber) {

        system = sys;
        this.viewer = viewer;
        this.viewNumber = viewNumber;

        panelWidth = screenWidth / 4;
        segmentWidth = panelWidth / 5;
        segmentHeight = (screenHeight - 20) / 5;

        temperatureBar = new ProgressBar(segmentWidth, segmentHeight - 20, 3 * segmentWidth, segmentHeight * 3,
                60, true, true, myFont, "Temperature", " C");

        socBar = new ProgressBar(segmentWidth + (3 * panelWidth), segmentHeight - 20,
                3 * segmentWidth, segmentHeight * 3,
                100, false, true, myFont, "SOC", " %");

        currentDial = new CustomDial(panelWidth - segmentWidth / 2, (screenHeight - (5 * panelWidth / 3)),
                panelWidth, 200, 135, 270, true, myFont, "A", true);

        RPMDial = new CustomDial(2 * panelWidth + segmentWidth / 2, (screenHeight - (5 * panelWidth / 3)),
                panelWidth, 4000, 135, 270, true, myFont, "RPM", false);

        timer.start();
    }

    private void updateDisplay() {
//        if (viewNumber == viewer.getCurrentView()) {
            HashMap<String, Sensor> cMap = system.getCustomMapping();

            String str;
            double val;
            double maxVal = 0.0;
            double minVal = 1000000000;

            for (int i = 0; i < 4; i++) {
                str = cMap.get(temperatures[i]).getCalibValue();
                val = str.equals("NaN?") ? 0.0 : Double.parseDouble(str);
                if (val > maxVal) maxVal = val;
            }

            temperatureBar.setValue((int) maxVal);

            for (int i = 0; i < 4; i++) {
                str = cMap.get(socs[i]).getCalibValue();
                val = str.equals("NaN?") ? 0.0 : Double.parseDouble(str);
                if (val < minVal) minVal = val;
            }

            socBar.setValue((int) minVal);

            for (int i = 0; i < 4; i++) {
                str = cMap.get(currents[i]).getCalibValue();
                val = str.equals("NaN?") ? 0.0 : Double.parseDouble(str);
                if (val > maxVal) maxVal = val;
            }

            currentDial.setValue((int) maxVal);

            str = cMap.get("MRPM").getCalibValue();
            val = str.equals("NaN?") ? 0.0 : Double.parseDouble(str);

            RPMDial.setValue((int) val);

            repaint();
//        }
    }

    private void drawBar(Graphics g, ProgressBar bar) {
        g.setFont(myFont);
        g.setColor(Color.black);
        g.drawRect(bar.x, bar.y, bar.width, bar.height);

        g.setColor(new Color(bar.red, bar.green, 0));
        int fillHeight = (int) (((float) bar.value / bar.max) * bar.height);

        g.fillRect(bar.x, bar.y + (bar.height - fillHeight), bar.width, fillHeight);

        g.setColor(Color.BLACK);
        if (bar.showPercent) g.drawString(bar.value + bar.over, bar.x + bar.width / 2 - percentLabelOffset, bar.y - 5);

        g.drawString(bar.label, bar.x + bar.width / 2 - bar.labelOffset,
                bar.y + bar.height + 25);

        bar.update = false;
    }

    private void drawDial(Graphics g, CustomDial dial) {
        g.setColor(Color.black);

        g.drawArc(dial.x, dial.y, dial.diameter, dial.diameter, 0, 360);

        g.drawLine(dial.innerX, dial.innerY, dial.outerX, dial.outerY);

//        g.drawString("" + ((int) (dial.percent * 100)) + "%", dial.innerX - 10, dial.y + dial.diameter + 25);

        if(dial.printVal) g.drawString("" + dial.value + " " + dial.label, dial.x + dial.diameter / 2 - dial.labelOffset,
                dial.y + dial.diameter + 25);
        else g.drawString("" + dial.label, dial.x + dial.diameter / 2 - dial.labelOffset,
                dial.y + dial.diameter + 25);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

        drawBar(g, temperatureBar);
        drawBar(g, socBar);

        drawDial(g, currentDial);
        drawDial(g, RPMDial);

        g.drawString("X mph", 2 * panelWidth - segmentWidth/3 - getFontMetrics(myFont).stringWidth("000") / 2, segmentHeight/2);

    }

    class ProgressBar {

        private int x;
        private int y;
        private int width;
        private int height;

        private int red;
        private int green;

        private String label;

        private int labelOffset;
        private int value = 0;
        private int max;

        private String over;
        private boolean update = false;

        private boolean deplete;
        private boolean showPercent;

        private ProgressBar(int x, int y, int width, int height, int max, boolean deplete, boolean showPercent, Font font, String label, String over) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.deplete = deplete;
            this.max = max;
            this.showPercent = showPercent;

            this.over = over;
            this.label = label;
            labelOffset = getFontMetrics(font).stringWidth(label) / 2;
        }

        public void setValue(int val) {
            if (val != value) {
                value = val;
                update = true;
                if (deplete) checkInvertedColors();
                else checkColors();
            }
        }

        private void checkColors() {
            //0-255
            //0-50 == red decreases
            //50-100 == green increases
            if (value >= 50) green = 255;
            else green = (int) ((value / 50f) * 255);

            if (value >= 50) red = (int) ((1 - ((value - 50) / 50f)) * 255);
            else red = 255;
        }

        private void checkInvertedColors() {
            //0-255
            //0-50 == red decreases
            //50-100 == green increases
            if (value >= 50) red = 255;
            else red = (int) ((value / 50f) * 255);

            if (value >= 50) green = (int) ((1 - ((value - 50) / 50f)) * 255);
            else green = 255;
        }

    }


    class CustomDial {
        public int x;
        public int y;

        public int innerX;
        public int innerY;

        public int outerX;
        public int outerY;

        public int angle;

        public int diameter;

        public int value = 0;
        public int maxValue;

        public int startAngle;
        public int maxAngle;

        public boolean clockwise;

        public float percent;
        private int radius;

        private String label;
        private int labelOffset;

        private boolean printVal;

        private CustomDial(int x, int y, int diameter, int maxValue, int startAngle, int maxAngle, boolean clockwise, Font font, String label, boolean printVal) {
            this.x = x;
            this.y = y;
            this.diameter = diameter;
            this.maxValue = maxValue;
            this.startAngle = startAngle;
            this.maxAngle = maxAngle;

            innerX = x + diameter / 2;
            innerY = y + diameter / 2;

            this.clockwise = clockwise;

            this.radius = diameter / 2 - 2;

            setValue(0);

            this.label = label;
            labelOffset = getFontMetrics(font).stringWidth(label) / 2;

            this.printVal = printVal;
        }

        public void setValue(int val) {
            value = val;

            if(value < 0) value = 0;
            if(value > maxValue) value = maxValue;

            percent = ((float) value / maxValue);

            angle = (int) (percent * maxAngle);
            if (!clockwise) angle *= -1;

            outerX = (int) (innerX + (Math.cos(Math.toRadians(startAngle + angle)) * radius));
            outerY = (int) (innerY + (Math.sin(Math.toRadians(startAngle + angle)) * radius));
        }

    }

}




/*
Thirds code
for (int i = 0; i < 4; i++) {
            currentBars[i] = new ProgressBar(segmentWidth + (segmentWidth * (i % 2 == 1 ? 2 : 0)),
                    i < 2 ? segmentHeight : 5 * segmentHeight, segmentWidth, segmentHeight * 3,
                    100, i == 1, i != 1, myFont, "Pack " + (i + 1));
        }

        for (int i = 0; i < 4; i++) {
            socBars[i] = new ProgressBar((int) (panelWidth * 2) + segmentWidth + (segmentWidth * (i % 2 == 1 ? 2 : 0)),
                    i < 2 ? segmentHeight : 5 * segmentHeight, segmentWidth, segmentHeight * 3,
                    100, i == 1, i != 1, myFont, "Pack " + (i + 1));
        }
 */