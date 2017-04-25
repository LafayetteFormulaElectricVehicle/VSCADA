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
    private String[] socs = {"PSOC1", "PSOC2", "PSOC3", "PSOC4"};

    private ProgressBar[] currentBars;
    private ProgressBar[] socBars;

    private Font myFont = new Font("DialogInput", Font.BOLD, 18);
    private int percentLabelOffset = getFontMetrics(myFont).stringWidth("100%") / 3;

    private int viewNumber;
    private Viewer viewer;

    private Timer timer = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            updateDisplay();
        }
    });

    public DriveView2(SCADASystem sys, Viewer viewer, int screenWidth, int screenHeight, int viewNumber) {

        system = sys;
        this.viewer = viewer;
        this.viewNumber = viewNumber;
        currentBars = new ProgressBar[4];
        socBars = new ProgressBar[4];

        double panelWidth = screenWidth / 3;
        int segmentWidth = (int) panelWidth / 5;
        int segmentHeight = (screenHeight - 20) / 9;

        System.out.println(screenHeight);

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

            String val;

            for (int i = 0; i < 4; i++) {
                val = cMap.get(currents[i]).getCalibValue();
                currentBars[i].setValue(val.equals("NaN?") ? 0 : (int) Double.parseDouble(val));
            }

            for (int i = 0; i < 4; i++) {
                val = cMap.get(socs[i]).getCalibValue();
                socBars[i].setValue(val.equals("NaN?") ? 0 : (int) Double.parseDouble(val));
            }

            repaint();
        }
    }

    private void drawBars(Graphics g, ProgressBar[] bars) {
        for (ProgressBar bar : bars) {

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
    }

    @Override
    protected void paintComponent(Graphics g) {
        drawBars(g, currentBars);
        drawBars(g, socBars);
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

}