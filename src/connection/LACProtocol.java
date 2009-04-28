package connection;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.Timestamp;
import java.text.ParseException;

import com.sun.org.apache.xml.internal.serializer.ToXMLSAXHandler;

import apps.LAC;

import no.ntnu.fp.net.co.Connection;
import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import model.Event.EventType;

public class LACProtocol {
	
	
	public static void handleMSG(String receive, LAC lac, Connection c) throws ConnectException, IOException{
			
		try {
			String[] s = receive.split(" ");
			
			if(s[0].equals("NEWROOM")){
				int id = Integer.parseInt(s[1]);
				int romNR  = Integer.parseInt(s[2]);
				String romType = s[3];
				String romInfo = s[4];
				lac.getModel().addRoom(new Room(id, romNR, romType, romInfo, lac.getModel()));
			}
			else if(s[0].equals("NEWSENSOR")){
				int id = Integer.parseInt(s[1]);
				boolean alarmState = (true ? s[2].equals("true"): false);
				Timestamp installationDate = XmlSerializer.makeTimestamp(s[3]);
				int battery = Integer.parseInt(s[4]);
				Room room = null;
				
				for (Room r : lac.getModel().getRooms()) {
					if(r.getID() == Integer.parseInt(s[5])) room = r;
				}
				room.addSensor(new Sensor(id, alarmState, battery, installationDate, room, true));
			}
			else if(s[0].equals("NEWEVENT")){
				int id = Integer.parseInt(s[1]);
				EventType eventtype = null;
				for (EventType e : EventType.values()) {
					if(e.toString().equals(s[2])) eventtype = e;
				}
				Timestamp installationDate = XmlSerializer.makeTimestamp(s[3]);
				Room room = null;
				Sensor sensor = null;
				for (Room r : lac.getModel().getRooms()) {
					if(r.getID() == Integer.parseInt(s[4])) room = r;
				}
				for (Sensor se : room.getSensorer()) {
					if(se.getID() == Integer.parseInt(s[5])) sensor = se;
				}
				sensor.addEvent(new Event(id, eventtype,installationDate, sensor));
			}
			else if(s[0].equals("UPDATEROOM")){
				int id = Integer.parseInt(s[1]);
				Room room = null;
				for (Room r : lac.getModel().getRooms()) {
					if(r.getID() == id) room = r;
				}
	
				room.setRomNR(Integer.parseInt(s[2]));
				room.setRomType(s[3]);
				room.setRomInfo(s[4]);
			}
			else if(s[0].equals("UPDATESENSOR")){
				int id = Integer.parseInt(s[1]);
				Sensor sensor = null;
				for (Sensor se : lac.getModel().getSensors()) {
					if(se.getID() == id) sensor = se;
				}
				sensor.setAlarmState((true ? s[2].equals("true"): false));
				sensor.setInstallationDate(XmlSerializer.makeTimestamp(s[3]));
				sensor.setBattery(Integer.parseInt(s[4]));
			}
			
			else if(s[0].equals("DELETEALLEVENTS")){
				Room room = null;
				for (Room r : lac.getModel().getRooms()) {
					if (r.getID() == Integer.parseInt(s[2])){
						room = r;
					}
				}
				for(Sensor se : room.getSensorer()){
					if(se.getID() == Integer.parseInt(s[1])){
						se.deleteAllEvents();
					}
				}
			}
		} catch (Exception e) {
			c.send("NAK");
		}
		
	}
	
	public static boolean connectionCheck(Connection connection) {
		try{
			connection.send("CHECK");
			if(connection.receive().equals("CHECK")){
				return true;
			}
		}
		catch (Exception e) {
		}
		return false;
	}
	

	public static Model receiveCompleteModel(Connection connection, int modelid) throws ConnectException, IOException, ParseException {
		connection.send("GETMODEL"+modelid);
		String s = connection.receive();
		if(s == "-1"){
			throw new IOException("Received a NAK in LACProtocol");
		}
		return XmlSerializer.toModel(s);
	}

	public static void updateLAC(Connection connection, Model model) throws ConnectException, IOException {
		connection.send("UPDATELAC" + " " + Integer.toString(model.getID()) + " " + model.getAdresse());
		if(connection.receive().equals("NAK")){
			throw new IOException("Received a NAK in LACProtocol");
		}
	}

	public static void updateRoom(Connection connection, Room room) throws ConnectException, IOException {
		connection.send("UPDATEROOM" + XmlSerializer.toXmlRoom(room));
		if(connection.receive().equals("NAK")){
			throw new IOException("Received a NAK in LACProtocol");
		}
	}
	
	public static void updateSensor(Connection connection, Sensor sensor) throws ConnectException, IOException {
		connection.send("UPDATESENSOR" + XmlSerializer.toXmlSensor(sensor));
		if(connection.receive().equals("NAK")){
			throw new IOException("Received a NAK in LACProtocol");
		}
	}
	

	public static int insertRoom(Connection connection, Room room) throws ConnectException, IOException {
		connection.send("INSERTROOM" + XmlSerializer.toXmlRoom(room));
		int id = Integer.parseInt(connection.receive());
		if(id == -1){
			throw new IOException("Received a NAK in LACProtocol");
		}
		return id; 
	}
	
	public static int insertSensor(Connection connection, Sensor sensor) throws ConnectException, IOException {
		connection.send("INSERTSENSOR" + XmlSerializer.toXmlSensor(sensor));
		int id = Integer.parseInt(connection.receive());
		if(id == -1){
			throw new IOException("Received a NAK in LACProtocol");
		}
		return id; 
	}
	
	public static int insertEvent(Connection connection, Event event) throws ConnectException, IOException {
		connection.send("INSERTEVENT" + XmlSerializer.toXmlEvent(event));
		int id = Integer.parseInt(connection.receive());
		if(id == -1){
			throw new IOException("Received a NAK in LACProtocol");
		}
		return id; 
	}

	public static void deleteSensorEvents(Sensor sensor) {
		// TODO Auto-generated method stub
		
	}

	public static int insertRoom(Connection connection, int roomNr,
			String roomType, String roomInfo) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
