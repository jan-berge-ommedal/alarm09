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
			else if(checkFlag(receive, UPDATEMODEL)){
				//FIXME kan ikke splitte på whitespace
				String modelstring = removeFlag(receive, UPDATEMODEL);
				
				Model model = XmlSerializer.toModel(modelstring, controller);
				
				sendACK(c);
			}
			else if(checkFlag(receive, UPDATEROOM)){
				
				String roomstring = removeFlag(receive, UPDATEROOM);
				
				Room room = XmlSerializer.toRoom(roomstring, controller.getModel());
				
				sendACK(c);
			}
			else if(checkFlag(receive, UPDATESENSOR)){
				
				String sensorstring = removeFlag(receive, UPDATESENSOR);
				
				Sensor sensor = XmlSerializer.toSensor(sensorstring);
				
				sendACK(c);
			}
			else if(checkFlag(receive, INSERTROOM)){
				
				String roomstring = removeFlag(receive, INSERTROOM);
				
				Room room = XmlSerializer.toRoom(roomstring, controller.getModel());
				
				//Konstruktører legger nå automatisk rommet som barn av modellen
				//Room room = new Room(-1,romNr, romType, romInfo, adapter.getModel());
				
				sendACK(c);
				
			}
			else if(checkFlag(receive, INSERTSENSOR)){

				String sensorstring = removeFlag(receive, INSERTSENSOR);
				
				Sensor sensor = XmlSerializer.toSensor(sensorstring);
				
				sendACK(c);
			}
			else if(checkFlag(receive, INSERTEVENT)){
				
				String eventstring = removeFlag(receive, INSERTEVENT);
				
				Event event = XmlSerializer.toEvent(eventstring);
				
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
