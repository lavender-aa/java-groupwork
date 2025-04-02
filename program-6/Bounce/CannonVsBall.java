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
implements WindowListener, ComponentListener, ActionListener, 
           AdjustmentListener, Runnable, MouseListener, MouseMotionListener {
 
    // serial UID
    private static final long serialVersionUID = 10L;
 
    // constants
    private final double SECONDS_TO_MILLIS = 1000;
    private final int BALLTICKS = 4; // ball moves once every 4 time steps

    // primitives
    private int winTop = 10;  // top of frame
    private int winLeft = 10; // left side of frame
    private int ballSize = 21;
    private boolean run; // control program loop
    private boolean paused; // control running vs paused
    private int delay = 50; // millis -> 0.05s -> 20fps
    private int angle = 45; // degrees
    private int velocity;
    private int maxVelocity;
    
    // objects
    private Insets insets;
    private GameArea game;
    private Label angleLabel = new Label("Angle (45)", Label.CENTER);
    private Label VelocityLabel = new Label("Initial Velocity ()", Label.CENTER);
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






    // actions
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        

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
        Rectangle b = game.getRect();
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
            // change angle
        }
        else if(sb == velocityScrollbar) {
            // change initial projectile velocity 
        }
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
        delay = 100; // millis; 0.1 seconds
        db = ZERO;
        maxVelocity = 100;

        // init perimiter
        perimiter.setBounds(0,0,screen.x,screen.y);
        perimiter.grow(-1,-1);

        // menu and items
        menu = new MenuBar();
        Menu ctrl = new Menu("Control");
        ctrl.add(new MenuItem("Pause", new MenuShortcut(KeyEvent.VK_P)));
        ctrl.add(new MenuItem("Run", new MenuShortcut(KeyEvent.VK_R)));
        ctrl.add(new MenuItem("Restart"));
        ctrl.addSeparator();
        ctrl.add(new MenuItem("Quit"));
        Menu params = new Menu("Parameters");
        Menu size = new Menu("Size");
        size.add(new MenuItem("x-small"));
        size.add(new MenuItem("small"));
        size.add(new MenuItem("medium"));
        size.add(new MenuItem("large"));
        size.add(new MenuItem("x-large"));
        Menu speed = new Menu("Speed");
        speed.add(new MenuItem("x-slow"));
        speed.add(new MenuItem("slow"));
        speed.add(new MenuItem("medium"));
        speed.add(new MenuItem("fast"));
        speed.add(new MenuItem("x-fast"));
        params.add(size);
        params.add(speed);
        Menu env = new Menu("Environment");
        env.add(new MenuItem("Mercury"));
        env.add(new MenuItem("Venus"));
        env.add(new MenuItem("Earth"));
        env.add(new MenuItem("Moon"));
        env.add(new MenuItem("Mars"));
        env.add(new MenuItem("Jupiter"));
        env.add(new MenuItem("Saturn"));
        env.add(new MenuItem("Neptune"));
        env.add(new MenuItem("Uranus"));
        env.add(new MenuItem("Pluto"));
        menu.add(ctrl);
        menu.add(params);
        menu.add(env);


        // create speed scroll bar
        angleScrollbar = new Scrollbar(Scrollbar.HORIZONTAL);
        angleScrollbar.setMaximum(90); // all vertical
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
        game = new GameArea(ballSize, screen);
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
    private Vector<Rectangle> walls;
    private Rectangle dragBox;
    private boolean paused;
    private boolean ballCollided;

    public GameArea(int size, Point screenSize) {
        screen = screenSize;
        ballSize = size;
        ballPos = new Point(screen.x/2, screen.y/2);
        ballDir = new Point(1,1);
        walls = new Vector<Rectangle>();
        dragBox = null;
        paused = true;
        ballCollided = false;
    }

    // ballballPosition, size, pause

    public int getObjSize() {
        return ballSize;
    }

    public void setPaused(boolean val) {
        paused = val;
    }

    public Rectangle getRect() {
        return new Rectangle(ballPos.x - ballSize/2, ballPos.y - ballSize/2, ballSize, ballSize);
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



    public void updateSize(int size) {

        // get half of the object size, set old size,
        // get origin of object
        int half = size/2;

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

        // get new ballPosition
        if(!paused) {
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