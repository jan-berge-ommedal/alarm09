package database;

import java.sql.*;

import connection.ModelEditController;

import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import model.Event.EventType;
import model.Sensor.Alarm;

/**
 * 
 * @author oddy was here
 *
 */

public class Database {
	
	private Connection db = null;
	Statement st;
	
	
	public Database(String url, String user, String password, String databaseName) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		db = DriverManager.getConnection("jdbc:mysql://"+url,user,password);
		System.out.println("Connecta te databasen");
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
	
	public Model getLACModel(int id,ModelEditController controller) {
		
		Model m = new Model(controller,id);
		//The controller should not handle changes when loading the model from database
		m.removePropertyChangeListener(controller);
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
				Room room = new Room(rooms.getInt("ID"),rooms.getInt("romNR"),rooms.getString("romType"),rooms.getString("romInfo"),m);
				
				// traverse room's sensors
				query = "SELECT id, alarmState, batteryStatus, installationDate FROM Sensor WHERE romID="+romID;
				ResultSet sensors = executeQuery(query);
				while(sensors.next()){

					// construct and add sensor to room
					Sensor s = new Sensor(sensors.getInt("id"), Sensor.Alarm.valueOf(sensors.getString("alarmState")),sensors.getInt("batteryStatus"), sensors.getTimestamp("installationDate"),room);
					
				}
				
				
				// add room to model's room-list
				
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//After loading is complete, the controller will listen to changes in the model
		m.addPropertyChangeListener(controller);
	
		return m;
		
	}
	
	
	
	public Sensor getEvents(Sensor s){
		
		int id = s.getID();
		
		try {
			String query = "SELECT id,eventType, time FROM Event WHERE sensorID="+id;
			ResultSet events = executeQuery(query);
			while(events.next()){
				
				s.addEvent(new Event(events.getInt("id"),EventType.valueOf(events.getString("eventType")),events.getTimestamp("time"),s));
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return s;
		
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

			executeUpdate("UPDATE Rom SET 	 romNR = " + romNR +
					 						",romType = '" + romType + "'" +
					 						",romInfo = '" + romInfo + "'" +
						  "WHERE ID = "+ID);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public void updateRoom(int ID,int romNR, String romType, String romInfo){ updateRoom(ID,romNR,romType,romInfo, false); }
	
	
	
	
	
	
	// uten oppdatering av installationDate
	public void updateSensor(int ID, Alarm alarm, int batteryStatus, Timestamp timestamp) throws SQLException{

			
			executeUpdate("UPDATE Sensor SET 	 alarmState  = '" + alarm.toString()  +
					 						"' ,batteryStatus  = '" + batteryStatus  + "'," + 
					 						"installationDate = '"+timestamp.toString()+"'"+
						  " WHERE id = "+ID);
			
	
			
	}
	
	
	

	public int insertLAC(String adress) throws SQLException{

		int id = -1;
		
			
			executeUpdate("INSERT INTO LAC (adress) VALUES ('"+adress+"')");
			
			String query = "SELECT MAX(ID) AS id FROM LAC GROUP BY NULL";
			ResultSet rs = executeQuery(query);
			rs.next();
			id = rs.getInt("id");
			


		return id;
		
	}
	
	
	
	public int insertRoom(int LACID, int romNR, String romType, String romInfo) throws SQLException{
		
		int id = -1;
		
			
			executeUpdate("INSERT INTO Rom (LACID, romNR, romType, romInfo ) VALUES ("+LACID+","+romNR+",'"+romType+"','"+romInfo+"')");
			
			String query = "SELECT MAX(ID) AS id FROM Rom GROUP BY NULL";
			ResultSet rs = executeQuery(query);
			rs.next();
			id = rs.getInt("id");
			
	

		return id;
		
	}
	
	

	public int insertSensor(int romID, Alarm alarm, int batteryStatus){
		

		int id = -1;
		
		try {

				
			executeUpdate("INSERT INTO Sensor (romID, installationDate, alarmState, batteryStatus) VALUES ("+romID+",NULL,'"+alarm.toString()+"',"+batteryStatus+")");

			String query = "SELECT MAX(id) AS id FROM Sensor GROUP BY NULL";
			ResultSet rs = executeQuery(query);
			rs.next();
			id = rs.getInt("id");	
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return id;
		
	}

	public int insertEvent(int sensorID, EventType eventType){
		
		int id = -1;
		
		try {
			
			executeUpdate("INSERT INTO Event (sensorID, eventType, time) VALUES ("+sensorID+",'"+eventType+"',NULL)");

			String query = "SELECT MAX(id) AS id FROM Event GROUP BY NULL";
			ResultSet rs = executeQuery(query);
			rs.next();
			id = rs.getInt("id");		
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return id;
		
	}
	
	public void emptyTables(){
		

		try {
			
			executeUpdate("TRUNCATE TABLE `Event`");
			executeUpdate("TRUNCATE TABLE `LAC`");
			executeUpdate("TRUNCATE TABLE `Rom`");
			executeUpdate("TRUNCATE TABLE `Sensor`");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void removeEvent(int id){
		try {
			executeUpdate("DELETE FROM Event WHERE id = "+id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void removeSensorsEvents(int sensorid){
		try {
			executeUpdate("DELETE FROM Sensor WHERE id = "+sensorid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	public void removeSensor(int id){
		try {
			executeUpdate("DELETE FROM Sensor WHERE id = "+id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	public int[] getIDs() {
		int[] ids = null;
		try {
			ResultSet rows = executeQuery("SELECT COUNT(ID) AS ids FROM LAC GROUP BY NULL"); 
			int nrOfIds = 0;
			if(rows.next())nrOfIds = rows.getInt("ids");
				
			ids = new int[nrOfIds];
			
			int pointer = 0;
			ResultSet lacs = executeQuery("SELECT ID FROM LAC");
			while(lacs.next()) {
				ids[pointer] = lacs.getInt("ID");
				pointer++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return ids;
	}
	


	public static void main(String[] args) {
		Database database;
		try {
			database = new Database("mysql.stud.ntnu.no","janberge_admin","1234","janberge_db");
			database.emptyTables();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
