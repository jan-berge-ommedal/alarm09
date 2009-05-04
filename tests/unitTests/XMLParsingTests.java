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
import model.Sensor.Alarm;

/**
 * 
 * A simple test that checks if the XMLSerializer is able to parse and reconstruct a model
 * 
 * @author Jan Berge Ommedal
 *
 */

public class XMLParsingTests extends TestCase{
	
	public void testParse(){
		ModelEditController controller = AlarmHelp.getDefaultModelController();
		Model model = AlarmHelp.getDefaultModel(controller);
		
		Room r = new Room(-1,51,"sdfgdfgh","asdfasdf",model);
		Room sensorLess = new Room(-1,51,"sdfgdfgh","asdfasdf",model);
		Sensor s = new Sensor(-1,Alarm.DEACTIVATED,90,LAC.getTime(),r);
		Sensor eventLess = new Sensor(-1,Alarm.DEACTIVATED,90,LAC.getTime(),r);
		Event e = new Event(-1,EventType.ALARM,new Timestamp(42367),s);
		
		
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

		String sensorString = XmlSerializer.toSensorString(eventLess);
		Sensor s2 = XmlSerializer.toSensor(sensorString, model);
		assertEquals(eventLess.toString(), s2.toString());

		
		String roomString = XmlSerializer.toRoomString(sensorLess);
		Room r2 = XmlSerializer.toRoom(roomString, model);
		assertEquals(sensorLess.toString(), r2.toString());
		
		
	}


}

