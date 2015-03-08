package com.thalmic.myo.main;
import com.thalmic.myo.enums.*;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.sound.sampled.*;
class SawDust{
	public double x,y,xDir,yDir;
	public SawDust(double x,double y,double xDir,double yDir)
	{
		this.x=x;
		this.y=y;
		this.xDir=xDir;
		this.yDir=yDir;
	}
	public boolean update()
	{
		x+=xDir;
		y+=yDir;
		yDir+=0.1;
		//yDir=Math.min(yDir,4);
		return (y<0||x<0||x>1920||y>1080);
	}
}
class Tree{
	public int x,y,type;
	public Tree(int x,int y,int type)
	{
		this.x=x;
		this.y=y;
		this.type=type;
	}
}
public class Game extends JPanel implements Runnable {
	final int numImages = 10;
	final int treeX = 600;
	final int treeY = 0;
	final int[] ogX = new int[numImages];
	final int[] ogY = new int[numImages];
	double toolX;
	int toolY;
	int activeTool;
	int progress = 50;
	JTextField name;
	JPanel[] screens = new JPanel[4];
	BufferedImage[] tools = new BufferedImage[numImages];
	BufferedImage[] trees = new BufferedImage[numImages];
	BufferedImage[] treesTop = new BufferedImage[numImages];
	BufferedImage[]backgroundTrees=new BufferedImage[3];
	BufferedImage background;
	int[]treeBottomAdjust=new int[numImages];
	Thread animator	= new Thread (this);
	Lumberjack l;
	boolean isAnimating = false;
	int direction = -1;
	int screen=0;
	ArrayList<SawDust> dust=new ArrayList<SawDust>();
	double falling=0;
	double speed=-3;
	int money=0;
	Toolshed toolshed;
	ArrayList<Tree> randomTrees=new ArrayList<Tree>();
	int choosing=0;
	Clip[] soundClips=new Clip[5];
	public Game(Lumberjack l) {
		try
		{
			File soundFile = new File("assets/sound-chop.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
			soundClips[1] = AudioSystem.getClip();
			soundClips[1].open(audioIn);
			File soundFile2 = new File("assets/punch.wav");
			AudioInputStream audioIn2 = AudioSystem.getAudioInputStream(soundFile2);
			soundClips[0] = AudioSystem.getClip();
			soundClips[0].open(audioIn2);
			File soundFile3 = new File("assets/sound-chainsaw.wav");
			AudioInputStream audioIn3 = AudioSystem.getAudioInputStream(soundFile3);
			soundClips[2] = AudioSystem.getClip();
			soundClips[2].open(audioIn3);
			File soundFile4 = new File("assets/sound-saw.wav");
			AudioInputStream audioIn4 = AudioSystem.getAudioInputStream(soundFile4);
			soundClips[3] = AudioSystem.getClip();
			soundClips[3].open(audioIn4);
			File soundFile5 = new File("assets/sound-sword.wav");
			AudioInputStream audioIn5 = AudioSystem.getAudioInputStream(soundFile5);
			soundClips[4] = AudioSystem.getClip();
			soundClips[4].open(audioIn5);
			
		}
		catch(Exception e)
		{
			System.out.println("failed music");
		}
		this.l=l;
		setLayout (new CardLayout());

		for (int a = 0 ; a < 4 ; a++) {
			screens[a] = new JPanel(new SpringLayout());
		}
		toolshed=new Toolshed();

		add(screens[0], "cut");
		add(toolshed, "toolshed");

		ogX[0] = 800;
		ogY[0] = 600;
		ogX[1] = 850;
		ogY[1] = 400;
		ogX[2] = 1350;
		ogY[2] = 500;
		ogX[3] = 1350;
		ogY[3] = 500;
		ogX[4] = 700;
		ogY[4] = 300;
		ogX[5] = 1300;
		ogY[5] =400;

		((CardLayout) getLayout()).show(this, "name");

		try {
			background=ImageIO.read(new File("assets/background-game.png"));
			tools[0] = ImageIO.read(new File("assets/tools/fist.png"));
			tools[1] = ImageIO.read(new File("assets/tools/swissarmyknife.png"));
			tools[2] = ImageIO.read(new File("assets/tools/axe.png"));
			tools[3] = ImageIO.read(new File("assets/tools/saw.png"));
			tools[4] = ImageIO.read(new File("assets/tools/sword.png"));
			tools[5] = ImageIO.read(new File("assets/tools/chainsaw.png"));

			trees[0] = ImageIO.read(new File("assets/trees/shrub-bottom.png"));
			treesTop[0] = ImageIO.read(new File("assets/trees/shrub-top.png"));
			trees[1] = ImageIO.read(new File("assets/trees/palm-bottom.png"));
			treesTop[1] = ImageIO.read(new File("assets/trees/palm-top.png"));
			trees[2] = ImageIO.read(new File("assets/trees/tree-bottom.png"));
			treesTop[2] = ImageIO.read(new File("assets/trees/tree-top.png"));
			trees[3] = ImageIO.read(new File("assets/trees/birch-bottom.png"));
			treesTop[3] = ImageIO.read(new File("assets/trees/birch-top.png"));
			trees[4] = trees[1];
			treesTop[4] = treesTop[1];
			trees[5] = trees[2];
			treesTop[5] =treesTop[2];

			backgroundTrees[0]=ImageIO.read(new File("assets/trees/fir-tree.png"));
			backgroundTrees[1]=ImageIO.read(new File("assets/trees/sitka-spruce.png"));
			backgroundTrees[2]=ImageIO.read(new File("assets/trees/willow.png"));
			
		}
		catch (IOException ioe) {
			System.out.println ("lolwtf it didn't work");
		}
		treeBottomAdjust[0]=419;
		treeBottomAdjust[1]=493;
		treeBottomAdjust[2]=614;
		treeBottomAdjust[3]=601;
		treeBottomAdjust[4]=493;
		treeBottomAdjust[5]=614;
		animator.start();
		randomize();
	}
	public boolean falling()
	{
		speed+=.1;
		falling+=speed;
		return 1500-falling<0;
	}
	public void run () {
		while (true) {
			if(screen==1)
				continue;
			try {
				Thread.sleep (10);
				if(isAnimating) {
					if(falling==0&&speed==-3)
					{
						if (activeTool == 0) {
							if (toolY <= -100) {
								direction*=-1;
							}
							if(progress>=300)
							{
								if(toolY==0)
								{
									progress=0;
									direction*=-1;
									falling();
									money+=5;
								}
							}
							else if (toolY >= progress) {
								soundClips[0].setFramePosition(0);
								soundClips[0].start();
								for(int x=0;x<Math.random()*100+toolY+100;x++)
								{
									dust.add(new SawDust(ogX[activeTool] - toolX+100, ogY[activeTool]-toolY,Math.random()*10-5,Math.random()*8-5));
								}
								direction*=-1;
								progress += 50;
								isAnimating=false;
								continue;
							}

							toolY += 10*direction;
						}
						else if (activeTool == 1) {
							if (toolX <= -100) { 
								direction=1; 
							} 
							if(progress>=300) {
								direction=-1;
								if(toolX==0) {
									progress=50;
									direction=-1;
									falling();
									money+=8;
								}
							}
							else if (toolX >= progress) {
								soundClips[1].setFramePosition(0);
								soundClips[1].start();
								for(int x=0;x<Math.random()*100+toolX;x++) {
									dust.add(new SawDust(ogX[activeTool] + toolX+100, ogY[activeTool]+ toolY+100,Math.random()*5-2.5,Math.random()*6-3));
								}
								direction=-1;
								progress += 50;
								isAnimating=false;
								continue;
							}
							toolX += 6*direction;
							toolY += 3*direction;
						}
						else if (activeTool == 2) {
							if (toolX <= -100) {
								direction*=-1;
							}
							if(progress>=800)
							{
								if(toolX==0)
								{
									progress=0;
									direction*=-1;
									falling();
									money+=15;
								}
							}
							else if (toolX >= progress) {
								soundClips[1].setFramePosition(0);
								soundClips[1].start();
								for(int x=0;x<Math.random()*100+toolX+100;x++)
								{
									dust.add(new SawDust(ogX[activeTool] - toolX+30, ogY[activeTool]+120,Math.random()*5+5,Math.random()*20-15));
								}
								direction*=-1;
								progress += 50;
								isAnimating=false;
								continue;
							}
							toolX -= 15*direction;
						}
						else if (activeTool == 3) {
							if(toolY==-3)
							{
								toolX-=20;
								if(toolX<=0)
								{
									toolX=0;
									toolY=0;
									falling();
									money+=25;
								}
							}
							else
							{
								if(toolX>=800)
								{
									toolY=-3;
									continue;
								}
								else if (toolY <= -1) {
									toolY = 0;
									isAnimating=false;
									direction=1;
									continue;
								}
								else if (toolY >= 100) {
									direction *= -1;
								}
								toolX += 1;
								toolY +=2*direction;
								for(int x=0;x<Math.random()*10+10;x++)
								{
									dust.add(new SawDust(ogX[activeTool] - toolX+30, ogY[activeTool]+120,Math.random()*10-5,Math.random()*8-5));
								}
							}

						}
						else if (activeTool == 4) {
							if (toolX <= -100) {
								direction=1;
							}
							if(progress>=300) {
								direction=-1;
								if(toolX==0) {
									progress=50;
									direction=-1;
									falling();
									money+=10;
								}
							}
							else if (toolX >= progress) {
								soundClips[4].setFramePosition(0);
								soundClips[4].start();
								for(int x=0;x<Math.random()*100+toolX;x++) {
									dust.add(new SawDust(ogX[activeTool] + toolX+100, ogY[activeTool]+ toolY+100,Math.random()*5-2.5,Math.random()*6-3));
								}
								direction=-1;
								progress += 150;
								isAnimating=false;
								continue;
							}
							toolX += 15*direction;
							toolY += 6 *direction;
						}
						else if (activeTool == 5) {
							if(toolY==-3) {
								toolX-=20;
								if(toolX<=0) {
									toolX=0;
									toolY=0;
									falling();
									money+=33;
								}
							}
							else {
								if(toolX>=800) {
									toolY=-3;
									continue;
								}
								else if (toolY <= -1)
								{
									toolY = 0;
									isAnimating=false;
									direction=1;
									continue;
								}
								else if (toolY >= 100) {
									direction *= -1;
								}
								toolX += 1;
								toolY +=2*direction;
								for(int x=0;x<Math.random()*100+20;x++) {
									dust.add(new SawDust(ogX[activeTool] - toolX+30, ogY[activeTool]+200,Math.random()*15-7.5,Math.random()*10-8));
								}
							}
						}
					}
					else
					{
						if(falling())
						{
							isAnimating=false;
							falling=0;
							speed=-3;
							if(activeTool==0&&money>=5)
							{
								nextScreen();
							}
							else if(activeTool==1&&money>=15)
							{
								nextScreen();
							}
							else if(activeTool==2&&money>=30)
							{
								nextScreen();
							}
							else if(activeTool==3&&money>=50)
							{
								nextScreen();
							}
							else if(activeTool==4&&money>=65)
							{
								nextScreen();
							}
							continue;
						}
					}
				}
				for(int x=0;x<dust.size();x++)
				{
					if(dust.get(x).update())
					{
						dust.remove(x);
					}
				}
				repaint ();
			}
			catch (InterruptedException e) {
				System.out.println ("wtf");
			}
		}
	}
	public void randomize()
	{
		randomTrees.clear();
		for(int x=0;x<2;x++)
		{
			randomTrees.add(new Tree((int)(Math.random()*500),(int)(Math.random()*100+500),(int)(Math.round(Math.random()*2))));
		}
		for(int x=0;x<2;x++)
		{
			randomTrees.add(new Tree((int)(Math.random()*500+1100),(int)(Math.random()*100+500),(int)(Math.round(Math.random()*2))));
		}
	}
	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		super.paint(g);
		if (screen == 0) {
			g.drawImage(background,0,0,null);
			for(int x=0;x<randomTrees.size();x++)
				g.drawImage(backgroundTrees[randomTrees.get(x).type], randomTrees.get(x).x, randomTrees.get(x).y, null);
			g.drawImage (trees[activeTool], treeX, treeY+treeBottomAdjust[activeTool], null);
			if (activeTool == 0) {
				g.drawImage(tools[activeTool], ogX[activeTool], ogY[activeTool] - (int) toolY, null);
			}
			else if (activeTool == 1) {
				g.drawImage(tools[activeTool], ogX[activeTool] + (int) toolX, ogY[activeTool] + (int) toolY, null);
			}
			else if (activeTool == 2) {
				g.drawImage(tools[activeTool], ogX[activeTool] - (int) toolX, ogY[activeTool], null);
			}
			else if (activeTool == 3) {
				g.drawImage(tools[activeTool], ogX[activeTool] - (int) toolX, ogY[activeTool] - toolY, null);
			}
			else if (activeTool == 4) {
				g.drawImage(tools[activeTool], ogX[activeTool] + (int) toolX, ogY[activeTool] + (int) toolY, null);
			}
			else if (activeTool == 5)
			{
				g.drawImage(tools[activeTool], ogX[activeTool] - (int) toolX, ogY[activeTool] - toolY, null);
			}
			g.drawImage (treesTop[activeTool], treeX, treeY+(int)falling, null);
			g.setColor(new Color(139,125,107));
			for(int x=0;x<dust.size();x++)
			{
				if(activeTool==5)
					g.setColor(new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));
				g.fillOval((int)dust.get(x).x,(int)dust.get(x).y,5,5);
			}
				
			g.setColor(Color.ORANGE);
			g.setFont(new Font("Courier New", Font.BOLD, 100));
			g.drawString("Gold: "+money,0,1080);
		}
	}

	public void cut() {
		if (isAnimating) {
			return;
		}
		isAnimating=true;
		if(activeTool==3)
		{
			soundClips[3].setFramePosition(0);
			soundClips[3].start();
		}
		else if(activeTool==5)
		{
			soundClips[2].setFramePosition(0);
			soundClips[2].start();
		}
		repaint();
	}

	public int getScreen() {
		return screen;
	}

	public void nextScreen() {
		if(screens[0].isShowing())
		{
			randomize();
			int temp=0;
			while(!toolshed.selected(temp))
				temp++;
			choosing=temp;
			toolshed.setGold(money);
		}
		((CardLayout) getLayout()).next(this);
		screen++;
		screen=screen%2;
		repaint();
	}
	public void change(PoseType pose)
	{
		if(toolshed.isShowing()){
			if (pose == PoseType.WAVE_IN)
			{
				choosing--;
				if(choosing==-1)
					choosing=4;
			}
			else if(pose == PoseType.WAVE_OUT) {
				choosing++;
				choosing=choosing%5;
			}
			if(!toolshed.selected(choosing))
			{
				change(pose);
			}
		}
	}
	public void select() {
		if(toolshed.isShowing()){
			if(activeTool<=4)
			{
				toolX=0;
				toolY=0;
				if(activeTool==0&&choosing==1)
				{
					l.setMode("slash");
					direction=-1;
					activeTool++;
					toolshed.bought(choosing);
					progress=-50;
					nextScreen();
					money-=5;
				}
				else if(activeTool==1&&choosing==0)
				{
					l.setMode("hack");
					direction=1;
					activeTool++;
					toolshed.bought(choosing);
					progress=0;
					nextScreen();
					money-=15;
				}
				else if(activeTool==2&&choosing==2)
				{
					l.setMode("punch");
					toolshed.bought(choosing);
					direction=-1;
					activeTool++;
					progress=0;
					nextScreen();
					money-=30;
				}
				else if(activeTool==3&&choosing==4)
				{
					l.setMode("slash");
					toolshed.bought(choosing);
					direction=-1;
					activeTool++;
					progress=0;
					nextScreen();
					money-=50;
				}
				else if(activeTool==4&&choosing==3)
				{
					l.setMode("punch");
					toolshed.bought(choosing);
					direction=-1;
					activeTool++;
					progress=50;
					nextScreen();
					money-=65;
				}
			}
		}
	}
}