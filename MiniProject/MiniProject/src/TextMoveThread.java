import java.awt.Container;
import java.time.Clock;
import javax.swing.JLabel;

class TextMoveThread extends Thread {
	private ScorePanel scorePanel = null;
	private GamePanel gamePanel = null;
	private int delay = 600;
	private int increase = 10;
	private int i = 0; // text[i]�� i
	private JLabel label = null;
	
	public TextMoveThread (JLabel label, ScorePanel scorePanel,GamePanel gamePanel, int i, int delay) {
		String name  = "text[" + i + "]";
		this.setName(name);
		this.i = i;
		this.delay = delay;
		this.label = label;
		this.scorePanel = scorePanel;
		this.gamePanel = gamePanel;
	}
	@Override
	public void run() { // ������ �ڵ�. ���ּҿ��� ������ �����ϵ���  TCB�� ��ϵȴ�.
		while(true) {
			Container c = label.getParent(); // label�� �پ� �ִ� �����̳�
			// label�� x,y ��ġ �˾Ƴ���
			int x = label.getX();
			int y = label.getY();
			label.setLocation(x,y+increase); // y�� increase��ŭ �̵� ��Ű��
				
			if(y >= c.getHeight()) { // label�� �� �Ʒ����� ������ ���
				scorePanel.lifeDecrease(); // life ����
				interrupt(); // thread ����
			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				gamePanel.textCountDecrease(); // text �� ����
				gamePanel.setTextChange(i,0); // �Է��ؾ��ϴ� �ܾ�� ����
				c.remove(label); //�����̳ʿ��� label ������Ʈ �����
				c.repaint(); //�����̳ʿ��� �ٽ� �׸� ���� ����
				return; //run()�� ������ ������ ����
			}
		}
	}
	public void textSpped(double n) { delay = (int)(delay*n); } // delay n��
}