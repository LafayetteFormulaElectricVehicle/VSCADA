package GUI;

/**
 * Created by CraigLombardo on 3/14/17.
 */

import cockpit.database.DBHandler;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.awt.*;
import javax.swing.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QueryView {

    private GridBagLayout searchLayout;
    private GridBagConstraints searchConstraints;
    private JPanel searchPanel;

    private JTextField tagSearch;
    private JTextField sysSearch;
    private JTextField date1Search;
    private JTextField date2Search;

    private JPanel panelMain;

    private DBHandler handler;

    private JTextArea results;
    private JScrollPane resultsScroll;

    private boolean working = false;

    public QueryView(DBHandler DBH) {
        handler = DBH;
        panelMain = new JPanel(new BorderLayout());

        createSearchPanel();

        results = new JTextArea();
        results.setEditable(false);
        resultsScroll = new JScrollPane(results); //place the JTextArea in a scroll pane
        panelMain.add(resultsScroll, BorderLayout.CENTER);
    }

    public JPanel getPane() {
        return panelMain;
    }

    public void addSearchComp(int x, int y, int wx, Component comp) {
        searchConstraints.gridx = x;
        searchConstraints.gridy = y;

        searchConstraints.weightx = wx;

        searchLayout.setConstraints(comp, searchConstraints);
        searchPanel.add(comp);
    }

    public void createSearchPanel() {
        createSearchConstraints();

        JLabel tag = new JLabel("ID(s)");
        tag.setHorizontalAlignment(JTextField.CENTER);

        JLabel sys = new JLabel("System(s)");
        sys.setHorizontalAlignment(JTextField.CENTER);

        JLabel date1 = new JLabel("Start Time");
        date1.setHorizontalAlignment(JTextField.CENTER);

        JLabel date2 = new JLabel("End Time");
        date2.setHorizontalAlignment(JTextField.CENTER);

        tagSearch = new JTextField(12);
        tagSearch.setHorizontalAlignment(SwingConstants.CENTER);
        tagSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                execute();
            }
        });

        sysSearch = new JTextField(12);
        sysSearch.setHorizontalAlignment(SwingConstants.CENTER);
        sysSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                execute();
            }
        });

        date1Search = new JTextField(12);
        date1Search.setHorizontalAlignment(SwingConstants.CENTER);
        date1Search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                execute();
            }
        });

        date2Search = new JTextField(12);
        date2Search.setHorizontalAlignment(SwingConstants.CENTER);
        date2Search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                execute();
            }
        });

        addSearchComp(0, 0, 25, tag);
        addSearchComp(1, 0, 25, sys);
        addSearchComp(2, 0, 25, date1);
        addSearchComp(3, 0, 25, date2);

        addSearchComp(0, 1, 25, tagSearch);
        addSearchComp(1, 1, 25, sysSearch);
        addSearchComp(2, 1, 25, date1Search);
        addSearchComp(3, 1, 25, date2Search);

        JButton executeQuery = new JButton("Execute Query");
        executeQuery.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //Do stuff
                        execute();
                    }
                }
        );

        JButton clean = new JButton("Clean Screen");
        clean.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //Do stuff
                        tagSearch.setText("");
                        sysSearch.setText("");
                        date1Search.setText("");
                        date2Search.setText("");
                        results.setText("");
                    }
                }
        );

        JButton export = new JButton("Export Data");
        export.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //Do stuff
                        exportData();
                    }
                }
        );

        addSearchComp(0, 2, 25, clean);
        addSearchComp(1, 2, 25, executeQuery);
        addSearchComp(3, 2, 25, export);

        panelMain.add(searchPanel, BorderLayout.PAGE_START);
    }

    public void execute() {
        if (working) return;
        working = true;
        checkDateFormat();

        String tagQueryIn = tagSearch.getText().toUpperCase();
        tagSearch.setText(tagQueryIn);
        String sysQueryIn = sysSearch.getText().toUpperCase();
        String d1QueryIn = date1Search.getText();
        String d2QueryIn = date2Search.getText();

        if (tagQueryIn.equals("")) tagQueryIn = null;
        if (sysQueryIn.equals("")) sysQueryIn = null;
        if (d1QueryIn.equals("")) d1QueryIn = null;
        if (d2QueryIn.equals("")) d2QueryIn = null;

        ArrayList<ArrayList<String>> output = handler.getInfo(tagQueryIn, sysQueryIn, d1QueryIn, d2QueryIn);

        String displayText = "tag,description,system,units,value,TimeStamp,\n";
        for (ArrayList<String> arr : output) {
            for (String s : arr) {
                displayText += s + ",";
            }
            displayText += "\n";
        }
        results.setText(displayText);
        results.setCaretPosition(0);
        working = false;
    }

    private void createSearchConstraints() {
        searchLayout = new GridBagLayout();
        searchConstraints = new GridBagConstraints();
        searchPanel = new JPanel(searchLayout);

        searchConstraints.fill = GridBagConstraints.BOTH;
        searchConstraints.anchor = GridBagConstraints.CENTER;
        searchConstraints.weighty = 1;
    }

    private void checkDateFormat() {
        String errMsg = "";

        String d1 = date1Search.getText();
        String d2 = date2Search.getText();

        if (!d1.equals("")) {
            if (!d1.matches("\\d+-\\d+-\\d+ \\d+:\\d+:\\d+")) {
                if (d1.matches("\\d+-\\d+-\\d+")) {
                    if (d2.isEmpty()) d2 = d1 + " 23:59:59";
                    d1 += " 00:00:00";
                } else {
                    errMsg += "'" + d1 + "' is invalid\n";
                    d1 = "";
                }
            }
        }
        if (!d2.equals("")) {
            if (!d2.matches("\\d+-\\d+-\\d+ \\d+:\\d+:\\d+")) {
                if (d2.matches("\\d+-\\d+-\\d+")) {
                    d2 += " 00:00:00";
                } else {
                    errMsg += "'" + d2 + "' is invalid\n";
                    d2 = "";
                }
            }
        }

        date1Search.setText(d1);
        date2Search.setText(d2);

        if (!errMsg.equals(""))
            JOptionPane.showMessageDialog(panelMain, "Proper Format:\n2017-04-09 23:29:58\n\n" + errMsg);
    }

    private void exportData() {
        try {
            JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));
            System.out.println("¯\\_(ツ)_/¯");
            if (fc.showOpenDialog(panelMain) == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                PrintWriter pr = new PrintWriter(file.getName());
                pr.print(results.getText());
                pr.close();
            }

        } catch (java.io.FileNotFoundException e) {
            System.out.println("File not found, or something ¯\\_(ツ)_/¯");
        }
    }

}