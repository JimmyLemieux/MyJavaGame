package com.javagame.game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {

	//Don't know what this is yet
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 160;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE = 3;
	public static final String NAME = "Game";
	public int tickCount = 0; 
	
	private JFrame frame;
	
	public boolean running = false;
	
	
	//Make a bufferedImage along with a pixel array in which we can mutate
	private BufferedImage image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
	
	//Contains the pixels of the image, Update this array:Update the image
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	public Game() {
		
		
		//This is for making the jFrame
		setMinimumSize(new Dimension(WIDTH * SCALE,HEIGHT*SCALE));
		setMaximumSize(new Dimension(WIDTH * SCALE,HEIGHT*SCALE));
		setPreferredSize(new Dimension(WIDTH * SCALE,HEIGHT*SCALE));
		
		frame = new JFrame(NAME);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		frame.add(this, BorderLayout.CENTER);
		
		//Sets at the preferred size
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	
	//This method will start the thread
	
	public synchronized void start() {
		running = true;
		new Thread(this).start();
	}
	
	
	//This will stop the thread
	public synchronized void stop() {
		running = false;
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		//How many nano seconds are in the 60 We want to count
		double nsPerTick = 1000000000D / 60D;
		
		int ticks = 0;
		int frames = 0;
		
		long lastTimer = System.currentTimeMillis();
		
		//How many nano seconds have gone by so far
		double delta = 0;
		
		//On different systems, this will run differently
		// Make sure it is the same on all systems
		while(running) {
			long now = System.nanoTime(); 
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = true;
			while(delta >= 1) {
				ticks++;
				tick();
				delta -= 1;
				shouldRender = true;
			}
			
			//Want to limit the frames that are rendering
			
			//We don't really need this sleep function atm, but it is cool to have
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//If the boolean is set to true then it should begin the render
			if(shouldRender) {
				frames++;
				render();
			}
			
			
			
			if(System.currentTimeMillis() - lastTimer >= 1000) {
				//update here
				lastTimer += 1000; 
				System.out.println(frames + " , " + ticks);
				frames = 0;
				ticks = 0;
			}
			
		}
	}

	//Updating the logic of the game
	//Update the data of the game (pixels etc)
	public void tick() {
		for(int i = 0;i<pixels.length;i++) {
			//Manipulating the pixels based on the tick count
			pixels[i] = tickCount * i;
		}
		tickCount ++;
	}
	
	//Display the updated game
	//This will take the updated logic from the tick method and then display it
	public void render() {
		//The bufferstrategy allows you to organize the data that is put on the Canvas
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			//Reduces tearing
			//Clear and Run
			createBufferStrategy(3);
			return;
		}
		//Make the graphics
		Graphics g = bs.getDrawGraphics();
		
		//g.setColor(Color.BLACK);
		//g.fillRect(0, 0, WIDTH, HEIGHT);
		
		//Basically just drawing the image in the buffer strategy
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		
		g.dispose();
		bs.show();
		
		
	}
	
	//The main function that is being called all of the time
	public static void main(String[] args) {
		new Game().start();
	}
}
