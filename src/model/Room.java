package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class Room {
 
	private int id;
	private int romNR;
	private String romType;
	private String romInfo;	 
	private ArrayList<Sensor> sensorer = new ArrayList<Sensor>();
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	

	public Room(int id, int romNR, String romType, String romInfo) {
		this.id = id;
		this.romNR = romNR;
		this.romType = romType;
		this.romInfo = romInfo;
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
		pcs.firePropertyChange("ROMTYPE", oldValue, romType);
	}

	public String getRomInfo() {
		return romInfo;
	}

	public void setRomInfo(String romInfo) {
		String oldValue = this.romInfo;
		this.romInfo = romInfo;
		pcs.firePropertyChange("ROMINFO", oldValue, romInfo);
	}

	public ArrayList<Sensor> getSensorer() {
		return sensorer;
	}

	public void addSensor(Sensor sensor) {
		int oldValue = this.sensorer.size();
		this.sensorer.add(sensor);
		pcs.firePropertyChange("SENSORS", oldValue, sensorer.size());
	}
	
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

	
	
	
	
	 
}
 