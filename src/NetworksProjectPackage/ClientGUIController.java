/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import javax.imageio.spi.ImageReaderSpi;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;

/**
 * The ClientGUIController is in charge of manipulating GUI components, including updating
 * GUI and process events from GUI
 * 
 * @author Drew Jeffrey
 */
public class ClientGUIController extends javax.swing.JFrame implements MouseMotionListener {
    
    //GUI Components on the game screen 
    public ArrayList<JLabel> objectList;
    private MainController controller;
    private JButton hostGame;
    private JButton joinGame;
    private JButton exitGame;
    private JLabel mmTitle;
    private Container movingObjects;
    private HashMap<InetAddress, JLabel> playerLabels = null;
    private HashMap<InetAddress, JLabel> ballLabels = null;
    
    //The welcome screen
    public mainMenu main_menu;
    
    //controllers
    public MainController mainController = null;
    private SoundController soundController = null;
    
    //a map keeping track of who just died
    private HashMap<InetAddress, Integer> playerJustDied = null;
    
    
    // boundaries for different teams
    public static int minXForTEAM1 = 0;
    public static int maxXForTEAM1 = 0;
    public static int minYForTEAM1 = 0;
    public static int maxYForTEAM1 = 0;
    
    public static int minXForTEAM2 = 0;
    public static int maxXForTEAM2 = 0;
    public static int minYForTEAM2 = 0;
    public static int maxYForTEAM2 = 0;
    
    public static int minYArena = 0;
    public static int minXArena = 0;
    public static int maxXArena = 0;
    public static int maxYArena = 0;
    
    
    
    
    /**
     * Creates new form ClientGUIController
     */
    //GUI controller's frame is the main area on which games are played.
    //GUIcontroller also creates and hides other forms that may be necessary to setup
    //a game.  Everything displayed on the screen is controlled by this class
    public ClientGUIController(MainController _mainController) {
        
        
        initComponents();
        
        //instantiate controllers
        this.mainController = _mainController;
        this.soundController = new SoundController();
        
        //instantiate GUI Components
        movingObjects = new Container();
        this.playerLabels = new HashMap<InetAddress, JLabel>();
        this.ballLabels = new HashMap<InetAddress, JLabel>();
        this.playerJustDied = new HashMap<InetAddress, Integer>();
        
        //add listener for mouse motion
        movingObjects.addMouseMotionListener(this);
        this.getContentPane().addMouseMotionListener(this);

    }
    
    /**
     * draw the main menu(the start up screen)
     */
    public void drawMainMenu() {
        main_menu = new mainMenu(this);
        main_menu.setVisible(true);
        soundController.stopMidi();
        this.setVisible(false);
    }

    /**
     * draw the game screen
     */
    public void drawGameScreen() {
        main_menu.setVisible(false);
        soundController.startMidi("backmusic.mid");
        this.putWindowInCenter();
        this.setVisible(true);
    }

    /**
     * Move the game screen to the middle of the screen
     */
    public void putWindowInCenter() {
        // Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // Determine the new location of the window
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;
        
        // Move the window
        this.setLocation(x, y);
    }

    /**
     * Draws the walls around the game and a center dividing line
     */
    public void drawArena() {
        String wallImage = "/NetworksProjectPackage/wall.gif";
        String lineImage = "/NetworksProjectPackage/line.gif";
        JLabel currentWall;
        JLabel currentLine;
        
        //draw all the walls
        for (int i = 0; i <= this.getWidth() - 17; i += 16) {
            for (int j = 50; j <= this.getHeight() - 16; j += 16) {
                if ((i < 1 || j < 51) || (i > this.getWidth() - 33 || j > this.getHeight() - 49)) {
                    currentWall = new JLabel(new ImageIcon(getClass().getResource(wallImage)));
                    currentWall.setSize(16, 16);
                    currentWall.setLocation(i, j);
                    currentWall.setVisible(true);
                    this.getContentPane().add(currentWall);
                }
            }
        }
        
        //draw the separation line between the two teams
        for (int i = 50; i <= this.getHeight() - 16; i += 16) {
            currentLine = new JLabel(new ImageIcon(getClass().getResource(lineImage)));
            currentLine.setSize(6, 16);
            currentLine.setLocation(this.getWidth() / 2 - 3, i);
            currentLine.setVisible(true);
            this.getContentPane().add(currentLine);
        }
        
        //set boundaries for two teams
        ClientGUIController.minYForTEAM1 = 82;
        ClientGUIController.minYForTEAM2 = 82;
        ClientGUIController.minXForTEAM1 = 32;
        ClientGUIController.maxXForTEAM1 = this.getContentPane().getWidth()/2 - 24;
        ClientGUIController.maxYForTEAM1 = this.getContentPane().getHeight() - 32;
        ClientGUIController.minXForTEAM2 = this.getContentPane().getWidth()/2 + 24;
        ClientGUIController.maxXForTEAM2 = this.getContentPane().getWidth() - 32;
        ClientGUIController.maxYForTEAM2 = this.getContentPane().getHeight() - 32;
        ClientGUIController.maxXArena = this.getContentPane().getWidth() - 32;
        ClientGUIController.maxYArena = this.getContentPane().getHeight() -32;
        ClientGUIController.minYArena = 66;
        ClientGUIController.minXArena = 16;
        
        //configure buttons
        this.jButton1.setSize(300, 50);
        this.jButton1.setOpaque(true);
        this.jButton1.setBorderPainted(false);
        this.jButton1.setLocation(this.getContentPane().getWidth()/2 - 300, 0);
        this.jButton2.setSize(300, 50);
        this.jButton2.setOpaque(true);
        this.jButton2.setBorderPainted(false);
        this.jButton2.setLocation(this.getContentPane().getWidth()/2, 0);
        this.jLabel1.setSize(200,50);
        this.jLabel1.setLocation(this.getContentPane().getWidth()/2 - 500,0);
        this.jLabel2.setSize(200,50);
        this.jLabel2.setLocation(this.getContentPane().getWidth()/2 + 300,0);
        
        //repain the entire screen
        this.repaintAll(NetworkController.realTimeData, false);
        
    }
    
    /**
     * This function defines the mouseMoved event.  This will be triggered
     * whenever mouse is moved on the content pane
     * @param e 
     */
    public void mouseMoved(MouseEvent e) {
        
        //only get my own player data
        PlayerData playerData = NetworkController.realTimeData.getPlayerData(NetworkController.myIPAddress);
        
        //get more information about the player
        int team = playerData.getTeam();
        int state = playerData.isAlive();
        
        //get your mouse coordinate relative to the game screen
        int x = e.getX();
        int y = e.getY();
        
        //don't the player move out of bound
        if(team == Constants.TEAM1)
        {
            if(x > ClientGUIController.maxXForTEAM1)
            {
                x = ClientGUIController.maxXForTEAM1;
            }else if(x < ClientGUIController.minXForTEAM1)
            {
                x = ClientGUIController.minXForTEAM1;
            }
            
            if(y > ClientGUIController.maxYForTEAM1)
            {
                y = ClientGUIController.maxYForTEAM1;
            }else if(y < ClientGUIController.minYForTEAM1)
            {
                y = ClientGUIController.minYForTEAM1;
            }
        }else if(team == Constants.TEAM2)
        {
            if(x > ClientGUIController.maxXForTEAM2)
            {
                x = ClientGUIController.maxXForTEAM2;
            }else if(x < ClientGUIController.minXForTEAM2)
            {
                x = ClientGUIController.minXForTEAM2;
            }
            
            if(y > ClientGUIController.maxYForTEAM2)
            {
                y = ClientGUIController.maxYForTEAM2;
            }else if(y < ClientGUIController.minYForTEAM2)
            {
                y = ClientGUIController.minYForTEAM2;
            }
        }
        
        //update the player data
        NetworkController.realTimeData.setPlayerData(NetworkController.myIPAddress, x, y, Constants.NOTPRESSED);
        
        //only repaint if the player is still alive
        if(state == Constants.ALIVE)
        {
            this.repaintAll(NetworkController.realTimeData, false);
        }
    }
    
    /**
     * This function defines the mouseDragged event.  This will be triggered
     * whenever mouse is moved on the content pane
     * @param e 
     */
    public void mouseDragged(MouseEvent e) {
        
        //Only get my own player data
        PlayerData playerData = NetworkController.realTimeData.getPlayerData(NetworkController.myIPAddress);
        
        //get more information about my own player
        int team = playerData.getTeam();
        int state = playerData.isAlive();
        
        //get x,y coordinates of my mouse position
        int x = e.getX();
        int y = e.getY();
        
        //make sure the player doesn't go out of bound
        if(team == Constants.TEAM1)
        {
            if(x > ClientGUIController.maxXForTEAM1)
            {
                x = ClientGUIController.maxXForTEAM1;
            }else if(x < ClientGUIController.minXForTEAM1)
            {
                x = ClientGUIController.minXForTEAM1;
            }
            
            if(y > ClientGUIController.maxYForTEAM1)
            {
                y = ClientGUIController.maxYForTEAM1;
            }else if(y < ClientGUIController.minYForTEAM1)
            {
                y = ClientGUIController.minYForTEAM1;
            }
        }else if(team == Constants.TEAM2)
        {
            if(x > ClientGUIController.maxXForTEAM2)
            {
                x = ClientGUIController.maxXForTEAM2;
            }else if(x < ClientGUIController.minXForTEAM2)
            {
                x = ClientGUIController.minXForTEAM2;
            }
            
            if(y > ClientGUIController.maxYForTEAM2)
            {
                y = ClientGUIController.maxYForTEAM2;
            }else if(y < ClientGUIController.minYForTEAM2)
            {
                y = ClientGUIController.minYForTEAM2;
            }
        }
        
        //update the player data with the new x,y coordinate
        NetworkController.realTimeData.setPlayerData(NetworkController.myIPAddress, x, y, Constants.PRESSED);
        
        
        //only repaint the screen when my own player is alive
        if(state == Constants.ALIVE)
        {
            this.repaintAll(NetworkController.realTimeData, false);
        }
    }
    
    /**
     * This function will repaint the game screen(not including arena).
     * @param data the RealTimeData that the new screen should be repainted based off
     * @param isBasedOnPacketFromServer 
     */
    public void repaintAll(RealTimeData data, boolean isBasedOnPacketFromServer) {
        
        //set the size of buttons and labels
        this.jButton1.setSize(200, 50);
        this.jButton1.setLocation(this.getContentPane().getWidth()/2 - 200, 0);
        this.jButton2.setSize(200, 50);
        this.jButton2.setLocation(this.getContentPane().getWidth()/2, 0);
        this.jLabel1.setSize(200,50);
        this.jLabel1.setLocation(this.getContentPane().getWidth()/2 - 400,0);
        this.jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        this.jLabel2.setSize(200,50);
        this.jLabel2.setLocation(this.getContentPane().getWidth()/2 + 200,0);
        this.jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        
        //get the hashmap that stores all the players data
        HashMap<InetAddress, PlayerData> playersData = data.getAllPlayerData();

        //set all the path/file name of all the icons
        String playerIconTeamOne = "/NetworksProjectPackage/athlete.png";
        String playerIconTeamTwo = "/NetworksProjectPackage/athleteblue.png";
        String ballIconTeamOne = "/NetworksProjectPackage/redBall.png";
        String ballIconTeamTwo = "/NetworksProjectPackage/blueBall.png";
        String deadAnimation = "/NetworksProjectPackage/explosion2.gif";
        
        //more GUI component updates
        this.jLabel1.setText(""+NetworkController.realTimeData.getScore(Constants.TEAM1));
        this.jLabel2.setText(""+NetworkController.realTimeData.getScore(Constants.TEAM2));
        
        //go through all the players and paint each individual players
        for (InetAddress ipAddress : playersData.keySet()) {
            
            PlayerData playerData = playersData.get(ipAddress);
            
            //if the player is exiting, remove him from the maps in the GUI, also remove the label for him
            if(playerData.getExiting() == Constants.EXITING)
            {
                if(this.playerLabels.get(ipAddress) != null)
                {
                    this.getContentPane().remove(this.playerLabels.get(ipAddress));
                    this.playerLabels.remove(ipAddress);
                }
                if(this.ballLabels.get(ipAddress) != null)
                {
                    this.getContentPane().remove(this.ballLabels.get(ipAddress));
                    this.ballLabels.remove(ipAddress);
                }
                
                //if I exited, get rid of the game screen entirely
                if(ipAddress.equals(NetworkController.myIPAddress))
                {
                    this.getContentPane().removeAll();
                    this.main_menu.setVisible(true);   
                    this.setVisible(false);
                    playerData.setExiting(Constants.NOT_EXITING);
                }
                
            }else
            {
                //if the player is not exiting
                
                //if the player is alive
                if (playerData.isAlive() == Constants.ALIVE)
                {
                    //reset his value in the playerJustDied map
                    if(this.playerJustDied.get(ipAddress) != null && this.playerJustDied.get(ipAddress).intValue() == 1)
                    {
                        this.playerJustDied.put(ipAddress, 0);
                    }

                    
                    //if he is a newly joined player, add his label to the label map
                    if(this.playerLabels.get(ipAddress) == null)
                    {
                        JLabel newPlayerLabel = new JLabel(new ImageIcon(getClass().getResource(playerIconTeamOne)));
                        this.playerLabels.put(ipAddress, newPlayerLabel);


                        movingObjects.add(this.playerLabels.get(ipAddress));
                        this.getContentPane().add(this.playerLabels.get(ipAddress));


                    }
                    
                    //update the icon based on team
                    if(playerData.getTeam() == Constants.TEAM1)
                    {
                        this.playerLabels.get(ipAddress).setIcon(new ImageIcon(getClass().getResource(playerIconTeamOne)));
                    }else if(playerData.getTeam() == Constants.TEAM2)
                    {
                        this.playerLabels.get(ipAddress).setIcon(new ImageIcon(getClass().getResource(playerIconTeamTwo)));
                    } 

                    //create ball label for newly joined member
                    if(this.ballLabels.get(ipAddress) == null)
                    {
                        JLabel newBallLabel = new JLabel(new ImageIcon(getClass().getResource(ballIconTeamOne)));
                        this.ballLabels.put(ipAddress, newBallLabel);

                        movingObjects.add(this.ballLabels.get(ipAddress));
                        this.getContentPane().add(this.ballLabels.get(ipAddress));
                    }
                    
                    //update ball label based on team
                    if(playerData.getTeam() == Constants.TEAM1)
                    {
                        this.ballLabels.get(ipAddress).setIcon(new ImageIcon(getClass().getResource(ballIconTeamOne)));
                    }else if(playerData.getTeam() == Constants.TEAM2)
                    {
                        this.ballLabels.get(ipAddress).setIcon(new ImageIcon(getClass().getResource(ballIconTeamTwo)));
                    } 
                    
                    //this is a work-around for after explosion
                    if(!isBasedOnPacketFromServer || !ipAddress.equals(NetworkController.myIPAddress))
                    {
                        this.playerLabels.get(ipAddress).setSize(32, 32);
                        this.playerLabels.get(ipAddress).setLocation(playerData.getPlayerX() - this.playerLabels.get(ipAddress).getWidth()/2, playerData.getPlayerY() - this.playerLabels.get(ipAddress).getHeight()/2);
                        this.playerLabels.get(ipAddress).setVisible(true);
                    }

                    //update the ball label
                    this.ballLabels.get(ipAddress).setSize(32, 32);
                    this.ballLabels.get(ipAddress).setLocation(playerData.getBallX() - this.ballLabels.get(ipAddress).getWidth()/2, playerData.getBallY() - this.ballLabels.get(ipAddress).getHeight()/2);
                    this.ballLabels.get(ipAddress).setVisible(true);

                 //if the player just died
                }else if (playersData.get(ipAddress).isAlive() == Constants.DEAD)
                {
                    
                    //dead animation icon needs to be loaded into the corresponding player label
                    playerLabels.get(ipAddress).setIcon(new ImageIcon(getClass().getResource(deadAnimation)));
                    
                    //only play the animation once
                    if(this.playerJustDied.get(ipAddress) == null || this.playerJustDied.get(ipAddress).intValue() == 0)
                    {
                        this.playerJustDied.put(ipAddress, 1);
                        playerLabels.get(ipAddress).setIcon(new ImageIcon(getClass().getResource(deadAnimation)));

                    }
                    
                    //ball needs to disappear(exist at the player's position) when the player is dead
                    this.ballLabels.get(ipAddress).setVisible(false);
                    this.ballLabels.get(ipAddress).setLocation(this.playerLabels.get(ipAddress).getLocation());

                }
            }
            
        }

        //repain the content pane
        this.getContentPane().repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setForeground(new java.awt.Color(255, 102, 0));
        setMaximizedBounds(new java.awt.Rectangle(0, 0, 1014, 764));
        setPreferredSize(new java.awt.Dimension(1014, 764));
        setResizable(false);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                escapeKeyPressed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(255, 0, 0));
        jButton1.setFont(new java.awt.Font("Comic Sans MS", 0, 14)); // NOI18N
        jButton1.setText("Team RED");
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 153, 255));
        jButton2.setFont(new java.awt.Font("Comic Sans MS", 0, 14)); // NOI18N
        jButton2.setText("Team BLUE");
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Comic Sans MS", 0, 14)); // NOI18N
        jLabel1.setText("0");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel2.setFont(new java.awt.Font("Comic Sans MS", 0, 14)); // NOI18N
        jLabel2.setText("0");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jButton3.setText("Sound Off");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Exit");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jButton3)
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addGap(38, 38, 38)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jLabel2)
                    .addComponent(jButton4)
                    .addComponent(jButton3))
                .addContainerGap(258, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    //This function is deprecated
    private void escapeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_escapeKeyPressed
        if (evt.getKeyCode() == 27) {
            final JDialog optionPaneDialog = new JDialog(this, "Leave Game?");

            //Note we are creating an instance of a JOptionPane
            //Normally it's just a call to a static method.
            JOptionPane optPane = new JOptionPane("Are You Sure You Want to Leave the Game?",
                    JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

            //Listen for the JOptionPane button click. It comes through as property change 
            //event with the property called "value". 
            optPane.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    if (e.getPropertyName().equals("value")) {
                        switch ((Integer) e.getNewValue()) {
                            case JOptionPane.OK_OPTION: 
                                //Put code to exit game here
                                optionPaneDialog.dispose();
                                drawMainMenu();
                                break;
                            case JOptionPane.CANCEL_OPTION:
                                //user clicks CANCEL
                                break;
                        }
                        optionPaneDialog.dispose();
                    }
                }
            });
            optionPaneDialog.setContentPane(optPane);

            //Let the JDialog figure out how big it needs to be
            //based on the size of JOptionPane by calling the pack() method
            optionPaneDialog.pack();
            optionPaneDialog.setLocationRelativeTo(this);
            optionPaneDialog.setVisible(true);
        }
    }//GEN-LAST:event_escapeKeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        NetworkController.realTimeData.getPlayerData(NetworkController.myIPAddress).setTeam(Constants.TEAM1);
        this.repaintAll(NetworkController.realTimeData, false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        NetworkController.realTimeData.getPlayerData(NetworkController.myIPAddress).setTeam(Constants.TEAM2);
        this.repaintAll(NetworkController.realTimeData, false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        byte[] emptyMsg = new byte[2];
        soundController.stopMidi();
        this.mainController.networkController.getUDPClient().sendPacket(NetworkController.serverAddress, NetworkController.serverListenPortNumber, emptyMsg, ProtocolInfo.TYPE_UNICAST_EXITGAME);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    soundController.toggleMidi();
    }//GEN-LAST:event_jButton3ActionPerformed
    
    public void startNetworkController()
    {
        this.mainController.startNetworkController();
    }
    
    public void requestGameServers()
    {
        this.mainController.requestGameServers();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientGUIController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientGUIController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientGUIController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientGUIController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ClientGUIController(null).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
