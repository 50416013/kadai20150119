
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
				res ="�N�����ɂ͐������w�肵�Ă�������";
				return res;
			}
			res = facility + " �\���\n\n";

		
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
					res = "�\��͂���܂���";
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
				frame.buttonLog.setLabel(" ���O�C�� "); 
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
						frame.buttonLog.setLabel("���O�A�E�g");
						res = "";
					}else {
						
						res = "���O�C���ł��܂���.ID �p�X���[�h���Ⴂ�܂��B";				}
				} else {
					res = "���O�C���ł��܂���.ID �p�X���[�h���Ⴂ�܂�";
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


		////// �V�K�\��̓o�^
		@SuppressWarnings("unused")
		public String makeReservation(MainFrame frame){

			String res="";		//���ʂ�����ϐ�

			if ( flagLogin){ // ���O�C�����Ă����ꍇ
				//�V�K�\���ʍ쐬
				ReservationDialog rd = new ReservationDialog(frame);

				// �V�K�\���ʂ̗\����ɁC���C����ʂɐݒ肳��Ă���N������ݒ肷��
				rd.tfYear.setText(frame.tfYear.getText());
				rd.tfMonth.setText(frame.tfMonth.getText());
				rd.tfDay.setText(frame.tfDay.getText());

				// �V�K�\���ʂ�����
				rd.setVisible(true);
				if ( rd.canceled){
					return res;
				}
				
				try {
					//�V�K�\���ʂ���N�������擾
					String ryear_str = rd.tfYear.getText();
					String rmonth_str = rd.tfMonth.getText();
					String rday_str = rd.tfDay.getText();

					// �N�������������ǂ��������`�F�b�N���鏈��
					int ryear = Integer.parseInt( ryear_str);
					int rmonth = Integer.parseInt( rmonth_str);
					int rday = Integer.parseInt( rday_str);

					if ( checkReservationDate( ryear, rmonth, rday)){	// ���Ԃ̏����𖞂����Ă���ꍇ
						// �V�K�\���ʂ���{�ݖ��C�J�n�����C�I���������擾
						String facility = rd.choiceFacility.getSelectedItem();
						String st = rd.startHour.getSelectedItem()+":" + rd.startMinute.getSelectedItem() +":00";
						String et = rd.endHour.getSelectedItem() + ":" + rd.endMinute.getSelectedItem() +":00";

						if( st.equals(et)){		//�J�n�����ƏI��������������
							res = "�J�n�����ƏI�������������ł�";
						} else {

							try {
								// ���Ɠ����ꌅ��������C�O��0�����鏈��
								if (rmonth_str.length()==1) {
									rmonth_str = "0" + rmonth_str;
								}
								if ( rday_str.length()==1){
									rday_str = "0" + rday_str;
								}
								//(2) MySQL�̑���(SELECT���̎��s)
								String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
						
								MySQL mysql = new MySQL();
								ResultSet rs = mysql.selectReservation(rdate, facility);
							      // �������ʂɑ΂��ďd�Ȃ�`�F�b�N�̏���
							      boolean ng = false;	//�d�Ȃ�`�F�b�N�̌��ʂ̏����l�i�d�Ȃ��Ă��Ȃ�=false�j��ݒ�
								  // �擾�������R�[�h���ɑ΂��Ċm�F
							      while(rs.next()){
								  		//���R�[�h�̊J�n�����ƏI�����������ꂼ��start��end�ɐݒ�
								        String start = rs.getString("start_time");
								        String end = rs.getString("end_time");

								  if ( (start.compareTo(st)<0 && st.compareTo(end)<0) ||		//���R�[�h�̊J�n�������V�K�̊J�n�����@AND�@�V�K�̊J�n���������R�[�h�̏I������
								     	 (st.compareTo(start)<0 && start.compareTo(et)<0)){		//�V�K�̊J�n���������R�[�h�̊J�n�����@AND�@���R�[�h�̊J�n�������V�K�̊J�n����
									 	// �d���L��̏ꍇ�� ng ��true�ɐݒ�
								        	ng = true; break;
								        }
//									
							      }
								  /// �d�Ȃ�`�F�b�N�̏����@�����܂�  ///////

							      if (!ng){	//�d�Ȃ��Ă��Ȃ��ꍇ
				
							    	  int rs_int = mysql.setReservation(rdate, st, et, reservation_userid, facility);
							    	//  System.out.println("rs_int="+ rs_int);
							    	  res ="�\�񂳂�܂���";
							      } else {	//�d�Ȃ��Ă����ꍇ
							    	  res = "���ɂ���\��ɏd�Ȃ��Ă��܂�";
							      }
							}catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						res = "�\����������ł��D";
					}
				} catch(NumberFormatException e){
					res ="�\����ɂ͐������w�肵�Ă�������";
				}
			} else { // ���O�C�����Ă��Ȃ��ꍇ
				res = "���O�C�����Ă�������";
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
				res = reservation_userid + "�l�̗\���\n";
				
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
							res = "���q�l�̂��\��͂���܂���\n";
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}else{
				 res = "���O�C�����Ă�������" ;
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
							res = "�J�n�����ƏI�������������ł��B";
						}else{
							
					         // ���Ɠ����ꌅ��������C�O��0�����鏈��
					        try {
								if (rmonth_str.length()==1) {
								    rmonth_str = "0" + rmonth_str;
								}
								if ( rday_str.length()==1){
								    rday_str = "0" + rday_str;
								}
								//(2) MySQL�̑���(SELECT���̎��s)
								String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
								MySQL mysql = new MySQL();
								int rs_int = mysql.deleteReservation(rdate,reservation_userid,facility);
								res = "����ɗ\�񂪃L�����Z������܂����B";
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}else {
						res = "�w�肳�ꂽ���͖����ł��B";
					}
				} catch (NumberFormatException e) {
					res = "�������w�肵�Ă�������";
				}
			}else{
				res ="���O�C�����Ă�������";		
			}
			return res;
		}
		


}
