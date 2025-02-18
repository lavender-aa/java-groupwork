/*
 * Program 4: Bounce
 * Course: CMSC 3320 -- Technical Computing Using Java
 * Authors: Group 6
 *      - Lavender Wilson (wil81891@pennwest.edu)
 *      - Camron Mellott (mel98378@pennwest.edu)
 *      - Nicola Razumic-Rushin (raz73517@pennwest.edu)
 */

// package Bounce;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Bounce extends Frame
implements WindowListener, ComponentListener, ActionListener {
    
    // serial UID
    private static final long serialVersionUID = 10L;

    // constants
    private final int WIDTH = 640;  // initial frame width
    private final int HEIGHT = 400; // initial frame height
    private final int BTNH = 20; // button height
    private final int BTNHS = 5; // button height spacing

    // primitives + strings
    private int winWidth = WIDTH;
    private int winHeight = HEIGHT;
    private int winTop = 10;  // top of frame
    private int winLeft = 10; // left side of frame
    private int screenWidth;
    private int screenHeight;
    private int screenCenter = WIDTH / 2;
    private int buttonWidth = 50;
    private int buttonSpacing = buttonWidth / 4;
    
    // objects
    private Insets insets;
    Button start, shape, clear, tail, quit;




    // actions
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if(source == start) {
            handleStartButton();
        }
        else if(source == shape) {
            handleShapeButton();
        }
        else if(source == clear) {
            handleClearButton();
        }
        else if(source == tail) {
            handleTailButton();
        }
        else { // source == quit
            handleQuitButton();
        }
    }

    void handleStartButton() {
        if(start.getLabel().equals("Pause")) {
            start.setLabel("Run");
        }
        else {
            start.setLabel("Pause");
        }
    }

    void handleShapeButton() {
        if(shape.getLabel().equals("Circle")) {
            shape.setLabel("Square");
        }
        else {
            shape.setLabel("Circle");
        }
    }

    void handleClearButton() {

    }

    void handleTailButton() {

    }

    void handleQuitButton() {

    }




    // component events
    @Override
    public void componentHidden(ComponentEvent e) {}

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentResized(ComponentEvent e) {
        winWidth = getWidth();
        winHeight= getHeight();
        calculateScreenSizes();
        setButtonPositions();
    }

    @Override
    public void componentShown(ComponentEvent e) {}




    // window events
    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        stop();
    }

    void stop() {
        // remove all listeners
        start.removeActionListener(this);
        shape.removeActionListener(this);
        clear.removeActionListener(this);
        tail.removeActionListener(this);
        quit.removeActionListener(this);
        this.removeComponentListener(this);
        this.removeWindowListener(this);

        // dispose, exit with code 0
        dispose();
        System.exit(0);
    }

    @Override
    public void windowDeactivated(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowOpened(WindowEvent e) {}




    // constructor
    public Bounce() {
        setLayout(null);
        setVisible(true);
        calculateScreenSizes();
        try {
            initComponents();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        setButtonPositions();
    }

    void calculateScreenSizes() {
        insets = getInsets();

        // set screen width (has borders on left and right)
        screenWidth = winWidth - insets.left - insets.right;

        // set screen height (vertical insets, space at bottom for two rows of buttons)
        screenHeight = winHeight - insets.top - insets.bottom - (2 * (BTNH + BTNHS));
        
        // set frame size
        setSize(winWidth, winHeight);

        // calculate center, button width, button spacing
        screenCenter = screenWidth / 2;
        buttonWidth = screenWidth / 11; // 11 units
        buttonSpacing = buttonWidth / 4;

        // set the background color
        setBackground(Color.lightGray);
    }

    void initComponents() throws Exception, IOException{
        
        // create buttons
        start = new Button("Run");
        shape = new Button("Circle");
        clear = new Button("Clear");
        tail = new Button("No Tail");
        quit = new Button("Quit");

        // add buttons to the frame
        add("Center", start);
        add("Center", shape);
        add("Center", clear);
        add("Center", tail);
        add("Center", quit);

        // add actionListeners to buttons
        start.addActionListener(this);
        shape.addActionListener(this);
        clear.addActionListener(this);
        tail.addActionListener(this);
        quit.addActionListener(this);

        // add listeners to the frame
        this.addComponentListener(this);
        this.addWindowListener(this);

        // set sizes, bounds, validate layout
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(getPreferredSize());
        setBounds(winLeft, winTop, WIDTH, HEIGHT);
        validate();
    }

    void setButtonPositions() {

        // set button positions
        start.setLocation(
            screenCenter - 2*(buttonWidth+buttonSpacing) - buttonWidth/2,
            screenHeight + BTNHS + insets.top
        );
        shape.setLocation(
            screenCenter - buttonWidth - buttonSpacing - buttonWidth/2,
            screenHeight + BTNHS + insets.top
        );
        tail.setLocation(
            screenCenter - buttonWidth/2,
            screenHeight + BTNHS + insets.top
        );
        clear.setLocation(
            screenCenter + buttonSpacing + buttonWidth/2,
            screenHeight + BTNHS + insets.top
        );
        quit.setLocation(
            screenCenter + buttonWidth + 2*buttonSpacing + buttonWidth/2,
            screenHeight + BTNHS + insets.top
        );

        // set button sizes
        start.setSize(buttonWidth, BTNH);
        shape.setSize(buttonWidth, BTNH);
        tail.setSize(buttonWidth, BTNH);
        clear.setSize(buttonWidth, BTNH);
        quit.setSize(buttonWidth, BTNH);
    }

    // main function
    public static void main(String[] args) {
        new Bounce();
    }
}
