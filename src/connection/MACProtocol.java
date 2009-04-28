package connection;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.Timestamp;

import no.ntnu.fp.net.co.Connection;

import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import model.Event.EventType;

import apps.MAC.LACAdapter;


public class MACProtocol {

	public static void handleMSG(LACAdapter adaper, String receive) throws ConnectException, IOException {

			try {
				if(receive.startsWith("CHECK")){
					adaper.getConnection().send("CHECK");
				}
				else if(receive.startsWith("GETMODEL")){
					if(!adaper.hasModel()){
						Model m = adaper.getMAC().getDatabase().getLACModel(Integer.parseInt(receive.substring(8)));
						adaper.setModel(m);
					}
					adaper.getConnection().send(XmlSerializer.toXml(adaper.getModel()));
				}
				else if(receive.substring(0, 9).equals("GETNEXTID")){
					adaper.getConnection().send(""+adaper.getMAC().getDatabase().insertLAC(receive.substring(9)));
				}
				else if(receive.substring(0, 9).equals("UPDATELAC")){
					String[] s = receive.split(" ");
					adaper.getMAC().getDatabase().updateLAC(Integer.parseInt(s[1]), s[2]);
					adaper.getModel().setID(Integer.parseInt(s[1]));
					adaper.getModel().setAdresse(s[2]);
				}
				else if(receive.substring(0, 10).equals("UPDATEROOM")){
					String[] s = receive.split(" ");
					adaper.getMAC().getDatabase().updateRoom(Integer.parseInt(s[1]), Integer.parseInt(s[2]), s[3], s[4]);
					Room r = null;
					for (Room room : adaper.getModel().getRooms()) {
						if(room.getID() == Integer.parseInt(s[1])){
							r = room;
							break;
						}
					}
					r.setRomNR(Integer.parseInt(s[2]));
					r.setRomType(s[3]);
					r.setRomInfo(s[4]);
				}
				else if(receive.substring(0, 12).equals("UPDATESENSOR")){
					String[] s = receive.split(" ");
					boolean b = (s[2].equals("true")) ? true : false;
					adaper.getMAC().getDatabase().updateSensor(Integer.parseInt(s[1]), b, Integer.parseInt(s[3]), XmlSerializer.makeTimestamp(s[4]));
					Room r = null;
					for (Room room : adaper.getModel().getRooms()) {
						if(room.getID() == Integer.parseInt(s[5])){
							r = room;
							break;
						}
					}
					Sensor se = null;
					for (Sensor sensor : r.getSensorer()) {
						if(sensor.getId() == Integer.parseInt(s[1])){
							se = sensor;
							break;
						}
					}
					se.setAlarmState(b);
					se.setBattery(Integer.parseInt(s[3]));
					se.setInstallationDate(XmlSerializer.makeTimestamp(s[4]));
				}
				else if(receive.substring(0, 10).equals("INSERTROOM")){
					String[] s = receive.split(" ");
					int lacID = Integer.parseInt(s[5]);
					int romNr = Integer.parseInt(s[2]);
					String romType = s[3];
					String romInfo = s[4];
					// Insert into database and receive ID
					int roomID = adaper.getMAC().getDatabase().insertRoom(lacID, romNr,romType , romInfo);
					// Send ID to LAC
					adaper.getConnection().send(Integer.toString(roomID));
					// IF no error occured, add the room to the adapter' model
					adaper.getModel().addRoom(new Room(roomID,romNr,romType,romInfo,adaper.getModel()));
					
				}
				else if(receive.substring(0, 12).equals("INSERTSENSOR")){
					String[] s = receive.split(" ");
					boolean b = (s[2].equals("true")) ? true : false;
					int romID = Integer.parseInt(s[1]);
					int battery = Integer.parseInt(s[5]);
					int sensorID = adaper.getMAC().getDatabase().insertSensor(romID, b, battery);
					adaper.getConnection().send(Integer.toString(sensorID));

					Room room = null;
					for (Room r : adaper.getModel().getRooms()) {
						if(r.getID() == romID){
							room = r;
						}
					}
					Timestamp time = XmlSerializer.makeTimestamp(s[3]);
					
					room.addSensor(new Sensor(romID, b, battery, time, room, true));
				}
				else if(receive.substring(0, 11).equals("INSERTEVENT")){
					String[] s = receive.split(" ");
					EventType et = null;
					for(EventType e : EventType.values()){
						if(s[2].equals(e.toString()))
							et = e;
					}
					int eventID = adaper.getMAC().getDatabase().insertEvent(Integer.parseInt(s[1]), et);
					adaper.getConnection().send(Integer.toString(eventID));
					
					Room r = null;
					for (Room room : adaper.getModel().getRooms()) {
						if(room.getID() == Integer.parseInt(s[3])){
							r = room;
							break;
						}
					}
					Sensor se = null;
					for (Sensor sensor : r.getSensorer()) {
						if(sensor.getId() == Integer.parseInt(s[1])){
							se = sensor;
							break;
						}
					}
					Timestamp time = new Timestamp(System.currentTimeMillis());
					se.addEvent(new Event(eventID,et, time,se));
				}
				
			} catch (Exception e) {
				if(receive.startsWith("INSERT")){
					adaper.getConnection().send(Integer.toString(-1));
				}
				else{
					adaper.getConnection().send("NAK");
				}
			}
		
	}
	public static void newRoom(Room r, Connection c) throws IOException{
		String s = "NEWROOM" + " " + r.getID() + " " + r.getRomNR() + " " + r.getRomType() + " " + r.getRomInfo();
		c.send(s);
		if(c.receive().equals("NAK")){
			throw new IOException("Received a NAK in MACProtocol");
		}
	}
	
	public static void newSensor(Sensor s, Connection c) throws IOException{
		String st = "NEWSENSOR" + " " + s.getId() + " " + s.isAlarmState() + " " + s.getInstallationDate().toString() + " " + s.getBattery() + " " + s.getRoom().getID();
		c.send(st);
		if(c.receive().equals("NAK")){
			throw new IOException("Received a NAK in MACProtocol");
		}
	}
	
	public static void newEvent(Event e, Connection c) throws IOException{
		String s = "NEWEVENT" + " " + e.getID() + " " + e.getEventType().toString() + " " + e.getTime().toString() + " " +  e.getSensor().getRoom().getID() + " " + e.getSensor().getId();
		c.send(s);
		if(c.receive().equals("NAK")){
			throw new IOException("Received a NAK in MACProtocol");
		}
	}
	
	public static void updateRoom(Room r, Connection c) throws IOException{
		String s = "UPDATEROOM" + " " + r.getID() + " " + r.getRomNR() + " " + r.getRomType() + " " + r.getRomInfo();
		c.send(s);
		if(c.receive().equals("NAK")){
			throw new IOException("Received a NAK in MACProtocol");
		}
	}
	
	public static void updateSensor(Sensor s, Connection c) throws IOException{
		String st = "UPDATESENSOR" + " " + s.getId() + " " + s.isAlarmState() + " " + s.getInstallationDate().toString() + " " + s.getBattery();
		c.send(st);
		if(c.receive().equals("NAK")){
			throw new IOException("Received a NAK in MACProtocol");
		}
	}
	public static void deleteAllEvents(Connection c, Sensor sensor) throws IOException{
		String s = "DELETEALLEVENTS " + sensor.getId() + " " + sensor.getRoom().getID();
		c.send(s);
		if(c.receive().equals("NAK")){
			throw new IOException("Received a NAK in MACProtocol");
		}
	}

}
