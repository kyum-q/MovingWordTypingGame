import java.awt.event.*;
import javax.swing.*;

public class GameSetFrame extends JFrame {
	private SetPanel setPanel = new SetPanel();
	private TextSource textSource = null;
	private GamePanel gamePanel = null;
	public GameSetFrame(TextSource textSource, GamePanel gamePanel) {
		setTitle("게임 설정");
		setSize(250,300);
		this.textSource = textSource;
		this.gamePanel = gamePanel;
		setContentPane(setPanel);
		setResizable(false); // 창크기 고정 (수정 불가)
		setVisible(true);
	}
	
	class SetPanel extends JPanel {
		private int [] delay = { 600, 300, 150 };
		private int [] maxCount = { 4, 6, 8 }; 
		private String[] level = {"level1", "level2", "level3"};
		private String[] language = { "English", "한글"};
		private JComboBox levelBox = new JComboBox(level);
		private JComboBox languageBox = new JComboBox(language);
		private JButton select = new JButton("적용");
		public SetPanel() {
			setLayout(null);
			
			JLabel levelset = new JLabel("레벨");
			levelset.setSize(50,20);
			levelset.setLocation(100,10);
			add(levelset);
			levelBox.setSize(100,30); 
			levelBox.setLocation(70,40);
			add(levelBox); // level 선택 콤포박스
			JLabel languageSet = new JLabel("타이핑 언어");
			languageSet.setSize(100,20);
			languageSet.setLocation(80,90);
			add(languageSet);
			languageBox.setSize(100,30);
			languageBox.setLocation(70,120);
			add(languageBox); // 언어 선택 콤보박스
			
			select.setSize(60,30);
			select.setLocation(85, 170);
			add(select); 
			select.addActionListener(new SelectAction()); // 선택후 버튼 누르면 게임 설정
		}
		private class SelectAction implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				String selectLevel = levelBox.getSelectedItem().toString(); 
				// levelBox에서 선택된 값 String으로 가져오기
				String selectLanguage = languageBox.getSelectedItem().toString(); 
				// languageBox에서 선택된 값 String으로 가져오기
				
				setLevel(selectLevel); // level 설정
				setLanguage(selectLanguage); // 언어 설정
			}
		}
		public void setLevel(String selectLevel) {
			for(int i=0;i<3;i++) 
			if(selectLevel.equals(level[i]))
				gamePanel.setGame(delay[i],maxCount[i]); 
			// 단계에 따른 속도와 화면에 나올 수 있는 최대 단어 수 설정
		}
		public void setLanguage(String selectLan) {
			if(selectLan.equals("English")) // 영어를 선택했을 경우
				textSource.setLanguage(0);
			else // 한글을 선택했을 경우
				textSource.setLanguage(1);
		}
	}	
}
