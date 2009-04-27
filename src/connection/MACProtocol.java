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

import apps.MAC.LACAdaper;


public class MACProtocol {

	public static void handleMSG(LACAdaper adaper, String receive) {

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
				}
				else if(receive.substring(0, 10).equals("UPDATEROOM")){
					String[] s = receive.split(" ");
					adaper.getMAC().getDatabase().updateRoom(Integer.parseInt(s[1]), Integer.parseInt(s[2]), s[3], s[4]);
				}
				else if(receive.substring(0, 12).equals("UPDATESENSOR")){
					String[] s = receive.split(" ");
					boolean b = (s[2].equals("true")) ? true : false;
					adaper.getMAC().getDatabase().updateSensor(Integer.parseInt(s[1]), b, Integer.parseInt(s[3]), XmlSerializer.makeTimestamp(s[4]));
				}
				else if(receive.substring(0, 10).equals("INSERTROOM")){
					String[] s = receive.split(" ");
					int roomID = adaper.getMAC().getDatabase().insertRoom(Integer.parseInt(s[5]), Integer.parseInt(s[2]), s[3], s[4]);
					adaper.getConnection().send(Integer.toString(roomID));
				}
				else if(receive.substring(0, 12).equals("INSERTSENSOR")){
					String[] s = receive.split(" ");
					boolean b = (s[2].equals("true")) ? true : false;
					int sensorID = adaper.getMAC().getDatabase().insertSensor(Integer.parseInt(s[1]), b, Integer.parseInt(s[5]));
					adaper.getConnection().send(Integer.toString(sensorID));
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
				}
				
			} catch (Exception e) {
				// e.printStackTrace();
				try {
					adaper.getConnection().send("Failed at MAC");
				} catch (ConnectException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		
	}
	public static void newRoom(Room r, Connection c) throws IOException{
		String s = "NEWROOM" + " " + r.getID() + " " + r.getRomNR() + " " + r.getRomType() + " " + r.getRomInfo();
		c.send(s);
	}
	
	public static void newSensor(Sensor s, Connection c) throws IOException{
		String st = "NEWSENSOR" + " " + s.getId() + " " + s.isAlarmState() + " " + s.getInstallationDate().toString() + " " + s.getBattery() + " " + s.getRoom().getID();
		c.send(st);
	}
	
	public static void newEvent(Event e, Connection c) throws IOException{
		String s = "NEWEVENT" + " " + e.getID() + " " + e.getEventType().toString() + " " + e.getTime().toString() + " " +  e.getSensor().getRoom().getID() + " " + e.getSensor().getId();
		c.send(s);
	}
	
	public static void updateRoom(Room r, Connection c) throws IOException{
		String s = "UPDATEROOM" + " " + r.getID() + " " + r.getRomNR() + " " + r.getRomType() + " " + r.getRomInfo();
		c.send(s);
	}
	
	public static void updateSensor(Sensor s, Connection c) throws IOException{
		String st = "UPDATESENSOR" + " " + s.getId() + " " + s.isAlarmState() + " " + s.getInstallationDate().toString() + " " + s.getBattery();
		c.send(st);
	}
	public static void deleteAllEvents(Connection c, Sensor sensor) throws IOException{
		String s = "DELETEALLEVENTS " + sensor.getId() + " " + sensor.getRoom().getID();
		c.send(s);
	}

}
