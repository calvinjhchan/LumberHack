package com.thalmic.myo.main;

import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.enums.*;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;

import java.awt.*;
import java.io.*;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.swing.*;
import javax.sound.sampled.*;

public class Lumberjack extends JFrame {
	Menu menu;
	Game game;
	JPanel stuff;
	DataCollector data = new DataCollector (this);
	public void movement(String mode,double toPrint)
	{
		if(game.isShowing())
			game.cut();
	}
	public static void main(String[] args) {
		new Lumberjack();
	}
	public void setMode(String mode)
	{
		data.setMode(mode);
	}
	public Lumberjack() {
		stuff = new JPanel(new CardLayout ());
		menu = new Menu();
		game = new Game(this);
		stuff.add(menu, "menu");
		stuff.add(game, "game");
		add(stuff);
		
		setUndecorated(true);
		setSize(1920, 1080);
		setVisible(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try {
			
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.open();

			InputStream is = new BufferedInputStream(new FileInputStream(new File("assets/darude-sandstorm.mid")));
			sequencer.setSequence(is);
			sequencer.start();
			
			Hub hub = new Hub("com.example.hello-myo");

			System.out.println("Attempting to find a Myo...");
			Myo myo = hub.waitForMyo(10000);

			if (myo == null) {
				throw new RuntimeException("Unable to find a Myo!");
			}
			System.out.println("Connected to a Myo armband!");
			hub.addListener(data);
			myo.unlock(UnlockType.UNLOCK_HOLD);
			data.setMode("punch");
			while (true) {
				hub.run(10);

			}
		} catch (Exception e) {
			System.err.println("Error: ");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void changeSelection(PoseType pose) {
		if (menu.isShowing()) {
			menu.change();
		}
		else if (game.isShowing ()) {
			if (game.getScreen() == 1) {
				game.change(pose);
			}
		}
	}
	
	public void select() {
		if (menu.isShowing()) {
			if (menu.getSelected() == 0) {
				((CardLayout) stuff.getLayout()).show(stuff, "game");
			}
			else {
				dispose ();
				System.exit(0);
			}
		}
		else if (game.isShowing ()) {
			game.select();
		}
		repaint();
	}
}