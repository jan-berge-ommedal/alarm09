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
	Statement st;
	
	private static int i =0;
	
	
	public Database(String url, String user, String password, String databaseName) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		db = DriverManager.getConnection("jdbc:mysql://"+url,user,password);
		st = db.createStatement();
		st.executeQuery("USE "+databaseName);
	}
	
	
	private ResultSet executeQuery(String query) throws SQLException{
		Statement st = db.createStatement();
		return st.executeQuery(query);
	}
	
	private void executeUpdate(String query) throws SQLException{
		Statement st = db.createStatement();
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
	
	
	
	
	
	
	// uten oppdatering av installationDate
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

	// m/ oppdatering av installationDate
	public void updateSensor(int ID, boolean alarmState, int batteryStatus, Timestamp installationDate){

		try {

			int alarmStateInt = alarmState ? 1 : 0;
			
			executeUpdate("UPDATE LAC SET 	 alarmState  = " + alarmStateInt  +
					 						",batteryStatus  = '" + batteryStatus  + "'" +
					 						",installationDate   = '" + installationDate.toString()  + "'" +
						  "WHERE id = "+ID);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
	}

	
	
	

	public int insertLAC(String adress){

		int id = -1;
		
		try {
			
			String query = "SELECT MAX(ID) AS id FROM LAC GROUP BY NULL";
			ResultSet rs = executeQuery(query);
			rs.next();
			id = rs.getInt("id")+1;
			executeUpdate("INSERT INTO LAC (adress) VALUES ('"+adress+"')");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return id;
		
	}
	
	
	
	public int insertRoom(int LACID, int romNR, String romType, String romInfo){
		
		int id = -1;
		
		try {
			
			String query = "SELECT MAX(ID) AS id FROM Rom GROUP BY NULL";
			ResultSet rs = executeQuery(query);
			rs.next();
			id = rs.getInt("id")+1;
			executeUpdate("INSERT INTO Rom (ID, LACID, romNR, romType, romInfo ) VALUES ("+id+","+LACID+","+romNR+",'"+romType+"','"+romInfo+"')");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return id;
		
	}
	
	

	public int insertSensor(int romID, boolean alarmState, int batteryStatus){
		

		int id = -1;
		
		try {

			int alarmStateInt = alarmState ? 1 : 0;
			
			String query = "SELECT MAX(id) AS id FROM Sensor GROUP BY NULL";
			ResultSet rs = executeQuery(query);
			rs.next();
			id = rs.getInt("id")+1;		
			executeUpdate("INSERT INTO Rom (id, romID, installationDate, alarmState, batteryStatus) VALUES ("+id+","+romID+",NULL,"+alarmStateInt+","+batteryStatus+")");

			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return id;
		
	}
	
	
	public void emptyTables(){
		

		try {
			
			executeUpdate("TRUCATE TABLE Event");
			executeUpdate("TRUCATE TABLE LAC");
			executeUpdate("TRUCATE TABLE Rom");
			executeUpdate("TRUCATE TABLE Sensor");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args){
		
		try {
			
			
			
			Database db = new Database("mysql.stud.ntnu.no","janberge_admin","1234","janberge_db");
			
			// int io = db.insertLAC("Rundt svingen");
			// System.out.println(io);
			
			Model m = db.getLACModel(3);
			System.out.println(m.toString());
		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println("Could not connect to database (fra main)");
		}
		
	}
	
}
