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
		if(receive.startsWith("GETMODEL")){
			try {
				if(!adaper.hasModel()){
					Model m = adaper.getMAC().getDatabase().getLACModel(Integer.parseInt(receive.substring(8)));
					adaper.setModel(m);
					adaper.getConnection().send(XmlSerializer.toXml(m));
				}else{
					adaper.getConnection().send(XmlSerializer.toXml(adaper.getModel()));
				}
			} catch (ConnectException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(receive.substring(0, 9).equals("GETNEXTID")){
			try {
				adaper.getConnection().send(""+adaper.getMAC().getDatabase().insertLAC(receive.substring(9)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(receive.substring(0, 9).equals("UPDATELAC")){
			try{
				String[] s = receive.split(" ");
				adaper.getMAC().getDatabase().updateLAC(Integer.parseInt(s[1]), s[2]);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(receive.substring(0, 10).equals("UPDATEROOM")){
			try{
				String[] s = receive.split(" ");
				adaper.getMAC().getDatabase().updateRoom(Integer.parseInt(s[1]), Integer.parseInt(s[2]), s[3], s[4]);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(receive.substring(0, 12).equals("UPDATESENSOR")){
			try{
				String[] s = receive.split(" ");
				boolean b = (s[2].equals("true")) ? true : false;
				adaper.getMAC().getDatabase().updateSensor(Integer.parseInt(s[1]), b, Integer.parseInt(s[3]), XmlSerializer.makeTimestamp(s[4]));
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(receive.substring(0, 10).equals("INSERTROOM")){
			try{
				String[] s = receive.split(" ");
				int roomID = adaper.getMAC().getDatabase().insertRoom(Integer.parseInt(s[5]), Integer.parseInt(s[2]), s[3], s[4]);
				adaper.getConnection().send(Integer.toString(roomID));
				
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(receive.substring(0, 12).equals("INSERTSENSOR")){
			try{
				String[] s = receive.split(" ");
				boolean b = (s[2].equals("true")) ? true : false;
				int sensorID = adaper.getMAC().getDatabase().insertSensor(Integer.parseInt(s[5]), b, Integer.parseInt(s[4]));
				adaper.getConnection().send(Integer.toString(sensorID));
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(receive.substring(0, 11).equals("INSERTEVENT")){
			try{
				String[] s = receive.split(" ");
				EventType et = null;
				for(EventType e : EventType.values()){
					if(s[2].equals(e.toString()))
						et = e;
				}
				int eventID = adaper.getMAC().getDatabase().insertEvent(Integer.parseInt(s[1]), et);
				adaper.getConnection().send(Integer.toString(eventID));
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void newRoom(Room r, Connection c) throws IOException{
		String s = r.getID() + " " + r.getRomNR() + " " + r.getRomType() + " " + r.getRomInfo();
		c.send(s);
	}
	
	public void newSensor(Sensor s, Connection c) throws IOException{
		String st = s.getId() + " " + s.isAlarmState() + " " + s.getInstallationDate().toString() + " " + s.getBattery();
		c.send(st);
	}
	
	public void newEvent(Event e, Connection c) throws IOException{
		String s = e.getID() + " " + e.getEventType().toString() + " " + e.getTime().toString();
		c.send(s);
	}
	
	public void updateRoom(Room r, Connection c) throws IOException{
		String s = r.getID() + " " + r.getRomNR() + " " + r.getRomType() + " " + r.getRomInfo();
		c.send(s);
	}
	
	public void updateSensor(Sensor s, Connection c) throws IOException{
		String st = s.getId() + " " + s.isAlarmState() + " " + s.getInstallationDate().toString() + " " + s.getBattery();
		c.send(st);
	}

}
