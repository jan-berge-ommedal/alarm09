package unitTests;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import sun.misc.GC.LatencyRequest;

import junit.framework.TestCase;

import model.Model;
import model.Room;
import model.Sensor;


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
		Room r = new Room(0,5,"TYPE","INFO");
		Sensor s1 = new Sensor(r);
		Sensor s2 = new Sensor(r);
		
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
		
	}
	
	
	/**
	 * 	This test verifies that MTTF is correctly computed (Requirement 8)
	 * 
	 */
	//FIXME Simon: MTTF-test
	public void testMTTF(){
		
	}
	
}
