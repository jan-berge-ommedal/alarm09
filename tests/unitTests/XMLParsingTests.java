package unitTests;

import help.AlarmHelp;

import java.sql.Timestamp;
import java.text.ParseException;

import apps.LAC;

import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;

import junit.framework.TestCase;

import connection.ModelEditController;
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
		ModelEditController controller = new DefaultModelEditController();
		Model model = AlarmHelp.getDefaultModel(controller);
		
		Room r = new Room(3,51,"sdfgdfgh","asdfasdf",model);
		Sensor s = new Sensor(0,false,90,LAC.getTime(),r,false);
		Event e = new Event(5,EventType.ALARM,new Timestamp(42367),s);
		
		
		// COMPLETE MODEL TRANSFER
		
		String xmlParse = XmlSerializer.toXmlComplete(model);
		Model m2;
		try {
			m2 = XmlSerializer.toModelComplete(xmlParse, controller);
			System.out.println("\n"+m2);
			assertEquals(model.toString(), m2.toString());
		} catch (ParseException ex) {
			assertEquals("Ddidnt read proper format", true, false);
		}
		
		
		
		
		String eventString = XmlSerializer.toEventString(e);
		Event e2 = XmlSerializer.toEvent(eventString, model);
		assertEquals(e.toString(), e2.toString());

		String sensorString = XmlSerializer.toSensorString(s);
		Sensor s2 = XmlSerializer.toSensor(sensorString, model);
		assertEquals(s.toString(), s2.toString());

		
		String roomString = XmlSerializer.toRoomString(r);
		Room r2 = XmlSerializer.toRoom(roomString, model);
		assertEquals(r.toString(), r2.toString());
		
		
	}

	public class DefaultModelEditController extends ModelEditController{

		
		
		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void deleteAllEvents(Sensor sensor) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
