/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;

import javax.swing.*;
import java.util.*;
import java.net.*;

/**
 *
 * @author 
 */
public class GUITest extends javax.swing.JFrame implements Runnable{

    //ball physics variables
    final int RADIUS = 50;
    final int DELAY = 50;
    int centerX = 0;
    int centerY = 0;
    int orbitX;
    int orbitY;
    int frame_time = 5;
    Thread animator;
    double Vx = 0;
    double Vy = 0;
    double Ax = 0;
    double Ay = 0;
    
    public int px = 0;
    public int py = 0;
    public int bx = 0;
    public int by = 0;

    //variables for data handling
    RealTimeData realTimeData = null;
    NetworkController networkController = null;

    HashMap<String, PlayerData> activePlayers = new HashMap<String, PlayerData>();
    JLabel otherPlayer = null;
    JLabel otherBall = null;

    /**
     * Creates new form GUITest
     */
    public GUITest() {
        realTimeData = new RealTimeData();
        networkController = new NetworkController(null, -1, null,this);
        networkController.start();
        this.setVisible(true);

    }

    @Override
    public void addNotify() {
        super.addNotify();
        animator = new Thread(this);
        animator.start(); //thread started here
    }

    public void run(){
        System.out.println("GUITest Run called ");
        initComponents();


        this.realTimeData.createTestPlayer();
          this.createOtherPlayer();


        while (true) {
            orbit();
            repaint();
            px = this.jLabel1.getLocationOnScreen().x;
            py = this.jLabel1.getLocationOnScreen().y;
            bx = this.jLabel2.getLocationOnScreen().x;
            by = this.jLabel2.getLocationOnScreen().y;
            System.out.println("Player location: " + this.otherBall.getLocationOnScreen().getX() + "  Y: " + this.otherBall.getLocationOnScreen().getY());
            
            this.realTimeData = this.networkController.getRealTimeData(realTimeData);
            this.repaintAll(this.realTimeData);
            this.realTimeData.changePlayerData();


            try {
                Thread.sleep(frame_time);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel1MouseClicked(evt);
            }
        });
        jPanel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jPanel1MouseMoved(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/NetworksProjectPackage/1363852977_ball.png"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        jLabel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jLabel1MouseMoved(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/NetworksProjectPackage/1363853010_Green Ball.png"))); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/NetworksProjectPackage/1363853010_Green Ball.png"))); // NOI18N
        jLabel3.setText("jLabel3");

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/NetworksProjectPackage/1363852977_ball.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(314, 314, 314)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(636, 636, 636)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(125, 125, 125)
                        .addComponent(jLabel4)
                        .addGap(351, 351, 351)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(892, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(170, 170, 170)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(34, 34, 34)
                        .addComponent(jLabel4)))
                .addGap(43, 43, 43)
                .addComponent(jLabel1)
                .addContainerGap(923, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void orbit() {



        Ax = -0.001* frame_time * (this.jLabel2.getLocationOnScreen().getX() - centerX+24);
        Ay = 0.001 * frame_time * (centerY - this.jLabel2.getLocationOnScreen().getY()-80);
        
        Vx += Ax;
        //if (Vx > 0.001)
        Vx *= 0.99;
        
        Vy += Ay; 
        //if (Vy > 0.001)
        Vy *= 0.99;
        this.jLabel2.setLocation((int) (this.jLabel2.getLocationOnScreen().getX() + Vx), (int) (this.jLabel2.getLocationOnScreen().getY() + Vy));
        

    }

    private void jLabel1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseMoved
        this.centerX = evt.getXOnScreen();
        this.centerY = evt.getYOnScreen();
        this.jLabel1.setLocation(evt.getXOnScreen() - (this.jLabel1.getWidth() / 2), evt.getYOnScreen() - (this.jLabel1.getHeight()));
    }//GEN-LAST:event_jLabel1MouseMoved

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
    }//GEN-LAST:event_jLabel1MouseClicked

    private void jPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseClicked
    }//GEN-LAST:event_jPanel1MouseClicked

    private void jPanel1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseMoved
        this.jLabel1.setLocation(evt.getXOnScreen() - (this.jLabel1.getWidth() / 2),
                evt.getYOnScreen() - (this.jLabel1.getHeight()));
        this.centerX = evt.getXOnScreen();
        this.centerY = evt.getYOnScreen();
    }//GEN-LAST:event_jPanel1MouseMoved





    //repaint the screen

    public void repaintAll(RealTimeData data){
//        this.getContentPane().removeAll();

        
        HashMap<InetAddress, PlayerData> newPlayers = data.getAllPlayerData();
        System.out.println("NewPlayers size: " + newPlayers.size());
        for(PlayerData pd: newPlayers.values()){
            this.jLabel3.setLocation(pd.getBallX(), pd.getBallY());
            this.jLabel4.setLocation(pd.getPlayerX(), pd.getPlayerY());
            this.repaint();
        }

        //paint each of the player and its ball in the screen one by one
//        String playerIcon = "/NetworksProjectPackage/1363852977_ball.png";
//        String ballIcon = "/NetworksProjectPackage/1363853010_Green Ball.png";

//        for(PlayerData player: newPlayers.values()){
//
//           //player
//           JLabel newPlayer =  new JLabel(new ImageIcon(getClass().getResource(playerIcon)));
//           newPlayer.setLocation(player.getPlayerX(), player.getPlayerY());
//           newPlayer.setVisible(true);
//           newPlayer.setSize(200,200);
//           this.getContentPane().add(newPlayer);
//
//           //ball
//           JLabel newBall = new JLabel(new ImageIcon(getClass().getResource(ballIcon)));
//           newBall.setLocation(player.getBallX(), player.getBallY());
//           newBall.setVisible(true);
//           newBall.setSize(200,200);
//           this.getContentPane().add(newBall);
//        }


    }


    private void createOtherPlayer(){

           String playerIcon = "/NetworksProjectPackage/1363852977_ball.png";
           String ballIcon = "/NetworksProjectPackage/1363853010_Green Ball.png";
           //player
           this.otherPlayer =  new JLabel(new ImageIcon(getClass().getResource(playerIcon)));
//           this.otherPlayer = this.jLabel3;
           otherPlayer.setLocation(100,100);
           otherPlayer.setVisible(true);
           otherPlayer.setSize(100,100);
           this.getContentPane().add(otherPlayer);

           //ball
           this.otherBall = new JLabel(new ImageIcon(getClass().getResource(ballIcon)));
//           this.otherBall = this.jLabel4;
           otherBall.setLocation(200,200);
           otherBall.setVisible(true);
           otherBall.setSize(100,100);
           this.getContentPane().add(otherBall);

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
            java.util.logging.Logger.getLogger(GUITest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUITest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUITest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUITest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        new GUITest();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
