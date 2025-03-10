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
import java.util.ArrayList;

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
    private AnimatedObject animatedObject;
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
        // startThread(); TODO: uncomment when startThread() written
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

        // create object
        animatedObject = new AnimatedObject(objectSize, maxObjectSize, screenWidth, screenHeight);
        animatedObject.setBackground(Color.white);

        // add scrollbars, labels, object to frame
        add(speedScrollbar);
        add(sizeScrollbar);
        add(speedLabel);
        add(sizeLabel);
        add(animatedObject);

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
        animatedObject.setBounds(insets.left, insets.top, screenWidth, screenHeight);

    }

    @Override
    public void run() {
        while (run) {
            if (!paused) {
                animatedObject.update();
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (e.getSource() == speedScrollbar) {
            scrollSpeed = speedScrollbar.getValue();
            delay = (int) ((1.0 / scrollSpeed) * SECONDS_TO_MILLIS);
        } else if (e.getSource() == sizeScrollbar) {
            objectSize = sizeScrollbar.getValue();
            animatedObject.setSize(objectSize);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start) {
            paused = !paused;
            start.setLabel(paused ? "Run" : "Pause");
        } else if (e.getSource() == shape) {
            animatedObject.setShape(!animatedObject.isCircle());
            shape.setLabel(animatedObject.isCircle() ? "Square" : "Circle");
        } else if (e.getSource() == clear) {
            animatedObject.clearTail();
        } else if (e.getSource() == tail) {
            animatedObject.setTail(!animatedObject.hasTail());
            tail.setLabel(animatedObject.hasTail() ? "No Tail" : "Tail");
        } else if (e.getSource() == quit) {
            System.exit(0);
        }
    }

    @Override
    public void componentHidden(ComponentEvent e) {}

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentResized(ComponentEvent e) {
        calculateScreenSizes();
        animatedObject.setBounds(insets.left, insets.top, screenWidth, screenHeight);
    }

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowOpened(WindowEvent e) {}

    public class animatedObject extends canvas {

        // Variables to store object properties
        private int x, y;
        private int dx, dy;
        private int size;
        private boolean isCircle;
        private boolean hasTail;
        private ArrayList<Point> tailPoints;

        private int maxSize, screenWidth, screenHeight;

        public AnimatedObject (int size, int maxSize, int screenWidth, int screenHeight) {
            this.size = size;
            this.maxSize = maxSize;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.x = screenWidth / 2; // Initial x position
            this.y = screenHeight / 2; // Initial y position
            this.dx = 2; // Initial horizontal speed
            this.dy = 2; // Initial vertical speed
            this.isCircle = false; // default shape is a square
            this.hasTail = true; // default has tail
            this.tailPoints = new ArrayList<>(); // Initialize tail points
        }

        // Update method to move the object
        public void update() {

            // Move object diagonally
            x += dx;
            y += dy;

            // Check for collisions with the boundaries
            if ((x <= 0) || (x >= screenWidth - size)) {
                dx = -dx;
            }
            if ((y <= 0) || (y >= screenHeight - size)) {
                dy = -dy;
            }

            // If tail is enabled, add current position to the tail list
            if (hasTail) {
                tailPoints.add(new Point(x, y));
            }

            // Redraw the object
            repaint();
        }

        // Paint method to draw the object
        @Override
        public void paint(Graphics g) {

            // If tail is enabled, draw previous positions
            if (hasTail) {
                g.setColor(Color.lightGray);
                for (Point p : tailPoints) {
                    g.fillOval(p.x, p.y, size, size);
                }
            }

            // Draw the current object (Circle or Square)
            if (isCircle) {
                g.setColor(Color.blue); // Set color for circle
                g.fillOval(x, y, size, size); // Draw Circle
            } else {
                g.setColor(Color.red); // Set color for square
                g.fillRect(x, y, size, size); // Draw Square
            }
        }

        // Getter and Setter methods for object properties
        public void setSize(int newSize) {
            this.size = Math.min(newSize, maxSize); // Ensure that the size does not exceed maxSize
        }

        public void setShape(boolean isCircle) {
            this.isCircle = isCircle; // Toggle between circle and square
        }

        public void setTail(boolean hasTail) {
            this.hasTail = hasTail; // Toggle tail between on and off
        }

        // Method to change the speed
        public void setSpeed(int speed) {
            this.dx = speed;
            this.dy = speed;
        }
    }
}
