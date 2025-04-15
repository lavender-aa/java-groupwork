/*
* Program 7: Chat
* Course: CMSC 3320 -- Technical Computing Using Java
* CET 350 (?)
* Authors: Group 6
*      - Lavender Wilson (wil81891@pennwest.edu)
*      - Camron Mellott (mel98378@pennwest.edu)
*/

import java.awt.*;
import java.awt.event.*;

public class Chat extends Frame
implements WindowListener, ActionListener, Runnable {

    // serial UID
    private static final long serialVersionUID = 10L;

    // GUI primitives
    private Dimension window = new Dimension(700, 400);

    // GUI objects
    private Panel controls;
    private TextArea chatTA;
    private TextField chatboxTF;
    private Button sendBTN;
    private Label hostLBL;
    private TextField hostTF;
    private Button changeHostBTN;
    private Button startServerBTN;
    private Label portLBL;
    private TextField portTF;
    private Button changePortBTN;
    private Button connectBTN;
    private Button disconnectBTN;
    private TextArea statusTA;

    public Chat() {
        setLayout(new BorderLayout());

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        // init layout
        double colWeight[] = {1, 0.25, 5, 1, 2, 2};
        gbl.columnWeights = colWeight;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;

        // panels
        controls = new Panel();
        controls.setLayout(gbl);

        // chat textarea
        chatTA = new TextArea("", 10, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);
        add("North", chatTA);

        // chatbox textfield
        chatboxTF = new TextField();
        c.gridy = 0;
        c.gridwidth = 5;
        gbl.setConstraints(chatboxTF, c);
        controls.add(chatboxTF);

        // send button
        sendBTN = new Button("Send");
        c.gridx = 5;
        c.gridwidth = 1;
        gbl.setConstraints(sendBTN, c);
        controls.add(sendBTN);

        // host label
        hostLBL = new Label("Host:");
        c.gridx = 1;
        c.gridy = 1;
        gbl.setConstraints(hostLBL, c);
        controls.add(hostLBL);

        // host textfield
        hostTF = new TextField();
        c.gridx = 2;
        gbl.setConstraints(hostTF, c);
        controls.add(hostTF);

        // change host button
        changeHostBTN = new Button("Change Host");
        c.gridx = 4;
        gbl.setConstraints(changeHostBTN, c);
        controls.add(changeHostBTN);

        // start server button
        startServerBTN = new Button("Start Server");
        c.gridx = 5;
        gbl.setConstraints(startServerBTN, c);
        controls.add(startServerBTN);

        // port label
        portLBL = new Label("Port:");
        c.gridx = 1;
        c.gridy = 2;
        gbl.setConstraints(portLBL, c);
        controls.add(portLBL);

        // port textfield
        portTF = new TextField();
        c.gridx = 2;
        gbl.setConstraints(portTF, c);
        controls.add(portTF);

        // change port button
        changePortBTN = new Button("Change Port");
        c.gridx = 4;
        gbl.setConstraints(changePortBTN, c);
        controls.add(changePortBTN);

        // connect button
        connectBTN = new Button("Connect");
        c.gridx = 5;
        gbl.setConstraints(connectBTN, c);
        controls.add(connectBTN);

        // disconnect button
        disconnectBTN = new Button("Disconnect");
        c.gridy = 3;
        gbl.setConstraints(disconnectBTN, c);
        controls.add(disconnectBTN);

        // control sheet
        add("Center", controls);

        // status textarea
        statusTA = new TextArea("", 3, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);
        add("South", statusTA);


        // listeners
        chatboxTF.addActionListener(this);
        sendBTN.addActionListener(this);
        hostTF.addActionListener(this);
        changeHostBTN.addActionListener(this);
        startServerBTN.addActionListener(this);
        portTF.addActionListener(this);
        changePortBTN.addActionListener(this);
        connectBTN.addActionListener(this);
        disconnectBTN.addActionListener(this);
        addWindowListener(this);

        // set sizes, validate layout
        setPreferredSize(window);
        setMinimumSize(new Dimension(300, 300));
        setSize(getPreferredSize());
        validate();
        setVisible(true);
    }

    public static void main(String[] args) {
        new Chat();
    }

    void stop() {
        // remove listeners
        chatboxTF.removeActionListener(this);
        sendBTN.removeActionListener(this);
        hostTF.removeActionListener(this);
        changeHostBTN.removeActionListener(this);
        startServerBTN.removeActionListener(this);
        portTF.removeActionListener(this);
        changePortBTN.removeActionListener(this);
        connectBTN.removeActionListener(this);
        disconnectBTN.removeActionListener(this);
        removeWindowListener(this);
        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        stop();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void run() {}
}