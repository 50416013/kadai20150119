
package reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class ReservationControl {

	String reservation_userid;
	private boolean flagLogin;

	ReservationControl(){
		flagLogin = false;
	}

	
		public String getReservationOn( String facility, String ryear_str, String rmonth_str, String rday_str){
			String res = "";
			
			try {
				int ryear = Integer.parseInt( ryear_str);
				int rmonth = Integer.parseInt( rmonth_str);
				int rday = Integer.parseInt( rday_str);
			} catch(NumberFormatException e){
				res ="年月日には数字を指定してください";
				return res;
			}
			res = facility + " 予約状況\n\n";

		
			if (rmonth_str.length()==1) {
				rmonth_str = "0" + rmonth_str;
			}
			if ( rday_str.length()==1){
				rday_str = "0" + rday_str;
			}
			
			String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;

			
			//connectDB();
			MySQL mysql = new MySQL();

			
			try {
				
				ResultSet rs = mysql.getReservation(rdate, facility);
				boolean exist = false;
				while(rs.next()){
					String start = rs.getString("start_time");
					String end = rs.getString("end_time");
					res += " " + start + " -- " + end + "\n";
					exist = true;
				}

				if ( !exist){ 
					res = "予約はありません";
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return res;
		}
		


		public String loginLogout( MainFrame frame){
			String res=""; 
			if ( flagLogin){ 
				flagLogin = false;
				frame.buttonLog.setLabel(" ログイン "); 
			} else {
				LoginDialog ld = new LoginDialog(frame);
				ld.setVisible(true);
				ld.setModalityType(LoginDialog.ModalityType.APPLICATION_MODAL);
				if ( ld.canceled){
					return "";
				}

				reservation_userid = ld.tfUserID.getText();
				String password = ld.tfPassword.getText();
	
			
			try { 
				MySQL mysql = new MySQL();
				ResultSet rs = mysql.getLogin(reservation_userid); 
				if (rs.next()){
					rs.getString("password");
					String password_from_db = rs.getString("password");
					if ( password_from_db.equals(password)){ 
						flagLogin = true;
						frame.buttonLog.setLabel("ログアウト");
						res = "";
					}else {
						
						res = "ログインできません.ID パスワードが違います。";				}
				} else {
					res = "ログインできません.ID パスワードが違います";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			}
			return res;
			
		}


		private boolean checkReservationDate( int y, int m, int d){
			
			Calendar dateR = Calendar.getInstance();
			dateR.set( y, m-1, d);	

			Calendar date1 = Calendar.getInstance();
			date1.add(Calendar.DATE, 1);

			Calendar date2 = Calendar.getInstance();
			date2.add(Calendar.DATE, 90);

			if ( dateR.after(date1) && dateR.before(date2)){
				return true;
			}
			return false;
		}


		////// 新規予約の登録
		@SuppressWarnings("unused")
		public String makeReservation(MainFrame frame){

			String res="";		//結果を入れる変数

			if ( flagLogin){ // ログインしていた場合
				//新規予約画面作成
				ReservationDialog rd = new ReservationDialog(frame);

				// 新規予約画面の予約日に，メイン画面に設定されている年月日を設定する
				rd.tfYear.setText(frame.tfYear.getText());
				rd.tfMonth.setText(frame.tfMonth.getText());
				rd.tfDay.setText(frame.tfDay.getText());

				// 新規予約画面を可視化
				rd.setVisible(true);
				if ( rd.canceled){
					return res;
				}
				
				try {
					//新規予約画面から年月日を取得
					String ryear_str = rd.tfYear.getText();
					String rmonth_str = rd.tfMonth.getText();
					String rday_str = rd.tfDay.getText();

					// 年月日が数字かどうかををチェックする処理
					int ryear = Integer.parseInt( ryear_str);
					int rmonth = Integer.parseInt( rmonth_str);
					int rday = Integer.parseInt( rday_str);

					if ( checkReservationDate( ryear, rmonth, rday)){	// 期間の条件を満たしている場合
						// 新規予約画面から施設名，開始時刻，終了時刻を取得
						String facility = rd.choiceFacility.getSelectedItem();
						String st = rd.startHour.getSelectedItem()+":" + rd.startMinute.getSelectedItem() +":00";
						String et = rd.endHour.getSelectedItem() + ":" + rd.endMinute.getSelectedItem() +":00";

						if( st.equals(et)){		//開始時刻と終了時刻が等しい
							res = "開始時刻と終了時刻が同じです";
						} else {

							try {
								// 月と日が一桁だったら，前に0をつける処理
								if (rmonth_str.length()==1) {
									rmonth_str = "0" + rmonth_str;
								}
								if ( rday_str.length()==1){
									rday_str = "0" + rday_str;
								}
								//(2) MySQLの操作(SELECT文の実行)
								String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
						
								MySQL mysql = new MySQL();
								ResultSet rs = mysql.selectReservation(rdate, facility);
							      // 検索結果に対して重なりチェックの処理
							      boolean ng = false;	//重なりチェックの結果の初期値（重なっていない=false）を設定
								  // 取得したレコード一つ一つに対して確認
							      while(rs.next()){
								  		//レコードの開始時刻と終了時刻をそれぞれstartとendに設定
								        String start = rs.getString("start_time");
								        String end = rs.getString("end_time");

								  if ( (start.compareTo(st)<0 && st.compareTo(end)<0) ||		//レコードの開始時刻＜新規の開始時刻　AND　新規の開始時刻＜レコードの終了時刻
								     	 (st.compareTo(start)<0 && start.compareTo(et)<0)){		//新規の開始時刻＜レコードの開始時刻　AND　レコードの開始時刻＜新規の開始時刻
									 	// 重複有りの場合に ng をtrueに設定
								        	ng = true; break;
								        }
//									
							      }
								  /// 重なりチェックの処理　ここまで  ///////

							      if (!ng){	//重なっていない場合
				
							    	  int rs_int = mysql.setReservation(rdate, st, et, reservation_userid, facility);
							    	//  System.out.println("rs_int="+ rs_int);
							    	  res ="予約されました";
							      } else {	//重なっていた場合
							    	  res = "既にある予約に重なっています";
							      }
							}catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						res = "予約日が無効です．";
					}
				} catch(NumberFormatException e){
					res ="予約日には数字を指定してください";
				}
			} else { // ログインしていない場合
				res = "ログインしてください";
			}
			return res;
		}

		public String getExplanation(MainFrame frame){
			String res = "";
			String fac = frame.choiceFacility.getSelectedItem();
			MySQL mysql = new MySQL();
			ResultSet rs = mysql.getExplanation(fac);
		
				try {
					while(rs.next()){
						String fe = rs.getString("explanation");
						res = fe ;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			return res;	
			
		}
		public String confirmReservation(MainFrame frame){
			String res = "";
			MySQL mysql = new MySQL();
			if (flagLogin){
				res = reservation_userid + "様の予約状況\n";
				
					try {
						ResultSet rs = mysql.confirmReservation(reservation_userid);
						boolean exist = false;
						while(rs.next()){
							String fn = rs.getString("facility_name");
							String date = rs.getString("date");
							String start = rs.getString("start_time");
							String end = rs.getString("end_time");
							res += " " + fn + " " + date + " " + start + " -- " + end + "\n";
							exist = true;
						}
						if(!exist){
							res = "お客様のご予約はありません\n";
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}else{
				 res = "ログインしてください" ;
			}
					return res;
		}
		public String cancelReservation(MainFrame frame){
			String res = "";
			if(flagLogin){
				CancelDialog cd = new CancelDialog(frame);
				cd.tfYear.setText(frame.tfYear.getText());
				cd.tfMonth.setText(frame.tfMonth.getText());
				cd.tfDay.setText(frame.tfDay.getText());
				cd.setVisible(true);
				if(cd.canceled){
					return res;
				}
				
				try {
					String ryear_str = cd.tfYear.getText();
					String rmonth_str = cd.tfMonth.getText();
					String rday_str = cd.tfDay.getText();
					
					int ryear = Integer.parseInt(ryear_str);
					int rmonth = Integer.parseInt(rmonth_str);
					int rday = Integer.parseInt(rday_str);
					
					if(checkReservationDate(ryear,rmonth,rday)){
						String facility = cd.choiceFacility.getSelectedItem();
						String st = cd.startHour.getSelectedItem()+":"+cd.startMinute.getSelectedItem()+":00";
						String et = cd.endHour.getSelectedItem()+":"+cd.endMinute.getSelectedItem()+":00";
						
						if(st.equals(et)){
							res = "開始時刻と終了時刻が同じです。";
						}else{
							
					         // 月と日が一桁だったら，前に0をつける処理
					        try {
								if (rmonth_str.length()==1) {
								    rmonth_str = "0" + rmonth_str;
								}
								if ( rday_str.length()==1){
								    rday_str = "0" + rday_str;
								}
								//(2) MySQLの操作(SELECT文の実行)
								String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
								MySQL mysql = new MySQL();
								int rs_int = mysql.deleteReservation(rdate,reservation_userid,facility);
								res = "正常に予約がキャンセルされました。";
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}else {
						res = "指定された情報は無効です。";
					}
				} catch (NumberFormatException e) {
					res = "数字を指定してください";
				}
			}else{
				res ="ログインしてください";		
			}
			return res;
		}
		


}
