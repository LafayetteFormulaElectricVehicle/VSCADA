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
 * <h1>Equation View</h1>
 * This class will serve as a means to view what Equations are in the DB
 *
 * @author Craig Lombardo
 * @version 1.0
 * @since 2017-04-14
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

    /**
     * The constructor for an Equation View Panel
     * @param DBH The DBHandler associated with the SCADA.db
     * @param SS The SCADASystem associated with the DBHandler
     */
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

    /**
     * This method returns the panel that the view was drawn on
     * @return The panel
     */
    public JPanel getPanel() {
        return panelMain;
    }

    private void createInnerComponents() {
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

    private void createButtonComponents() {
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

    private void addInnerComp(int x, int y, int wx, Component comp) {
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


    private void addButtonComp(int x, int y, int wx, Component comp) {
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
