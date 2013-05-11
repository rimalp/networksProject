/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;

/**
 * This class controls the sound effect(background music) in the game
 * @author Drew Jeffery
 */
import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.sound.midi.*;
import  sun.audio.*;

public class SoundController {

    //private AudioClip explodeClip;
    private static Sequencer midiPlayer;
            
    /**
     * constructor
     */
    public SoundController() {
    }
    
      public void startMidi(String midFilename) {
      try {
         File midiFile = new File(midFilename);
         Sequence song = MidiSystem.getSequence(getClass().getResource(midFilename));
         midiPlayer = MidiSystem.getSequencer();
         midiPlayer.open();
         midiPlayer.setSequence(song);
         midiPlayer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
         midiPlayer.start();
      } catch (MidiUnavailableException e) {
         e.printStackTrace();
      } catch (InvalidMidiDataException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      }
      
      public void stopMidi()
      {
          if (midiPlayer != null && midiPlayer.isRunning()) midiPlayer.stop();
      }
      
      public void toggleMidi()
      {
          if (midiPlayer != null && midiPlayer.isRunning())
              stopMidi();
          else
              startMidi("backmusic.mid");
      }
      
   

    public void loadExplosionSound(String explodeFilename) {
        //File backgroundMusic = new File("NetworksProjectPackage/bjorn_lynne-_the_great_river_race.mid");
        try {
     InputStream in = new FileInputStream(new File(explodeFilename));
     AudioStream as = new AudioStream(in); 
     AudioPlayer.player.start(as);
        } catch (Exception e) {
            System.out.println("Cannot Open Explode File");
        }

    }

    public void playExplosion() {
        //explodeClip.start();
    }

    public static void main(String[] args) {
        SoundController sound = new SoundController();
        sound.toggleMidi();
        //sound.playExplosion();
    }
}