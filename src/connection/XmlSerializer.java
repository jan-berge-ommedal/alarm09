/*
 * Created on Oct 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package connection;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;

import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import model.Event.EventType;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

/**
 * @author Simon Grøndahl
 */
public class XmlSerializer {

	/**
	 * 
	 * <strong>This method takes a model as parameter and  makes a xmlString out of it</strong>
	 * 
	 * @param aModel the model that is made to a  XmlString
	 */
	public static String toXml(Model aModel) {
		Element root = new Element("LAC");
		
		Element id = new Element("id");
		id.appendChild(Integer.toString(aModel.getID()));
		root.appendChild(id);
		
		Element adress = new Element("adress");
		adress.appendChild(aModel.getAdresse());
		root.appendChild(adress);
		
		for (Room r : aModel.getRooms()) {
			Element element = sensorToXml(r);
			root.appendChild(element);
		}
		
		return root.toXML();
	}
	
	public static Model toModel(String xml) throws ParseException {
		Model aModel = new Model();
		String[] xmls = xml.split("<");
		aModel.setID(Integer.parseInt(xmls[1].substring(4)));
		aModel.setAdresse(xmls[2].substring(8));
		
		int roomteller = 100;
		int sensorteller = 100;
		int eventteller = 100;
		roomteller++;
		int en = 0;
		int to = 0;
		String tre = "";
		String fire = ""; 
		Timestamp fem = new Timestamp(en);
		boolean seks = false;
		EventType sju = EventType.ALARM;
		
		for (int i = 3; i < xmls.length; i++) {
			// Checks for room
			if(xmls[i] == "<room>"){
				roomteller = 0;
			}
			// Gets the right roomattributes
			if(roomteller == 1){en = Integer.parseInt(xmls[i].substring(4));}
			else if(roomteller == 3){to = Integer.parseInt(xmls[i].substring(7));}
			else if(roomteller == 5){tre = xmls[i].substring(9);}
			else if(roomteller == 7){fire = xmls[i].substring(8);}
			else if(roomteller == 9){
				Room r = new Room(en, to, tre, fire);
				aModel.addRoom(r);
			}
			// Checks for sensors
			if(xmls[i] == "<Sensors>"){
				sensorteller = 0;
			}
			// Gets the right sensorattributes
			if(sensorteller == 1){en = Integer.parseInt(xmls[i].substring(4));}
			else if(sensorteller == 3){seks = true ? xmls[i].substring(12) == "true" : false;}
			else if(sensorteller == 5){fem = makeTimestamp(xmls[i].substring(11));}
			else if(sensorteller == 7){to = Integer.parseInt(xmls[i].substring(9));}
			else if(sensorteller == 9){
				Sensor s = new Sensor(en, seks, to,fem, aModel.getRooms().get(aModel.getRooms().size()-1));
				aModel.getRooms().get(aModel.getRooms().size()-1).addSensor(s);
			}
			// Checks for events
			if(xmls[i] == "<events>"){
				eventteller = 0;
			}
			// Gets the right eventattributes
			if(eventteller == 1){en = Integer.parseInt(xmls[i].substring(4));}
			else if(eventteller == 3){
				if(xmls[i].substring(11) == "FALSEALARM") sju = EventType.FALSEALARM;
				else if(xmls[i].substring(11) == "ALARM") sju = EventType.ALARM;
				else if(xmls[i].substring(11) == "STARTUP") sju = EventType.STARTUP;
				else sju = EventType.BATTERYREPLACEMENT;
			}
			else if(eventteller == 5){fem = makeTimestamp(xmls[i].substring(6));}
			else if(eventteller == 7){
				
				Event e = new Event(en,sju,fem);
				aModel.getRooms().get(aModel.getRooms().size() -1).getSensorer().get(aModel.getRooms().get(aModel.getRooms().size() -1).getSensorer().size()-1).addEvent(e);
			}
			
		
		}
		
		
		return aModel;
	}

	private static Timestamp makeTimestamp(String time) {
		return new Timestamp(Integer.parseInt(time.substring(0, 4)),Integer.parseInt(time.substring(5, 7)),Integer.parseInt(time.substring(8, 10)),Integer.parseInt(time.substring(11, 13)),Integer.parseInt(time.substring(14, 16)),Integer.parseInt(time.substring(17, 19)),Integer.parseInt(time.substring(20, 23)));
	}


	
	private static Element sensorToXml(Room aRoom) {
		Element element = new Element("room");
		
		Element roomid = new Element("id");
		roomid.appendChild(Integer.toString(aRoom.getID()));
		Element romNR = new Element("romNR");
		romNR.appendChild(Integer.toString(aRoom.getRomNR()));
		Element roomType = new Element("romtype");
		roomType.appendChild(aRoom.getRomType());
		Element roomInfo = new Element("rominfo");
		roomInfo.appendChild(aRoom.getRomInfo());
		
		element.appendChild(roomid);
		element.appendChild(romNR);
		element.appendChild(roomType);
		element.appendChild(roomInfo);
		
		
		for (Sensor s : aRoom.getSensorer()) {
			Element sensor = new Element("Sensors");
			
			Element id = new Element("id");
			id.appendChild(Integer.toString(s.getId()));
			Element alarmState = new Element("alarmState");
			alarmState.appendChild((s.isAlarmState() ? "true" : "false"));
			Element timeStamp = new Element ("timeStamp");
			timeStamp.appendChild(s.getInstallationDate().toString());
			Element battery = new Element("battery");
			battery.appendChild(Integer.toString(s.getBattery()));
			
			sensor.appendChild(id);
			sensor.appendChild(alarmState);
			sensor.appendChild(timeStamp);
			sensor.appendChild(battery);
			
			for (Event e : s.getEvents()) {
				Element events = new Element("events");
				
				Element eventId = new Element("id");
				eventId.appendChild(Integer.toString(e.getID()));
				Element eventType = new Element("eventType");
				eventType.appendChild(e.getEventType().toString());
				Element time = new Element("time");
				time.appendChild(e.getTime().toString());
				
				events.appendChild(eventId);
				events.appendChild(eventType);
				events.appendChild(time);
				
				sensor.appendChild(events);
			} 
			
			element.appendChild(sensor);
		}
		
		return element;
	}

}

