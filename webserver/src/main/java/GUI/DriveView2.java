package GUI;

/**
 * Created by CraigLombardo on 3/14/17.
 */

import cockpit.database.DBHandler;
import cockpit.database.SCADASystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import cockpit.database.Sensor;

public class DriveView2 extends JPanel {

    private SCADASystem system;

    private String[] currents = {"PA1", "PA2", "PA3", "PA4"};
    private String[] socs = {"PSOC1", "PSOC1", "PSOC3", "PSOC4"};

    private ProgressBar currentBar;
    private ProgressBar socBar;

    private Font myFont = new Font("DialogInput", Font.BOLD, 18);
    private int percentLabelOffset = getFontMetrics(myFont).stringWidth("100%") / 3;

    private int viewNumber;
    private Viewer viewer;

    private CustomDial c1;

    private Timer timer = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            updateDisplay();
        }
    });

    public DriveView2(SCADASystem sys, Viewer viewer, int screenWidth, int screenHeight, int viewNumber) {

        system = sys;
        this.viewer = viewer;
        this.viewNumber = viewNumber;


        int panelWidth = screenWidth / 4;
        int segmentWidth = panelWidth / 3;
        int segmentHeight = (screenHeight - 20) / 5;


        currentBar = new ProgressBar(segmentWidth, segmentHeight, segmentWidth, segmentHeight * 3,
                100, true, true, myFont, "Pack 1");

        socBar = new ProgressBar(segmentWidth + (panelWidth * 3), segmentHeight, segmentWidth, segmentHeight * 3,
                100, false, true, myFont, "Pack 1");

        c1 = new CustomDial(300, 300, 200, 100, 135, 270, true);

        timer.start();
    }

    public static void main(String[] args) {

        String file = System.getProperty("user.home") + "/Desktop/output.txt";
        DBHandler handler = new DBHandler();
        SCADASystem sys = new SCADASystem(handler, file);

        Thread thr = new Thread(sys);
        thr.start();

        SCADAViewer test = new SCADAViewer(sys);
        test.addCard(new DriveView2(sys, test, test.frameWidth, test.frameHeight, 0), "Drive View");
    }

    private void updateDisplay() {
        if (viewNumber == viewer.getCurrentView()) {
            HashMap<String, Sensor> cMap = system.getCustomMapping();

            String str;
            double val;
            double maxVal = 0.0;

            for (int i = 0; i < 4; i++) {
                str = cMap.get(currents[i]).getCalibValue();
                val = str.equals("NaN?") ? 0.0 : Double.parseDouble(str);
                if (val > maxVal) maxVal = val;
            }

            currentBar.setValue((int) maxVal);

            maxVal = 0.0;
            for (int i = 0; i < 4; i++) {
                str = cMap.get(socs[i]).getCalibValue();
                val = str.equals("NaN?") ? 0.0 : Double.parseDouble(str);
                if (val > maxVal) maxVal = val;
            }

            socBar.setValue((int) maxVal);
            c1.setValue((int) maxVal);

            repaint();
        }
    }

    private void drawBar(Graphics g, ProgressBar bar) {

        g.setColor(Color.black);
        g.drawRect(bar.x, bar.y, bar.width, bar.height);

        g.setColor(new Color(bar.red, bar.green, 0));
        int fillHeight = (int) (((float) bar.value / bar.max) * bar.height);

        g.fillRect(bar.x, bar.y + (bar.height - fillHeight), bar.width, fillHeight);

        g.setColor(Color.BLACK);
        if (bar.showPercent) g.drawString(bar.value + "%", bar.x + bar.width / 2 - percentLabelOffset, bar.y - 5);

        g.drawString(bar.label, bar.x + bar.width / 2 - bar.labelOffset,
                bar.y + bar.height + 20);

        bar.update = false;
    }

    private void drawDial(Graphics g, CustomDial dial) {
        g.setColor(Color.black);

        g.drawArc(dial.x, dial.y, dial.diameter, dial.diameter, 0, 360);

        g.drawLine(dial.innerX, dial.innerY, dial.outerX, dial.outerY);

        g.drawString("" + ((int) (dial.percent * 100)) + "%", dial.innerX - 10, dial.y + dial.diameter + 20);
    }

    @Override
    protected void paintComponent(Graphics g) {

        drawBar(g, currentBar);
        drawBar(g, socBar);

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));

        drawDial(g, c1);
    }

    class ProgressBar {

        public int x;
        public int y;
        public int width;
        public int height;

        public int red;
        public int green;

        public String label;

        public int labelOffset;
        public int value = 0;
        public int max;

        public boolean update = false;

        public boolean deplete;
        public boolean showPercent;

        private ProgressBar(int x, int y, int width, int height, int max, boolean deplete, boolean showPercent, Font font, String label) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.deplete = deplete;
            this.max = max;
            this.showPercent = showPercent;

            this.label = label;
            labelOffset = getFontMetrics(font).stringWidth(label) / 3;
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

        private CustomDial(int x, int y, int diameter, int maxValue, int startAngle, int maxAngle, boolean clockwise) {
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
        }

        public void setValue(int val) {
            value = val;

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