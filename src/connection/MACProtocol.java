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
					adaper.getConnection().send(XmlSerializer.toXml(adaper.getModel()));
				}
				else if(receive.substring(0, 9).equals("GETNEXTID")){
//					adaper.getConnection().send(""+adaper.getNextLACID(receive.substring(9)));
				}
				else if(receive.substring(0, 9).equals("UPDATELAC")){
					String[] s = receive.split(" ");
					adaper.getModel().setAdresse(s[2]);
					adaper.getModel().setID(Integer.parseInt(s[1]));
				}
				else if(receive.substring(0, 10).equals("UPDATEROOM")){
					String[] s = receive.split(" ");
					
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
					Room r = null;
					for (Room room : adaper.getModel().getRooms()) {
						if(room.getID() == Integer.parseInt(s[5])){
							r = room;
							break;
						}
					}
					Sensor se = null;
					for (Sensor sensor : r.getSensorer()) {
						if(sensor.getID() == Integer.parseInt(s[1])){
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
					int modelID = adaper.getModel().getID();
					
					Room r = adaper.insertRoom(modelID, romNr, romType, romInfo);
					
					adaper.getConnection().send("" + r.getID());
					
				}
				else if(receive.substring(0, 12).equals("INSERTSENSOR")){
					String[] s = receive.split(" ");
					boolean b = (s[2].equals("true")) ? true : false;
					int romID = Integer.parseInt(s[1]);
					int battery = Integer.parseInt(s[5]);
					
					Sensor se = adaper.insertSensor(romID, b, battery);
					
					adaper.getConnection().send(Integer.toString(se.getID()));

				}
				else if(receive.substring(0, 11).equals("INSERTEVENT")){
					String[] s = receive.split(" ");
					EventType et = null;
					for(EventType e : EventType.values()){
						if(s[2].equals(e.toString()))
							et = e;
					}
					//NBNBNBNBNB SKAFF ROMID OG SENSIOR IDDDDDD
					Event e = adaper.insertEvent(0,0,et);
					
					adaper.getConnection().send(Integer.toString(e.getID()));
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
		String st = "NEWSENSOR" + " " + s.getID() + " " + s.isAlarmState() + " " + s.getInstallationDate().toString() + " " + s.getBattery() + " " + s.getRoom().getID();
		c.send(st);
		if(c.receive().equals("NAK")){
			throw new IOException("Received a NAK in MACProtocol");
		}
	}
	
	public static void newEvent(Event e, Connection c) throws IOException{
		String s = "NEWEVENT" + " " + e.getID() + " " + e.getEventType().toString() + " " + e.getTime().toString() + " " +  e.getSensor().getRoom().getID() + " " + e.getSensor().getID();
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
		String st = "UPDATESENSOR" + " " + s.getID() + " " + s.isAlarmState() + " " + s.getInstallationDate().toString() + " " + s.getBattery();
		c.send(st);
		if(c.receive().equals("NAK")){
			throw new IOException("Received a NAK in MACProtocol");
		}
	}
	public static void deleteAllEvents(Connection c, Sensor sensor) throws IOException{
		String s = "DELETEALLEVENTS " + sensor.getID() + " " + sensor.getRoom().getID();
		c.send(s);
		if(c.receive().equals("NAK")){
			throw new IOException("Received a NAK in MACProtocol");
		}
	}

}
