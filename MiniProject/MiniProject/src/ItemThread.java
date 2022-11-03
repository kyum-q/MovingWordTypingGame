import java.awt.*;
import javax.swing.JLabel;

public class ItemThread extends Thread {
	private JLabel item = null;
	private int delay = 0; 
	
	public ItemThread(JLabel item, int delay) {
		this.item = item;
		this.delay = delay;
	}
	@Override
	public void run() { // ������ �ڵ�. ���ּҿ��� ������ �����ϵ���  TCB�� ��ϵȴ�.
		while(true) {
			Container c = item.getParent(); // label�� �پ� �ִ� �����̳�
			int x = item.getX();
			int y = item.getY();
			item.setLocation(x,y+10);
				
			if(y >= c.getHeight()) {
				interrupt(); // thread ����
			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				c.remove(item); //�����̳ʿ��� label ������Ʈ �����
				c.repaint(); //�����̳ʿ��� �ٽ� �׸� ���� ����
				
				return; //run()�� ������ ������ ����
			}
		}
	}
}
