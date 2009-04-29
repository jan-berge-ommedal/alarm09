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
	public static String toXmlComplete(Model aModel) {
		Element root = new Element("LAC");
		
		Element id = new Element("id");
		id.appendChild(Integer.toString(aModel.getID()));
		root.appendChild(id);
		
		Element adress = new Element("adress");
		adress.appendChild(aModel.getAdresse());
		root.appendChild(adress);
		
		for (Room r : aModel.getRooms()) {
			Element element = insertXmlRoom(r);
			root.appendChild(element);
		}
		
		return root.toXML();
	}
	// Takes a String and sets the modelattributes
	public static Model toModel(String xml, ModelEditController controller) throws ParseException {
		System.out.println(xml + "\n");
		Model aModel = new Model(controller);
		
		//We don't want that the controller handles propertychangeEvents, when setting ut the model
		aModel.removePropertyChangeListener(controller);
		
		String[] xmls = xml.split("<");
		aModel.setID(Integer.parseInt(xmls[2].substring(3)));
		aModel.setAdresse(xmls[4].substring(7));
		
		int roomteller = 100;
		int sensorteller = 100;
		int eventteller = 100;
		roomteller++;
		int en = 0;
		int to = 0;
		String tre = "";
		String fire = ""; 
		Timestamp fem = new Timestamp(0);
		boolean seks = false; 
		EventType sju = EventType.ALARM;
		Room r;
		Sensor s = null;
		
		for (int i = 5; i < xmls.length; i++) {
			roomteller++; sensorteller++; eventteller++;
			
			// Checks for room
			if(xmls[i].equals("room>")){
				roomteller = 0;
			}
			// Gets the right roomattributes
			if(roomteller == 1){en = Integer.parseInt(xmls[i].substring(3));}
			else if(roomteller == 3){to = Integer.parseInt(xmls[i].substring(6));}
			else if(roomteller == 5){tre = xmls[i].substring(8);}
			else if(roomteller == 7){fire = xmls[i].substring(8);}
			else if(roomteller == 9){
				r = new Room(en, to, tre, fire, aModel);
			}
			// Checks for sensors
			if(xmls[i].equals("Sensors>")){
				sensorteller = 0;
			}
			// Gets the right sensorattributes
			if(sensorteller == 1){en = Integer.parseInt(xmls[i].substring(3));}
			else if(sensorteller == 3){seks = (true ? xmls[i].substring(11).equals("true"): false);}
			else if(sensorteller == 5){fem = makeTimestamp(xmls[i].substring(10));}
			else if(sensorteller == 7){to = Integer.parseInt(xmls[i].substring(8));}
			else if(sensorteller == 9){
				s = new Sensor(en, seks, to,fem, aModel.getRooms().get(aModel.getRooms().size()-1), true);
			}
			// Checks for events
			if(xmls[i].equals("events>")){
				eventteller = 0;
			}
			// Gets the right eventattributes
			if(eventteller == 1){en = Integer.parseInt(xmls[i].substring(3));}
			else if(eventteller == 3){
				for(EventType e : EventType.values()){
					if(xmls[i].substring(10).equals(e.toString()))
						sju = e;
				}
			}
			else if(eventteller == 5){fem = makeTimestamp(xmls[i].substring(5));}
			else if(eventteller == 7){
				
				Event e = new Event(en,sju,fem, s);
			}
			
		
		}
		
		//After the setup is complete, the controller should handle changes
		aModel.addPropertyChangeListener(controller);
		
		return aModel;
	}
	// Takes a String an uses it to make a timestamp
	public static Timestamp makeTimestamp(String time) {
		int aar = Integer.parseInt(time.substring(0, 4));
		int maaned = Integer.parseInt(time.substring(5, 7));
		int dag = Integer.parseInt(time.substring(8, 10));
		int timer = Integer.parseInt(time.substring(11, 13));
		int min = Integer.parseInt(time.substring(14, 16));
		int sek = Integer.parseInt(time.substring(17, 19));
		int nano = Integer.parseInt(time.substring(20));
		
		Timestamp t = new Timestamp(aar - 1900,maaned - 1,dag,timer,min,sek,(nano)*1000000);
		return t;
	}


		
	public static Element insertXmlRoom(Room aRoom) {
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
			Element e = insertXmlSensor(s); 
			element.appendChild(e);
		}
		
		return element;
	}

	public static String toXmlRoom(Room room) {
		String s = " " + Integer.toString(room.getID()) + "#" + Integer.toString(room.getRomNR()) + "#" + room.getRomType() + "#" + room.getRomInfo() + "#" + room.getModel().getID(); 
		return s;
	}

	public static String toXmlSensor(Sensor sensor) {
		String a = Integer.toString(sensor.getID());
		String b = (sensor.isAlarmState() ? "true" : "false");
		String c = sensor.getInstallationDate().toString();
		String d = Integer.toString(sensor.getBattery());
		String e = Integer.toString(sensor.getRoom().getID());
		
		return "#" + a + "#" + b + "#" + c + "#" + d + "#" + e;
	}

	public static Element insertXmlSensor(Sensor s) {
		Element main = new Element("Sensors");
		
		Element id = new Element("id");
		id.appendChild(Integer.toString(s.getID()));
		Element alarmState = new Element("alarmState");
		alarmState.appendChild((s.isAlarmState() ? "true" : "false"));
		Element timeStamp = new Element ("timeStamp");
		timeStamp.appendChild(s.getInstallationDate().toString());
		Element battery = new Element("battery");
		battery.appendChild(Integer.toString(s.getBattery()));
		
		main.appendChild(id);
		main.appendChild(alarmState);
		main.appendChild(timeStamp);
		main.appendChild(battery);
		
		for (Event e : s.getEvents()) {
			
			Element ele = insertXmlEvent(e);
			main.appendChild(ele);
		} 
		return main;
	}

	private static Element insertXmlEvent(Event e) {
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
		
		return events;
	}

	public static String toXml(Event event) {
		String a = Integer.toString(event.getSensor().getID());
		String b = event.getEventType().toString();
		String c = Integer.toString(event.getSensor().getRoom().getID());
		return "#"+ event.getID() + "#" + a + "#" + b + "#" + c + "#" + event.getTime();
	}
	
	
	public static String toXml(Model model) {
		return "<MODEL><ID>"+model.getID()+"</ID><ADDRES>"+model.getAdresse()+"</ADDRES></MODEL>";
	}
	public static Room toRoom(String roomString,Model model) {
		// TODO Auto-generated method stub
		
		System.out.println("This is a XMLString for a room:\n----------------\n\n");
		
		return null;
	}
	public static Sensor toSensor(String sensorString) {
		// TODO Auto-generated method stub
		
		System.out.println("This is a XMLString for a sensor:\n----------------\n\n");
		
		return null;
	}
	public static Event toEvent(String eventString) {
		// TODO Auto-generated method stub
		
		System.out.println("This is a XMLString for a event:\n----------------\n\n");
		
		return null;
	}


}

