package 블록깨기;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class BreakBrick extends JFrame {
	JButton start;       //시작버튼
	JButton suspend;     //일시중지버튼
	JButton cont;        //계속버튼
	JButton end;         //종료버튼

	Timer t;             //그래픽 객체의 움직임을 관장할 타이머 

	ArrayList<Bar> list1 = new ArrayList<Bar>();    //패들객체 
	ArrayList<Ball>list2=new ArrayList<Ball>();     //공객체 
	ArrayList<Block>blocks=new ArrayList<Block>();  //블럭객체
	ArrayList<Block>toRemove=new ArrayList<Block>(); //블럭객체 지울목록
	final int WIN_WIDTH=800;         //화면 크기 
	final int WIN_HEIGHT=700;
	final int STEPS=15;              //패들이 움직일 거리 
	final int BALL_SIZE=30; 
	final int BAR_WIDTH=100;         //패들 크기 
	final int BAR_HEIGHT=10;
	final int BLOCK_WIDTH=50;        //블럭 크기
	final int BLOCK_HEIGHT=30;
	final int BLOCK_APERTURE=40;     //블럭간 사이간격
    
	// 버튼 토글을 위한 비트 연산에 사용될 상수들  
	private final int START = 1;
	private final int SUSPEND = 2;
	private final int CONT = 4;
	private final int END = 8;
	
	int sec=5;    //타이머 주기 
	int gamePanelWidth;    //실제 게임이 이루어질 패널의 크기 
	int gamePanelHeight;
	int score=0;         //점수
	int count;
	
	boolean isSuspend=false;  //일시중지 버튼이 눌렸는지 확인할 boolean 변수

	public BreakBrick() {
		MainPanel game= new MainPanel(); //게임이 이루어질 패널
		JPanel control = new JPanel();   //버튼 조작 패널

		start=new JButton("시작");            //버튼
		suspend= new JButton("일시중지");
		cont=new JButton("계속");
		end= new JButton("종료");

		control.add(start);                //버튼 조작 패널에 버튼을 달아줌 
		control.add(suspend);
		control.add(cont);
		control.add(end);



		this.add(BorderLayout.CENTER,game);            
		this.add(BorderLayout.SOUTH,control);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(WIN_WIDTH,WIN_HEIGHT);
		this.setVisible(true);

		t = new Timer(sec,new MyTimer());           

		ButtonListener b=new ButtonListener();   //버튼 리스너
		start.addActionListener(b);              //버튼에 버튼리스너를 달아줌 
		suspend.addActionListener(b);
		cont.addActionListener(b);
		end.addActionListener(b);

		buttonToggler(START);               //시작 버튼 활성화           

		game.addKeyListener(game);          //메인 패널에 키리스너를 달아줌 

		list2.add(new Ball(400,350,BALL_SIZE,BALL_SIZE));    //공 리스트 
 
		list1.add(new Bar(350, 600, BAR_WIDTH, BAR_HEIGHT));  //패들 리스트 

		//블럭 리스트 (블럭값에 따라 바뀜)
		for(int j=0;j+BLOCK_HEIGHT+BLOCK_APERTURE<300;j+=BLOCK_HEIGHT+BLOCK_APERTURE) 
			for(int i=0;i+BLOCK_APERTURE+BLOCK_WIDTH<WIN_WIDTH;i+=BLOCK_APERTURE+BLOCK_WIDTH) {
				blocks.add(new Block(i,j,BLOCK_WIDTH,BLOCK_HEIGHT));
				}
	}
		
	
	
	//버튼의 활성,비활성화를 위한 루틴
	private void buttonToggler(int flags) {
		if ((flags & START) != 0)
			start.setEnabled(true);
		else
			start.setEnabled(false);
		if ((flags & SUSPEND) != 0)
			suspend.setEnabled(true);
		else
			suspend.setEnabled(false);
		if ((flags & CONT) != 0)
			cont.setEnabled(true);
		else
			cont.setEnabled(false);
		if ((flags & END) != 0)
			end.setEnabled(true);
		else
			end.setEnabled(false);
	}
	
	public static void main(String[] args) {
		new BreakBrick();
		
	}
	private class MyTimer implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			repaint();					// 포함하는 프레임이 다시 그려지게 함.
		}								// 이에 의해 포함된 DrawPanel의 paintComponent 메소드가 실행됨
	}


	private class MainPanel extends JPanel implements KeyListener{
		public void paintComponent(Graphics g) {
			int w = this.getWidth();				// 현재의 패널 넓이 획득
			int h= this.getHeight();
			gamePanelWidth=this.getWidth();// 현재의 패널 높이 획득
			gamePanelHeight=this.getHeight();
			g.setColor(Color.white);				
			g.fillRect(0,0,w,h);					// 노란색으로 칠해 줌
			g.setColor(Color.black);
			g.drawLine(0, 0, w, 0);
			{
				for(Block bl : blocks) {       
					bl.draw(g);           //메인 패널에 블럭을 그려줌 
				
					for (Ball o :list2) 
						//공과 블럭사이의 충돌 논리 구현 
					{ 
						if(o.pX >= bl.pX && o.pX <= bl.pX+BLOCK_WIDTH && o.pY >= bl.pY && o.pY <= bl.pY+BLOCK_HEIGHT) 
							//공의 x좌표>벽돌의 x좌표 
							//공의 x좌표 <벽돌의 x좌표 + 벽돌의 길이 
							//공의 y좌표 > 벽돌의 y좌표
							//공의 y좌표 < 벽돌의 y좌표 + 벽돌의 높이 
						{
							o.moveY = o.moveY * -1;
							bl.breakBrick();   //공과 블록이 충돌함 
						}

						if(o.pX+BALL_SIZE >= bl.pX && o.pX+BALL_SIZE <= bl.pX+BLOCK_WIDTH && o.pY+BALL_SIZE >= bl.pY && o.pY+BALL_SIZE <= bl.pY+BLOCK_HEIGHT) 
						{
							o.moveY = o.moveY * -1;
							bl.breakBrick();
							
						}
					}
					if(score==count) {
						Clear c = new Clear();
						c.draw(g);
						buttonToggler(END);
						t.stop();
					}

				}
			
			


				for (Bar pi : list1) 	{			
					pi.draw(g);	      //메인 패널에 패들을 그려줌 
					for (Ball o :list2) {
						o.move(o.moveX, o.moveY);
						//공과 패들,공과 메인 패널 사이의 충돌 논리구현 

						if (o.pX >w-BALL_SIZE || o.pX <0)
							o.moveX = o.moveX * -1;
						if (o.pY < 0)
							o.moveY = o.moveY * -1;
						if(o.pY>h-BALL_SIZE) {
							GameOver e = new GameOver();
							e.draw(g);
							t.stop();  
							buttonToggler(END);
						}                                    //공이 바닥에 떨어지면 게임 오버 
						else  if(o.pY>=h-BALL_SIZE-BAR_HEIGHT) {
							if(o.pX > pi.pX && o.pX<pi.pX + BAR_WIDTH)
								if(Math.abs(o.moveY)<=2)
									o.moveY= o.moveY*-2;
								else {
									o.moveY=o.moveY*-1;
								}
						}

						o.draw(g);	//메인 패널에 공을 그려줌 

					}

				}
				if(isSuspend){   //일시정지 버튼이 눌렸을때 
					Suspend s= new Suspend();
					s.draw(g);
					t.stop();
				}
				setFocusable(true);	   //패널에서의 조작을 가능하게함 				
				requestFocus();
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {
			//패들조작 리스너 구현 
			// TODO Auto-generated method stub
			int keycode = e.getKeyCode();
			if(keycode==KeyEvent.VK_RIGHT) {      
				for (Bar icon : list1)
					if(icon.pX<=gamePanelWidth-BAR_WIDTH)
						icon.pX+=STEPS;
			}
			else if(keycode==KeyEvent.VK_LEFT) {
				for(Bar icon:list1)
					if(icon.pX>=0)
						icon.pX-=STEPS;
			}

		}

		@Override
		public void keyReleased(KeyEvent e) {}

	}
	private class ButtonListener implements ActionListener{
     //버튼 리스너 구현 
		@Override
		public void actionPerformed(ActionEvent e){
			// TODO Auto-generated method stub
			if(e.getSource()== start) {
				buttonToggler(SUSPEND+END);	
				t.start();
			}
			else if(e.getSource()==suspend) {
				isSuspend=true;
				buttonToggler(CONT+END);
				}

			else if(e.getSource()==cont) {
				isSuspend=false;
				t.restart();
				buttonToggler(SUSPEND+END);	
			}
			else if(e.getSource()==end) {
				buttonToggler(START);
				t.stop();
				System.exit(0);
			}

		}

	}
	//미구현 클래스 
	private class Clear{    //게임 클리어시 CONGRATULATION!! 을 화면에 출력 
		public void draw(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g.setColor(Color.green);
			g2d.setFont(new Font("나눔고딕코딩 보통", Font.BOLD, 90));
			g2d.drawString("CONGRATULATION!!",200,350);
	}
	}

	private class Suspend{   //게임 일시중지시 PAUSED 를 화면에 출력 
		public void draw(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g.setColor(Color.red);
			g2d.setFont(new Font("나눔고딕코딩 보통", Font.BOLD, 90));
			g2d.drawString("PAUSED",200,350);
		}
		
	}
	private class GameOver{     //게임오버시 GAME OVER를 화면에 출력 
		public void draw(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g.setColor(Color.red);
			g2d.setFont(new Font("나눔고딕코딩 보통", Font.BOLD, 90));
			g2d.drawString("GAME OVER",100,350);
		}
	}
	private class Bar {   //패들의 좌표값과 이동값설정 및 그리기 
		int pX;
		int pY;
		int moveX=1;
		int moveY=1;
		Color color = Color.blue;

		public Bar(int x,int y,int width,int height) {
			pX=x;
			pY=y;
		}
		public int getpX() {
			return pX;
		}
		public void setpX(int pX) {
			this.pX = pX;
		}
		public int getpY() {
			return pY;
		}
		public void setpY(int pY) {
			this.pY = pY;
		}
		public void move(int x,int y) {
			pX+=x;
			pY+=y;
		}
		public void draw (Graphics g) {
			g.setColor(color);
			g.fillRect(pX, gamePanelHeight-BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
		}
	}
	private class Ball{   //공의 좌표값과 이동값설정및 그리기 
		int pX;
		int pY;
		int moveX=1;
		int moveY=2;
		Color color = new Color(255,204,000);

		public Ball(int x,int y,int width,int height) {
			pX=x;
			pY=y;
		}
		public int getpX() {
			return pX;
		}
		public void setpX(int pX) {
			this.pX = pX;
		}
		public int getpY() {
			return pY;
		}
		public void setpY(int pY) {
			this.pY = pY;
		}
		public void move(int x,int y) {
			pX+=x;
			pY+=y;
		}
		public void draw(Graphics g) {
			g.setColor(color);
			g.fillOval(pX, pY, BALL_SIZE, BALL_SIZE);
		}

	}
	private class Block{ //블럭의 좌표값 설정 및 그리기 
		int pX;
		int pY;
		int moveX=1;
		int moveY=1;
		int status=1;
		Color color = new Color(255,102,102);

		public Block(int x,int y,int width,int height) {
			pX=x+BLOCK_APERTURE;
			pY=y+BLOCK_APERTURE;
		}
		public int getpX() {
			return pX;
		}
		public void setpX(int pX) {
			this.pX = pX;
		}
		public int getpY() {
			return pY;
		}
		public void setpY(int pY) {
			this.pY = pY;
		}
		public void move(int x,int y) {
			pX+=x;
			pY+=y;
		}
		public void draw (Graphics g) {
			g.setColor(color);
			g.fillRect(pX, pY, BLOCK_WIDTH, BLOCK_HEIGHT);
		}
		public void breakBrick(){  // 공과 부딫힌 블록을 리스트에서 지운다
				for(Block block : blocks) {
				    {
					  if(block==this)
						  toRemove.add(block);
				    }
				    blocks.removeAll(toRemove);
					}
			}
		}
	}
