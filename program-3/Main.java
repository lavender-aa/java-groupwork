/*
 * Program 3: GUI File Copy
 * Course: CMSC 3320 -- Technical Computing Using Java
 * Authors: Group 6
 *      - Lavender Wilson (wil81891@pennwest.edu)
 *      - Camron Mellott (mel98378@pennwest.edu)
 *      - Nicola Razumic-Rushin (raz73517@pennwest.edu)
 */

import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends Frame
implements WindowListener, ActionListener{

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
    private boolean sourceSelected;
    private String separator;

    public static void main(String[] args) {
        new Main(args);
    }

    Main(String[] args) {
        
        // screen elements
        list = new List(100);
        sourceLabel = new Label("Source: ");
        sourcePathLabel = new Label("[Select a file]");
        targetPathLabel = new Label("");
        fileNameLabel = new Label("File Name: ");
        messageLabel = new Label("");
        fileTextField = new TextField();
        targetButton = new Button("Target");
        okButton = new Button("OK");
        sourceSelected = false;
        separator = System.getProperty("file.separator");

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
        drawList(dir);

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

    void drawList(File dir) {

        // clear list
        list.removeAll();

        // update title bar
        this.setTitle(dir.getAbsolutePath());

        // add parent folder (if not root folder)
        if(this.getTitle().indexOf(separator) != this.getTitle().length() - 1) {
            list.add("..");
        }

        // add dir contents to list
        String[] contents = dir.list();
        for(int i = 0; i < contents.length; i++) {
            File dirTest = new File(dir.getPath() + separator + contents[i]);
            String toAdd = dirTest.getName();
            if(dirTest.isDirectory()) {
                toAdd += "+";
            }
            list.add(toAdd);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // clear any messages
        messageLabel.setText("");

        // get the action of the source
        Object source = e.getSource();

        // handle accordingly
        if(source == list) handleList();
        else if(source == targetButton) handleTarget();
        else if(source == fileTextField) handleTextField();
        else if(source == okButton) handleOkButton();
        else messageLabel.setText("Unknown source detected.");
    }

    void handleList() {
        String item = list.getSelectedItem();

        // if clicked on parent folder: draw parent to screen
        // otherwise: handle item selected
        if(item.equals("..")) {
            String dirPath = this.getTitle();
            File parent = new File(dirPath.substring(0,dirPath.lastIndexOf(separator) + 1));
            drawList(parent);
        }
        else {
            // file of item selected
            if(item.contains("+")) item = item.substring(0,item.length() - 1);
            File file = new File(this.getTitle() + separator + item);

            // full directory: draw to screen
            if(file.isDirectory() && file.list().length > 0) {
                drawList(file);
            }

            // empty directory: print message
            else if(file.isDirectory()) {
                messageLabel.setText("Error: Cannot display empty directory.");
            }

            // file: update source or target and text field
            else {
                fileTextField.setText(file.getName());
                if(!sourceSelected) {
                    sourcePathLabel.setText(file.getAbsolutePath());
                    targetButton.setEnabled(true);
                }
                else if(sourcePathLabel.getText().equals(file.getAbsolutePath())) {
                    messageLabel.setText("Error: Target cannot be the same file as the source.");
                }
                else {
                    targetPathLabel.setText(file.getAbsolutePath());
                    okButton.setEnabled(true);
                }
            }
        }
    }

    void handleTarget() {
        sourceSelected = true;
        targetPathLabel.setText("[Select a file]");
        targetButton.setEnabled(false);
    }

    void handleTextField() {
        File test;
        // use the current directory, unless the prompt
        // starts with a slash (presumably is a valid path)
        
        // determine if input is local or global path, set accordingly
        if(fileTextField.getText().isEmpty()) return;
        if(fileTextField.getText().charAt(0) == '/') {
            test = new File(fileTextField.getText());
        }
        else {
            test = new File(this.getTitle() + separator + fileTextField.getText());
        }

        // check if data is good, act accordingly
        // print specific error messages for bad data
        String path = test.getAbsolutePath();
        if(test.exists() && test.isFile()) {
            if(!sourceSelected) {
                sourcePathLabel.setText(path);
                targetButton.setEnabled(true);
            }
            else if(path.equals(sourcePathLabel.getText())) {
                messageLabel.setText("Error: Target cannot be the same file as the source.");
            }
            else {
                targetPathLabel.setText(test.getAbsolutePath());
                targetButton.setEnabled(false);
                okButton.setEnabled(true);
            }
        }
        else if(!test.exists()){
            messageLabel.setText("Error: File doesn't exist.");
        }
        else if(!test.isFile()) {
            messageLabel.setText("Error: Input is a directory.");
        }

    }

    void handleOkButton() {
        // copy source into target
        File source = new File(sourcePathLabel.getText());
        File target = new File(targetPathLabel.getText());

        // open files for reading and writing
        BufferedReader reader = null;
        FileWriter writer = null;
        try{
            reader = new BufferedReader(new FileReader(source));
            writer = new FileWriter(target);
        } catch (FileNotFoundException e) {
            messageLabel.setText("Error: Source or target file not found.");
        } catch (IOException e) {
            messageLabel.setText("Error: Cannot open target file");
        }

        // copy files
        try {
            String line = reader.readLine();
            while(line != null) {
                writer.write(line + "\n");
                line = reader.readLine();
            }
            writer.close();
            reader.close();
        } catch (IOException e) {
            messageLabel.setText("Error: Input file closed before copy finished.");
        }

        // reset screen elements
        messageLabel.setText("copy success");
        sourcePathLabel.setText("[Select a file]");
        targetPathLabel.setText("");
        targetButton.setEnabled(false);
        fileTextField.setText("");
        sourceSelected = false;
        okButton.setEnabled(false);
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