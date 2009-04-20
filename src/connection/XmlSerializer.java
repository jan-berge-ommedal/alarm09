/*
 * Created on Oct 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package connection;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;

import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

/**
 * @author Simon Gr�ndahl
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
	
	public Model toModel(String xml) throws ParseException {
		Model aModel = new Model();
		String[] xmls = xml.split("<");
		Element groupElement = new Element("xml");
		aModel.setID(Integer.parseInt(xmls[1].substring(5)));
		aModel.setAdresse(xmls[3].substring(9));
		
		
		
		return aModel;
	}

//    public Person toPerson(String xml) throws java.io.IOException, java.text.ParseException, nu.xom.ParsingException {
//		nu.xom.Builder parser = new nu.xom.Builder(false);
//		nu.xom.Document doc = parser.build(xml, "");
//		return assemblePerson(doc.getRootElement());
//    }
	
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
	
//	private Person assemblePerson(Element personElement) throws ParseException {
//		String name = null, email = null;
//		Date date = null;
//		Element element = personElement.getFirstChildElement("name");
//		if (element != null) {
//			name = element.getValue();
//		}
//		element = personElement.getFirstChildElement("email");
//		if (element != null) {
//			email = element.getValue();
//		}
//		element = personElement.getFirstChildElement("date-of-birth");
//		if (element != null) {
//			date = parseDate(element.getValue());
//		}
//		return new Person(name, email, date);
//	}
	
	/**
	 * TODO: handle this one to avoid duplicate code
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	private Date parseDate(String date) throws ParseException {
		DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM, java.util.Locale.US);
		return format.parse(date);
	}

}

