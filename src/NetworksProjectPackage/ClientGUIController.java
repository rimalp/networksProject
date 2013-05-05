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

/**
 *
 * @author Drew Jeffrey
 */
public class ClientGUIController extends javax.swing.JFrame {

    public ArrayList<JLabel> objectList;
    private MainController controller;
    private JButton hostGame;
    private JButton joinGame;
    private JButton exitGame;
    private JLabel mmTitle;
    private mainMenu main_menu;
    private Container movingObjects;
    /**
     * Creates new form ClientGUIController
     */
    //GUI controller's frame is the main area on which games are played.
    //GUIcontroller also creates and hides other forms that may be necessary to setup
    //a game.  Everything displayed on the screen is controlled by this class
    public ClientGUIController() {
        initComponents();

        //Create Test Data
        RealTimeData test = new RealTimeData();
        movingObjects = new Container();
        test.createTestPlayer();
        this.repaintAll(test);
        this.repaintAll(test);


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
            for (int j = 0; j <= this.getHeight() - 16; j += 16) {
                if ((i < 1 || j < 1) || (i > this.getWidth() - 33 || j > this.getHeight() - 49)) {
                    currentWall = new JLabel(new ImageIcon(getClass().getResource(wallImage)));
                    currentWall.setSize(16, 16);
                    currentWall.setLocation(i, j);
                    currentWall.setVisible(true);
                    this.getContentPane().add(currentWall);
                }
            }
        }
        for (int i = 0; i <= this.getHeight() - 16; i += 16) {
            currentLine = new JLabel(new ImageIcon(getClass().getResource(lineImage)));
            currentLine.setSize(6, 16);
            currentLine.setLocation(this.getWidth() / 2 - 3, i);
            currentLine.setVisible(true);
            this.getContentPane().add(currentLine);
        }

        this.getContentPane().repaint();
    }
    

    public void repaintAll(RealTimeData data) {
        for (int i = 0; i < movingObjects.getComponentCount(); i++)
                {
                    if (this.getContentPane().getComponent(i).getSize().getHeight()!=250)
                    {
                    this.getContentPane().remove(movingObjects.getComponent(i));
                    System.out.println("Removed Object");
                    }
                }

        HashMap<InetAddress, PlayerData> players = data.getAllPlayerData();

        //paint each of the player and its ball in the screen one by one
        String playerIcon = "/NetworksProjectPackage/1363852977_ball.png";
        String ballIcon = "/NetworksProjectPackage/1363853010_Green Ball.png";
        String deadAnimation = "/NetworksProjectPackage/explosion2.gif";

        for (PlayerData player : players.values()) {

            if (player.isAlive() == Constants.ALIVE)
            {
                System.out.println("Draw Alive Player");
            //player
            JLabel newPlayer = new JLabel(new ImageIcon(getClass().getResource(playerIcon)));
            newPlayer.setLocation(player.getPlayerX(), player.getPlayerY());
            newPlayer.setVisible(true);
            newPlayer.setSize(200, 200);
            movingObjects.add(newPlayer);
            this.getContentPane().add(newPlayer);

            //ball
            JLabel newBall = new JLabel(new ImageIcon(getClass().getResource(ballIcon)));
            newBall.setLocation(player.getBallX(), player.getBallY());
            newBall.setVisible(true);
            newBall.setSize(200, 200);
            movingObjects.add(newBall);
            this.getContentPane().add(newBall);
            }
            else if (player.isAlive() != Constants.DEAD)
            {
            System.out.println("Draw Dead Animation");
            JLabel deadPlayer = new JLabel(new ImageIcon(getClass().getResource(deadAnimation)));
            deadPlayer.setLocation(player.getPlayerX(), player.getPlayerY());
            deadPlayer.setVisible(true);
            deadPlayer.setSize(250, 250);
            movingObjects.add(deadPlayer);
            this.getContentPane().add(deadPlayer);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
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
                new ClientGUIController().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
