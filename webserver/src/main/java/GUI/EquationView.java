package GUI;

import cockpit.database.DBHandler;
import cockpit.database.Equation;
import cockpit.database.SCADASystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by CraigLombardo on 4/14/17.
 */
public class EquationView {

    private GridBagLayout innerLayout;
    private GridBagConstraints innerConstraints;
    private JPanel innerPanel;

    private GridBagLayout buttonLayout;
    private GridBagConstraints buttonConstraints;
    private JPanel buttonPanel;

    private JPanel panelMain;
    private DBHandler handler;
    private SCADASystem sys;

    private int equationCount = 0;

    private ArrayList<JTextField> destinations;
    private ArrayList<JTextField> equations;

    public EquationView(DBHandler DBH, SCADASystem SS) {

        panelMain = new JPanel(new BorderLayout());

        handler = DBH;
        sys = SS;

        destinations = new ArrayList<>();
        equations = new ArrayList<>();

        createInnerConstraints();
        createInnerComponents();

        createButtonConstraints();
        createButtonComponents();

        JScrollPane scrollPanel = new JScrollPane(innerPanel);
        panelMain.add(scrollPanel, BorderLayout.CENTER);
        scrollPanel.getVerticalScrollBar().setUnitIncrement(20);

        panelMain.add(buttonPanel, BorderLayout.PAGE_START);
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Testing");
        frame.setPreferredSize(new Dimension(850, 600));
        frame.setMinimumSize(new Dimension(300, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DBHandler h = new DBHandler();
        String file = System.getProperty("user.home") + "/Desktop/output.txt";
        SCADASystem s = new SCADASystem(h, file);

        for(Equation e : s.getEquations()){
            System.out.println(e.equation);

        }

        EquationView e = new EquationView(h, s);

        frame.add(e.getPanel());

        frame.pack();
        frame.setVisible(true);

    }

    public JPanel getPanel() {
        return panelMain;
    }

    public void createInnerComponents() {
        ArrayList<Equation> eqs = sys.getEquations();

        addInnerComp(0, 0, 30, new JLabel(""));

        JLabel lbl = new JLabel("Destination");
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        addInnerComp(1, 0, 20, lbl);

        lbl = new JLabel("Equation");
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        addInnerComp(2, 0, 20, lbl);

        addInnerComp(3, 0, 30, new JLabel("        "));

        JTextField dest;
        JTextField eq;

        for (int i = 0; i < eqs.size(); i++) {
            dest = new JTextField(eqs.get(i).destination);
            dest.setHorizontalAlignment(SwingConstants.CENTER);

            eq = new JTextField(eqs.get(i).equation);
            eq.setHorizontalAlignment(SwingConstants.CENTER);

            addInnerComp(1, i + 1, 20, dest);
            addInnerComp(2, i + 1, 20, eq);

            destinations.add(dest);
            equations.add(eq);

            equationCount++;
        }
    }

    public void createButtonComponents() {
        JButton quit = new JButton("Quit");

        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JButton newLine = new JButton("New Equation");
        newLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEquation();
            }
        });

        JButton update = new JButton("Update Equations");
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handler.updateEquations(destinations, equations);
            }
        });

        addButtonComp(0, 0, 20, new JLabel(" "));
        addButtonComp(1, 0, 20, newLine);
        addButtonComp(2, 0, 20, update);
        addButtonComp(3, 0, 20, quit);
        addButtonComp(4, 0, 20, new JLabel(" "));
    }

    public void addInnerComp(int x, int y, int wx, Component comp) {
        innerConstraints.gridx = x;
        innerConstraints.gridy = y;

        innerConstraints.weightx = wx;

        innerLayout.setConstraints(comp, innerConstraints);
        innerPanel.add(comp);
    }

    private void createInnerConstraints() {
        innerLayout = new GridBagLayout();
        innerConstraints = new GridBagConstraints();
        innerPanel = new JPanel(innerLayout);

        innerConstraints.fill = GridBagConstraints.HORIZONTAL;
        innerConstraints.anchor = GridBagConstraints.CENTER;
    }


    public void addButtonComp(int x, int y, int wx, Component comp) {
        buttonConstraints.gridx = x;
        buttonConstraints.gridy = y;

        buttonConstraints.weightx = wx;

        buttonLayout.setConstraints(comp, buttonConstraints);
        buttonPanel.add(comp);
    }

    private void createButtonConstraints() {
        buttonLayout = new GridBagLayout();
        buttonConstraints = new GridBagConstraints();
        buttonPanel = new JPanel(buttonLayout);

        buttonConstraints.fill = GridBagConstraints.BOTH;
        buttonConstraints.anchor = GridBagConstraints.CENTER;
        buttonConstraints.weighty = 1;
    }

    private void addEquation() {
        JTextField dest = new JTextField();
        dest.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField eq = new JTextField();
        eq.setHorizontalAlignment(SwingConstants.CENTER);

        addInnerComp(1, ++equationCount, 20, dest);
        addInnerComp(2, equationCount, 20, eq);

        destinations.add(dest);
        equations.add(eq);
    }

}
