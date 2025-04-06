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
import java.util.Vector;
 
public class CannonVsBall extends Frame
implements WindowListener, ComponentListener, ActionListener, ItemListener,
           AdjustmentListener, Runnable, MouseListener, MouseMotionListener {
 
    // serial UID
    private static final long serialVersionUID = 10L;
 
    // constants
    private final int cannonLength = 100;
    private final int cannonWidth = 20;
    private final int xsmallSize = 10;
    private final int smallSize = 50;
    private final int mediumSize = 100;
    private final int largeSize = 150;
    private final int xlaregSize = 200;
    private final int xslowSpeed = 25;
    private final int slowSpeed = 10;
    private final int mediumSpeed = 5;
    private final int fastSpeed = 2;
    private final int xfastSpeed = 1;

    // primitives
    private int winTop = 10;  // top of frame
    private int winLeft = 10; // left side of frame
    private int ballSize = 21;
    private boolean run; // control program loop
    private boolean paused; // control running vs paused
    private int delay; // millis -> 0.05s -> 20fps
    private int time = 0; // millis
    private int angle = 45; // degrees
    private int maxVelocity = 200;
    private int ballScore = 0;
    private int cannonScore = 0;
    
    // objects
    private Insets insets;
    private GameArea game;
    private Label angleLabel = new Label("Angle (45)", Label.CENTER);
    private Label VelocityLabel = new Label("Initial Velocity (10)", Label.CENTER);
    private Label placeholder = new Label("", Label.CENTER);
    private Label boundsStatus = new Label("Projectile in bounds.", Label.CENTER);
    private Label timeLabel = new Label("Time: 0s", Label.CENTER);
    private Label ballScoreLabel = new Label("Ball: 0", Label.CENTER);
    private Label cannonScoreLabel = new Label("Cannon: 0", Label.CENTER);
    private Scrollbar angleScrollbar, velocityScrollbar;
    private Thread thread;
    private Panel sheet = new Panel();
    private Panel control = new Panel();
    private Point screen;
    private Point window = new Point(650, 400);
    private Point m1 = new Point(0,0); // first mouse point
    private Point m2 = new Point(0,0); // second
    private Rectangle perimiter = new Rectangle(); // bouncing perimiter
    private Rectangle db = new Rectangle(); // mouse drag box
    private static final Rectangle ZERO = new Rectangle(0,0,0,0);
    private MenuBar menu;
    private CheckboxMenuItem sz1, sz2, sz3, sz4, sz5; // sizes
    private CheckboxMenuItem sp1, sp2, sp3, sp4, sp5; // speeds
    private CheckboxMenuItem p1, p2, p3, p4, p5, p6, p7, p8, p9, p10; // gravities
    private MenuItem start, pause, restart, quit;






    // actions
    @Override
    public void actionPerformed(ActionEvent e) {
        MenuItem item = (MenuItem)e.getSource();
        
        if(item == start) {
            paused = false;
            game.setPaused(false);
        }
        else if(item == pause) {
            paused = true;
            game.setPaused(true);
        }
        else if(item == restart) {
            ballScore = 0;
            ballScoreLabel.setText("Ball: 0");

            cannonScore = 0;
            cannonScoreLabel.setText("Cannon: 0");

            time = 0;
            timeLabel.setText("Time: 0s");
        }
        else if(item == quit) {
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
        game.resize(screen);
    }

    @Override
    public void componentShown(ComponentEvent e) {}




    // mouse events

    @Override
    public void mouseDragged(MouseEvent e) {
        db.setBounds(getDragBox(e));
        if(perimiter.intersection(db) != db) {
            db = perimiter.intersection(db);
        }
        game.setDragBox(new Rectangle(db));
    }

    Rectangle getDragBox(MouseEvent e) {
        m2.setLocation(e.getPoint());

        // create box
        int x = Math.min(m1.x, m2.x);
        int y = Math.min(m1.y, m2.y);
        int width = Math.abs(m1.x - m2.x);
        int height = Math.abs(m1.y - m2.y);

        return new Rectangle(x, y, width, height);
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        game.updateWalls(new Point(e.getPoint()));
        game.fireCannonTest(e.getPoint());
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        m1.setLocation(e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Rectangle b = game.getBallRect();
        b.grow(1, 1);

        // don't create wall if it's intersecting with the ball 
        if(!db.intersects(b) && db != ZERO) {
            game.addWall(new Rectangle(db));
        }
        game.nullifyDragBox();
        db = ZERO;
    }
   




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
        angleScrollbar.removeAdjustmentListener(this);
        velocityScrollbar.removeAdjustmentListener(this);
        this.removeComponentListener(this);
        this.removeWindowListener(this);
        game.removeMouseMotionListener(this);
        game.removeMouseListener(this);

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
        if(sb == angleScrollbar) {
            game.setCannonAngle(sb.getValue());
        }
        else if(sb == velocityScrollbar) {
            game.setVelocity(sb.getValue());
        }
    }



    @Override
    public void itemStateChanged(ItemEvent e) {
        CheckboxMenuItem box = (CheckboxMenuItem)e.getSource();

        if(box == sz1 || box == sz2 || box == sz3 || box == sz4 || box == sz5) {
            sz1.setState(false);
            sz2.setState(false);
            sz3.setState(false);
            sz4.setState(false);
            sz5.setState(false);
            box.setState(true);
            setSize();
        }
        if(box == sp1 || box == sp2 || box == sp3 || box == sp4 || box == sp5) {
            sp1.setState(false);
            sp2.setState(false);
            sp3.setState(false);
            sp4.setState(false);
            sp5.setState(false);
            box.setState(true);
            setSpeed();
        }
        if(box == p1 || box == p2 || box == p3 || box == p4 || box == p5 || 
           box == p6 || box == p7 || box == p8 || box == p9 || box == p10) {
            p1.setState(false);
            p2.setState(false);
            p3.setState(false);
            p4.setState(false);
            p5.setState(false);
            p6.setState(false);
            p7.setState(false);
            p8.setState(false);
            p9.setState(false);
            p10.setState(false);
            box.setState(true);
            setPlanet();
        }
    }

    void setSize() {
        if(sz1.getState()) { ballSize = xsmallSize; }
        if(sz2.getState()) { ballSize = smallSize; }
        if(sz3.getState()) { ballSize = mediumSize; }
        if(sz4.getState()) { ballSize = largeSize; }
        if(sz5.getState()) { ballSize = xlaregSize; }
        game.updateBallSize(ballSize);
    }

    void setSpeed() {
        if(sp1.getState()) { delay = xslowSpeed; }
        if(sp2.getState()) { delay = slowSpeed; }
        if(sp3.getState()) { delay = mediumSpeed; }
        if(sp4.getState()) { delay = fastSpeed; }
        if(sp5.getState()) { delay = xfastSpeed; }
    }

    void setPlanet() {
        if(p1.getState()) { game.setAccel(-4); }
        if(p2.getState()) { game.setAccel(-9); }
        if(p3.getState()) { game.setAccel(-10); }
        if(p4.getState()) { game.setAccel(-2); }
        if(p5.getState()) { game.setAccel(-4); }
        if(p6.getState()) { game.setAccel(-25); }
        if(p7.getState()) { game.setAccel(-11); }
        if(p8.getState()) { game.setAccel(-9); }
        if(p9.getState()) { game.setAccel(-11); }
        if(p10.getState()) { game.setAccel(-1); }
    }




    // constructor
    public CannonVsBall() {
        setLayout(new BorderLayout());
        setVisible(true);
        calculateScreenSizes();
        initComponents();
        startThread();
    }

    void calculateScreenSizes() {
        insets = getInsets();

        // set screen width, height (has borders on left and right)
        screen = new Point();
        screen.x = window.x - insets.left - insets.right;
        screen.y = window.y - insets.top - insets.bottom - control.getHeight();
        
        // set frame size
        setSize(window.x, window.y);

        // set the perimiter size
        perimiter = new Rectangle(0,0,screen.x, screen.y);

        // set the background color
       setBackground(Color.lightGray);
    }

    void initComponents() {

        // initialize program variables
        paused = true;
        run = true;
        delay = mediumSpeed;
        db = ZERO;
        maxVelocity = 100;

        // init perimiter
        perimiter.setBounds(0,0,screen.x,screen.y);
        perimiter.grow(-1,-1);

        // menu and items
        menu = new MenuBar();
        Menu ctrl = new Menu("Control");
        ctrl.add(pause = new MenuItem("Pause", new MenuShortcut(KeyEvent.VK_P)));
        ctrl.add(start = new MenuItem("Run", new MenuShortcut(KeyEvent.VK_R)));
        ctrl.add(restart = new MenuItem("Restart"));
        ctrl.addSeparator();
        ctrl.add(quit = new MenuItem("Quit"));
        Menu params = new Menu("Parameters");
        Menu size = new Menu("Size");
        size.add(sz1 = new CheckboxMenuItem("x-small"));
        size.add(sz2 = new CheckboxMenuItem("small"));
        size.add(sz3 = new CheckboxMenuItem("medium"));
        sz3.setState(true);
        size.add(sz4 = new CheckboxMenuItem("large"));
        size.add(sz5 = new CheckboxMenuItem("x-large"));
        Menu speed = new Menu("Speed");
        speed.add(sp1 = new CheckboxMenuItem("x-slow"));
        speed.add(sp2 = new CheckboxMenuItem("slow"));
        speed.add(sp3 = new CheckboxMenuItem("medium"));
        sp3.setState(true);
        speed.add(sp4 = new CheckboxMenuItem("fast"));
        speed.add(sp5 = new CheckboxMenuItem("x-fast"));
        params.add(size);
        params.add(speed);
        Menu env = new Menu("Environment");
        env.add(p1 = new CheckboxMenuItem("Mercury"));
        env.add(p2 = new CheckboxMenuItem("Venus"));
        env.add(p3 = new CheckboxMenuItem("Earth"));
        p3.setState(true);
        env.add(p4 = new CheckboxMenuItem("Moon"));
        env.add(p5 = new CheckboxMenuItem("Mars"));
        env.add(p6 = new CheckboxMenuItem("Jupiter"));
        env.add(p7 = new CheckboxMenuItem("Saturn"));
        env.add(p8 = new CheckboxMenuItem("Uranus"));
        env.add(p9 = new CheckboxMenuItem("Neptune"));
        env.add(p10 = new CheckboxMenuItem("Pluto"));
        menu.add(ctrl);
        menu.add(params);
        menu.add(env);


        // create speed scroll bar
        angleScrollbar = new Scrollbar(Scrollbar.HORIZONTAL);
        angleScrollbar.setMaximum(110); // all vertical
        angleScrollbar.setMinimum(0); // all horizontal
        angleScrollbar.setUnitIncrement(10);
        angleScrollbar.setBlockIncrement(45);
        angleScrollbar.setValue(angle);
        angleScrollbar.setVisibleAmount(20);
        angleScrollbar.setBackground(Color.gray);

        // create size scroll bar
        velocityScrollbar = new Scrollbar(Scrollbar.HORIZONTAL);
        velocityScrollbar.setMaximum(maxVelocity);
        velocityScrollbar.setMinimum(10);
        velocityScrollbar.setUnitIncrement(10);
        velocityScrollbar.setBlockIncrement(50);
        velocityScrollbar.setValue(ballSize);
        velocityScrollbar.setVisibleAmount(25);
        velocityScrollbar.setBackground(Color.gray);

        // create ball
        game = new GameArea(ballSize, screen, cannonLength, cannonWidth);
        game.setBackground(Color.white);

        // init sheet, control panels
        sheet.setLayout(new BorderLayout(0,0));
        sheet.add("Center", game);
        sheet.setVisible(true);

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        control.setLayout(gbl);
        control.setVisible(true);

        // weights
        double colWeight[] = {1, 5, 1, 2, 2, 2, 1, 5, 1};
        gbl.columnWeights = colWeight;

        // add buttons, scrollbars, text to control panel
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(angleScrollbar, c);
        control.add(angleScrollbar);

        c.gridy = 1;
        gbl.setConstraints(angleLabel, c);
        control.add(angleLabel);

        c.gridx = 3;
        c.gridy = 0;
        c.gridwidth = 3;
        gbl.setConstraints(boundsStatus, c);
        control.add(boundsStatus);

        c.gridy = 1;
        c.gridwidth = 1;
        gbl.setConstraints(timeLabel, c);
        control.add(timeLabel);

        c.gridx = 4;
        gbl.setConstraints(ballScoreLabel, c);
        control.add(ballScoreLabel);

        c.gridx = 5;
        gbl.setConstraints(cannonScoreLabel, c);
        control.add(cannonScoreLabel);

        c.gridx = 7;
        c.gridy = 0;
        gbl.setConstraints(velocityScrollbar, c);
        control.add(velocityScrollbar);

        c.gridy = 1;
        gbl.setConstraints(VelocityLabel, c);
        control.add(VelocityLabel);

        c.gridx = 8;
        c.gridy = 0;
        gbl.setConstraints(placeholder, c);
        control.add(placeholder);

        // add sheets to frame
        add("Center", sheet);
        add("South", control);
        setMenuBar(menu);

        // init mouse points
        m1.setLocation(0, 0);
        m2.setLocation(0, 0);

        // add listeners
        angleScrollbar.addAdjustmentListener(this);
        velocityScrollbar.addAdjustmentListener(this);
        this.addComponentListener(this);
        this.addWindowListener(this);
        game.addMouseListener(this);
        game.addMouseMotionListener(this);

        // menu listeners
        sz1.addItemListener(this);
        sz2.addItemListener(this);
        sz3.addItemListener(this);
        sz4.addItemListener(this);
        sz5.addItemListener(this);
        sp1.addItemListener(this);
        sp2.addItemListener(this);
        sp3.addItemListener(this);
        sp4.addItemListener(this);
        sp5.addItemListener(this);
        p1.addItemListener(this);
        p2.addItemListener(this);
        p3.addItemListener(this);
        p4.addItemListener(this);
        p5.addItemListener(this);
        p6.addItemListener(this);
        p7.addItemListener(this);
        p8.addItemListener(this);
        p9.addItemListener(this);
        p10.addItemListener(this);
        start.addActionListener(this);
        pause.addActionListener(this);
        restart.addActionListener(this);
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
                try {
                    Thread.sleep(delay);
                    time += delay;
                    game.setTime(time);
                    timeLabel.setText("Time: " + (time / 1000) + "s");
                    ballScoreLabel.setText("Ball: " + ballScore);
                    cannonScoreLabel.setText("Cannon: " + cannonScore);
                } catch (InterruptedException e) {}
                game.repaint();
            }
        }
    }
    

    

    // main function
    public static void main(String[] args) {
        new CannonVsBall();
    }
}
 
 
// class where all the drawing happens
// contains the ball, projectile, cannon, walls
class GameArea extends Canvas {

    // data
    private static final long serialVersionUID = 11L;
    private Image buffer;
    private Graphics nextFrame;
    private Point screen;
    private int ballSize;
    private Point ballPos;
    private Point ballDir;
    private int projSize;
    private Point cannonBase;
    private int cannonLength;
    private int cannonWidth;
    private int cannonAngle;
    private int initAngle;
    private int initVelocity;
    private Point initPos;
    private int cannonVelocity;
    private boolean launchProj;
    private int projIteration;
    private Vector<Rectangle> walls;
    private Rectangle dragBox;
    private boolean paused;
    private boolean ballCollided;
    private boolean projMoving;
    private int time;
    private int timeBallShot;
    private double acceleration;
    private double xf;
    private double yf;

    public GameArea(int size, Point screenSize, int cannonL, int cannonW) {
        screen = screenSize;
        ballSize = size;
        ballPos = new Point(screen.x/2, screen.y/2);
        ballDir = new Point(1,1);
        walls = new Vector<Rectangle>();
        dragBox = null;
        paused = true;
        ballCollided = false;
        cannonBase = new Point(screen.x, screen.y);
        cannonLength = cannonL;
        cannonWidth = cannonW;
        cannonAngle = 45;
        launchProj = false;
        projSize = 15;
        projIteration = 0;
        timeBallShot = 0;
        time = 0;
        initVelocity = 10;
        cannonVelocity = 10;
        acceleration = -10.0;
        projMoving = false;
        xf = -10;
        yf = -10;
    }

    public void setPaused(boolean val) {
        paused = val;
    }

    public Rectangle getBallRect() {
        return new Rectangle(ballPos.x - ballSize/2, ballPos.y - ballSize/2, ballSize, ballSize);
    }

    public void setAccel(int acc) {
        acceleration = acc;
    }

    public void setVelocity(int vel) {
        cannonVelocity = vel;
    }


    // cannon related

    public void setTime(int newtime) {
        time = newtime;
    }

    public void setCannonAngle(int degrees) {
        cannonAngle = degrees;
        if(paused) repaint();
    }

    public void launchProjectile() {
        launchProj = true;
        projMoving = true;
        timeBallShot = time;
        initVelocity = cannonVelocity;
        initAngle = cannonAngle;
        double cos = Math.cos(Math.toRadians(initAngle));
        double sin = Math.sin(Math.toRadians(initAngle));
        initPos = new Point(cannonBase.x + (int)(-1*cannonLength*cos),cannonBase.y + (int)(-1*cannonLength*sin));
        repaint();
    }

    public void fireCannonTest(Point mouse) {
        Rectangle base = new Rectangle(cannonBase.x - 30, cannonBase.y - 30, 60, 60);
        if(base.contains(mouse) && !projMoving) {
            launchProjectile();
        };
    }



    // wall related

    public void updateWalls(Point p) {
        int i = 0;
        while(i < walls.size()) {
            if(walls.elementAt(i).contains(p)) {
                walls.removeElementAt(i);
            }
            i++;
        }
        repaint();
    }

    public void setDragBox(Rectangle db) {
        dragBox = db;
        if(paused) repaint();
    }

    public void nullifyDragBox() {
        dragBox = null;
        repaint();
    }

    public void addWall(Rectangle r) {
        boolean add = true;

        // only add if not dominated
        int i = 0;
        while(i < walls.size()) {
            if(walls.elementAt(i).contains(r)) {
                add = false;
            }
            i++;
        }

        if(add) walls.add(r);

        // remove any dominated walls
        i = 0;
        while(i < walls.size()) {
            Rectangle wall = walls.elementAt(i);
            if(r.contains(wall) && !wall.equals(r)) {
                walls.removeElementAt(i);
            }
            i++;
        }
        // debug
        i++;
    }

    void updateWallDirs() {
        Rectangle ball = new Rectangle(ballPos.x - ballSize/2, ballPos.y - ballSize/2, ballSize, ballSize);
        ball.grow(1,1);

        int i = 0;
        while(i < walls.size() && !ballCollided) {
            Rectangle r = walls.elementAt(i);
            if(r.intersects(ball)) {
                ballCollided = true;

                // update ball direction: test each edge of the rect
                Rectangle top = new Rectangle(r.x + 1, r.y, r.width - 2, 1);
                Rectangle bottom = new Rectangle(r.x + 1, r.y + r.height, r.width - 2, 1);
                Rectangle left = new Rectangle(r.x, r.y + 1, 1, r.height - 2);
                Rectangle right = new Rectangle(r.x + r.width, r.y + 1, 1, r.height - 2);

                if(ball.intersects(top)) {
                    ballDir.y = -1;
                }
                else if(ball.intersects(bottom)) {
                    ballDir.y = 1;  
                }
                else if(ball.intersects(left)) {
                    ballDir.x = -1; 
                }
                else if(ball.intersects(right)) {
                    ballDir.x = 1;
                }
            }
            else {
                i++;
            }
        }
    }


    public void updateBallSize(int size) {

        // get half of the object size, set old size,
        // get origin of object
        int half = size/2;

        // limit object to maximum size
        // limit object size based on collisions with edges
        if(ballPos.x + half >= screen.x) {
            ballSize = (screen.x - ballPos.x) * 2;
        }
        else if(ballPos.x - half <= 0) {
            ballSize = ballPos.x * 2;
        }
        else if(ballPos.y + half >= screen.y) {
            ballSize = (screen.y - ballPos.y) * 2;
        }
        else if(ballPos.y - half <= 0) {
            ballSize = ballPos.y * 2;
        }
        else { // no collisions, good
            ballSize = size;
        }

    }




    public void resize(Point newScreen) {
        screen = newScreen;
        if(ballPos.x + ballSize >= screen.x) {
            ballPos.x = screen.x - ballSize;
        }
        if(ballPos.y + ballSize >= screen.y) {
            ballPos.y = screen.y - ballSize;
        }

        cannonBase = new Point(screen.x - 45, screen.y - 45);
    }

    @Override
    public void repaint() {
        paint(getGraphics());
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
        nextFrame.setColor(Color.black);

        // draw walls
        int i = 0;
        while(i < walls.size()) {
            Rectangle temp = walls.elementAt(i);
            nextFrame.fillRect(temp.x, temp.y, temp.width, temp.height);
            i++;
        }

        // draw drag box
        if(dragBox != null) {
            nextFrame.drawRect(dragBox.x, dragBox.y, dragBox.width, dragBox.height);
        }

        // draw cannon
        double angleRad = Math.toRadians(cannonAngle);
        double cos = Math.cos(angleRad);
        double sin = Math.sin(angleRad);
        Point offsetL = new Point(
                                    (int)(-1 * cannonWidth * sin)/2,
                                    (int)(cannonWidth * cos)/2
                                 );
        Point offsetU = new Point(-1 * offsetL.x, -1 * offsetL.y);
        Point c = new Point(
                                cannonBase.x + (int)(-1*cannonLength*cos),
                                cannonBase.y + (int)(-1*cannonLength*sin)
                           );
        Point c1 = new Point(c.x + offsetL.x, c.y + offsetL.y);
        Point c2 = new Point(c.x + offsetU.x, c.y + offsetU.y);
        Point a1 = new Point(cannonBase.x + offsetL.x, cannonBase.y + offsetL.y);
        Point a2 = new Point(cannonBase.x + offsetU.x, cannonBase.y + offsetU.y);

        Polygon cannon = new Polygon();
        cannon.addPoint(c1.x, c1.y);
        cannon.addPoint(c2.x, c2.y);
        cannon.addPoint(a2.x, a2.y);
        cannon.addPoint(a1.x, a1.y);
        nextFrame.setColor(Color.BLACK);
        nextFrame.fillPolygon(cannon);
        
        // circle over base of cannon
        nextFrame.setColor(Color.MAGENTA);
        nextFrame.fillOval(cannonBase.x - 30, cannonBase.y - 30, 60, 60);

        // get new ball and projectile positions
        if(!paused) {
            if(projIteration == 0) {

                // update ball directions
                if(ballPos.x + ballSize/2 >= screen.x) {
                    ballDir.x = -1;
                }
                if(ballPos.x - ballSize/2 <= 0) {
                    ballDir.x = 1;
                }
                if(ballPos.y - ballSize/2 <= 0) {
                    ballDir.y = 1;
                }
                if(ballPos.y + ballSize/2 >= screen.y) {
                    ballDir.y = -1;
                }
                updateWallDirs();
                ballCollided = false;
                ballPos.x += ballDir.x;
                ballPos.y += ballDir.y;
            }

            if(launchProj) {
                // get time step
                double dt = (time - timeBallShot)/100.0;

                // cannon already drawn; update angleRad for projectile use
                // (uses the angle stored when the cannon was pressed instead of current angle)
                double projAngleRad = Math.toRadians(initAngle);

                // x direction
                double x0 = initPos.x;
                double v_x0 = -1 * initVelocity * Math.cos(projAngleRad);
                xf = x0 + (v_x0 * dt);

                // y direction
                double y0 = initPos.y;
                double v_y0 = -1 * initVelocity * Math.sin(projAngleRad);
                yf = y0 + (v_y0 * dt) + (-0.5 * acceleration * Math.pow(dt, 2));

                if(yf > screen.y) {
                    launchProj = false;
                    projMoving = false;
                }
            }
            projIteration = (projIteration + 1) % 3; // cycles of 0-2
        }

        // offset location to make x/y the origin
        int xProjPos = (int)xf - (projSize-1)/2;
        int yProjPos = (int)yf - (projSize-1)/2;

        // draw projectile to graphics
        nextFrame.setColor(Color.blue);
        nextFrame.fillOval(xProjPos, yProjPos, projSize, projSize);
        nextFrame.setColor(Color.black);
        nextFrame.drawOval(xProjPos, yProjPos, projSize, projSize);

        // offset location to make x/y the origin
        int xballPos = ballPos.x - (ballSize-1)/2;
        int yballPos = ballPos.y - (ballSize-1)/2;
        
        // draw the circle to the graphics
        nextFrame.setColor(Color.lightGray);
        nextFrame.fillOval(xballPos, yballPos, ballSize, ballSize);
        nextFrame.setColor(Color.black);
        nextFrame.drawOval(xballPos, yballPos, ballSize-1, ballSize-1);

        current.drawImage(buffer, 0, 0, null);
        Toolkit.getDefaultToolkit().sync(); // to remove animation stutters on linux
    }
}