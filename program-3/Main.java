import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/*
 * things to do (general):
 *      - window layout (main constructor) -- 1 person
 *      - window events -- 1 person (easy? most are empty)
 *      - button functions (actionPerformed, all program control) -- split among 2 people
 */

public class Main extends Frame
implements WindowListener, ActionListener, ItemSelectable {

    private List list;
    private Label sourceLabel;
    private Label sourcePathLabel;
    private Label targetPathLabel;
    private Label fileNameLabel;
    private Label messageLabel;
    private TextField fileTextField;
    private Button targetButton;
    private Button okButton;
    private boolean sourceSelected;

    public static void main(String[] args) {
        new Main(args);
    }

    Main(String[] args) {
        
        // screen elements
        list = new List(1, false);
        sourceLabel = new Label("Source: ");
        sourcePathLabel = new Label("[Select a file]");
        targetPathLabel = new Label("");
        fileNameLabel = new Label("File Name: ");
        messageLabel = new Label("");
        fileTextField = new TextField();
        targetButton = new Button("Target");
        okButton = new Button("Copy");
        sourceSelected = false;

        // set up grid bag layout
        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout displ = new GridBagLayout();

        // initalize weights, width/height
        double rowWeight[] = {2};
        double colWeight[] = {1,50,4};

        // set weights, width/height
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

        // set up list
        File dir = getValidDir(args);
        updateList(dir); 
        list.addItemListener(ItemListener listner);


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
        targetButton.setEnabled(false);
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
        okButton.setEnabled(false);
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

    File getValidDir(String[] args) {
        File argDir;
        File dir = new File(System.getProperty("user.dir"));

        // command line directory is valid if it:
        //      - exists
        //      - isn't empty
        // otherwise, default to the directory the program is executed in
        if(args.length > 0) {
            argDir = new File(args[0]);
            if(argDir.exists() && argDir.list().length > 0 ) {
                dir = argDir;
            }
        }

        return dir;
    }

    /* 
          - updates the list with the contents of the passed directory
          - updates the title bar with the path of the passed directory
          This method loops through the current directory to find all subdirectories
    and files. It will also loop through any subdirectories to see if they have their
    own subdirectories and then adds a plus to them when adding them to the list.
     */
    void updateList(File directory) {
        ArrayList<File> fileList = directory.listFiles();
        String currentPath = directory.getPath();
        list.removeAll();
        if (!file.isRoot())
            list.add("..");
        for (File file: fileList) {

            if(file.isDirectory() && !file.listFiles().isEmpty()){
                boolean hasSubdirectories;
                ArrayList<File> subList = file.listFiles();

                for (File f: subList) {
                    if (f.isDirectory()) 
                        hasSubdirectories = true;
                }

                if (hasSubdirectories) {
                    String item = file.getName() + " +";
                    list.add(item);
                }

            }
            else  
                list.add(file.getName());
        }

        this.setTitle(currentPath);
    }

    /* Overriden method from ItemSelcetable Interface.
        It is set to display the name of a selected file in the
        fileNameLabel label
    */
    @Override
    public void itemStateChanged(ItemEvent event)
    {
        if (event.getStateChange() == ItemEvent.selected){
            Object source = event.getItemSelectable();
            File selectedFile = new File(source);
            if (!selectedFile.isDirectory())
                fileNameLabel.setText(source);
        }
        if (event.getStateChange() == ItemEvent.deselected){
            fileNameLabel.setText("");
        }
    }

    /*
        This is the action taken when a directory is double clicked 
        or a file is double clicked.
    */
    public void listAction(){
        String item = list.getSelectedItem();
        File selectedFile = new File(item);

        if (selectedFile.isDirectory())
            updateList(selectedFile);
        else
            fileTextField.setText(selectedFile.getName());
    }

    // separate out each action into their own function:
    //      - list action
    //      - target button action
    //      - text field action
    //      - ok button action
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if (e.source == list)
            listAction();

        throw new UnsupportedOperationException("Unimplemented method 'actionPerformed'");
    }

    @Override
    public void windowActivated(WindowEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'windowActivated'");
    }

    @Override
    public void windowClosed(WindowEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'windowClosed'");
    }

    @Override
    public void windowClosing(WindowEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'windowClosing'");
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'windowDeactivated'");
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'windowDeiconified'");
    }

    @Override
    public void windowIconified(WindowEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'windowIconified'");
    }

    @Override
    public void windowOpened(WindowEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'windowOpened'");
    }
    
}