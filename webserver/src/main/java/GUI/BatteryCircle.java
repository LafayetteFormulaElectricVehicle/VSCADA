/*
package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class BatteryCircle extends JPanel {
    private final static int MAX_PROGRESS_AMOUNT = 100;
    private static final int DELAY = 50;
    private Timer timer;
    private int prgValue = 0;

    private int red;
    private int green;

    private double x;
    private double y;
    private double width;
    private double height;

    public BatteryCircle(double x, double y, double width, double height) {

        this.x = x;
        this.y = x;
        this.width = width;
        this.height = height;

        timer = new Timer(1000/60, null);
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                action();
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Circular Battery");
        frame.add(new BatteryCircle(0,0,100,100));
        frame.setSize(1000, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
//        Graphics2D g2 = (Graphics2D) g;
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font("DialogInput", Font.BOLD, 18));
        int maxWidth = g.getFontMetrics().stringWidth("100%");

        checkColors();


        g.setColor(new Color(red, green, 0));
        int angle = -(int) (((float) prgValue / MAX_PROGRESS_AMOUNT) * 360);
        g.fillArc(0, 0, getWidth(), getHeight(), 90, angle);

        g.setColor(getBackground());

        g.fillArc(getWidth() / 4, getHeight() / 4,
                getWidth() / 2, getHeight() / 2, 90, angle - 5);

        g.setColor(Color.BLACK);
        g.drawString(prgValue + "%", getWidth() / 2 - maxWidth / 2, getHeight() / 2);
    }

    private void checkColors() {
//        0-255
//        0-50 == red decreases
//        50-100 == green increases

        if (prgValue >= 50) green = 255;
        else green = (int) ((prgValue / 50f) * 255);

        if (prgValue >= 50) red = (int) ((1 - ((prgValue - 50) / 50f)) * 255);
        else red = 255;

    }

    private void action() {
        prgValue++;
        repaint();
        if (prgValue >= MAX_PROGRESS_AMOUNT) timer.stop();
    }
}
*/
