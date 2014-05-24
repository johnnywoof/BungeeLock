package me.johnnywoof.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import me.johnnywoof.BungeeLock;
import me.johnnywoof.Settings;

public class MySql implements Database{

	private Statement st;
	
	@Override
	public String getPassword(UUID uuid) {
		
		try {
			
			ResultSet rs = st.executeQuery("SELECT * FROM `" + Settings.dbprefix + "_accounts` WHERE uuid = '" + uuid.toString() + "'");
			
			String pass = null;
			
			while(rs.next()){
				
				pass = rs.getString(1);
				
			}
			
			rs.close();
			
			return pass;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public void setPassword(UUID uuid, String epass) {
		
		try{
			
			if(this.getPassword(uuid) == null){
			
				PreparedStatement stmt = this.st.getConnection().prepareStatement("UPDATE `" + Settings.dbprefix + "_accounts` SET password = ? WHERE uuid = ?;");
				stmt.setString(1, epass);
				stmt.setString(2, uuid.toString());
				stmt.executeUpdate();
			
			}else{
				
				PreparedStatement stmt = this.st.getConnection().prepareStatement("INSERT INTO `" + Settings.dbprefix + "_accounts` (`uuid`, `password`) VALUES (?, ?);");
				stmt.setString(1, epass);
				stmt.setString(2, uuid.toString());
				stmt.executeUpdate();
				
			}
			
		}catch(SQLException e){
			
			e.printStackTrace();
			
		}
		
	}

	@Override
	public void check(BungeeLock bl) {
		
		try{
		
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn = DriverManager.getConnection("jdbc:mysql://" + Settings.dbhost + ":" + Settings.dbport + "/" + Settings.dbname + "?autoReconnect=true?useUnicode=yes", Settings.dbusername, Settings.dbpass);
			this.st = conn.createStatement();
			
			if(!this.doesTableExist(Settings.dbprefix + "_accounts")){
			
				st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + Settings.dbprefix + "_accounts` (`uuid` varchar(80), `password` TEXT)");
				
				st.executeUpdate("ALTER TABLE `" + Settings.dbprefix + "_accounts` ADD INDEX (`uuid`)");
			
			}
	    
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
		
	}

	@Override
	public int getTypeID() {
		return 1;
	}
	
	private boolean doesTableExist(String tablename) throws SQLException{
    	ResultSet rs = st.getConnection().getMetaData().getTables(null, null, tablename, null);
    	if (rs.next()) {
    		rs.close();
    	  return true;
    	}
    	rs.close();
    	return false;
    }

}
