
package reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {

	// JDBC�h���C�o�̓o�^
    String driver;
    // �f�[�^�x�[�X�̎w��
    String server, dbname, url, user, password;
    Connection con;
    Statement stmt;
    ResultSet rs;
    
	public MySQL() {
		this.driver  = "org.gjt.mm.mysql.Driver";
		this.server  = "j11000.sangi01.net";      // MySQL�T�[�o ( IP �܂��� �z�X�g�� );
		this.dbname  = "50416013";         // �f�[�^�x�[�X��;
		this.url = "jdbc:mysql://" + server + "/" + dbname + "?useUnicode=true&characterEncoding=UTF-8";
		this.user = "50416013";         // �f�[�^�x�[�X�쐬���[�U��;
		this.password  = "50416013";     // �f�[�^�x�[�X�쐬���[�p�X���[�h;
		
		try {
			this.con = DriverManager.getConnection(url, user, password);
			this.stmt = con.createStatement ();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Class.forName (driver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void close(){
		try {
			rs.close();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
public ResultSet getReservation(String rdate, String facility){
		
		String sql = "SELECT * FROM reservation WHERE date ='" + rdate + "' AND facility_name = '"+ facility +"' ORDER BY start_time;";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery (sql);  //try catch�ň͂�
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
		
	}
public ResultSet getLogin(String reservation_userid){
	String sql = "SELECT * FROM user WHERE user_id ='" + reservation_userid + "';";
	try {
		rs = stmt.executeQuery(sql);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	return rs;
	
}
public ResultSet selectReservation(String rdate, String facility){
	
	String sql = "SELECT * FROM reservation WHERE facility_name ='" + facility +
		      "' AND date = '" + rdate + "' ;";
		     
	ResultSet rs = null;
	try {
		rs = stmt.executeQuery(sql);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return rs;
}

    public int setReservation(String rdate, String st, String et, String reservation_userid, String facility){
	     int rs_int = 0;
	     String sql = "INSERT INTO reservation (date,start_time,end_time,user_id,facility_name) VALUES ( '"
	       + rdate +"', '"  + st +"','" + et + "','" + reservation_userid +"','" + facility +"');";
	    	  try {
				rs_int = stmt.executeUpdate(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return rs_int;
            }
    public ResultSet getExplanation(String facility_name){
    	String sql = "SELECT * FROM facility WHERE facility_name = '"+ facility_name +"' ;";
    	try {
    		rs = stmt.executeQuery(sql);
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	return rs;
    }
    public ResultSet confirmReservation(String reservation_userid){
    	String sql = "SELECT * FROM reservation WHERE user_id = '"+ reservation_userid + "';";
    	try {
    		rs = stmt.executeQuery(sql);
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	return rs;
    }
    public int deleteReservation(String rdate,String reservation_userid,String facility_name){
    	String sql = "DELETE FROM reservation WHERE date = '" + rdate + "' AND user_id = '"
    			+ reservation_userid + "'AND facility_name = '" + facility_name + "' ;";
    	int rs_int = 0;
    	try {
    		rs_int = stmt.executeUpdate(sql);
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	return rs_int;
    }


}