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
/**
 *
 * @author Drew Jeffrey
 */
public class ClientGUIController extends javax.swing.JFrame {

    public ArrayList<JLabel> objectList;

    private MainController controller;

    /**
     * Creates new form ClientGUIController
     */
    public ClientGUIController() {
        initComponents();
        this.drawArena();


        ArrayList<Integer> x = new ArrayList<Integer>();
        ArrayList<Integer> y = new ArrayList<Integer>();
        ArrayList<Integer> object = new ArrayList<Integer>();
        object.add(new Integer(10));
        object.add(new Integer(11));
        x.add(new Integer(200));
        x.add(new Integer(250));
        y.add(new Integer(50));
        y.add(new Integer(300));
        drawScreen(object,x,y);


    }
    
        public void drawArena() {
        String wallImage =  "/NetworksProjectPackage/wall.gif";
        ImageIcon wall = new ImageIcon(getClass().getResource(wallImage));
        JLabel currentWall;
        for (int i = 0; i <= this.getWidth()-16; i += 16)
        {
            for(int j = 0; j <= this.getHeight()-16; j += 16)
            {
                if ((i<1 || j < 1) || (i > this.getWidth()-33 || j > this.getHeight()-49))
                {
            currentWall = new JLabel(new ImageIcon(getClass().getResource(wallImage)));
                currentWall.setSize(16, 16);
            currentWall.setLocation(i,j);
            currentWall.setVisible(true);
            this.getContentPane().add(currentWall);}}
                    
        }
               
    }

    public void repaintAll(RealTimeData data){
        this.getContentPane().removeAll();

        HashMap<InetAddress, PlayerData> players = data.getAllPlayerData();

        //paint each of the player and its ball in the screen one by one
        String playerIcon = "/NetworksProjectPackage/1363852977_ball.png";
        String ballIcon = "/NetworksProjectPackage/1363853010_Green Ball.png";

        for(PlayerData player: players.values()){

           //player
           JLabel newPlayer =  new JLabel(new ImageIcon(getClass().getResource(playerIcon)));
           newPlayer.setLocation(player.getPlayerX(), player.getPlayerY());
           newPlayer.setVisible(true);
           newPlayer.setSize(200,200);
           this.getContentPane().add(newPlayer);

           //ball
           JLabel newBall = new JLabel(new ImageIcon(getClass().getResource(ballIcon)));
           newBall.setLocation(player.getBallX(), player.getBallY());
           newBall.setVisible(true);
           newBall.setSize(200,200);
           this.getContentPane().add(newBall);
        }


    }

    
    public void drawScreen(ArrayList<Integer> object, ArrayList<Integer> xpositions, ArrayList<Integer> ypositions)
    {
        ArrayList<JLabel> paintedObjects = new ArrayList<JLabel>();
        //this.getContentPane().removeAll(); //Remove all previous objects on the screen
        for (int i = 0; i < object.size(); i++) //Paint each object on the screen
        {
            String icon;
            int xpos, ypos;
            switch (object.get(i).intValue()){
                case Constants.PLAYER1: icon = "/NetworksProjectPackage/1363852977_ball.png"; break;
                case Constants.PLAYER1BALL: icon = "/NetworksProjectPackage/1363853010_Green Ball.png"; break;
                default: icon = ""; break;                  
           }
            xpos = xpositions.get(i).intValue();
            ypos = ypositions.get(i).intValue();
            
            //Create and add the label to the screen
            paintedObjects.add(new JLabel(new ImageIcon(getClass().getResource(icon))));
            paintedObjects.get(i).setLocation(xpos,ypos);
            paintedObjects.get(i).setVisible(true);
            paintedObjects.get(i).setSize(200,200);
            this.getContentPane().add(paintedObjects.get(i));
        }
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1024, 768));

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
