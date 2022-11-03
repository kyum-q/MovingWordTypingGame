import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class EditPanel extends JPanel{
	private TextSource textSource = null;
	private JTextField edit = new JTextField(15);
	private Font font = new Font("Ÿ����_�ֹ��� B", Font.BOLD, 15);
	private JButton addButton = new JButton("add");
	private JLabel please1 = new JLabel("������ �ִ� ����");
	private JLabel please2 = new JLabel("������ �˷���!");
	
	public EditPanel(TextSource textSource) {
		this.textSource = textSource;
		setBackground(Color.LIGHT_GRAY);
		setLayout(new FlowLayout());
		please1.setFont(font);
		please2.setFont(font);
		add(please1);
		add(please2);
		add(edit);
		add(addButton);

		addButton.addActionListener(new ActionListener() { // ��ư�� action listener ���
			@Override
			public void actionPerformed(ActionEvent e) {
				String inWord = edit.getText();  // edit�� �����ִ� �ܾ� �˾Ƴ���
				textSource.writeFile(inWord); // txt ���Ͽ� inWord �߰�
			}
		});
	}
}
