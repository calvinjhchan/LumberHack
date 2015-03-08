package com.thalmic.myo.main;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Menu extends JPanel {
	int imageCount = 5;
	BufferedImage[] image = new BufferedImage[imageCount];
	int selected;
	
	public Menu() {
		try {
			image[0] = ImageIO.read(new File("assets/background-menu.png"));
			image[1] = ImageIO.read(new File("assets/button-login.png"));
			image[2] = ImageIO.read(new File("assets/button-login-selected.png"));
			image[3] = ImageIO.read(new File("assets/button-quit.png"));
			image[4] = ImageIO.read(new File("assets/button-quit-selected.png"));
		}
		catch (IOException ioe) {
		}
		
		selected=0;
		JLabel l = new JLabel ("Lumberjack Simulator");
		add(l);
		setVisible (true);
		updateUI();
	}
	
	public void run() {
		
	}
	
	public void change() {
		if (selected == 0) {
			selected = 1;
			System.out.println(selected);
		}
		else {
			selected = 0;
			System.out.println(selected);
		}
		repaint();
	}
	
	public int getSelected() {
		return selected;
	}
	
	public void update(Graphics g) {
		paint(g);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(image[0], 0, 0, null);
		if (selected == 0) {
			g.drawImage(image[2], 250, 700, null);
			g.drawImage(image[3], 1100, 700, null);
		}
		else {
			g.drawImage(image[1], 250, 700, null);
			g.drawImage(image[4], 1100, 700, null);
		}
	}
}
