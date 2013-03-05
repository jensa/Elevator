package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

public class Audiotest {
	

public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(300,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        KeyListener s = new KeyListener (){

			@Override
			public void keyTyped (KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed (KeyEvent e) {
				try{
				AudioInputStream audio = AudioSystem.getAudioInputStream(new File("sound.wav"));
	            Clip clip = AudioSystem.getClip();
	            clip.open(audio);
	            clip.start();
	            Thread.sleep (2000);
				} catch (Exception ex){
					
				}
				
			}

			@Override
			public void keyReleased (KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
        	
        };
        frame.addKeyListener (s);

        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File("sound.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        }
        
        catch(UnsupportedAudioFileException uae) {
            System.out.println(uae);
        }
        catch(IOException ioe) {
            System.out.println(ioe);
        }
        catch(LineUnavailableException lua) {
            System.out.println(lua);
        }
}

}
