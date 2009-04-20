package model;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import apps.LAC;

/**
 * The data-model of a LAC. 
 * <br><br>
 * This is a JAVA-Bean, that supports PropertyChange-listening. 
 * @author Jan Berge Ommedal
 *
 */

public class Model implements PropertyChangeListener {
 
	/* START DATAFELTER */
	private int id;
	private String adress = "<adress>";
	/* SLUTT DATAFELTER */
	
	private ArrayList<Sensor> sensorer = new ArrayList<Sensor>();
	private ArrayList<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

	
	/* SECTION OF SIMPLE GET & SET */
	
	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}
	
	public String getAdresse() {
		return adress;
	}

	public void setAdresse(String adresse) {
		this.adress = adresse;
	}
	
	
	public void addSensor(Sensor s){
		s.addPropertyChangeListener(this);
		sensorer.add(s);
	}
	
	public void removeSensor(Sensor s){
		sensorer.remove(this);
	}


	public ArrayList<Sensor> getSensors() {
		return sensorer;
	}
	
	/* END SECTION OF SIMPLE GET & SET */
	
	
	
	/**
	 * Adds the specified PropertyChangeListener listener to receive change-events from this model.
	 *  
	 * @param listener the listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener){
		listeners.add(listener);
	}
	
	/**
	 * Stops the given PropertyChangeListener from listening to this model.
	 *  
	 * @param listener the listener
	 */
	public void removePropertyChangeListener(Object object) {
		listeners.remove(object);
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		for(PropertyChangeListener pcl : listeners){
			pcl.propertyChange(e);
		}
		
	}



	
	
}
 
