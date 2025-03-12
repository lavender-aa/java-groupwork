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
 
public class BouncingBall extends Frame
implements WindowListener, ComponentListener, ActionListener, 
           AdjustmentListener, Runnable, MouseListener, MouseMotionListener {
 
    // serial UID
    private static final long serialVersionUID = 10L;
 
    // constants
    private final Point FRAMESIZE = new Point(640, 400);
    private final int BUTTONHEIGHT = 30;
    private final int MINOBJECTSIZE = 10;
    private final int DEFAULTOBJECTSIZE = 21;
    private final int SPEEDSCROLLVISIBLE = 50;
    private final int SIZESCROLLVISIBLE = 25;
    private final int SPEEDSCROLLMIN = 1;
    private final int SPEEDSCROLLMAX = 300;
    private final int SCROLLUNIT = 10; // unit step size
    private final int SCROLLBLOCK = 50; // block step size
    private final double SECONDS_TO_MILLIS = 1000;

    // primitives + strings
    private int winTop = 10;  // top of frame
    private int winLeft = 10; // left side of frame
    private int maxObjectSize = 500;
    private int objectSize = DEFAULTOBJECTSIZE;
    private boolean run; // control program loop
    private boolean paused; // control running vs paused
    private static boolean started; // control animation
    private int scrollSpeed;
    private int delay; // current time delay
    
    // objects
    private Insets insets;
    private Button start, pause, quit;
    private Ball ball;
    private Label speedLabel = new Label("Speed", Label.CENTER);
    private Label sizeLabel = new Label("Size", Label.CENTER);
    private Label placeholder = new Label("", Label.CENTER);
    private Scrollbar speedScrollbar, sizeScrollbar;
    private Thread thread;
    private Panel sheet = new Panel();
    private Panel control = new Panel();
    private Point screen;
    private Point window = FRAMESIZE;
    private Point m1 = new Point(0,0); // first mouse point
    private Point m2 = new Point(0,0); // second
    private Rectangle perimiter = new Rectangle(); // bouncing perimiter
    private Rectangle db = new Rectangle(); // mouse drag box






    // actions
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if(source == start) {
            start.setEnabled(false);
            pause.setEnabled(true);
            paused = false;
        }
        else if(source == pause) {
            pause.setEnabled(false);
            start.setEnabled(true);
            paused = true;
        }
        else { // source == quit
            stop();
        }
    }




    // component events
    @Override
    public void componentHidden(ComponentEvent e) {}

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentResized(ComponentEvent e) {
        window.x = getWidth();
        window.y = getHeight();
        calculateScreenSizes();
        sizeScrollbar.setMaximum(maxObjectSize);
        ball.resize(screen, maxObjectSize);
    }

    @Override
    public void componentShown(ComponentEvent e) {}




     // mouse events

     @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}
   




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
        pause.removeActionListener(this);
        quit.removeActionListener(this);
        speedScrollbar.removeAdjustmentListener(this);
        sizeScrollbar.removeAdjustmentListener(this);
        this.removeComponentListener(this);
        this.removeWindowListener(this);
        ball.removeMouseMotionListener(this);
        ball.removeMouseListener(this);

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
        Scrollbar sb = (Scrollbar) e.getSource();
        if(sb == speedScrollbar) {
            delay = (int) ((1.0/sb.getValue()) * SECONDS_TO_MILLIS);
        }
        else if(sb == sizeScrollbar) {
            paused = true;
            ball.setPaused(true);
            start.setLabel("Run");
            int newSize = sb.getValue();
            newSize = (newSize/2)*2 + 1; // force the size to be odd for center position
            ball.updateSize(newSize);
            if(ball.getObjSize() != newSize) {
                sb.setValue(ball.getObjSize());
            }
            ball.repaint();
        }
    }




    // constructor
    public BouncingBall() {
        setLayout(new BorderLayout());
        setVisible(true);
        calculateScreenSizes();
        initComponents();
        startThread();
    }

    void calculateScreenSizes() {
        insets = getInsets();

        // set screen width (has borders on left and right)
        screen = new Point();
        screen.x = window.x - insets.left - insets.right;
        screen.y = window.y - insets.top - insets.bottom - 2*BUTTONHEIGHT;
        
        // set frame size
        setSize(window.x, window.y);

        // recalculate max object size for screen
        if(screen.x >= screen.y) { // limited by height
            maxObjectSize = screen.y - screen.y/4;
        }
        else { // limited by width
            maxObjectSize = screen.x - screen.x/4;
        }

        // set the background color
       setBackground(Color.lightGray);
    }

    void initComponents() {

        // initialize program variables
        paused = true;
        started = false;
        run = true;
        scrollSpeed = 50;
        delay = (int) ((1.0/scrollSpeed) * SECONDS_TO_MILLIS);

        // init perimiter
        perimiter.setBounds(0,0,screen.x,screen.y);
        perimiter.grow(-1,-1);
        
        // create buttons
        start = new Button("Run");
        pause = new Button("Pause");
        quit = new Button("Quit");
        start.setEnabled(true);
        pause.setEnabled(false);

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

        // create ball
        ball = new Ball(objectSize, maxObjectSize, screen);
        ball.setBackground(Color.white);

        // init sheet, control panels
        sheet.setLayout(new BorderLayout(0,0));
        sheet.add("Center", ball);
        sheet.setVisible(true);

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        control.setLayout(gbl);
        control.setVisible(true);
        control.setSize(window.x, 2*BUTTONHEIGHT);

        // weights, width/height
        double colWeight[] = {1, 5, 1, 2, 2, 2, 1, 5, 1};
        gbl.columnWeights = colWeight;

        // add buttons, scrollbars, text to control panel
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(speedScrollbar, c);
        control.add(speedScrollbar);

        c.gridy = 1;
        gbl.setConstraints(speedLabel, c);
        control.add(speedLabel);

        c.gridx = 3;
        c.gridy = 0;
        gbl.setConstraints(start, c);
        control.add(start);

        c.gridx = 4;
        gbl.setConstraints(pause, c);
        control.add(pause);

        c.gridx = 5;
        gbl.setConstraints(quit, c);
        control.add(quit);

        c.gridx = 7;
        gbl.setConstraints(sizeScrollbar, c);
        control.add(sizeScrollbar);

        c.gridy = 1;
        gbl.setConstraints(sizeLabel, c);
        control.add(sizeLabel);

        c.gridx = 8;
        c.gridy = 0;
        gbl.setConstraints(placeholder, c);
        control.add(placeholder);

        // add sheets to frame
        add("Center", sheet);
        add("South", control);

        // init mouse points
        m1.setLocation(0, 0);
        m2.setLocation(0, 0);

        // add listeners
        speedScrollbar.addAdjustmentListener(this);
        sizeScrollbar.addAdjustmentListener(this);
        this.addComponentListener(this);
        this.addWindowListener(this);
        ball.addMouseMotionListener(this);
        ball.addMouseListener(this);
        start.addActionListener(this);
        pause.addActionListener(this);
        quit.addActionListener(this);

        // set sizes, bounds, validate layout
        setPreferredSize(new Dimension(window.x, window.y));
        setMinimumSize(getPreferredSize());
        setBounds(winLeft, winTop, window.x, window.y);
        validate();
    }




    // thread related

    void startThread() {
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
                ball.repaint();
            }
        }
        started = false;
    }
    

    

    // main function
    public static void main(String[] args) {
        new BouncingBall();
    }
}
 
 
// ball class
class Ball extends Canvas {

    // data
    private static final long serialVersionUID = 11L;
    private Image buffer;
    private Graphics nextFrame;
    private Point screen;
    private int objectSize;
    private int maxObjectSize;
    private Point pos;
    private Point dir;
    private boolean paused;

    public Ball(int size, int max, Point screenSize) {
        screen = screenSize;
        objectSize = size;
        maxObjectSize = max;
        pos = new Point(screen.x/2, screen.y/2);
        dir = new Point(1,1);
        paused = true;
    }
    public void setPos(Point newpos) {
       pos = newpos;
    }

    public int getObjSize() {
        return objectSize;
    }

    public void setPaused(boolean val) {
        paused = val;
    }

    public void updateSize(int size) {

        // get half of the object size, set old size,
        // get origin of object
        int half = size/2;

        // limit object to maximum size
        if(size >= maxObjectSize) {
            objectSize = maxObjectSize;
        }
        else { 
            // limit object size based on collisions with edges
            if(pos.x + half >= screen.x) {
                objectSize = (screen.x - pos.x) * 2;
            }
            else if(pos.x - half <= 0) {
                objectSize = pos.x * 2;
            }
            else if(pos.y + half >= screen.y) {
                objectSize = (screen.y - pos.y) * 2;
            }
            else if(pos.y - half <= 0) {
                objectSize = pos.y * 2;
            }
            else { // no collisions, good
                objectSize = size;
            }
        }
        repaint();
    }

    public void resize(Point newScreen, int max) {
        screen = newScreen;
        if(pos.x + objectSize >= screen.x) {
            pos.x = screen.x - objectSize;
        }
        if(pos.y + objectSize >= screen.y) {
            pos.y = screen.y - objectSize;
        }
        maxObjectSize = max;
        if(objectSize > maxObjectSize) {
            objectSize = maxObjectSize;
        }
    }

    @Override
    public void paint(Graphics current) {
        buffer = createImage(screen.x, screen.y);
        if(nextFrame != null) {
            nextFrame.dispose();
        }
        nextFrame = buffer.getGraphics();
        nextFrame.setColor(Color.red);
        nextFrame.drawRect(0, 0, screen.x-1, screen.y-1);
        update(nextFrame);
        current.drawImage(buffer, 0, 0, null);
        Toolkit.getDefaultToolkit().sync(); // to remove animation stutters on linux
    }

    @Override
    public void update(Graphics g) {

        // get new position
        if(!paused) {
            updateDirections();
            pos.x += dir.x;
            pos.y += dir.y;
        }

        // offset location to make x/y the origin
        int xpos = pos.x - (objectSize-1)/2;
        int ypos = pos.y - (objectSize-1)/2;
        
        // draw the circle to the graphics
        g.setColor(Color.lightGray);
        g.fillOval(xpos, ypos, objectSize, objectSize);
        g.setColor(Color.black);
        g.drawOval(xpos, ypos, objectSize-1, objectSize-1);
    }

    void updateDirections() {

        // right bound check
        if(pos.x + objectSize/2 >= screen.x) {
            dir.x = -1;
        }
                
        // left bound check
        if(pos.x - objectSize/2 <= 0) {
            dir.x = 1;
        }

        // top bound check
        if(pos.y - objectSize/2 <= 0) {
            dir.y = 1;
        }

        // bottom bound check
        if(pos.y + objectSize/2 >= screen.y) {
            dir.y = -1;
        }
    }
}
 