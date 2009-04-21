package unitTests;

import java.sql.Timestamp;
import java.text.ParseException;

import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;

import junit.framework.TestCase;

import connection.XmlSerializer;

import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import model.Event.EventType;

/**
 * 
 * A simple test that checks if the XMLSerializer is able to parse and reconstruct a model
 * 
 * @author Jan Berge Ommedal
 *
 */

public class XMLParsingTests extends TestCase{
	
	public void testParse(){
		Model m = new Model();
		Room r = new Room(3,51,"sdfgdfgh","asdfasdf");
		Sensor s = new Sensor(r);
		s.addEvent(new Event(5,EventType.ALARM,new Timestamp(42367)));
		s.addEvent(new Event(8,EventType.STARTUP, new Timestamp(4232)));
		r.addSensor(s);
		r.addSensor(new Sensor(6,false,67,new Timestamp(5674645),r));
		m.addRoom(r);
		
		System.out.println(m);
		
		String xmlParse = XmlSerializer.toXml(m);
		Model m2;
		try {
			m2 = XmlSerializer.toModel(xmlParse);
			assertEquals(m.toString(), m2.toString());
		} catch (ParseException e) {
			assertEquals("Ddidnt read proper format", true, false);
		}
		
	}

}
