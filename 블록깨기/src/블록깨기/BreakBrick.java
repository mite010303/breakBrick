package ��ϱ���;

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
	JButton start;       //���۹�ư
	JButton suspend;     //�Ͻ�������ư
	JButton cont;        //��ӹ�ư
	JButton end;         //�����ư

	Timer t;             //�׷��� ��ü�� �������� ������ Ÿ�̸� 

	ArrayList<Bar> list1 = new ArrayList<Bar>();    //�е鰴ü 
	ArrayList<Ball>list2=new ArrayList<Ball>();     //����ü 
	ArrayList<Block>blocks=new ArrayList<Block>();  //����ü
	ArrayList<Block>toRemove=new ArrayList<Block>(); //����ü ������
	final int WIN_WIDTH=800;         //ȭ�� ũ�� 
	final int WIN_HEIGHT=700;
	final int STEPS=15;              //�е��� ������ �Ÿ� 
	final int BALL_SIZE=30; 
	final int BAR_WIDTH=100;         //�е� ũ�� 
	final int BAR_HEIGHT=10;
	final int BLOCK_WIDTH=50;        //�� ũ��
	final int BLOCK_HEIGHT=30;
	final int BLOCK_APERTURE=40;     //���� ���̰���
    
	// ��ư ����� ���� ��Ʈ ���꿡 ���� �����  
	private final int START = 1;
	private final int SUSPEND = 2;
	private final int CONT = 4;
	private final int END = 8;
	
	int sec=5;    //Ÿ�̸� �ֱ� 
	int gamePanelWidth;    //���� ������ �̷���� �г��� ũ�� 
	int gamePanelHeight;
	int score=0;         //����
	int count;
	
	boolean isSuspend=false;  //�Ͻ����� ��ư�� ���ȴ��� Ȯ���� boolean ����

	public BreakBrick() {
		MainPanel game= new MainPanel(); //������ �̷���� �г�
		JPanel control = new JPanel();   //��ư ���� �г�

		start=new JButton("����");            //��ư
		suspend= new JButton("�Ͻ�����");
		cont=new JButton("���");
		end= new JButton("����");

		control.add(start);                //��ư ���� �гο� ��ư�� �޾��� 
		control.add(suspend);
		control.add(cont);
		control.add(end);



		this.add(BorderLayout.CENTER,game);            
		this.add(BorderLayout.SOUTH,control);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(WIN_WIDTH,WIN_HEIGHT);
		this.setVisible(true);

		t = new Timer(sec,new MyTimer());           

		ButtonListener b=new ButtonListener();   //��ư ������
		start.addActionListener(b);              //��ư�� ��ư�����ʸ� �޾��� 
		suspend.addActionListener(b);
		cont.addActionListener(b);
		end.addActionListener(b);

		buttonToggler(START);               //���� ��ư Ȱ��ȭ           

		game.addKeyListener(game);          //���� �гο� Ű�����ʸ� �޾��� 

		list2.add(new Ball(400,350,BALL_SIZE,BALL_SIZE));    //�� ����Ʈ 
 
		list1.add(new Bar(350, 600, BAR_WIDTH, BAR_HEIGHT));  //�е� ����Ʈ 

		//�� ����Ʈ (������ ���� �ٲ�)
		for(int j=0;j+BLOCK_HEIGHT+BLOCK_APERTURE<300;j+=BLOCK_HEIGHT+BLOCK_APERTURE) 
			for(int i=0;i+BLOCK_APERTURE+BLOCK_WIDTH<WIN_WIDTH;i+=BLOCK_APERTURE+BLOCK_WIDTH) {
				blocks.add(new Block(i,j,BLOCK_WIDTH,BLOCK_HEIGHT));
				}
	}
		
	
	
	//��ư�� Ȱ��,��Ȱ��ȭ�� ���� ��ƾ
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
			repaint();					// �����ϴ� �������� �ٽ� �׷����� ��.
		}								// �̿� ���� ���Ե� DrawPanel�� paintComponent �޼ҵ尡 �����
	}


	private class MainPanel extends JPanel implements KeyListener{
		public void paintComponent(Graphics g) {
			int w = this.getWidth();				// ������ �г� ���� ȹ��
			int h= this.getHeight();
			gamePanelWidth=this.getWidth();// ������ �г� ���� ȹ��
			gamePanelHeight=this.getHeight();
			g.setColor(Color.white);				
			g.fillRect(0,0,w,h);					// ��������� ĥ�� ��
			g.setColor(Color.black);
			g.drawLine(0, 0, w, 0);
			{
				for(Block bl : blocks) {       
					bl.draw(g);           //���� �гο� ���� �׷��� 
				
					for (Ball o :list2) 
						//���� �������� �浹 �� ���� 
					{ 
						if(o.pX >= bl.pX && o.pX <= bl.pX+BLOCK_WIDTH && o.pY >= bl.pY && o.pY <= bl.pY+BLOCK_HEIGHT) 
							//���� x��ǥ>������ x��ǥ 
							//���� x��ǥ <������ x��ǥ + ������ ���� 
							//���� y��ǥ > ������ y��ǥ
							//���� y��ǥ < ������ y��ǥ + ������ ���� 
						{
							o.moveY = o.moveY * -1;
							bl.breakBrick();   //���� ����� �浹�� 
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
					pi.draw(g);	      //���� �гο� �е��� �׷��� 
					for (Ball o :list2) {
						o.move(o.moveX, o.moveY);
						//���� �е�,���� ���� �г� ������ �浹 ������ 

						if (o.pX >w-BALL_SIZE || o.pX <0)
							o.moveX = o.moveX * -1;
						if (o.pY < 0)
							o.moveY = o.moveY * -1;
						if(o.pY>h-BALL_SIZE) {
							GameOver e = new GameOver();
							e.draw(g);
							t.stop();  
							buttonToggler(END);
						}                                    //���� �ٴڿ� �������� ���� ���� 
						else  if(o.pY>=h-BALL_SIZE-BAR_HEIGHT) {
							if(o.pX > pi.pX && o.pX<pi.pX + BAR_WIDTH)
								if(Math.abs(o.moveY)<=2)
									o.moveY= o.moveY*-2;
								else {
									o.moveY=o.moveY*-1;
								}
						}

						o.draw(g);	//���� �гο� ���� �׷��� 

					}

				}
				if(isSuspend){   //�Ͻ����� ��ư�� �������� 
					Suspend s= new Suspend();
					s.draw(g);
					t.stop();
				}
				setFocusable(true);	   //�гο����� ������ �����ϰ��� 				
				requestFocus();
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {
			//�е����� ������ ���� 
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
     //��ư ������ ���� 
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
	//�̱��� Ŭ���� 
	private class Clear{    //���� Ŭ����� CONGRATULATION!! �� ȭ�鿡 ��� 
		public void draw(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g.setColor(Color.green);
			g2d.setFont(new Font("��������ڵ� ����", Font.BOLD, 90));
			g2d.drawString("CONGRATULATION!!",200,350);
	}
	}

	private class Suspend{   //���� �Ͻ������� PAUSED �� ȭ�鿡 ��� 
		public void draw(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g.setColor(Color.red);
			g2d.setFont(new Font("��������ڵ� ����", Font.BOLD, 90));
			g2d.drawString("PAUSED",200,350);
		}
		
	}
	private class GameOver{     //���ӿ����� GAME OVER�� ȭ�鿡 ��� 
		public void draw(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g.setColor(Color.red);
			g2d.setFont(new Font("��������ڵ� ����", Font.BOLD, 90));
			g2d.drawString("GAME OVER",100,350);
		}
	}
	private class Bar {   //�е��� ��ǥ���� �̵������� �� �׸��� 
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
	private class Ball{   //���� ��ǥ���� �̵��������� �׸��� 
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
	private class Block{ //���� ��ǥ�� ���� �� �׸��� 
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
		public void breakBrick(){  // ���� �΋H�� ����� ����Ʈ���� �����
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
