package reservation;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainFrame extends Frame implements ActionListener,WindowListener,KeyListener {

	ReservationControl reservationControl;
	
	Panel panelNorth; 		//上部パネル
	Panel panelNorthSub1; 	//上部パネルの上
	Panel panelNorthSub2; 	//上部パネルの中央
	Panel panelNorthSub3; 	//上部パネルの下
	Panel panelMid;			//中央パネル
	Panel panelSouth;		//下部パネル

	Button buttonLog;		// ログイン ・ ログアウト ボタン
	Button buttonExplanation;	// 施設概要 説明ボタン
	Button buttonVacancy;	// 空き状況確認
	Button buttonReservation;	// 新規予約ボタン
	Button buttonConfirm;	// 予約の確認
	Button buttonCancel;	// 予約のキャンセルボタン

	ChoiceFacility choiceFacility;	// 施設選択用選択ボックス
	TextField tfYear, tfMonth, tfDay;	//年月日のテキストフィールド
	TextArea textMessage;	// 結果表示用メッセージ欄



	
	public MainFrame(ReservationControl reservationControl)
	    throws HeadlessException {
		this.reservationControl = reservationControl;

				buttonLog = new Button("ログイン");
				buttonExplanation = new Button(" 施設概要");
				buttonVacancy = new Button(" 空き状況確認");
				buttonReservation = new Button(" 新規予約");
				buttonConfirm = new Button(" 予約の確認");
				buttonCancel = new Button(" 予約のキャンセル");
        

				choiceFacility = new ChoiceFacility();
				tfYear = new TextField("",4);
				tfMonth = new TextField("",2);
				tfDay = new TextField("",2);


				setLayout( new BorderLayout());
		

				panelNorthSub1 = new Panel();
				panelNorthSub1.add(new Label("施設予約システム "));
				panelNorthSub1.add(buttonLog);


				panelNorthSub2 = new Panel();
				panelNorthSub2.add(new Label("施設 "));
				panelNorthSub2.add( choiceFacility);
				panelNorthSub2.add(new Label(" "));
				panelNorthSub2.add( buttonExplanation);


				panelNorthSub3 = new Panel();
				panelNorthSub3.add(new Label(" "));
				panelNorthSub3.add(tfYear);
				panelNorthSub3.add(new Label("年"));
				panelNorthSub3.add(tfMonth);
				panelNorthSub3.add(new Label("月"));
				panelNorthSub3.add(tfDay);
				panelNorthSub3.add(new Label("日 "));
				panelNorthSub3.add( buttonVacancy);
				

				panelNorth = new Panel(new BorderLayout());
				panelNorth.add(panelNorthSub1, BorderLayout.NORTH);
				panelNorth.add(panelNorthSub2, BorderLayout.CENTER);
				panelNorth.add(panelNorthSub3, BorderLayout.SOUTH);


				add(panelNorth,BorderLayout.NORTH);

				
				
				panelMid = new Panel();
				textMessage = new TextArea( 20, 80);
				textMessage.setEditable(false);
				panelMid.add(textMessage);

				
				add( panelMid,BorderLayout.CENTER);
				
				panelSouth = new Panel();
				panelSouth.add(buttonReservation);
				panelSouth.add(new Label(" "));
				panelSouth.add(buttonConfirm);
				panelSouth.add(new Label(" "));
				panelSouth.add(buttonCancel);

				add( panelSouth,BorderLayout.SOUTH);
				
				buttonLog.addActionListener(this);
				buttonExplanation.addActionListener(this);
				buttonVacancy.addActionListener(this);
				buttonReservation.addActionListener(this);
				buttonConfirm.addActionListener(this);
				buttonCancel.addActionListener(this);

				
		addWindowListener(this);
		addKeyListener(this);
		
	}

		

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		System.exit(0);
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String result = new String();
		textMessage.setText("");
		if ( e.getSource() == buttonVacancy){
			result = reservationControl.getReservationOn(choiceFacility.getSelectedItem(), tfYear.getText(), tfMonth.getText(), tfDay.getText());
		}
		else if (e.getSource() == buttonLog){
				result = reservationControl.loginLogout(this);
		}
		else if (e.getSource() == buttonReservation){
					result = reservationControl.makeReservation(this);
		}
		else if (e.getSource() == buttonExplanation){
        	result = reservationControl.getExplanation(this);	
        }
        else if (e.getSource() == buttonConfirm){
        	result = reservationControl.confirmReservation(this);
        }
        else if (e.getSource() == buttonCancel){
        	result = reservationControl.cancelReservation(this);
        }
		textMessage.setText(result);

	
	}
}

