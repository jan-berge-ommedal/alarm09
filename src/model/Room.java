package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import connection.ModelEditControll;

import apps.LAC;

/**
 * This is a JAVA-Bean that represents a room. It supports PropertyChange-listening. 
 * 
 * @author Jan Berge Ommedal
 *
 */

public class Room {
	
	public static final String PC_SENSORADDED = "SENSOR_ADDED";
	
	/* START DATAFIELDS */
	private int id;
	private int romNR;
	private String romType;
	private String romInfo;
	/* END DATAFIELDS */
	
	private Model model;
	
	
	private ArrayList<Sensor> sensorer = new ArrayList<Sensor>();
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	/**
	 *
	 * 
	 * @param id
	 * @param romNR
	 * @param romType
	 * @param romInfo
	 * @param model
	 */

	public Room(int id, int romNR, String romType, String romInfo, Model model) {
		this.id = id;
		this.romNR = romNR;
		this.romType = romType;
		this.romInfo = romInfo;
		
		this.model=model;
	}
	
	
	/* SECTION OF SIMPLE GET & SET */

	public Model getModel() {
		return model;
	}

	public int getID() {
		return id;
	}


	public int getRomNR() {
		return romNR;
	}

	public void setRomNR(int romNR) {
		int oldValue = this.romNR;
		this.romNR = romNR;
		pcs.firePropertyChange("ROMNR", oldValue, romNR);

	}

	public String getRomType() {
		return romType;
	}

	public void setRomType(String romType) {
		String oldValue = this.romType;
		this.romType = romType;
		pcs.firePropertyChange("SENSORADDED", oldValue, romType);
	}

	public String getRomInfo() {
		return romInfo;
	}

	public void setRomInfo(String romInfo) {
		String oldValue = this.romInfo;
		this.romInfo = romInfo;
		pcs.firePropertyChange("ROOMINFO", oldValue, romInfo);
	}

	public ArrayList<Sensor> getSensorer() {
		return sensorer;
	}
	/* END SECTION OF SIMPLE GET & SET */
	
	
	/**
	 * Adds the given sensor
	 * @param sensor
	 */
	public void addSensor(Sensor sensor) {
		int oldValue = this.sensorer.size();
		this.sensorer.add(sensor);
		pcs.firePropertyChange("SENSORS", oldValue, sensorer.size());
	}
	
	/**
	 * Removes the given sensor
	 * @param sensor
	 */
	
	public void removeSensor(Sensor sensor) {
		int oldValue = this.sensorer.size();
		this.sensorer.remove(sensor);
		pcs.firePropertyChange("SENSORS", oldValue, sensorer.size());
	}
	
	/**
	 * Adds the specified PropertyChangeListener listener to receive change-events from this room.
	 *  
	 * @param listener the listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener){
		pcs.addPropertyChangeListener(listener);
	}

	@SuppressWarnings("unchecked")
	public Iterator iterator() {
		return sensorer.iterator();
	}

	
	public String toString(){
		String s = "";
		s+="Room: "+romType+" "+id+" - "+romNR+" - "+romInfo+"\n";
		for(Sensor sens : sensorer){
			s+="\t\t"+sens.toString()+"\n";
		}
		
		return s;
		
	}
	

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
	
	
	 
}
 
