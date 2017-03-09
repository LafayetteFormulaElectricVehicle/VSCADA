    package cockpit.database;

    import javax.swing.*;

    import java.awt.*;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;
    import java.awt.Dimension;

    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;

    public class SCADAViewer extends JFrame {

        private JPanel grid;
        private DBHandler handler;

        private String file = "/Users/CraigLombardo/Desktop/output.txt";
        private Boolean rel = true;

        private HashMap<String, JLabel> sensors;

        private SCADASystem sys;

        public SCADAViewer() {
            Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            setTitle("SCADA Viewer");
            setSize((int) d.getWidth()/2, (int) d.getHeight());
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            init();
            setVisible(true);
        }

        public static void main(String[] args) {
            SCADAViewer test = new SCADAViewer();
        }

        public void init() {
            sensors = new HashMap<String, JLabel>();
            grid = new JPanel(new GridLayout(0, 4));
            JButton quit = new JButton("Quit");

            quit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

    //        primaryStage.setScene(scene);

            grid.add(new JLabel("Sensor ID:"));
            grid.add(new JLabel("Sensor Name:"));
            grid.add(new JLabel("Sensor Value:"));
            grid.add(quit);

            handler = new DBHandler("SCADA.db", "SQLSchema/");
            sys = new SCADASystem(handler, file);
            createMapping(handler.getIDNames());
            Thread thr = new Thread(sys);
            thr.start();

            add(grid);

    //        Thread thr = new Thread(sys);
    //        thr.start();
    //
    //        //Timer here
    //
            updateNow();

            Timer displayTimer = new Timer(1000, listener);
            displayTimer.start();
        }

        public void createMapping(ArrayList<ArrayList<String>> info) {
            String id;
            String name;
            for (ArrayList<String> r : info) {
                id = r.get(0);
                name = r.get(1);
                JLabel tmp = new JLabel("");
                sensors.put(id, tmp);
                grid.add(new JLabel("0x" + Integer.toHexString(Integer.parseInt(id))));
                grid.add(new JLabel(name));
                grid.add(tmp);
                grid.add(new JLabel(""));
            }
        }

        private void updateNow(){
            for (Map.Entry<String, String> entry : sys.getMap().entrySet()) {
                sensors.get(entry.getKey()).setText(entry.getValue());
            }
        }

        ActionListener listener = new ActionListener(){
            public void actionPerformed(ActionEvent event){
                updateNow();
            }
        };

    }