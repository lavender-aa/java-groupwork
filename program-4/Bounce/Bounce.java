/*
 * Program 4: Bounce
 * Course: CMSC 3320 -- Technical Computing Using Java
 * Authors: Group 6
 *      - Lavender Wilson (wil81891@pennwest.edu)
 *      - Camron Mellott (mel98378@pennwest.edu)
 *      - Nicola Razumic-Rushin (raz73517@pennwest.edu)
 */

package Bounce;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Bounce extends Frame
implements WindowListener, ComponentListener, ActionListener, AdjustmentListener, Runnable {
    
    // debug TODO: remove
    void print(String s) {
        System.out.println(s);
    }
    
    // serial UID
    private static final long serialVersionUID = 10L;

    // constants
    private final int WIDTH = 640;  // initial frame width
    private final int HEIGHT = 400; // initial frame height
    private final int BUTTONHEIGHT = 30; // button height
    private final int BUTTONHEIGHTSPACING = 5; // button height spacing
    private final int MINOBJECTSIZE = 10;
    private final int DEFAULTOBJECTSIZE = 21;
    private final int SPEEDSCROLLVISIBLE = 50;
    private final int SIZESCROLLVISIBLE = 25;
    private final int SPEEDSCROLLMIN = 1;
    private final int SPEEDSCROLLMAX = 300;
    private final int SCROLLUNIT = 10; // unit step size
    private final int SCROLLBLOCK = 50; // block step size
    private final int SCROLLBARHEIGHT = BUTTONHEIGHT;
    private final double SECONDS_TO_MILLIS = 1000;

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
    private int maxObjectSize = 500;
    private int objectSize = DEFAULTOBJECTSIZE;
    private int scrollWidth;
    private boolean run; // control program loop
    private boolean paused; // control running vs paused
    private static boolean started; // control animation
    private int scrollSpeed;
    private int delay; // current time delay
    
    // objects
    private Insets insets;
    Button start, shape, clear, tail, quit;
    private Objc object; //TODO: uncomment when object class written
    private Label speedLabel = new Label("Speed", Label.CENTER);
    private Label sizeLabel = new Label("Size", Label.CENTER);
    Scrollbar speedScrollbar, sizeScrollbar;
    private Thread thread;



    // main
    public static void main(String[] args) {
        new Bounce();
    }

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
        setElementPositions();
        started = false;
        startThread(); //TODO: uncomment when startThread() written
    }

    void calculateScreenSizes() {
        insets = getInsets();

        // set screen width (has borders on left and right)
        screenWidth = winWidth - insets.left - insets.right;

        // set screen height (vertical insets, space at bottom for two rows of buttons)
        screenHeight = winHeight - insets.top - insets.bottom - (2 * (BUTTONHEIGHT + BUTTONHEIGHTSPACING));
        
        // set frame size
        setSize(winWidth, winHeight);

        // calculate center, button width, button spacing
        screenCenter = screenWidth / 2;
        buttonWidth = screenWidth / 11; // 11 units
        buttonSpacing = buttonWidth / 4;

        // determine scroll bar width
        scrollWidth = 2 * buttonWidth;

        // recalculate max object size for screen
        if(screenWidth >= screenHeight) { // limited by height
            maxObjectSize = screenHeight - screenHeight/4;
        }
        else { // limited by width
            maxObjectSize = screenWidth - screenWidth/4;
        }

        // set the background color
        setBackground(Color.lightGray);
    }

    void initComponents() throws Exception, IOException {

        // initialize program variables
        paused = true;
        run = true;
        scrollSpeed = 50;
        delay = (int) ((1.0/scrollSpeed) * SECONDS_TO_MILLIS);
        
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

        // create speed scroll bar
        speedScrollbar = new Scrollbar(Scrollbar.HORIZONTAL);
        speedScrollbar.setMaximum(SPEEDSCROLLMAX);
        speedScrollbar.setMinimum(SPEEDSCROLLMIN);
        speedScrollbar.setUnitIncrement(SCROLLUNIT);
        speedScrollbar.setBlockIncrement(SCROLLBLOCK);
        speedScrollbar.setValue(scrollSpeed);
        speedScrollbar.setVisibleAmount(SPEEDSCROLLVISIBLE);
        speedScrollbar.setBackground(Color.gray);

        // create size scroll bar
        sizeScrollbar = new Scrollbar(Scrollbar.HORIZONTAL);
        sizeScrollbar.setMaximum(maxObjectSize);
        sizeScrollbar.setMinimum(MINOBJECTSIZE);
        sizeScrollbar.setUnitIncrement(SCROLLUNIT);
        sizeScrollbar.setBlockIncrement(SCROLLBLOCK);
        sizeScrollbar.setValue(objectSize);
        sizeScrollbar.setVisibleAmount(SIZESCROLLVISIBLE);
        sizeScrollbar.setBackground(Color.gray);

        // create object TODO: uncomment when object class written
        object = new Objc(objectSize, maxObjectSize, screenWidth, screenHeight);
        object.setBackground(Color.white);

        // add scrollbars, labels, object to frame
        add(speedScrollbar);
        add(sizeScrollbar);
        add(speedLabel);
        add(sizeLabel);
        add(object); //TODO: uncomment when object class written

        // add listeners to scrollbars
        speedScrollbar.addAdjustmentListener(this);
        sizeScrollbar.addAdjustmentListener(this);

        // add listeners to the frame
        this.addComponentListener(this);
        this.addWindowListener(this);

        // set sizes, bounds, validate layout
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(getPreferredSize());
        setBounds(winLeft, winTop, WIDTH, HEIGHT);
        validate();
    }

    void setElementPositions() {

        // set button positions
        start.setLocation(
            screenCenter - 2*(buttonWidth+buttonSpacing) - buttonWidth/2,
            screenHeight + BUTTONHEIGHTSPACING + insets.top
        );
        shape.setLocation(
            screenCenter - buttonWidth - buttonSpacing - buttonWidth/2,
            screenHeight + BUTTONHEIGHTSPACING + insets.top
        );
        tail.setLocation(
            screenCenter - buttonWidth/2,
            screenHeight + BUTTONHEIGHTSPACING + insets.top
        );
        clear.setLocation(
            screenCenter + buttonSpacing + buttonWidth/2,
            screenHeight + BUTTONHEIGHTSPACING + insets.top
        );
        quit.setLocation(
            screenCenter + buttonWidth + 2*buttonSpacing + buttonWidth/2,
            screenHeight + BUTTONHEIGHTSPACING + insets.top
        );

        // set button sizes
        start.setSize(buttonWidth, BUTTONHEIGHT);
        shape.setSize(buttonWidth, BUTTONHEIGHT);
        tail.setSize(buttonWidth, BUTTONHEIGHT);
        clear.setSize(buttonWidth, BUTTONHEIGHT);
        quit.setSize(buttonWidth, BUTTONHEIGHT);

        // set scrollbar positions
        speedScrollbar.setLocation(
            insets.left + buttonSpacing,
            screenHeight + BUTTONHEIGHTSPACING + insets.top
        );
        sizeScrollbar.setLocation(
            winWidth - scrollWidth - insets.right - buttonSpacing,
            screenHeight + BUTTONHEIGHTSPACING + insets.top
        );

        // set scrollbar sizes
        speedScrollbar.setSize(scrollWidth, SCROLLBARHEIGHT);
        sizeScrollbar.setSize(scrollWidth, SCROLLBARHEIGHT);

        // set label positions
        speedLabel.setLocation(
            insets.left + buttonSpacing,
            screenHeight + BUTTONHEIGHTSPACING + BUTTONHEIGHT + insets.top
        );
        sizeLabel.setLocation(
            winWidth - scrollWidth - insets.right - buttonSpacing,
            screenHeight + BUTTONHEIGHTSPACING + BUTTONHEIGHT + insets.top
        );

        // set label sizes
        speedLabel.setSize(scrollWidth, BUTTONHEIGHT);
        sizeLabel.setSize(scrollWidth, SCROLLBARHEIGHT);

        // set object bounds
        object.setBounds(insets.left, insets.top, screenWidth, screenHeight); //TODO: uncomment when object class written

    }

    public void startThread()
    {
        if(thread == null)
        {
            thread = new Thread(this);
            thread.start();
            started = true;
        }
        object.repaint();
    }

    public void stop()
    {
        thread.interrupt();
        start.removeActionListener();
        shape.removeActionListener();
        clear.removeActionListener();
        tail.removeActionListener();
        quit.removeActionListener();
        speedScrollbar.removeAdjustmentListener();
        sizeScrollbar.removeAdjustmentListener();
        this.removeComponentListener(this);
        this.removeWindowListener(this);
        dispose();
        System.exit(0); 
    }

    public void startAction(){
        if(start.getLabel().equals("Start")){
            start.setLabel("Stop");
            thread.start();
            paused = false;
        }
        if(start.getLabel().equals("Stop")) {
            start.setLabel("Start");
            thread.interrupt();
            paused = true;
        }
    }

    public void shapeAction(){
        if(shape.getLabel().equals("Circle")){
            shape.setLabel("Square");
            object.rectangle(false);
        }
        if(shape.getLabel().equals("Square")) {
            shape.setLabel("Circle");
            object.rectangle(true);
        }
        if(!started){
            object.clear();
        }
        object.repaint();
    }

    public void tailAction(){
        if(tail.getLabel().equals("Tail")){
            tail.setLabel("No Tail");
            object.setTail(true);
        }
        if(tail.getLabel().equals("No Tail")) {
            tail.setLabel("Tail");
            object.setTail(false);
    }

    public void clearAction(){
        object.clear();
        object.repaint();
    }

    public void quitAction(){
        stop();
    }

    public void speedAction(int ts){
        ts = (ts/2) * 2 + 1; // make odd
        object.update();
    }

    public void sizeAction(){
        
    }

    @Override
    public void run() {}

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        Scrollbar source = e.getSource();

        if (source == sizeScrollbar) {
            sizeAction(e.getValue());
        }
        if (source == speedScrollbar) {
            speedAction();
        }
        object.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == start) {
            startAction();
        }
        if (source == shape) {
            shapeAction();
        }
        if (source == clear) {
            clearAction();
        }
        if (source == tail) {
            tailAction();
        }
        if (source == quit) {
            quitAction();
        }
    }

    @Override
    public void componentHidden(ComponentEvent e) {}

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentResized(ComponentEvent e) {
        winWidth = getWidth();
        winHeight = getHead();
        calculateScreenSizes();
        setElementPositions();
        object.resize(winWidth, winHeight);
    }

    @Override
    public void componentShown(ComponentEvent e) {}

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
}


class Objc extends Canvas
{
    private static final long serialVersionUID = 11L;
    private int screenWidth;
    private int screenHeight;
    private  int sObj;

    private int x, y;
    private boolean rect = true;
    private boolean clear = false;
    private boolean tail = false;

    public Objc(int sb, int w, int h)
    {
        this.screenWidth = w;
        this.screenHeight = h;
        this.sObj = sb;
        rect = true;
        clear = false;
        y = this.screenHeight/2;
        x = this.screenWidth/2;
    }

    public void rectangle(boolean r)
    {
        rect = r;
    }

    public void setTail(boolean t)
    {
        tail = t;
    }

    @Override
    public void update(int ns)
    {
        sObj = ns;
    }

    public void reSize(int w, int h)
    {
        screenWidth = w;
        screenHeight = h;
        y = screenHeight/2;
        x = screenWidth/2;
    }

    @Override
    public void clear()
    {
        clear = true;
    }

    @Override
    public void paint(Graphics g)
    {
        g.setColor(Color.red);
        g.drawRect(0 ,0, this.screenWidth - 1, this.screenHeight -1);
        update(g);
    }

    @Override
    public void update(Graphics g)
    {
        if(!tail)
            clear = true;

        if(clear)
        {
            super.paint(g);
            clear = false;
            g.setColor(Color.red);
            g.drawRect(0 ,0, this.screenWidth - 1, this.screenHeight -1);
        }

        if(rect)
        {
            g.setColor(Color.lightGray);
            g.fillRect(x - (sObj - 1) / 2, y - (sObj - 1) / 2, sObj, sObj);
            g.setColor(Color.black);
            g.drawRect(x - (sObj - 1) / 2, y - (sObj - 1) / 2, sObj - 1, sObj - 1);     
        }
        else 
        {
            g.setColor(Color.lightGray);
            g.fillOval(x - (sObj - 1) / 2, y - (sObj - 1) / 2, sObj, sObj);
            g.setColor(Color.black);
            g.drawOval(x - (sObj - 1) / 2, y - (sObj - 1) / 2, sObj - 1, sObj - 1);     
        }
    }
//Close the class
}