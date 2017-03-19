package cockpit.database;

/**
 * Created by CraigLombardo on 3/14/17.
 */
import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QueryView extends JPanel {

    JScrollPane panelItems;
    GridBagLayout panelLayout;
    GridBagConstraints panelConstraints;
    JPanel panel;

    JTextField idSearch;
    JTextField sysSearch;
    JTextField dateSearch;

    JPanel panelMain;

    public QueryView(){
        panelMain = new JPanel(new BorderLayout());
        panelLayout = new GridBagLayout();
        panelConstraints = new GridBagConstraints();
        panel = new JPanel(panelLayout);

        panelConstraints.fill = GridBagConstraints.NONE;
        panelConstraints.anchor = GridBagConstraints.CENTER;
        panelConstraints.weighty = 1;

        createHeader();
        panelMain.add(panel);

        JTextArea text = new JTextArea();
        text.setEditable(false);
        JScrollPane scroll = new JScrollPane(text); //place the JTextArea in a scroll pane
        panelMain.add(scroll, BorderLayout.CENTER);
    }

    public JPanel getPane(){
        return panelMain;
    }

    public void addComp(int x, int y, Component comp){
        panelConstraints.gridx = x;
        panelConstraints.gridy = y;

        panelLayout.setConstraints(comp, panelConstraints);
        panel.add(comp);
    }

    public void createHeader(){
        JPanel buttons = new JPanel(new GridLayout(3,3));

        JLabel id = new JLabel("  ID(s)");
        idSearch = new JTextField();

        JLabel sys = new JLabel("  System(s)");
        sysSearch = new JTextField();

        JLabel date = new JLabel("  Date or Date Range");
        dateSearch = new JTextField();

        buttons.add(id);
        buttons.add(sys);
        buttons.add(date);

        buttons.add(idSearch);
        buttons.add(sysSearch);
        buttons.add(dateSearch);

        buttons.add(new JLabel("  Results"));
        buttons.add(new JLabel());

        JButton executeQuery = new JButton("Execute Query");
        executeQuery.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //Do stuff
                        execute();
                    }
                }
        );
        buttons.add(executeQuery);

        panelMain.add(buttons, BorderLayout.PAGE_START);
    }

    public void execute(){
        System.out.println("'" + idSearch.getText() + "' , '" + sysSearch.getText() + "' , '" + dateSearch.getText() + "'");
    }

    public static void main(String[] args) {

        JFrame window = new JFrame("Test");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(600, 600);
        window.setMinimumSize(new Dimension(300, 300));

        QueryView test1 = new QueryView();
        window.getContentPane().add(test1.getPane(), BorderLayout.CENTER);

        window.setVisible(true);
    }

}