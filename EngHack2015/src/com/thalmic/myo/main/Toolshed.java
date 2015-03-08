package com.thalmic.myo.main;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;




public class Toolshed extends JPanel{

	int imageCount = 12;
	BufferedImage[] image = new BufferedImage[imageCount];

	int bought [] = {0,0,0,0,0};
	int money=0;
	public int selected [] = {0,0,0,0,0};

	public Toolshed(){
		setSize(1920,1080);
		try {




			image[0] = ImageIO.read(new File("assets/toolshed/shelves.png"));
			image[1] = ImageIO.read(new File("assets/toolshed/basic_axe.png"));
			image[2] = ImageIO.read(new File("assets/toolshed/chain_saw.png"));
			image[3] = ImageIO.read(new File("assets/toolshed/saw.png"));
			image[4] = ImageIO.read(new File("assets/toolshed/swiss_army_knife.png"));
			image[5] = ImageIO.read(new File("assets/toolshed/sword.png"));


			image[6] = ImageIO.read(new File("assets/toolshed/5.png"));
			image[7] = ImageIO.read(new File("assets/toolshed/15.png"));
			image[8] = ImageIO.read(new File("assets/toolshed/30.png"));
			image[9] = ImageIO.read(new File("assets/toolshed/50.png"));
			image[10] = ImageIO.read(new File("assets/toolshed/65.png"));
			image[11] = ImageIO.read(new File("assets/toolshed/100.png"));

		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void unBuy(int num){
		bought[num]=0;
	}

	public void bought(int num){
		bought[num]=1;
	}

	public boolean selected(int num){
		if(bought[num]!=1)
		{
			selected[0]=0;
			selected[1]=0;
			selected[2]=0;
			selected[3]=0;
			selected[4]=0;
			selected[num]=1;
			repaint();
			return true;
		}
		return false;
	}
	public void setGold(int gold)
	{
		this.money=gold;
	}
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(image[0], 0, 0, null);		

		if(selected[0]==1)
			for(int i = 0 ; i<15 ; i++){
				g.drawRect(440-i, 0+i, 260-i, 475+i);
			}
		if(bought[0] == 0){
			g.drawImage(image[1], 350, -50, null);
			g.drawImage(image[7], 400, 270, null);
		}

		if(selected[3]==1)
			for(int i = 0 ; i<15 ; i++){
				g.drawRect(410-i, 550+i, 450-i, 240+i);
			}

		if(bought[3] == 0){
			g.drawImage(image[2],380,530,null);
			g.drawImage(image[10], 400, 650, null);
		}

		if(selected[2]==1)
			for(int i = 0 ; i<15 ; i++){
				g.drawRect(1050-i, 0+i, 240-i, 450+i);
			}
		if(bought[2] == 0){
			g.drawImage(image[3],1000,0,null);
			g.drawImage(image[8], 980, 270, null);
		}

		if(selected[1]==1)
			for(int i = 0 ; i<15 ; i++){
				g.drawRect(710-i, 200+i, 330-i, 270+i);
			}

		if(bought[1] == 0){
			g.drawImage(image[4],640,-25,null);
			g.drawImage(image[6],710,280,null);
		}

		if(selected[4]==1)
			for(int i = 0 ; i<15 ; i++){
				g.drawRect(835-i, 550+i, 450-i, 270+i);
			}

		if(bought[4] == 0){
			g.drawImage(image[5],800,580,null);
			g.drawImage(image[9],850,650,null);
		}

		if(bought[0] == 0 && bought[1] == 0 && bought[2] == 0 && bought[3] == 0 && bought[4] == 0){
			//draw lightsaber
		}
		g.setColor(Color.ORANGE);
		g.setFont(new Font("Courier New", Font.BOLD, 100));
		g.drawString("Gold: "+money,0,1080);
	}
}