import javax.swing.*;

public class GameThread extends Thread {
	private GamePanel gamePanel = null;
	private GameGroundPanel groundPanel = null;
	private Thread textTh[] = null;
	
	private int end = 2; // 게임이 종료될때 게임의 성공여부
	private int maxTextCount = 4; // 화면에 표시할 수 있는 최대 카운트
	private int textChange[] = new int [10];
	private int delayFirst = 2; // 새로운 단어가 빠르면 delayFirst초 만에 나온다
	private int runNumber = 0; // run이 아직 안 불러졌을때 0
	
	public GameThread(GamePanel gamePanel,GameGroundPanel groundPanel,Thread[] textTh) {
		this.gamePanel = gamePanel;
		this.groundPanel = groundPanel;
		this.textTh = textTh;
	}

	public void setTextSpeed(double n) {
		SetSpeedThread setSpeedTh = new SetSpeedThread(5000, textTh, n);
		setSpeedTh.start(); // 스레드 시작
		
	}
	public void allEnd() { // 모든 Thread 종료
		gamePanel.textMoveThreadEnd();
		gamePanel.itemThreadEnd();
		gamePanel.diamondThreadEnd();
	}
	public void setEnd(int i) { end = i; }
	// end 변경(성공 여부 변경)
	@Override
	public void run() {
		if(runNumber == 0) { // run이 처음 불러졌을 때
			gamePanel.startGamePanel(); // 게임 시작
			runNumber++; // runNumber 추가
			JTextField j = gamePanel.getInput(); //input가져오기
			j.requestFocus(); // j에 focus 두기
		}
		while(true) {
			int i=0;
			for(i=0;i<maxTextCount;i++) {
				if(gamePanel.getTextChange(i) == 0) // text[] 중 사용하지 않는 text 찾기
					break;
			}
			if(i<maxTextCount)
				gamePanel.textMake(i); // text 만들기
			
			int delay = (int)(Math.random()*1)+delayFirst; // delayFirst~delayFirst+4초 사이 중 랜덤하게 선택
			delay *= 1000; //(1000ms = 1초)
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				allEnd(); // Thread 모두 종료
				gamePanel.gameEndPanel(end); // 성공여부(end)에 따른 화면 변환
				return; // run()의 리턴은 스레드 종료
			} 
		}
	}
	class SetSpeedThread extends Thread { // 일시적으로 speed 변화(곡괭이 item)
		private int timer = 0; // 속도를 줄이는 시간
		private Thread textTh[] = null;
		private double n; // 속도 n배
		public SetSpeedThread(int timer, Thread textTh[], double n) {
			this.timer = timer;
			this.textTh = textTh;
			this.n = n;
		}
		public void run() {
			int i=0;
			for(i=0;i<maxTextCount;i++) // 현재 실행 중인 textTh 중지
				if(gamePanel.getTextChange(i) != 0) // 화면에 있을 경우 
					((TextMoveThread) textTh[i]).textSpped(n); // 현재 존재하는 text 스레드 속도 변화(n배)
			gamePanel.delaySet(n,"Speed Down"); // 앞으로 나올 스레드 속도 조절, Speed Down 출력
			try {
				Thread.sleep(timer); // timer작동 (아이템 작동하는 timer)
				this.interrupt(); // thread 종료
				Thread.sleep(1000); // 위에 interrupt를 실행시키기 위해 만들어 놓은 것 
			} catch (InterruptedException e) {
				for(i=0;i<maxTextCount;i++) // 현재 실행 중인 textTh
					if(gamePanel.getTextChange(i) != 0) 
						((TextMoveThread) textTh[i]).textSpped(1/(n*n)); // 현재 존재하는 text 스레드 원래 속도로 변환
				gamePanel.delaySet(1/n, " "); // 앞으로 나올 스레드 속도 조절
				return;
			}
		}
	}
}
