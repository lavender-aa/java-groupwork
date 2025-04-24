import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import EDU.emporia.mathbeans.*;
import EDU.emporia.mathtools.*;
import java.util.*;


/*
 * todo list:
 * - [] TODO: make right wall more obvious
 * - [] TODO: find, fix calculation error 
 * - [] TODO: find, fix operational problems
 * 
 * 
 */

public class SpatterApplication extends JFrame implements WindowListener, ActionListener {
    final double gravity=4;
    final double wallDistance=6;
    // private boolean isStandalone = false; // unused
    double t=0; // time for proj. motion
    double x1=0, y1=4; // left handle coords
    double oldx1=x1, oldy1=y1; // left handle old coords
    double x2=1, y2=5; // right handle coords
    double oldx2=x2, oldy2=y2; // right handle old coords
    double spatterWidth=0, spatterLength=0;
    boolean dragging1=false; // user is dragging the left handle
    boolean dragging2=false; // user is dragging the right handle
    // boolean move=false; // unused
    javax.swing.Timer animationTimer; // used for timing projectinge (in place of thread)
    JPanel jPanel1 = new JPanel();
    MathGrapher graph = new MathGrapher(); // graph to show projectile motion
    MathGrapher dropShapeGraph = new MathGrapher(); // graph to show splatter shape
    SymbolicParametricCurve bloodPath = new SymbolicParametricCurve(); // the curve that the projectile follows
    SymbolicParametricCurve directionVector = new SymbolicParametricCurve(); // moved by user to change direction
    SymbolicParametricCurve wall = new SymbolicParametricCurve(); // curve that represents the wall
    JLabel jLabel1 = new JLabel(); // title (left side of program)
    JButton trackButton = new JButton();
    JLabel floorOrWallLabel = new JLabel(); // label explaninig if splatter window is on wall or floor
    Ellipse spatterEllipse = new Ellipse(); // elipse inside splatter window
    MathTextField widthMathTextField = new MathTextField(); // width of splatter
    MathTextField lengthMathTextField = new MathTextField(); // height of splatter
    JLabel widthLabel = new JLabel();
    JLabel lengthLabel = new JLabel();
    MathTextField angleMathTextField = new MathTextField(); // angle of impact
    JLabel jLabel2 = new JLabel(); // angle of impact label
    JButton resetButton = new JButton();
    JLabel initHeight = new JLabel(); // initial projectile height (y1)
    JLabel initVelocity = new JLabel(); // initial projectile velocity (sqrt((x2-x1)^2 + (y2-y1)^2))
    JLabel initAngle = new JLabel(); // initial projectile angle
    
    // Get a parameter value
    private String getProperty(Properties property, String key, String def) {
        String temp;
        try {
            temp = property.getProperty(key);
            if(temp.equals(""))
                temp = def;
        }
        catch (NullPointerException e) {
            temp = def;
        }
        return temp;
    }

    public static void main(String[] args) {
        try {
            new SpatterApplication(); 
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

 
    // Component initialization
    public SpatterApplication() throws Exception {
        animationTimer = new javax.swing.Timer(1, this);
        this.setSize(new Dimension(660,440));
        jPanel1.setLayout(null);
        graph.setTraceEnabled(false);
        graph.setF(bloodPath);
        graph.setG(directionVector);
        graph.setGridLines(MathGrapher.GRIDOFF);
        graph.setToolTipText("Drag left hand point to adjust height, right hand point to adjust direction and velocity");
        graph.setXMax(6.1);
        graph.setXMin(0.0);
        graph.setYMax(6.0);
        graph.setYMin(0.0);
        graph.setBounds(new Rectangle(140, 5, 364, 390));
        graph.addMouseMotionListener(new SpatterApplication_graph_mouseMotionAdapter(this));
        graph.addMouseListener(new SpatterApplication_graph_mouseAdapter(this));
        dropShapeGraph.setTraceEnabled(false);
        dropShapeGraph.setAxesColor(Color.lightGray);
        dropShapeGraph.setGridColor(Color.lightGray);
        dropShapeGraph.setTitleEnabled(false);
        dropShapeGraph.setXLabel("");
        dropShapeGraph.setXMax(5.0);
        dropShapeGraph.setXMin(-5.0);
        dropShapeGraph.setYLabel("");
        dropShapeGraph.setYMax(5.0);
        dropShapeGraph.setYMin(-5.0);
        dropShapeGraph.setBounds(new Rectangle(507, 7, 131, 124));
        bloodPath.setYFormula("1");
        directionVector.setXFormula("0");
        bloodPath.setTMax(20.0);
        bloodPath.setTMin(0.0);
        directionVector.setTMax(1.0);
        directionVector.setTMin(0.0);
        wall.setXFormula(""+wallDistance);
        wall.setYFormula("t");
        wall.setTMin(0.0);
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 16));
        jLabel1.setText("Blood Spatter");
        jLabel1.setBounds(new Rectangle(7, 17, 133, 38));
        trackButton.setBounds(new Rectangle(23, 81, 101, 39));
        trackButton.setText("Trace path");
        trackButton.addActionListener(new SpatterApplication_trackButton_actionAdapter(this));
        floorOrWallLabel.setHorizontalAlignment(SwingConstants.CENTER);
        floorOrWallLabel.setText("Press Trace path ");
        floorOrWallLabel.setBounds(new Rectangle(517, 135, 114, 28));
        widthMathTextField.setMaxNumberOfCharacters(8);
        widthMathTextField.setEditable(false);
        widthMathTextField.setFont(new java.awt.Font("Dialog", 0, 14));
        widthMathTextField.setHorizontalAlignment(SwingConstants.CENTER);
        widthMathTextField.setMargin(new Insets(1, 1, 1, 1));
        widthMathTextField.setRequestFocusEnabled(true);
        widthMathTextField.setText("");
        widthMathTextField.setBounds(new Rectangle(520, 207, 110, 30));
        lengthMathTextField.setBounds(new Rectangle(521, 271, 110, 30));
        lengthMathTextField.setMaxNumberOfCharacters(8);
        lengthMathTextField.setEditable(false);
        lengthMathTextField.setFont(new java.awt.Font("Dialog", 0, 14));
        lengthMathTextField.setHorizontalAlignment(SwingConstants.CENTER);
        lengthMathTextField.setText("");
        widthLabel.setText("width (in mm):");
        widthLabel.setBounds(new Rectangle(521, 184, 111, 25));
        lengthLabel.setBounds(new Rectangle(520, 248, 112, 25));
        lengthLabel.setText("height (in mm):");
        angleMathTextField.setBounds(new Rectangle(521, 355, 110, 30));
        angleMathTextField.setText("");
        angleMathTextField.setRequestFocusEnabled(true);
        angleMathTextField.setMargin(new Insets(1, 1, 1, 1));
        angleMathTextField.setHorizontalAlignment(SwingConstants.CENTER);
        angleMathTextField.setFont(new java.awt.Font("Dialog", 0, 14));
        angleMathTextField.setEditable(false);
        angleMathTextField.setMaxNumberOfCharacters(10);
        jLabel2.setText("Angle of impact:");
        jLabel2.setBounds(new Rectangle(520, 328, 111, 24));
        resetButton.addActionListener(new SpatterApplication_resetButton_actionAdapter(this));
        resetButton.setText("reset");
        resetButton.addActionListener(new SpatterApplication_resetButton_actionAdapter(this));
        resetButton.setBounds(new Rectangle(23, 142, 101, 39));
        initHeight.setBounds(new Rectangle(10, 203, 101, 39));
        initHeight.setText("init. height: " + y1);
        initVelocity.setBounds(new Rectangle(10, 264, 201, 39));
        initVelocity.setText("init. velocity: " + places1(getInitVelocity()));
        initAngle.setBounds(new Rectangle(10, 325, 201, 39));
        initAngle.setText("init. angle: " + places1(-radToDeg(angle(0.02))));
        this.getContentPane().add(jPanel1, BorderLayout.CENTER);
        this.setResizable(false);
        setVisible(true); // make it visible
        validate(); // validate the layout
        addWindowListener(this);
        setTitle("Spatter Application");

        jPanel1.add(graph, null);
        jPanel1.add(jLabel1, null);
        jPanel1.add(trackButton, null);
        jPanel1.add(dropShapeGraph, null);
        jPanel1.add(floorOrWallLabel, null);
        jPanel1.add(widthLabel, null);
        jPanel1.add(lengthLabel, null);
        jPanel1.add(lengthMathTextField, null);
        jPanel1.add(widthMathTextField, null);
        jPanel1.add(angleMathTextField, null);
        jPanel1.add(jLabel2, null);
        jPanel1.add(resetButton, null);
        jPanel1.add(initHeight, null);
        jPanel1.add(initVelocity, null);
        jPanel1.add(initAngle, null);
        graph.setPointRadius(4);
        graph.updateGraph();
        dropShapeGraph.removeAll();
        repaint();
    }

    public void stop() {
        this.removeWindowListener(this);
    }

    public void windowClosing(WindowEvent e) {
        stop();
        dispose();
        System.exit(0);
    }
    public void windowClosed(WindowEvent e){}
    public void windowOpened(WindowEvent e){}
    public void windowActivated(WindowEvent e){}
    public void windowDeactivated(WindowEvent e){}
    public void windowIconified(WindowEvent e){}
    public void windowDeiconified(WindowEvent e){}

    public void actionPerformed(ActionEvent e) {
        Point2D p = bloodPath.getPoint(t);
        graph.plotPoint(p.getX(),p.getY());
        t += 0.02;
        graph.updateGraph();

        // projectile hits wall (time > time it would take for proj to hit wall)
        if(t>wallDistance/(x2-x1)) {
            animationTimer.stop();
            floorOrWallLabel.setText("Wall spatter shape");
            lengthLabel.setText("Height (in mm):");
            spatterEllipse.setXRadius((1+Math.random())/2);
            spatterEllipse.setYRadius(spatterEllipse.getXRadius()/Math.cos(angle(t)));
            dropShapeGraph.addGraph(spatterEllipse, Color.RED);
            widthMathTextField.setMathValue(places1(10*spatterEllipse.getXRadius()));
            lengthMathTextField.setMathValue(places1(10*spatterEllipse.getYRadius()));
            angleMathTextField.setMathValue(places1(90-angle(t)*180/Math.PI));
        }

        // projectile hits floor (time > time it would take for proj. to hit ground)
        if(t>(((y2-y1)+Math.sqrt((y1-y2)*(y1-y2)+4*gravity*y1)))/(2*gravity)) {
            animationTimer.stop();
            floorOrWallLabel.setText("Floor spatter shape");
            lengthLabel.setText("Length (in mm):");
            spatterEllipse.setXRadius((1+Math.random())/2);
            spatterEllipse.setYRadius(spatterEllipse.getXRadius()/Math.sin(angle(t)));
            dropShapeGraph.addGraph(spatterEllipse, Color.RED);
            widthMathTextField.setMathValue(places1(10*spatterEllipse.getXRadius()));
            lengthMathTextField.setMathValue(places1(10*spatterEllipse.getYRadius()));
            angleMathTextField.setMathValue(places1(angle(t)*180/Math.PI));
        }
    }

    // determine if the user is moving either left or right handle
    void graph_mousePressed(MouseEvent e) {
        int xMouse=e.getX();
        int yMouse=e.getY();
        int distance1Squared = (xMouse-graph.xMathToPixel(x1)) * (xMouse-graph.xMathToPixel(x1)) // x^2
                              +(yMouse-graph.yMathToPixel(y1)) * (yMouse-graph.yMathToPixel(y1)); // + y^2
        if (distance1Squared < 100) dragging1 = true;
        int distance2Squared = (xMouse-graph.xMathToPixel(x2)) * (xMouse-graph.xMathToPixel(x2)) // x^2
                              +(yMouse-graph.yMathToPixel(y2)) * (yMouse-graph.yMathToPixel(y2)); // + y^2
        if (distance2Squared < 100 && distance1Squared >=100) dragging2 = true;
    }

    void graph_mouseReleased(MouseEvent e) {
        dragging1 = false;
        dragging2 = false;
    }

    // rounds doubles to nearest tenth
    public double places1(double x) {
        x=10*x;
        x=Math.round(x);
        x=(double) x/10;
        return x;
    }

    // rounds doubles to nearest hundredth
    public double places2(double x) {
        x=100*x;
        x=Math.round(x);
        x=(double) x/100;
        return x;
    }

    // get the x value of the projectile given time t
    public double x(double t) {
        return ((x2-x1)*t);
    }

    // get the y value of the projectile given time t
    public double y(double t) {
        return (y1+(y2-y1)*t-gravity*t*t);
    }

    // returns arctan(dy/dx) (approx. angle at time t)
    public double angle(double t) {
        //return -Math.atan((y(t)-y(t-0.02))/(x(t)-x(t-0.02)));   <- 0.02 time difference
        return -Math.atan((y(t)-y(t-0.04))/(x(t)-x(t-0.04)));
    }

    public double radToDeg(double rad) {
        return (rad * 180) / Math.PI;
    }

    public double getInitVelocity() {
        // distance from left handle to right handle (pythagorean theorem)
        // sqrt( (x2-x1)^2 + (y2-y1)^2 )
        return Math.sqrt(Math.pow(x2-x1,2) + Math.pow(y2-y1,2));
    }

    public void repaint() {
        graph.removeAllPoints();
        graph.removeAll();
        graph.addPoint(x1,y1,Color.magenta);
        graph.addPoint(x2,y2,Color.magenta);

        // update initial velocity/height/angle labels
        initHeight.setText("init. height: " + places1(y1));
        initVelocity.setText("init. velocity: " + places1(getInitVelocity()));
        initAngle.setText("init. angle: " + places1(-radToDeg(angle(0.02))));

        try {
            directionVector.setXFormula(x2+"*t");
            directionVector.setYFormula(y1+"+"+"("+y2+"-"+y1+")*"+"t");
        } catch(Graphable_error e){}
        try{
            bloodPath.setXFormula("("+x2+"-"+x1+")*t");
            bloodPath.setYFormula(y1+"+("+y2+"-"+y1+")*t-"+gravity+"*t*t");
        } catch(Graphable_error e){}
        graph.addGraph(directionVector, Color.MAGENTA);
        graph.addGraph(bloodPath, Color.RED);
        graph.addGraph(wall, Color.BLUE);
        graph.updateGraph();
    }

    void graph_mouseDragged(MouseEvent e) {
        if (dragging1) {
            oldy1=y1;
            oldy2=y2;

            // keep left handle inside graph
            double newY = graph.yPixelToMath(e.getY());
            if(newY > 6) newY = 6;
            else if(newY < 0) newY = 0;
            y1 = newY;

            // keep right handle inside graph
            if(oldy2 - oldy1 + y1 > 6) y2 = 6;
            else if(oldy2 - oldy1 + y1 < 0) y2 = 0;
            else y2=oldy2-oldy1+y1;
            repaint();
        }
        if (dragging2) {
            oldx2=x2;
            oldy2=y2;

            // keep handle inside graph
            double newX = graph.xPixelToMath(e.getX());
            double newY = graph.yPixelToMath(e.getY());
            if(newX < 0) newX = 0;
            else if(newX > 6) newX = 6;
            if(newY < 0) newY = 0;
            else if(newY > 6) newY = 6;

            x2=newX;
            y2=newY;
            repaint();
        }
    }

    // start start animating the projectile
    void trackButton_actionPerformed(ActionEvent e) {
        dropShapeGraph.removeGraph(spatterEllipse);
        t=0;
        animationTimer.start();
    }

    void resetButton_actionPerformed(ActionEvent e) {
        animationTimer = null;
        animationTimer = new javax.swing.Timer(1, this);
        t=0;
        x1=0;
        y1=4;
        oldx1=x1;
        oldy1=y1;
        x2=1;
        y2=5;
        oldx2=x2;
        oldy2=y2;
        spatterWidth=0;
        spatterLength=0;

        try {
            bloodPath.setYFormula("1");
            directionVector.setXFormula("0");
            wall.setXFormula(""+wallDistance);
            wall.setYFormula("t");
        } catch (Graphable_error r){}

        floorOrWallLabel.setText("Press Trace path ");
        lengthLabel.setText("height (in mm):");
        widthMathTextField.setText("");
        lengthMathTextField.setText("");
        angleMathTextField.setText("");

        graph.setPointRadius(4);
        graph.updateGraph();
        dropShapeGraph.removeGraph(spatterEllipse);
        repaint();
    }
}


class SpatterApplication_graph_mouseAdapter extends java.awt.event.MouseAdapter {
    SpatterApplication adaptee;

    SpatterApplication_graph_mouseAdapter(SpatterApplication adaptee) {
        this.adaptee = adaptee;
    }
    public void mousePressed(MouseEvent e) {
        adaptee.graph_mousePressed(e);
    }
    public void mouseReleased(MouseEvent e) {
        adaptee.graph_mouseReleased(e);
    }
}

class SpatterApplication_graph_mouseMotionAdapter extends java.awt.event.MouseMotionAdapter {
    SpatterApplication adaptee;

    SpatterApplication_graph_mouseMotionAdapter(SpatterApplication adaptee) {
        this.adaptee = adaptee;
    }
    public void mouseDragged(MouseEvent e) {
        adaptee.graph_mouseDragged(e);
    }
}

class SpatterApplication_trackButton_actionAdapter implements java.awt.event.ActionListener {
    SpatterApplication adaptee;

    SpatterApplication_trackButton_actionAdapter(SpatterApplication adaptee) {
        this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e) {
        adaptee.trackButton_actionPerformed(e);
    }
}

class SpatterApplication_resetButton_actionAdapter implements java.awt.event.ActionListener {
    SpatterApplication adaptee;

    SpatterApplication_resetButton_actionAdapter(SpatterApplication adaptee) {
        this.adaptee = adaptee;
    }
    public void actionPerformed(ActionEvent e) {
        adaptee.resetButton_actionPerformed(e);
    }
}