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
	
	private Connection db = null;
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
			String query = "SELECT MAX(id) FROM LAC GROUP BY NULL";
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
			m.setAdresse(rs.getString("adress"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			
			String query = "SELECT ID, romNR, romType, romInfo FROM Rom WHERE LACID="+id;
			ResultSet rooms = executeQuery(query);
			while(rooms.next()) {
			
				// construct room
				int romID = rooms.getInt("ID");
				Room room = new Room(rooms.getInt("ID"),rooms.getInt("romNR"),rooms.getString("romType"),rooms.getString("romInfo"));
				
				// traverse room's sensors
				query = "SELECT id, alarmState, batteryStatus, installationDate FROM Sensor WHERE romID="+romID;
				ResultSet sensors = executeQuery(query);
				while(sensors.next()){

					// construct and add sensor to room
					Sensor s = new Sensor(sensors.getInt("id"), sensors.getBoolean("alarmState"),sensors.getInt("batteryStatus"), sensors.getTimestamp("installationDate"),room);
					room.addSensor(s);
					
				}
				
				
				// add room to model's room-list
				m.addRoom(room);
				
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		return m;
		
	}
	
	
	
	
	public void updateLAC(int ID,String adress){

		try {

			executeUpdate("UPDATE LAC SET adress = '" + adress + "' WHERE ID = "+ID);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	
	
	
	public void updateRoom(int ID,int romNR, String romType, String romInfo, boolean dontUpdateEmpties){
		
		try {

			String query = "SELECT romNR, romType, romInfo FROM Rom WHERE ID="+ID;
			ResultSet rs = executeQuery(query);
			rs.next();
			
			romNR = (romNR==0 && dontUpdateEmpties) ? rs.getInt("romNR") : romNR;
			romType = (romType=="" && dontUpdateEmpties) ? rs.getString("romType") : romType;
			romInfo = (romInfo=="" && dontUpdateEmpties) ? rs.getString("romInfo") : romInfo;

			executeUpdate("UPDATE LAC SET 	 romNR = " + romNR +
					 						",romType = '" + romType + "'" +
					 						",romInfo = '" + romInfo + "'" +
						  "WHERE ID = "+ID);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public void updateRoom(int ID,int romNR, String romType, String romInfo){ updateRoom(ID,romNR,romType,romInfo, false); }
	
	
	
	
	
	
	
	public void updateSensor(int ID, boolean alarmState, int batteryStatus){

		try {

			int alarmStateInt = alarmState ? 1 : 0;
			
			executeUpdate("UPDATE LAC SET 	 alarmState  = " + alarmStateInt  +
					 						",batteryStatus  = '" + batteryStatus  + "'" +
						  "WHERE id = "+ID);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
	}

	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args){
		
		try {
			Database db = new Database("mysql.stud.ntnu.no","janberge_admin","1234","janberge_db");
			Model m = db.getLACModel(1);
			System.out.println(m.toString());
		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println("Could not connect to database (fra main)");
		}
		
	}
	
}
