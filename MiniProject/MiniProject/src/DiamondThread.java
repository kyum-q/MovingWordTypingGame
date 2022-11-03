import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DiamondThread extends Thread {
	private ScorePanel scorePanel = null;
	private GamePanel gamePanel = null;
	private JLabel label = null;
	private MyLabel bar = new MyLabel(100);
	private int delay;
	
	public DiamondThread (JLabel label, ScorePanel scorePanel, GamePanel gamePanel, int delay) {
		String name  = "diamond";
		this.delay = delay;
		this.setName(name);
		this.label = label;
		this.scorePanel = scorePanel;
		this.gamePanel = gamePanel;
	}
	public void getTedScore() { // Ű���带 ������ ���� ȹ���ϴ� �Լ�
		Container c = label.getParent();
		bar.setBackground(Color.WHITE);
		bar.setOpaque(true);
		bar.setLocation(100,  200);
		bar.setSize(300, 20); 
		c.add(bar);
		c.addKeyListener(new KeyAdapter() { // Ű�� ������ ��
			public void keyPressed(KeyEvent e) 
			{ bar.fill(); } // bar ä���
		});
		c.requestFocus(); // c�� focus �α�
		ConsumerThread consumerTh = new ConsumerThread(bar, scorePanel, gamePanel);
		consumerTh.start(); // consumerTh ����
		
		interrupt(); // diamondThread ����
	}
	@Override
	public void run() { // ������ �ڵ�. ���ּҿ��� ������ �����ϵ���  TCB�� ��ϵȴ�.
		while(true) {
			Container c = label.getParent(); // label�� �پ� �ִ� �����̳�
			int x = label.getX();
			int y = label.getY();
			label.setLocation(x,y+10);
				
			if(y >= c.getHeight()) {  
				gamePanel.decreaseDiamond();
				interrupt(); //InterruptedException�� ������ �Լ�. �ߴ� ��Ű�� �;�
			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				c.remove(label); // �����̳ʿ��� label ������Ʈ �����
				c.repaint(); // �����̳ʿ��� �ٽ� �׸� ���� ����
				return; // run()�� ������ ������ ����
			}
		}
	}
	public class ConsumerThread extends Thread {
		private MyLabel bar;
		private ScorePanel scorePanel = null;
		private GamePanel gamePanel = null;
		ConsumerThread(MyLabel bar, ScorePanel scorePanel,GamePanel gamePanel) {
			this.bar = bar;
			this.scorePanel = scorePanel;
			this.gamePanel = gamePanel;
		}
		public void run() {
			while(true) {
				try {
					sleep(3000); // 3�ʰ� �۵��ϰ� ����
					interrupt(); 
				} catch (InterruptedException e) { 
					scorePanel.increase(bar.getBarSize()); // Ű ���� * 2point��ŭ ���� ����
					Container c = bar.getParent(); // bar ������ ���� �ڵ�
					c.remove(bar); // bar����
					c.repaint(); // �ٽ� �׸���
					
					JTextField j = gamePanel.getInput();
					j.requestFocus(); // j�� focus �α�
					return; 
				}
			}
		}
	}
	public class MyLabel extends JLabel {
		private int barSize = 0; // ���� ũ��
		private int maxBarSize;
			
		MyLabel(int maxBarSize) { 
			this.maxBarSize = maxBarSize;
		}
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.YELLOW);
			int width = (int)(((double)(this.getWidth()))
					/maxBarSize*barSize); 
			if(width==0) return; 
			g.fillRect(0, 0, width, this.getHeight());
		}
		
		synchronized void fill() {
			if(barSize == maxBarSize) {
				return;
			}
			barSize += 2; // �ѹ� Ŭ������ �� 2�� ����
			repaint(); // �� �ٽ� �׸���
			notify(); 
		}
		public int getBarSize() {
			return barSize;
		}
	}
}