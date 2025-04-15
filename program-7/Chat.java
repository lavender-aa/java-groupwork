/*
 * Program 7: Chat
 * Course: CMSC 3320 -- Technical Computing Using Java
 * Authors: Group 6
 *      - Lavender Wilson (wil81891@pennwest.edu)
 *      - Camron Mellott (mel98378@pennwest.edu)
 *      - Nicola Razumic-Rushin (raz73517@pennwest.edu)
 */

 import java.awt.*;
 import java.awt.event.*;
 import java.io.*;
 import java.net.*;
 
 public class Chat extends Frame implements WindowListener, ActionListener, Runnable, KeyListener {
 
     private static final long serialVersionUID = 10L;
 
     // GUI
     private Dimension window = new Dimension(700, 400);
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
 
     // Networking
     private ServerSocket serverSocket;
     private Socket socket;
     private BufferedReader reader;
     private PrintWriter writer;
     private Thread listenThread;
     private boolean isServer = false;
 
     public Chat() {
         super("Chat");
 
         setLayout(new BorderLayout());
         GridBagLayout gbl = new GridBagLayout();
         GridBagConstraints c = new GridBagConstraints();
 
         double colWeight[] = {1, 0.25, 5, 1, 2, 2};
         gbl.columnWeights = colWeight;
         c.anchor = GridBagConstraints.WEST;
         c.fill = GridBagConstraints.BOTH;
 
         controls = new Panel();
         controls.setLayout(gbl);
 
         // Chat Area
         chatTA = new TextArea("", 10, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);
         chatTA.setEditable(false);
         add("North", chatTA);
 
         // Chatbox
         chatboxTF = new TextField();
         c.gridy = 0;
         c.gridwidth = 5;
         gbl.setConstraints(chatboxTF, c);
         controls.add(chatboxTF);
 
         sendBTN = new Button("Send");
         c.gridx = 5;
         c.gridwidth = 1;
         gbl.setConstraints(sendBTN, c);
         controls.add(sendBTN);
 
         // Host
         hostLBL = new Label("Host:");
         c.gridx = 1;
         c.gridy = 1;
         gbl.setConstraints(hostLBL, c);
         controls.add(hostLBL);
 
         hostTF = new TextField("localhost");
         c.gridx = 2;
         gbl.setConstraints(hostTF, c);
         controls.add(hostTF);
 
         changeHostBTN = new Button("Change Host");
         c.gridx = 4;
         gbl.setConstraints(changeHostBTN, c);
         controls.add(changeHostBTN);
 
         startServerBTN = new Button("Start Server");
         c.gridx = 5;
         gbl.setConstraints(startServerBTN, c);
         controls.add(startServerBTN);
 
         // Port
         portLBL = new Label("Port:");
         c.gridx = 1;
         c.gridy = 2;
         gbl.setConstraints(portLBL, c);
         controls.add(portLBL);
 
         portTF = new TextField("44004");
         c.gridx = 2;
         gbl.setConstraints(portTF, c);
         controls.add(portTF);
 
         changePortBTN = new Button("Change Port");
         c.gridx = 4;
         gbl.setConstraints(changePortBTN, c);
         controls.add(changePortBTN);
 
         connectBTN = new Button("Connect");
         c.gridx = 5;
         gbl.setConstraints(connectBTN, c);
         controls.add(connectBTN);
 
         disconnectBTN = new Button("Disconnect");
         c.gridy = 3;
         gbl.setConstraints(disconnectBTN, c);
         controls.add(disconnectBTN);
 
         add("Center", controls);
 
         statusTA = new TextArea("Chat is running.", 3, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);
         statusTA.setEditable(false);
         add("South", statusTA);
 
         // Listeners
         chatboxTF.addActionListener(this);
         chatboxTF.addKeyListener(this);
         sendBTN.addActionListener(this);
         hostTF.addActionListener(this);
         changeHostBTN.addActionListener(this);
         startServerBTN.addActionListener(this);
         portTF.addActionListener(this);
         changePortBTN.addActionListener(this);
         connectBTN.addActionListener(this);
         disconnectBTN.addActionListener(this);
         addWindowListener(this);
 
         // Window setup
         setPreferredSize(window);
         setMinimumSize(new Dimension(300, 300));
         setSize(getPreferredSize());
         validate();
         setVisible(true);
     }
 
     public static void main(String[] args) {
         new Chat();
     }
 
     // Action Handling
     @Override
     public void actionPerformed(ActionEvent e) {
         Object src = e.getSource();
 
         if (src == sendBTN || src == chatboxTF) {
             sendMessage();
         } else if (src == startServerBTN) {
             startServer();
         } else if (src == connectBTN) {
             connectToServer();
         } else if (src == disconnectBTN) {
             closeConnection();
         } else if (src == changeHostBTN) {
             statusTA.setText("Host changed to: " + hostTF.getText());
         } else if (src == changePortBTN) {
             statusTA.setText("Port changed to: " + portTF.getText());
         }
     }
 
     private void sendMessage() {
         String msg = chatboxTF.getText().trim();
         if (!msg.isEmpty() && writer != null) {
             writer.println(msg);
             writer.flush();
             chatTA.append((isServer ? "Server: " : "Client: ") + msg + "\n");
             chatboxTF.setText("");
         }
     }
 
     private void startServer() {
         try {
             int port = Integer.parseInt(portTF.getText());
             serverSocket = new ServerSocket(port);
             statusTA.setText("Server started. Waiting for client on port " + port + "...");
             isServer = true;
             new Thread(() -> {
                 try {
                     socket = serverSocket.accept();
                     statusTA.setText("Client connected.");
                     setupStreams();
                     listen();
                 } catch (IOException ex) {
                     ex.printStackTrace();
                 }
             }).start();
         } catch (IOException ex) {
             statusTA.setText("Error starting server.");
         }
     }
 
     private void connectToServer() {
         try {
             String host = hostTF.getText();
             int port = Integer.parseInt(portTF.getText());
             statusTA.setText("Connecting to " + host + ":" + port + "...");
             socket = new Socket(host, port);
             statusTA.setText("Connected to server.");
             isServer = false;
             setupStreams();
             listen();
         } catch (IOException ex) {
             statusTA.setText("Connection failed.");
         }
     }
 
     private void setupStreams() throws IOException {
         reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         writer = new PrintWriter(socket.getOutputStream(), true);
     }
 
     private void listen() {
         listenThread = new Thread(() -> {
             String msg;
             try {
                 while ((msg = reader.readLine()) != null) {
                     chatTA.append((isServer ? "Client: " : "Server: ") + msg + "\n");
                 }
             } catch (IOException ex) {
                 statusTA.setText("Connection closed.");
             }
         });
         listenThread.start();
     }
 
     private void closeConnection() {
         try {
             if (reader != null) reader.close();
             if (writer != null) writer.close();
             if (socket != null) socket.close();
             if (serverSocket != null) serverSocket.close();
             if (listenThread != null) listenThread.interrupt();
             statusTA.setText("Disconnected.");
         } catch (IOException ex) {
             statusTA.setText("Error closing connection.");
         }
     }
 
     void stop() {
         closeConnection();
         removeWindowListener(this);
         dispose();
     }
 
     // Window Events
     @Override
     public void windowClosing(WindowEvent e) {
         stop();
     }
 
     @Override public void windowOpened(WindowEvent e) {}
     @Override public void windowClosed(WindowEvent e) {}
     @Override public void windowIconified(WindowEvent e) {}
     @Override public void windowDeiconified(WindowEvent e) {}
     @Override public void windowActivated(WindowEvent e) {}
     @Override public void windowDeactivated(WindowEvent e) {}
 
     // KeyListener for Enter in chatbox
     @Override public void keyTyped(KeyEvent e) {}
     @Override public void keyPressed(KeyEvent e) {
         if (e.getSource() == chatboxTF && e.getKeyCode() == KeyEvent.VK_ENTER) {
             sendMessage();
         }
     }
     @Override public void keyReleased(KeyEvent e) {}
 
     @Override
     public void run() {}
 }