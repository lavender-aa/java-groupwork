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
    
    // serial UID
    private static final long serialVersionUID = 10L;

    // constants
    private final int WIDTH = 640;  // initial frame width
    private final int HEIGHT = 400; // initial frame height
    private final int BUTTONHEIGHT = 30; // button height
    private final int BUTTONHEIGHTSPACING = 5; // button height spacing
    private final int MAXOBJECTSIZE = 500;
    private final int MINOBJECTSIZE = 10;
    private final int DEFAULTOBJECTSIZE = 21;
    private final int SCROLLVISIBLE = 10;
    private final int SCROLLUNIT = 1; // unit step size
    private final int SCROLLBLOCK = 10; // block step size
    private final int SCROLLBARHEIGHT = BUTTONHEIGHT;
    private final double DELAY = 10; // smallest delay between steps

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
    private int objectSize = DEFAULTOBJECTSIZE;
    private int speedScrollMin = 1;
    private int speedScrollMax = 100 + SCROLLVISIBLE;
    private int scrollWidth;
    private boolean run; // control program loop
    private boolean paused; // control running vs paused
    private static boolean started; // control animation
    private int scrollSpeed;
    private int delay; // current time delay (?)
    
    // objects
    private Insets insets;
    Button start, shape, clear, tail, quit;
    private Objc object;
    private Label speedLabel = new Label("Speed", Label.CENTER);
    private Label sizeLabel = new Label("Size", Label.CENTER);
    Scrollbar speedScrollbar, sizeScrollbar;
    private Thread thread;




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
            paused = true;
            thread.interrupt();
        }
        else {
            start.setLabel("Pause");
            paused = false;
            start();
        }
    }

    void handleShapeButton() {
        if(!started) {
            object.clear();
        }
        if(shape.getLabel().equals("Circle")) {
            shape.setLabel("Square");
            object.rectangle(false);
        }
        else {
            shape.setLabel("Circle");
            object.rectangle(true);
        }
        object.repaint();
    }

    void handleClearButton() {
        object.clear();
        object.repaint();
    }

    void handleTailButton() {
        if(tail.getLabel().equals("Tail")) {
            tail.setLabel("No Tail");
            object.setTail(true);
            
        }
        else if(tail.getLabel().equals("No Tail")) {
            tail.setLabel("Tail");
            object.setTail(false);
        }
    }

    void handleQuitButton() {
        stop();
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
        object.resize(screenWidth, screenHeight);
        setElementPositions();
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
        speedScrollbar.removeAdjustmentListener(this);
        sizeScrollbar.removeAdjustmentListener(this);
        this.removeComponentListener(this);
        this.removeWindowListener(this);

        // close thread
        run = false;
        thread.interrupt();

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




    // adjustment events
    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        int newSize;
        int newSpeed;
        Scrollbar sb = (Scrollbar) e.getSource();
        if(sb == speedScrollbar) {
            newSpeed = sb.getValue();
            delay = (int) ((1/newSpeed) * DELAY);
            thread.interrupt();
        }
        else if(sb == sizeScrollbar) {
            newSize = sb.getValue();
            newSize = (newSize/2)*2 + 1; // force the size to be odd for center position
            object.updateSize(newSize);
            sizeScrollbar.setValue(object.getObjSize());
        }
        object.repaint();
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
        start();
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

        // set the background color
        setBackground(Color.lightGray);
    }

    void initComponents() throws Exception, IOException{

        // initialize program variables
        paused = true;
        run = true;
        scrollSpeed = 10;
        delay = (int) ((1/scrollSpeed) * DELAY);
        
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
        speedScrollbar.setMaximum(speedScrollMax);
        speedScrollbar.setMinimum(speedScrollMin);
        speedScrollbar.setUnitIncrement(SCROLLUNIT);
        speedScrollbar.setBlockIncrement(SCROLLBLOCK);
        speedScrollbar.setValue(scrollSpeed);
        speedScrollbar.setVisibleAmount(SCROLLVISIBLE);
        speedScrollbar.setBackground(Color.gray);

        // create size scroll bar
        sizeScrollbar = new Scrollbar(Scrollbar.HORIZONTAL);
        sizeScrollbar.setMaximum(MAXOBJECTSIZE);
        sizeScrollbar.setMinimum(MINOBJECTSIZE);
        sizeScrollbar.setUnitIncrement(SCROLLUNIT);
        sizeScrollbar.setBlockIncrement(SCROLLBLOCK);
        sizeScrollbar.setValue(objectSize);
        sizeScrollbar.setVisibleAmount(SCROLLVISIBLE);
        sizeScrollbar.setBackground(Color.gray);

        // create object
        object = new Objc(objectSize, screenWidth, screenHeight);
        object.setBackground(Color.white);

        // add scrollbars, labels, object to frame
        add(speedScrollbar);
        add(sizeScrollbar);
        add(speedLabel);
        add(sizeLabel);
        add(object);

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
        object.setBounds(insets.left, insets.top, screenWidth, screenHeight);

    }




    // thread related

    void start() {
        if(thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void run() {
        while(run) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {}
            if(!paused) {
                started = true;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {}
                object.updateSize(objectSize);
                object.repaint();
            }
        }
        started = false;
    }

    void checkObjectResize() {
        int x = object.getX();
        int y = object.getY();
        int half = (object.getObjSize() - 1)/2;

        // right bound check
        if(x + half >= screenWidth) {
            object.setX(screenWidth - half);
        }

        // bottom bound check
        if(y + half >= screenHeight) {
            object.setY(screenHeight - half);
        }
    }
    
    
    
    
    // main function
    public static void main(String[] args) {
        started = false;
        new Bounce();
    }
}


// objc class
class Objc extends Canvas {
    
    // data
    private static final long serialVersionUID = 11L;
    private int screenWidth;
    private int screenHeight;
    private int objectSize;
    private int oldsize;
    private int oldx, oldy;
    private int x, y;
    private int xdir, ydir;
    private boolean rect;
    private boolean clear;
    private boolean tail;

    public Objc(int size, int w, int h) {
        screenWidth = w;
        screenHeight = h;
        objectSize = size;
        rect = true;
        clear = false;
        y = screenHeight/2;
        x = screenWidth/2;
        xdir = 1;
        ydir = 1;
    }

    public void setTail(boolean val) {
        tail = val;
    }

    public void setX(int val) {
        x = val;
    }

    public void setY(int val) {
        y = val;
    }

    public void rectangle(boolean r) {
        rect = r;
    }

    public int getObjSize() {
        return objectSize;
    }

    public void updateSize(int size) {
        int half = size/2;
        oldsize = objectSize;
        if(y + half > screenHeight) {
            objectSize = screenHeight - y;
        }
        else if(x + half > screenWidth) {
            objectSize = screenWidth - x;
        }
        else {
            objectSize = size;
        }
    }

    public void resize(int w, int h) {
        screenWidth = w;
        screenHeight = h;
        y = screenHeight / 2;
        x = screenWidth / 2;
    }

    public void clear() {
        clear = true;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.red);
        g.drawRect(0, 0, screenWidth-1, screenHeight-1);
        update(g);
    }

    @Override
    public void update(Graphics g) {
        if(!tail) {
            g.setColor(getBackground());
            if(rect) {
                g.fillRect(oldx-(objectSize-1)/2, oldy-(objectSize-1)/2, objectSize, objectSize);
            }
            else {
                g.fillOval(oldx-(objectSize-1)/2-1, oldy-(objectSize-1)/2-1, objectSize+2, objectSize+2);
            }
        }
        if(clear) {
            super.paint(g);
            clear = false;
            g.setColor(Color.red);
            g.drawRect(0, 0, screenWidth-1, screenHeight-1);
        }
        if(rect) {
            g.setColor(Color.lightGray);
            g.fillRect(x-(objectSize-1)/2, y-(objectSize-1)/2, objectSize, objectSize);
            g.setColor(Color.black);
            g.drawRect(x-(objectSize-1)/2, y-(objectSize-1)/2, objectSize-1, objectSize-1);
        }
        else {
            g.setColor(Color.lightGray);
            g.fillOval(x-(objectSize-1)/2, y-(objectSize-1)/2, objectSize-1, objectSize-1);
            g.setColor(Color.black);
            g.drawOval(x-(objectSize-1)/2, y-(objectSize-1)/2, objectSize-1, objectSize-1);
        }
        updateDirections();
        oldx = x;
        oldy = y;
        x += xdir;
        y += ydir;
    }

    void updateDirections() {

        // right bound check
        if(x + objectSize/2 >= screenWidth) {
            xdir = -1;
        }
                
        // left bound check
        if(x + objectSize/2 <= 0) {
            xdir = 1;
        }

        // top bound check
        if(x + objectSize/2 >= screenHeight) {
            ydir = -1;
        }

        // bottom bound check
        if(x + objectSize/2 <= 0) {
            ydir = 1;
        }
    }
}