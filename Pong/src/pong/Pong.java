/*
 * 8
 * https://www.youtube.com/watch?v=1wD2CdFlDaE
 * 
 */

package pong;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Pong implements ActionListener, KeyListener {

	public static Pong pong;

	public int width = 700, height = 700;

	public Renderer renderer;

	public Paddle player1;
	public Paddle player2;

	public Ball ball;

	public boolean bot = false, selectingDifficulty;
	public boolean w, s, up, down;

	public int gameStatus = 0, scoreLimit = 1, playerWon; // 0 = Stopped, 1= Paused, 2 = Playing, 3 = Over;

	public int botDifficulty, botMoves, botCooldown = 0;
	
	public Random random;
	
	public JFrame jframe;
	
	
	public Pong() { // constructor alt shift s C

		Timer timer = new Timer(20, this);
		random = new Random();
		JFrame jframe = new JFrame("Pong");

		renderer = new Renderer();

		jframe.setSize(width + 15, height);
		jframe.setVisible(true);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.add(renderer);
		jframe.addKeyListener(this);
		timer.start();

	}
	
	public void start() {
		gameStatus = 2;
		player1 = new Paddle(this, 1);
		player2 = new Paddle(this, 2);
		ball = new Ball(this);
	}

	public void update() {

		if(player1.score>=scoreLimit) {
			playerWon = 1;
			gameStatus = 3;
		}
		if(player2.score>=scoreLimit) {
			playerWon = 2;
			gameStatus = 3;
		}		
		

		if (w == true) {
			player1.move(true);
		}
		if (s == true) {
			player1.move(false);
		}

		if(!bot) {
			
			if (up == true) {
				player2.move(true);
			}
			if (down == true) {
				player2.move(false);
			}
			
		} else {
			if(botCooldown>0) {
				botCooldown--;
				if(botCooldown == 0) {
					botMoves = 0;
				}
			}
			
			if(botCooldown < 10) {
				if(player2.y + player2.height/2 < ball.y) {
					player2.move(false);
					botMoves++;
				}
			
				if(player2.y + player2.height/2 > ball.y) {
					player2.move(true);
					botMoves++;
				}
				
				if(botDifficulty == 0) {
					botCooldown = 14;
				}
				if(botDifficulty == 1) {
					botCooldown = 12;
				}
				if(botDifficulty == 2) {
					botCooldown = 10;
				}
			}
		}
		
		
		ball.update(player1, player2);
	}

	public void render(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if(gameStatus == 0) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Times New Roman", 1, 80));
			g.drawString("Pong", width/2 - 100, height/3 + 50);
			
			if(!selectingDifficulty) {				
				g.setFont(new Font("Times New Roman", 1, 30));
				
				g.drawString("Press Space to Play", width/2 - 140, height/2 + 50);
				g.drawString("Press Shift to Play with Bot", width/2 - 140, height/2 + 100);
				g.drawString("Score Limit: " + scoreLimit + "", width/2-140, height/2 + 150);
			}
		}
		
		
		if(selectingDifficulty) {
			g.setFont(new Font("Times New Roman", 1, 30));
			
			String string = botDifficulty == 0 ? "Easy" : (botDifficulty == 1 ? "Medium" : "Hard");
			
			g.drawString("Bot Difficulty: " + string, width/2-140, height/2 + 100);
			g.drawString("Press Space to Play", width/2 - 140, height/2 + 150);
			
		}
		
		if(gameStatus == 1) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Times New Roman", 1, 120));
			g.drawString("Paused", width/2 - 197, height/2);
		}
		
		if (gameStatus == 2 || gameStatus == 1) {
			g.setColor(Color.WHITE);
			g.setStroke(new BasicStroke(2f));
			
			g.drawLine(width / 2, height, width / 2, 0);
			g.drawLine(width, 0, 0, 0);
			
			g.setFont(new Font("Times New Roman", 1, 50));
			g.drawString(String.valueOf(player1.score), width/2 - 60, 50);
			g.drawString(String.valueOf(player2.score), width/2 + 40, 50);
			
			player1.render(g);
			player2.render(g);
			ball.render(g);
			
			
		}
		
		if(gameStatus == 3){
			g.setColor(Color.WHITE);
			g.setFont(new Font("Times New Roman", 1, 60));
			g.drawString("Pong", width/2 - 100, height/3 - 50);
			
			
			if(bot && playerWon == 2) {
			
				g.drawString("The bot wins!", width/2 - 200, height/2 - 50);
			} else {
				g.drawString("Player " + playerWon + " wins!", width/2 - 200, height/2 - 30);
			}
			
			g.setFont(new Font("Times New Roman", 1, 30));
			g.drawString("Press Space to Play Again", width/2 - 200, height/2 + 100);
			g.drawString("Press ESC for menu", width/2 - 200, height/2 + 150);
			
		}
	}




	public void actionPerformed(ActionEvent arg0) {
		if (gameStatus == 2) {
			update();
		}
		renderer.repaint();
	}

	public static void main(String[] args) {

		pong = new Pong();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		int id = arg0.getKeyCode();

		if (id == KeyEvent.VK_W) {
			w = true;
		} else if (id == KeyEvent.VK_S) {
			s = true;
		} else if (id == KeyEvent.VK_UP) {
			up = true;
		} else if (id == KeyEvent.VK_DOWN) {
			down = true;
		} else if (id == KeyEvent.VK_RIGHT) {
			if(selectingDifficulty){
			if(botDifficulty<2) {
				botDifficulty++;
			} else {
				botDifficulty = 0;
			}
			} else if (gameStatus==0) {
				scoreLimit++;
			}
		} else if (id==KeyEvent.VK_LEFT) {
			if(selectingDifficulty) {
				if(botDifficulty>0) {
					botDifficulty--;
				} else {
					botDifficulty = 2;
				}
			} else if (gameStatus == 0 &&  scoreLimit>1) {
			scoreLimit--;
			}
			} else if(id==KeyEvent.VK_ESCAPE && (gameStatus == 2 || gameStatus == 3)) {
				gameStatus = 0;
			} else if(id == KeyEvent.VK_SHIFT && gameStatus == 0) {
			bot = true;
			selectingDifficulty = true;
		} else if(id == KeyEvent.VK_SPACE) {
			if(gameStatus == 0 || gameStatus == 3) { // 0 Stop, 1 Paused, 2 Playing, 3 Over
				if(!selectingDifficulty) {
					bot = false;
					
				} else {
					selectingDifficulty = false;
				}
				start();
			} else if(gameStatus == 1) { //If Paused Play
				gameStatus = 2;
			} else if(gameStatus == 2) { // If playing pause
				gameStatus = 1;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getKeyCode();

		if (id == KeyEvent.VK_W) {
			w = false;
		}
		if (id == KeyEvent.VK_S) {
			s = false;
		}
		if (id == KeyEvent.VK_UP) {
			up = false;
		}
		if (id == KeyEvent.VK_DOWN) {
			down = false;
		}
		


	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
