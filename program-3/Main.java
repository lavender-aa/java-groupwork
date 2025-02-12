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
    private Label targetLabel;
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
        list = new List(40);
        sourceLabel = new Label("Source: C:/aoeusntahoeu/abcdefg/example_file.txt");
        targetLabel = new Label("C:/some_dir/file.txt");
        fileNameLabel = new Label("File Name: ");
        messageLabel = new Label("This is at the bottom of the screen."); // TODO: remove text
        fileTextField = new TextField();
        targetButton = new Button("Target");
        okButton = new Button("Ok");

        // set up grid bag layout
        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout displ = new GridBagLayout();

        // initalize weights, width/height
        // int rowHeight[] = {10,1,1,1,1};
        // int colWidth[] = {1,20,3};
        double rowWeight[] = {100,1,1,1,1};
        double colWeight[] = {1,20,5};

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
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.NONE;

        // add list
        c.gridwidth = 3;
        c.gridheight = 1;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
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
        // c.gridwidth = 1;
        // c.gridheight = 1;
        // c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        displ.setConstraints(sourceLabel, c);
        this.add(sourceLabel);

        // add target button
        // c.gridwidth = 1;
        // c.gridheight = 1;
        // c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 2;
        displ.setConstraints(targetButton, c);
        this.add(targetButton);
        targetButton.addActionListener(this);

        // add target label
        c.gridwidth = 1;
        c.gridheight = 1;
        // c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 2;
        c.gridx = 1;
        displ.setConstraints(targetLabel, c);
        this.add(targetLabel);

        // add file name label
        // c.gridwidth = 1;
        // c.gridheight = 1;
        // c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 3;
        displ.setConstraints(fileNameLabel, c);
        this.add(fileNameLabel);

        // add text field
        c.gridwidth = 1;
        // c.gridheight = 1;
        // c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        displ.setConstraints(fileTextField, c);
        this.add(fileTextField);
        fileTextField.addActionListener(this);

        // add ok button
        c.gridwidth = 1;
        // c.gridheight = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 2;
        c.gridy = 3;
        displ.setConstraints(okButton, c);
        this.add(okButton);
        okButton.addActionListener(this);

        // add message label
        c.gridwidth = 3;
        // c.gridheight = 1;
        // c.fill = GridBagConstraints.HORIZONTAL;
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