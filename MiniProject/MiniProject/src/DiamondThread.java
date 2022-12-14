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
	public void getTedScore() { // 키보드를 눌러서 점수 획득하는 함수
		Container c = label.getParent();
		bar.setBackground(Color.WHITE);
		bar.setOpaque(true);
		bar.setLocation(100,  200);
		bar.setSize(300, 20); 
		c.add(bar);
		c.addKeyListener(new KeyAdapter() { // 키를 눌렀을 때
			public void keyPressed(KeyEvent e) 
			{ bar.fill(); } // bar 채우기
		});
		c.requestFocus(); // c에 focus 두기
		ConsumerThread consumerTh = new ConsumerThread(bar, scorePanel, gamePanel);
		consumerTh.start(); // consumerTh 시작
		
		interrupt(); // diamondThread 종료
	}
	@Override
	public void run() { // 스레드 코드. 이주소에서 실행을 시작하도록  TCB에 기록된다.
		while(true) {
			Container c = label.getParent(); // label이 붙어 있는 컨테이너
			int x = label.getX();
			int y = label.getY();
			label.setLocation(x,y+10);
				
			if(y >= c.getHeight()) {  
				gamePanel.decreaseDiamond();
				interrupt(); //InterruptedException을 보내는 함수. 중단 시키고 싶어
			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				c.remove(label); // 컨테이너에서 label 컴포넌트 떼어내기
				c.repaint(); // 컨테이너에게 다시 그릴 것을 지시
				return; // run()의 리턴은 스레드 종료
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
					sleep(3000); // 3초간 작동하고 종료
					interrupt(); 
				} catch (InterruptedException e) { 
					scorePanel.increase(bar.getBarSize()); // 키 누름 * 2point만큼 점수 증가
					Container c = bar.getParent(); // bar 삭제를 위한 코드
					c.remove(bar); // bar삭제
					c.repaint(); // 다시 그리기
					
					JTextField j = gamePanel.getInput();
					j.requestFocus(); // j에 focus 두기
					return; 
				}
			}
		}
	}
	public class MyLabel extends JLabel {
		private int barSize = 0; // 바의 크기
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
			barSize += 2; // 한번 클릭했을 때 2씩 증가
			repaint(); // 바 다시 그리기
			notify(); 
		}
		public int getBarSize() {
			return barSize;
		}
	}
}