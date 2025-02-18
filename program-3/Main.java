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

    // screen elements
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
        // init screen elements
        list = new List(100);
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
        // updateList(dir); TODO: uncomment, delete below (after implementation)
        list.add("..");
        list.add("example_empty_dir");
        list.add("example_file.txt");
        list.add("example_nonempty_dir+");
        for(int i = 4; i < 100; i++) {
            list.add("list element " + (i + 1));
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

    // this method:
    //      - updates the list with the contents of the passed directory
    //      - updates the title bar with the path of the passed directory
    void updateList(File directory) {

    }

    // separate out each action into their own function:
    //      - list action
    //      - target button action
    //      - text field action
    //      - ok button action
    @Override
    public void actionPerformed(ActionEvent e) {
        // Determine which source triggered the action
        Object source = e.getSource();
        
        // Handle list item selection (directory/file)
        if (source == list) {
            String selectedItem = list.getSelectedItem();
            
            // If ".." is selected, go to parent directory (if not already at root)
            if ("..".equals(selectedItem)) {
                File parentDir = new File(sourcePathLabel.getText()).getParentFile();
                if (parentDir != null) {
                    updateList(parentDir);
                }
            }
            // Otherwise, handle file or directory selection
            else {
                File selectedFile = new File(sourcePathLabel.getText(), selectedItem);
                
                // If it is a file, update source path label
                if (selectedFile.isFile()) {
                    sourcePathLabel.setText(selectedFile.getAbsolutePath());
                    sourceSelected = true;  // Mark that source is selected
                }
                // If it's a directory, update the list with the contents of the directory
                else if (selectedFile.isDirectory()) {
                    updateList(selectedFile);
                }
            }
        }
        
        // Handle Target button click
        if (source == targetButton) {
            // Enable the OK button once a target path is selected
            if (sourceSelected) {
                targetPathLabel.setText(sourcePathLabel.getText());
                okButton.setEnabled(true);
                fileTextField.setEnabled(true);  // Enable the target file name text field
            } else {
                messageLabel.setText("Source file not specified.");
            }
        }

        // Handle OK button click
        if (source == okButton) {
            String sourcePath = sourcePathLabel.getText();
            String targetPath = targetPathLabel.getText();
            String targetFileName = fileTextField.getText().trim();

            // Validate if the source and target paths are set
            if (sourcePath.isEmpty()) {
                messageLabel.setText("Source file not specified.");
            } else if (targetFileName.isEmpty()) {
                messageLabel.setText("Target file not specified.");
            } else {
                // Perform file copy using FileReader and PrintWriter
                try {
                    // Open the source and target files
                    File sourceFile = new File(sourcePath);
                    File targetFile = new File(targetPath, targetFileName);

                    // Use BufferedReader to read the source file
                    BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
                    PrintWriter writer = new PrintWriter(new FileWriter(targetFile));

                    // Read from source file and write to target file
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.println(line);
                    }

                    reader.close();
                    writer.close();

                    // Success message
                    messageLabel.setText("File Copied");

                    // Reset the UI after copying
                    resetUI();

                } catch (IOException ex) {
                    messageLabel.setText("An IO Error occurred, terminating.");
                    ex.printStackTrace();
                }
            }
        }
        
        // Handle text field (target file name) action (when the user presses Enter)
        if (source == fileTextField) {
            // Directly call the OK button action logic when Enter is pressed in the TextField
            String sourcePath = sourcePathLabel.getText();
            String targetPath = targetPathLabel.getText();
            String targetFileName = fileTextField.getText().trim();

            // Validate if the source and target paths are set
            if (sourcePath.isEmpty()) {
                messageLabel.setText("Source file not specified.");
            } else if (targetFileName.isEmpty()) {
                messageLabel.setText("Target file not specified.");
            } else {
                // Perform file copy using FileReader and PrintWriter
                try {
                    // Open the source and target files
                    File sourceFile = new File(sourcePath);
                    File targetFile = new File(targetPath, targetFileName);

                    // Use BufferedReader to read the source file
                    BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
                    PrintWriter writer = new PrintWriter(new FileWriter(targetFile));

                    // Read from source file and write to target file
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.println(line);
                    }

                    reader.close();
                    writer.close();

                    // Success message
                    messageLabel.setText("File Copied");

                    // Reset the UI after copying
                    resetUI();

                } catch (IOException ex) {
                    messageLabel.setText("An IO Error occurred, terminating.");
                    ex.printStackTrace();
                }
            }
        }
    }

    // Reset the UI after the copy operation
    void resetUI() {
        sourcePathLabel.setText("[Select a file]");
        targetPathLabel.setText("");
        fileTextField.setText("");
        messageLabel.setText("");
        okButton.setEnabled(false);
        fileTextField.setEnabled(false);
        sourceSelected = false;
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