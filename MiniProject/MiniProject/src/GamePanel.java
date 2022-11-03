import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePanel extends JPanel{ 
	private GameThread gameTh = null; // ������ �ð��� ������ ������ ��Ÿ�����ϴ� thread
	private Thread textTh [] = null; // �Ϲ� ������ �����̱� ���� ���� thread
	private Thread diamondTh = null; // diamond�� �����̱� ���� ���� thread
	private Thread itemTh [] = new ItemThread[3]; // item�� �����̱� ���� ���� thread
	
	private JTextField input = new JTextField(40);
	private JLabel text [] = new JLabel[10]; // �Ϲ� ���� label
	private JLabel diamond = null;  // ���̾Ƹ�� label
	private JLabel item[] = new JLabel [3]; // item
	
	private int textChange[] = new int [10]; // text ������ Ƚ��(text ��ȭ Ƚ��)
	private int textCount = 0; // ȭ�鿡 ��Ÿ�� �Ϲ� ���� ��
	private int maxTextCount = 4; // ȭ�鿡 ǥ���� �� �ִ� �ִ� ��
	private int diamondCount = 0; // ȭ�鿡 ��Ÿ�� ���̾Ƹ�� ��
	private int itemCount[] = new int [3]; // ȭ�鿡 ��Ÿ�� ������ ��
	
	private int delay = 600; // ������ �������� �ӵ�
	private int gameStart = 0; // game�� ����Ǵ��� �˱� ���� ��� (0:���� X || 1:���� O)
	
	ImageIcon level1Icon = new ImageIcon("level1-1.png");
	ImageIcon diamondIcon = new ImageIcon("diamond.png");
	ImageIcon [] level2Icon = {
			new ImageIcon("level2-1.png"), 
			new ImageIcon("level2-2.png"),
	};
	ImageIcon [] itemIcon = {
			new ImageIcon("bread.png"), 
			new ImageIcon("bomb.png"), 
			new ImageIcon("pickax.png")
	};
	
	private ScorePanel scorePanel = null;
	private EditPanel editPanel = null;
	private GameGroundPanel groundPanel = null;
	private GameInitPanel startPanel = null;
	private GameFrame gameFrame = null;
	
	public GamePanel(ScorePanel scorePanel, EditPanel editPanel, GameGroundPanel groundPanel,Thread[] textTh, GameFrame gameFrame) {
		this.scorePanel = scorePanel;
		this.editPanel = editPanel;
		this.groundPanel = groundPanel;
		this.textTh = textTh;
		this.gameFrame = gameFrame;
		
		startPanel = new GameInitPanel(0,"������ ã�� ���ڰ� �ɰž�!"); // �ʱ�ȭ��
		
		gameStart = 0; //  0 : ���� �ʱ�ȭ (���� �� �ܰ�) -> 1 : ���� ���� (���� �ߺ� ���� ����)
		
		// imgSizeSet(img) = img ������ ����
		level1Icon = imgSizeSet(level1Icon); 
		diamondIcon = imgSizeSet(diamondIcon);
		for(int i=0;i<2;i++)
			level2Icon[i] = imgSizeSet(level2Icon[i]);
		for(int i=0;i<3;i++)
			itemIcon[i] = imgSizeSet(itemIcon[i]);
		
		setLayout(new BorderLayout());
		add(startPanel,BorderLayout.CENTER); 
		add(new InputPanel(),BorderLayout.SOUTH);
		input.addActionListener(new ActionListener() { // input â�� �ܾ �Է��ϰ� ���͸� ���� ��
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField t = (JTextField)e.getSource(); // e�� ������ ��ü �˾Ƴ���
				String inWord = t.getText(); // t�� �ִ� text �˾Ƴ���
				if(inWord.equals("play") && gameStart == 0) { // ���� ���� ���� play��� �Է½� ���� ����
					gameFrame.startGameTh();
				}
				
				if(inWord.equals("!") && scorePanel.getBombCount()>0) { // ��ź item�� ������ ���� �� '!'�� �Է����� ��
					scorePanel.bombDecrease(); // ��ź ���� ���̱�
					((GameThread) gameTh).allEnd(); // �������� Thread ��� ����			
				}
				
				if(diamondCount != 0) // diamond�� ȭ�鿡 ���� �� ��
					if(diamond.getText().equals(inWord)) { // diamond�� ������ ���
						diamondCount--; // ���̾Ƹ�� ���� ���̱�
						textMoveThreadEnd(); // �������� textMoveThread ��� ����
						itemThreadEnd(); // �������� item ��� ����
						((DiamondThread) diamondTh).getTedScore(); // Ű���� �Է����� ���� ȹ�� â �����
					}
				
				for(int i=0;i<3;i++) {
					if(itemCount[i]!=0) { // ���� �������� ȭ�鿡 ���� ���
						if(item[i].getText().equals(inWord)) { // ���߱� ����(����)
							switch(i) {
							// 0 = ��item(����++) | 1 = ��ź item(���� ��� ����) | 2 = ���item(�ӵ� ���߱�)
							case 0: scorePanel.lifeiIncrease(); break;
							case 1: scorePanel.bombIncrease(); break;
							case 2: ((GameThread) gameTh).setTextSpeed(2); break;
							}
							itemCount[i]--; // �ش� item ���� ���̱�
							itemTh[i].interrupt(); // �ش� item Thread ����
						}
					}
				}
				
				for(int i=0;i<maxTextCount;i++) { // ȭ�鿡 �ִ� text�� ��� �˻��ϱ� ���� 0~textCount-1���� ��� ��
					if(textChange[i] != 0) { // ���� ȭ�鿡 ������ ���
						if(text[i].getText().equals(inWord)) { // ���߱� ����(����)
							textChange[i]--; // �������� 1 ����
							if(textChange[i]==0) { // �Է��ؾ��ϴ� �ܾ� ���� ��� �Է����� ��(level1:1�� level2:2��)
								Icon icon = text[i].getIcon(); // text��  icon �˾Ƴ���
								if(icon.equals(level2Icon[0]) || icon.equals(level2Icon[1])) // level 2�ϰ�� true
									scorePanel.increase(20); // ���� �ø��� (20��)
								else
									scorePanel.increase(10); // ���� �ø��� (10��)
								textTh[i].interrupt(); // textTh[i]�����忡�� InterruptedException�� ������ �Լ�. �ߴ� ��Ű�� �;�
							}
							else
								groundPanel.addNewWord(text[i]); // ���ο� �ܾ� ���
						}
					}
				}
				t.setText(""); // input â �����
			}
		});
	}
	public void setGameThread(GameThread gameTh) { this.gameTh = gameTh; }  // gameThread ����
	public void decreaseDiamond() { diamondCount--; } // diamond �� �ϳ� ���̱�
	public int getTextChange(int i) { return textChange[i]; } // �Է��ؾ��ϴ� �ܾ� �� �˾Ƴ���
	public void setTextChange(int i, int n) { this.textChange[i] = n; } // textChange ����
	public void textCountDecrease() { this.textCount--; } // textCount ����
	public JTextField getInput() { return input; }	// input ���� (input�� focus�� ���߱� ���� ����)
	public int getGameStart() { return gameStart; } // ���� ������ ���������� �˾Ƴ���
	public void setGame(int delay, int maxTextCount) { // ���� ����
		this.delay = delay; // �ӵ� ����
		this.maxTextCount = maxTextCount; // ȭ�鿡 ��Ÿ�� �� �ִ� �ִ� ���� ����
	}
	public void startGamePanel(){ 
		add(groundPanel,BorderLayout.CENTER);
		startPanel.setVisible(false); // ���� �ʱ�ȭ�� �����
		groundPanel.setVisible(true); // ����ȭ�� ��Ÿ����
		gameStart = 1; // ���� ���������Ƿ� 1�� ����
	}
	public void gameEndPanel(int i) { // ���� ����
		String s = (i==1)?"GAME CLEAR":"GAME OVER";
		// i�� 1�̸� game ���� | 1�� �ƴϸ� game ����
		startPanel = new GameInitPanel(i,s); // ���� ȭ�� ����
		add(startPanel,BorderLayout.CENTER);
		groundPanel.setVisible(false); // ����ȭ�� �����
		gameStart = 0; // ���� ���������Ƿ� 0���� ����
	}
	public ImageIcon imgSizeSet(ImageIcon icon) {
		Image img = icon.getImage();
		Image changeImg = img.getScaledInstance(20,20,Image.SCALE_SMOOTH);
		ImageIcon changeIcon = new ImageIcon(changeImg);
		return changeIcon;
	}
	public void delaySet(double n,String s) { // �ӵ� ��ȭ 
		delay = (int)(delay*n);	// delay n��
		scorePanel.changeSpeed(s); 
		}
	public void textMake(int i) {
		int randomFrequency = (int)(Math.random()*99)+1; // ������ �����ϴ� �󵵼� ����
		
		if(randomFrequency >= 90 && diamondCount==0) { // 10%�� ���̾Ƹ�� ���� && diamond�� �ѹ��� �ϳ��� ���� ����
			diamondCount++; 
			diamond = new JLabel("",diamondIcon,JLabel.CENTER);
			groundPanel.textAdd(diamond); // diamond �ܾ� �߰�
			
			diamondTh = new DiamondThread(diamond, scorePanel, this, (int)(delay/2));
			diamondTh.start(); // ������ ����
		}
		else if(randomFrequency >= 75) { // 15%�� ������ ����
			// 0 = ��item(����++) | 1 = ��ź item(���� ��� ����) | 2 = ���item(�ӵ� ���߱�)
			int randomitem = (int)(Math.random()*3); // item ����(� �������ΰ�)
			if(scorePanel.checkLifeCount() && randomitem == 0) // �� �������� ����� �ִ��϶� �ȳ����� ����
				randomitem =(int)(Math.random()*2)+1; // ���� �ƴϴ� �ٸ� �� ����
			if(itemCount[randomitem] == 0) { // �� item�� ȭ�鿡 �ϳ��� ���� �� ����(ȭ�鿡 item�� �����ϴ��� Ȯ��)
				item[randomitem] = new JLabel("",itemIcon[randomitem], JLabel.CENTER);
				groundPanel.textAdd(item[randomitem]); // item �ܾ� �߰�
				itemCount[randomitem]++;
				
				itemTh[randomitem] = new ItemThread(item[randomitem], delay);
				itemTh[randomitem].start(); // ������ ����
			}
		}
		else {
			if(randomFrequency >= 20) {  // 55%�� 1�� �Է��ϸ� ȹ���� �� �ִ� ����
				textChange[i] = 1; // �ܾ� �Է� �� 1��
				text[i] = new JLabel("",level1Icon,JLabel.CENTER);
			}
			else  { // 20%�� 2�� �Է��ϸ� ȹ���� �� �ִ� ����
				textChange[i] = 2; // �ܾ� �Է� �� 2��
				text[i] = new JLabel("",level2Icon[(int)(Math.random()*2)],JLabel.CENTER);
			}
			groundPanel.textAdd(text[i]); // ���� �ܾ� �߰�
			textCount++; // text�� �����Ǿ����Ƿ� count����
		
			textTh[i] = new TextMoveThread(text[i], scorePanel, this, i, delay); // ������ ����
			textTh[i].start(); // ������ ����(JVM���� �����ٸ��ص� �˴ϴ�)
		}
	}
	public void textMoveThreadEnd () {
		for(int i=0;i<maxTextCount;i++) { // ���� ���� ���� textTh ����
			if(getTextChange(i) != 0) { 
				textChange[i] = 0; 
				textTh[i].interrupt(); // ���� �����ϴ� text ������ ����
			}
		}
	}
	public void itemThreadEnd() {
		for(int i=0;i<3;i++) { // ���� ���� ���� textTh ����
			if(itemCount[i] != 0) { 
				itemCount[i] = 0; 
				itemTh[i].interrupt(); // ���� �����ϴ� item ������ ����
			}
		}
	}
	public void diamondThreadEnd() {
		if(diamondCount != 0) {
			diamondCount = 0;
			diamondTh.interrupt(); // ���� �����ϴ� diamond ������ ����
		}
	}
	
	class InputPanel extends JPanel { // �ܾ� �Է� â
		public InputPanel() {
			setLayout(new FlowLayout());
			setBackground(Color.lightGray);
			add(input);
		}
	}
	class GameInitPanel extends JPanel {
		private ImageIcon [] icon = {
				new ImageIcon("game.png"),
				new ImageIcon("success.png"),
				new ImageIcon("fail.png"),
		};
		private Image img = null;
		private Font font = new Font("Ÿ����_�ֹ��� B", Font.BOLD, 30);
		
		private JLabel initText = new JLabel("");
		
		public GameInitPanel(int select, String s) {
			img = icon[select].getImage();
			initText.setText(s);
			setLayout(new BorderLayout());
			initGame(); // �ʱ� ȭ�� ����
		}
		public void initGame() { // �ʱ�ȭ��
			initText.setHorizontalAlignment(JLabel.CENTER);
			initText.setFont(font); // font ����
			initText.setForeground(Color.WHITE);
			add(initText);
		}
		@Override
		public void paintComponent(Graphics g) { //call back �Լ�
			super.paintComponent(g); // �г��� ��� ����� -> ������ ĥ�Ѵ�
			g.drawImage(img, 0,0, this.getWidth(), this.getHeight(), null);
		}
	}
}