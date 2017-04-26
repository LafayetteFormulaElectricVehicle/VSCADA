package GUI;

/**
 * Created by CraigLombardo on 3/14/17.
 */

import cockpit.database.SCADASystem;

import javax.swing.*;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cockpit.database.Sensor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import server.HTTPRequest;

public class ChargingView extends JPanel {

    private SCADASystem system;

    private String[] couls = {"PCOUL1", "PCOUL2", "PCOUL3", "PCOUL4"};
    private String[] socs = {"PSOC1", "PSOC2", "PSOC3", "PSOC4"};

    private BatteryCircle[] progressBars;

    private Font myFont = new Font("DialogInput", Font.BOLD, 18);
    private int percentLabelOffset = getFontMetrics(myFont).stringWidth("100%") / 3;

    private int viewNumber;
    private Viewer viewer;

    private String ip;
    private boolean server;

    private HTTPRequest request;

    private Timer timer = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            updateDisplay();
        }
    });

    public ChargingView(SCADASystem sys, Viewer viewer, int screenWidth, String ipAddr, int viewNumber) {
        request = new HTTPRequest();

        system = sys;
        this.viewer = viewer;
        this.viewNumber = viewNumber;
        progressBars = new BatteryCircle[4];

        int diameter = screenWidth / 5;

        for (int i = 0; i < 4; i++) {
            progressBars[i] = new BatteryCircle(diameter / 5 + (diameter * 6 * i / 5), 20, diameter, myFont, "Pack " + (i + 1));
        }

        ip = ipAddr;
        server = !ip.equals("");

        timer.start();
    }

//    public static void main(String[] args) {
//
//        JFrame window = new JFrame("Test");
//        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        window.setSize(1000, 400);
//        window.setMinimumSize(new Dimension(300, 300));
//
//        ChargingView demo = new ChargingView(1000);
//        window.getContentPane().add(demo, BorderLayout.CENTER);
//
//        window.setVisible(true);
//
//    }

    private void updateDisplay() {
        if (viewNumber == viewer.getCurrentView()) {

            String coulVal;
            String socVal;

            if (server) {
                try {
                    JsonElement j = request.sendGet("http://" + ip + ":3000/cmap");
                    JsonObject obj = j.getAsJsonObject();

                    for (int i = 0; i < 4; i++) {
                        System.out.println("Here");
                        coulVal = getCalibValue(obj.get(couls[i]).toString());
                        socVal = getCalibValue(obj.get(socs[i]).toString());

                        progressBars[i].setCoulombs(coulVal.equals("NaN?") ? 0 : (int) Double.parseDouble(coulVal));
                        progressBars[i].setValue(socVal.equals("NaN?") ? 0 : (int) Double.parseDouble(socVal));
                    }

                } catch (Exception e) {
//                System.out.println("No Connection");
                }

            } else {
                HashMap<String, Sensor> cMap = system.getCustomMapping();
                for (int i = 0; i < 4; i++) {
                    coulVal = cMap.get(couls[i]).getCalibValue();
                    socVal = cMap.get(socs[i]).getCalibValue();

                    progressBars[i].setCoulombs(coulVal.equals("NaN?") ? 0 : (int) Double.parseDouble(coulVal));
                    progressBars[i].setValue(socVal.equals("NaN?") ? 0 : (int) Double.parseDouble(socVal));
                }
            }

            for (BatteryCircle circle : progressBars) {
                if (circle.update) {
                    repaint();
                    break;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        for (BatteryCircle circle : progressBars) {
//            g.setColor(Color.black);
//            g.drawArc(circle.x, circle.y, circle.diameter, circle.diameter, 0, 360);

            g.setColor(new Color(circle.red, circle.green, 0));
            int angle = -(int) (((float) circle.value / 100) * 360);

            g.fillArc(circle.x, circle.y, circle.diameter, circle.diameter, 90, angle);

            g.setColor(getBackground());

            g.fillArc(circle.x + circle.diameter / 4, circle.y + circle.diameter / 4,
                    circle.diameter / 2, circle.diameter / 2, 90, angle - 5);

            g.setColor(Color.BLACK);
            g.drawString(circle.value + "%", circle.x + circle.diameter / 2 - percentLabelOffset,
                    circle.y + circle.diameter / 2);

            g.drawString(circle.label, circle.x + circle.diameter / 2 - circle.labelOffset,
                    circle.y + circle.diameter + 20);

            if (circle.charging) {
                g.drawString("Charging", circle.x + circle.diameter / 2 - 25,
                        circle.y + circle.diameter + 40);
            }

            circle.update = false;
        }
    }

    private String getCalibValue(String json) {
        int end = 0;

        for (int i = json.length() - 1; i >= 0; i--) {
            if (json.charAt(i) == '"') {
                if (end == 0) end = i;
                else return json.substring(i + 1, end);
            }
        }
        return "";
    }

    class BatteryCircle {

        public int x;
        public int y;
        public int diameter;
        public int red;

        public String label;
        public int labelOffset;

        public int green;
        public int value = 0;

        public int coulombs = 0;

        public boolean update = false;
        public boolean charging = false;

        private BatteryCircle(int x, int y, int diameter, Font font, String label) {
            this.x = x;
            this.y = y;
            this.diameter = diameter;
            this.label = label;
            labelOffset = getFontMetrics(font).stringWidth(label) / 3;
        }

        private void setCoulombs(int c) {
            if (c >= coulombs && c != 0) {
                if (charging) update = true;
                charging = true;
            } else {
                if (!charging) update = true;
                charging = false;
            }
            coulombs = c;
        }

        public void setValue(int val) {
            if (val != value) {
                value = val;
                update = true;
                checkColors();
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

    }

}