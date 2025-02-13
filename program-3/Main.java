import java.io.*;
import java.awt.*;
import java.awt.event.*;

/*
 * things to do (general):
 *      - window layout (main constructor) -- 1 person
 *      - window events -- 1 person (easy? most are empty)
 *      - button functions (actionPerformed, all program control) -- split among 2 people
 */

public class Main extends Frame
implements WindowListener, ActionListener {

    // elements in window
    private List list;
    private Label sourceLabel;
    private Label sourcePathLabel;
    private Label targetPathLabel;
    private Label fileNameLabel;
    private Label messageLabel;
    private TextField fileTextField;
    private Button targetButton;
    private Button okButton;

    public static void main(String[] args) {
        new Main();
    }

    Main() {
        // init screen elements
        list = new List(100);
        sourceLabel = new Label("Source: ");
        sourcePathLabel = new Label("c:/source");
        targetPathLabel = new Label("c:/target");
        fileNameLabel = new Label("File Name: ");
        messageLabel = new Label("messages go here");
        fileTextField = new TextField();
        targetButton = new Button("Target");
        okButton = new Button("Ok");

        // set up grid bag layout
        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout displ = new GridBagLayout();

        // initalize weights, width/height
        // int rowHeight[] = {10,1,1,1,1};
        // int colWidth[] = {1,10,3};
        double rowWeight[] = {2};
        double colWeight[] = {1,10,2};

        // set weights, width/height
        // displ.rowHeights = rowHeight;
        // displ.columnWidths = colWidth;
        displ.rowWeights = rowWeight;
        displ.columnWeights = colWeight;

        // set bounds, layout
        this.setBounds(20,20,800,800);
        this.setLayout(displ);

        // set constraints
        c.anchor = GridBagConstraints.WEST;

        // add list
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        list.setSize(300,800);
        displ.setConstraints(list, c);
        this.add(list);
        list.addActionListener(this); // only sends events on double click

        // init list
        list.removeAll();
        list.add(".."); // parent folder
        list.add("example_file_1.txt");
        for(int i = 2; i < 40; i++) {
            list.add("list item number " + (i + 1));
        }

        // add source label
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        displ.setConstraints(sourceLabel, c);
        this.add(sourceLabel);

        // add source path label
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        displ.setConstraints(sourcePathLabel, c);
        this.add(sourcePathLabel);

        // add target button
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        displ.setConstraints(targetButton, c);
        this.add(targetButton);
        targetButton.addActionListener(this);

        // add file name label
        c.gridy = 3;
        displ.setConstraints(fileNameLabel, c);
        this.add(fileNameLabel);

        // add target path label
        c.gridy = 2;
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        displ.setConstraints(targetPathLabel, c);
        this.add(targetPathLabel);

        // add text field
        c.gridx = 1;
        c.gridy = 3;
        displ.setConstraints(fileTextField, c);
        this.add(fileTextField);
        fileTextField.addActionListener(this);

        // add ok button
        c.gridx = 2;
        displ.setConstraints(okButton, c);
        this.add(okButton);
        okButton.addActionListener(this);

        // add message label
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 4;
        displ.setConstraints(messageLabel, c);
        this.add(messageLabel);

        // set visible, window listener
        this.setVisible(true);
        this.addWindowListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        // remove listeners, dispose 
        this.removeWindowListener(this);
        list.removeActionListener(this);
        this.dispose();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowOpened(WindowEvent e) {}
    
}