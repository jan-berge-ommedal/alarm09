package unitTests;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;

import apps.LAC;

import sun.misc.GC.LatencyRequest;

import junit.framework.TestCase;

import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import model.Event.EventType;


public class ModelTests extends TestCase implements PropertyChangeListener {
	private PropertyChangeEvent lastEvent;
	
	
	
	/**
	 * Denne testen sjekker at lytting på modell fungerer
	 * Dette er kun et eksempel på en UnitTest
	 * @author Jan Berge Ommedal
	 * 
	 */
	public void testModelListening(){
		Model m = new Model();
		Room r = new Room(0,5,"TYPE","INFO",m);
		
		Sensor s1 = new Sensor(0,false,50,LAC.getTime(),r,false);
		Sensor s2 = new Sensor(1,false,50,LAC.getTime(),r,false);
		
		m.addPropertyChangeListener(this);
		m.setID(5);
		
		//Tester at melding mottas
		assertNotNull("Didnt receive change notification", lastEvent);
	
		
		//Tester at eventsa er riktig navngitt
		m.setID(4);
		assertEquals("Property not correctly named", "LACID", lastEvent.getPropertyName());
		m.setAdresse("asdfsadf");
		assertEquals("Property not correctly named", "ADDRESS", lastEvent.getPropertyName());
		r.setRomInfo("NEW INFO");
		assertEquals("Property not correctly named", "ROOMINFO", lastEvent.getPropertyName());
		r.setRomNR(9);
		assertEquals("Property not correctly named", "ROOMNR", lastEvent.getPropertyName());
		r.setRomType("NEW TYPE");
		assertEquals("Property not correctly named", "ROOMTYPE", lastEvent.getPropertyName());
		
		
		
		lastEvent = null;
		m.addPropertyChangeListener(this);
		m.removePropertyChangeListener(this);
		m.setID(5);

		//Tester at en listener også blir remova skikkelig
		assertNull("Listener was not removed properly", lastEvent);
		
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		lastEvent = evt;
		System.out.println("CHANGE-EVENT ("+evt.getPropertyName()+"):\n----------------------------\n"+evt);
	}
	
	
	/**
	 * 	This test verifies that MTTF is correctly computed (Requirement 8)
	 * 
	 */
	//FIXME Simon: MTTF-test
	public void testMTTF(){
		Model m = new Model();
		Room r = new Room(1, 12, "Pokerrom", "Dette er et svett pokerrom", m);
		long currentTime = System.currentTimeMillis();
		Timestamp currentTimeTimestamp = new Timestamp(currentTime);
		long currentTimeOffset = currentTime + 50000;
		
		Sensor s = new Sensor(2, false, 100, currentTimeTimestamp , r, true);
		m.addRoom(r);
		r.addSensor(s);
		
		s.setInstallationDate(new Timestamp(System.currentTimeMillis()-50000));
		long expected = 50000;
		assertEquals(s.computeMTTF(), -1);
		
		Event e1 = new Event(2,EventType.ALARM,currentTimeTimestamp, s);
		s.addEvent(e1);
		assertEquals(true, checkNearness(s.computeMTTF(),expected ));
		
		Event e2 = new Event(3,EventType.ALARM,currentTimeTimestamp, s);
		s.addEvent(e2);
		expected = 25000;
		assertEquals(true, checkNearness(s.computeMTTF(),expected ));
		
		Event e3 = new Event(4,EventType.ALARM,currentTimeTimestamp, s);
		s.addEvent(e2);
		expected = 16667;
		assertEquals(true, checkNearness(s.computeMTTF(),expected ));
	}

	private boolean checkNearness(long computeMTTF, long expected) {
		return (computeMTTF< expected+500 && computeMTTF>expected-500 ? true : false);
	}
	
}
