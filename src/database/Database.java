package database;

import java.sql.*;
import java.util.ArrayList;

import apps.LAC;

import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import model.Event.EventType;

public class Database {
	
	private Connection db;
	private Statement st;
	
	private static int i =0;
	
	
	public Database(String url, String user, String password, String databaseName) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		db = DriverManager.getConnection("jdbc:mysql://"+url,user,password);
		st = db.createStatement();
		st.executeQuery("USE "+databaseName);
	}
	
	
	private ResultSet executeQuery(String query) throws SQLException{
		return st.executeQuery(query);
	}
	
	private void executeUpdate(String query) throws SQLException{
		st.executeUpdate(query);

	}
	public void close(){
		try {
			st.close();
			db.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public int getNextLACID(){
		try {
			String query = "SELECT MAX(id) FROM LAC";
			ResultSet rs = executeQuery(query);
			rs.next();
			//Column 1 er den første kolonnen!
			return rs.getInt(1)+1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
		
	}
	
	public Model getLACModel(int id) {
		Model m = new Model();
		m.setID(id);
		
		
		try {
			String query = "SELECT adress FROM LAC WHERE ID="+id;
			ResultSet rs = executeQuery(query);
			rs.next();
			m.setAdresse(rs.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			String query = "SELECT ID, romNR, romType, romInfo FROM Rom WHERE LACID="+id;
			ResultSet rs = executeQuery(query);
			while(rs.next()) {
			
				Array romID = rs.getArray("ID");
				
				Room room;
				
				String query2 = "SELECT * FROM Sensor WHERE romID="+romID;
				ResultSet srs = executeQuery(query2);
				
				Array sensorID = rs.getArray("id");
				
				while(srs.next()){
					String query3 = "SELECT * FROM Event WHERE ";
				}
			
				
				
					
				
				
					Sensor s = new Sensor(romid,room,timestamp);
				
					
						s.addEvent(new Event(id,eventtype,timestamp));
				
				
				
				
				
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
	
		Room r = new Room(0,0,"BAD","Et nydelig bad");
		Sensor s = new Sensor(0,r,LAC.getTime());
		
		s.addEvent(new Event(0,EventType.ALARM,LAC.getTime(),s));
		s.addEvent(new Event(0,EventType.FALSEALARM,LAC.getTime(),s));
		
		return m;
		
	}
	

}
