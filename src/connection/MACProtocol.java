package connection;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.Timestamp;

import no.ntnu.fp.net.co.Connection;

import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import model.Event.EventType;

import apps.LAC;
import apps.MAC.LACAdapter;


public class MACProtocol extends AbstractApplicationProtocol {

	@Override
	public void checkFlag(String msg, String flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMSG(String receive, ModelEditController controller, Connection c) {
		LACAdapter adapter = (LACAdapter)controller;
		try {
			if(receive.startsWith("CHECK")){
				adapter.resetTimeout();
				c.send("CHECK");
			}
			else if(receive.startsWith("GETMODEL")){
				c.send(XmlSerializer.toXml(adapter.getModel()));
			}
			else if(receive.substring(0, 9).equals("UPDATELAC")){
				//FIXME kan ikke splitte på whitespace
				String[] s = receive.split("#");
				adapter.getModel().setAdresse(s[2]);
				adapter.getModel().setID(Integer.parseInt(s[1]));
				sendACK(c);
			}
			else if(receive.substring(0, 10).equals("UPDATEROOM")){
				String[] s = receive.split("#");
				
				Room r = null;
				for (Room room : adapter.getModel().getRooms()) {
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
				String[] s = receive.split("#");
				boolean b = (s[2].equals("true")) ? true : false;
				Room r = null;
				for (Room room : adapter.getModel().getRooms()) {
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

				String[] s = receive.split("#");
				int lacID = Integer.parseInt(s[4]);
				int romNr = Integer.parseInt(s[1]);
				String romType = s[2];
				String romInfo = s[3];
				int modelID = adapter.getModel().getID();


				
				//Konstruktører legger nå automatisk rommet som barn av modellen
				Room room = new Room(-1,romNr, romType, romInfo, adapter.getModel());
				
				sendACK(c);
				
			}
			else if(receive.substring(0, 12).equals("INSERTSENSOR")){

				String[] s = receive.split("#");
				boolean b = (s[2].equals("true")) ? true : false;
				int romID = Integer.parseInt(s[5]);
				int battery = Integer.parseInt(s[4]);
				
				Room room = adapter.getModel().getRoom(romID);
				
				Timestamp t = Timestamp.valueOf(s[3]);
				

				
				//Konstruktører legger nå automatisk sensoren som barn av rommet
				Sensor se = new Sensor(-1, b, battery, t,room,false); 
				
				sendACK(c);

			}
			else if(receive.substring(0, 11).equals("INSERTEVENT")){
				String[] s = receive.split("#");
				EventType et = null;
				for(EventType e : EventType.values()){
					if(s[3].equals(e.toString()))
						et = e;
				}
				
				Model m = adapter.getModel();
				
				int eventID = Integer.parseInt(s[1]);
				Timestamp timestamp = Timestamp.valueOf(s[5]);
				
				
				Sensor sensor = adapter.getModel().getSensor(Integer.parseInt(s[2]));
				
				//Eventkonstruktøren legger automatisk event til som barn av sensor
				Event e = new Event(eventID,et,timestamp,sensor);
				
				sendACK(c);
			}
			
		} catch (Exception e) {
				this.sendNAK(c);
			
		}

		
	}

	@Override
	public void insertEvent(ModelEditController controller, Connection c, Event e) throws ConnectException,IOException {
		String s = "NEWEVENT" + "#" + e.getID() + "#" + e.getEventType().toString() + "#" + e.getTime().toString() + "#" +  e.getSensor().getRoom().getID() + "#" + e.getSensor().getID();
		c.send(s);
		receiveACK(c);
	}

	@Override
	public void insertRoom(ModelEditController controller,Connection c, Room r) throws ConnectException, IOException {
		String s = "NEWROOM" + "#" + r.getID() + "#" + r.getRomNR() + "#" + r.getRomType() + "#" + r.getRomInfo();
		c.send(s);
		receiveACK(c);
	}

	@Override
	public void insertSensor(ModelEditController controller,Connection c, Sensor s) throws ConnectException, IOException {
		String st = "NEWSENSOR" + "#" + s.getID() + "#" + s.isAlarmState() + "#" + s.getInstallationDate().toString() + "#" + s.getBattery() + "#" + s.getRoom().getID();
		c.send(st);
		receiveACK(c);
	}

	@Override
	public void updateEvent(ModelEditController controller, Connection c, Event e) throws ConnectException, IOException {
		//FIXME
		//Trenger bare sende id?
		c.send("" + e.getID());
		receiveACK(c);
	}

	@Override
	public void updateModel(ModelEditController controller, Connection c) throws ConnectException, IOException {
		// TODO Auto-generated method stub
		Model m = controller.getModel();
		String st = "UPDATEMODEL" + "#" + m.getID() + "#" + m.getAdresse();
		c.send(st);
		receiveACK(c);
	}

	@Override
	public void updateRoom(ModelEditController controller, Connection c, Room r) throws IOException {
		String s = "UPDATEROOM" + "#" + r.getID() + "#" + r.getRomNR() + "#" + r.getRomType() + "#" + r.getRomInfo();
		c.send(s);
		if(c.receive().equals("NAK")){
			throw new IOException("Received a NAK in MACProtocol");
		}
	}

	@Override
	public void updateSensor(ModelEditController controller,Connection c, Sensor s) throws IOException {
		String st = "UPDATESENSOR" + "#" + s.getID() + "#" + s.isAlarmState() + "#" + s.getInstallationDate().toString() + "#" + s.getBattery();
		c.send(st);
		receiveACK(c);
	}
/*
	
	public static void deleteAllEvents(Connection c, Sensor sensor) throws IOException{
		String s = "DELETEALLEVENTS " + sensor.getID() + " " + sensor.getRoom().getID();
		c.send(s);
		//FIXME
		if(c.receive().equals("NAK")){
			throw new IOException("Received a NAK in MACProtocol");
		}
	}
	
	*/

}
