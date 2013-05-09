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
 *
 * @author Drew Jeffrey
 */
public class ClientGUIController extends javax.swing.JFrame implements MouseMotionListener {

    public ArrayList<JLabel> objectList;
    private MainController controller;
    private JButton hostGame;
    private JButton joinGame;
    private JButton exitGame;
    private JLabel mmTitle;
    public mainMenu main_menu;
    private Container movingObjects;
    
    public MainController mainController = null;
    private HashMap<InetAddress, JLabel> playerLabels = null;
    private HashMap<InetAddress, JLabel> ballLabels = null;
    private HashMap<InetAddress, Integer> playerJustDied = null;
    private int currentNumOfPlayers = 0;
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
        this.mainController = _mainController;
        //Create Test Data
        RealTimeData test = new RealTimeData();
        movingObjects = new Container();
//        movingObjects.addMouseListener(this.createMouseListener());
        movingObjects.addMouseMotionListener(this);
        this.getContentPane().addMouseMotionListener(this);
//        addMouseMotionListener(this);
        
        
                // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            cursorImg, new Point(0, 0), "blank cursor");

        // Set the blank cursor to the JFrame.
        this.getContentPane().setCursor(blankCursor);

        
//        
//        test.createTestPlayer();
//        this.repaintAll(test);
//        this.repaintAll(test);
        this.playerLabels = new HashMap<InetAddress, JLabel>();
        this.ballLabels = new HashMap<InetAddress, JLabel>();
        this.playerJustDied = new HashMap<InetAddress, Integer>();
        
        
    }
    //Moves to the main menu

    public void drawMainMenu() {
        main_menu = new mainMenu(this);
        main_menu.setVisible(true);
        this.setVisible(false);
    }
    //Moves to the game screen

    public void drawGameScreen() {
        main_menu.setVisible(false);
        this.putWindowInCenter();
        this.setVisible(true);
    }

    //Automatically moves the frame to the center of the screen
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

    //Draws the walls around the game and a center dividing line
    public void drawArena() {
        String wallImage = "/NetworksProjectPackage/wall.gif";
        String lineImage = "/NetworksProjectPackage/line.gif";
        JLabel currentWall;
        JLabel currentLine;
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
        for (int i = 50; i <= this.getHeight() - 16; i += 16) {
            currentLine = new JLabel(new ImageIcon(getClass().getResource(lineImage)));
            currentLine.setSize(6, 16);
            currentLine.setLocation(this.getWidth() / 2 - 3, i);
            currentLine.setVisible(true);
            this.getContentPane().add(currentLine);
        }
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
        this.repaintAll(NetworkController.realTimeData, false);
        
    }
    
    public void mouseMoved(MouseEvent e) {
        PlayerData playerData = NetworkController.realTimeData.getPlayerData(NetworkController.myIPAddress);
        int team = playerData.getTeam();
        int state = playerData.isAlive();
        
        int x = e.getX();
        int y = e.getY();
        
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
        
        NetworkController.realTimeData.setPlayerData(NetworkController.myIPAddress, x, y, Constants.NOTPRESSED);
        
        if(state == Constants.ALIVE)
        {
            this.repaintAll(NetworkController.realTimeData, false);
        }
    }
     
    public void mouseDragged(MouseEvent e) {
        PlayerData playerData = NetworkController.realTimeData.getPlayerData(NetworkController.myIPAddress);
        int team = playerData.getTeam();
        int state = playerData.isAlive();
        
        int x = e.getX();
        int y = e.getY();
        
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
        
        NetworkController.realTimeData.setPlayerData(NetworkController.myIPAddress, x, y, Constants.PRESSED);
        
        if(state == Constants.ALIVE)
        {
            this.repaintAll(NetworkController.realTimeData, false);
        }
    }

    public void repaintAll(RealTimeData data, boolean isBasedOnPacketFromServer) {

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
        
        HashMap<InetAddress, PlayerData> playersData = data.getAllPlayerData();

        //paint each of the player and its ball in the screen one by one
        String playerIconTeamOne = "/NetworksProjectPackage/athlete.png";
        String playerIconTeamTwo = "/NetworksProjectPackage/athleteblue.png";
        String ballIconTeamOne = "/NetworksProjectPackage/redBall.png";
        String ballIconTeamTwo = "/NetworksProjectPackage/blueBall.png";
        String deadAnimation = "/NetworksProjectPackage/explosion2.gif";
        
        this.jLabel1.setText(""+NetworkController.realTimeData.getScore(Constants.TEAM1));
        this.jLabel2.setText(""+NetworkController.realTimeData.getScore(Constants.TEAM2));
        
        for (InetAddress ipAddress : playersData.keySet()) {
            
            PlayerData playerData = playersData.get(ipAddress);

            if (playerData.isAlive() == Constants.ALIVE)
            {
                if(this.playerJustDied.get(ipAddress) != null && this.playerJustDied.get(ipAddress).intValue() == 1)
                {
                    this.playerJustDied.put(ipAddress, 0);
                }
//                System.out.println("Draw Alive Player");
                
                //player
                if(this.playerLabels.get(ipAddress) == null)
                {
                    JLabel newPlayerLabel = new JLabel(new ImageIcon(getClass().getResource(playerIconTeamOne)));
//                    newPlayerLabel.addMouseMotionListener(this);
                    this.playerLabels.put(ipAddress, newPlayerLabel);
                    
                    
                    movingObjects.add(this.playerLabels.get(ipAddress));
                    this.getContentPane().add(this.playerLabels.get(ipAddress));
                    
                    
                }
                
                if(playerData.getTeam() == Constants.TEAM1)
                {
                    this.playerLabels.get(ipAddress).setIcon(new ImageIcon(getClass().getResource(playerIconTeamOne)));
                }else if(playerData.getTeam() == Constants.TEAM2)
                {
                    this.playerLabels.get(ipAddress).setIcon(new ImageIcon(getClass().getResource(playerIconTeamTwo)));
                } 
                
                if(this.ballLabels.get(ipAddress) == null)
                {
                    JLabel newBallLabel = new JLabel(new ImageIcon(getClass().getResource(ballIconTeamOne)));
//                       newBallLabel.addMouseMotionListener(this);
                    this.ballLabels.put(ipAddress, newBallLabel);
                    
                    movingObjects.add(this.ballLabels.get(ipAddress));
                    this.getContentPane().add(this.ballLabels.get(ipAddress));
                }
                
                if(playerData.getTeam() == Constants.TEAM1)
                {
                    this.ballLabels.get(ipAddress).setIcon(new ImageIcon(getClass().getResource(ballIconTeamOne)));
                }else if(playerData.getTeam() == Constants.TEAM2)
                {
                    this.ballLabels.get(ipAddress).setIcon(new ImageIcon(getClass().getResource(ballIconTeamTwo)));
                } 
                
                if(!isBasedOnPacketFromServer || !ipAddress.equals(NetworkController.myIPAddress))
                {
                    this.playerLabels.get(ipAddress).setSize(32, 32);
                    this.playerLabels.get(ipAddress).setLocation(playerData.getPlayerX() - this.playerLabels.get(ipAddress).getWidth()/2, playerData.getPlayerY() - this.playerLabels.get(ipAddress).getHeight()/2);
                    this.playerLabels.get(ipAddress).setVisible(true);
                }
                
                //ball
                this.ballLabels.get(ipAddress).setSize(32, 32);
                this.ballLabels.get(ipAddress).setLocation(playerData.getBallX() - this.ballLabels.get(ipAddress).getWidth()/2, playerData.getBallY() - this.ballLabels.get(ipAddress).getHeight()/2);
                this.ballLabels.get(ipAddress).setVisible(true);
                
                
                
                
            }else if (playersData.get(ipAddress).isAlive() == Constants.DEAD)
            {
                System.out.println("Draw Dead Animation");
//                JLabel deadPlayer = new JLabel(new ImageIcon(getClass().getResource(deadAnimation)));
                
                playerLabels.get(ipAddress).setIcon(new ImageIcon(getClass().getResource(deadAnimation)));
                if(this.playerJustDied.get(ipAddress) == null || this.playerJustDied.get(ipAddress).intValue() == 0)
                {
                    this.playerJustDied.put(ipAddress, 1);
                    playerLabels.get(ipAddress).setIcon(new ImageIcon(getClass().getResource(deadAnimation)));
                    
                }
                
                this.ballLabels.get(ipAddress).setVisible(false);
                this.ballLabels.get(ipAddress).setLocation(this.playerLabels.get(ipAddress).getLocation());
                
//                deadPlayer.setLocation(playersData.get(ipAddress).getPlayerX(), playersData.get(ipAddress).getPlayerY());
//                deadPlayer.setVisible(true);
                
            }
            
        }

        //this.getContentPane().add(movingObjects);
        this.getContentPane().repaint();
        //movingObjects.paintComponents(this.getContentPane().getGraphics());

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(98, 98, 98)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addGap(38, 38, 38)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addContainerGap(12, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jLabel2))
                .addContainerGap(258, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
        //Create a non-modal dialog box (so game can run in background) to allow user to exit game
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
