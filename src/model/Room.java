package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import connection.ModelEditController;

import apps.LAC;

/**
 * This is a JAVA-Bean that represents a room. It supports PropertyChange-listening. 
 * 
 * @author Jan Berge Ommedal
 *
 */

public class Room extends IDElement{
	
	public static final String PC_SENSORADDED = "SENSOR_ADDED";
	public static final String PC_SENSOREMOVED = "SENOR_REMOVED";

	public static final String PC_ROOMNRCHANGED = "ROOM_NR_CHANGED";
	public static final String PC_ROOMTYPECHANGED = "ROOM_TYPE_CHANGED";
	public static final String PC_ROOMINFOCHANGED = "ROOM_INFO_CHANGED";
	
	
	/* START DATAFIELDS */
	private int romNR;
	private String romType;
	private String romInfo;
	/* END DATAFIELDS */
	
	private Model model;
	
	
	private ArrayList<Sensor> sensorer = new ArrayList<Sensor>();
	

	
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
		super(id);
		this.romNR = romNR;
		this.romType = romType;
		this.romInfo = romInfo;
		
		this.model=model;
		this.model.addRoom(this);
	}
	
	
	/* SECTION OF SIMPLE GET & SET */

	public Model getModel() {
		return model;
	}




	public int getRomNR() {
		return romNR;
	}
	
	
	/**
	 * 
	 * @param romNR
	 */

	public void setRomNR(int romNR) {
		int oldValue = this.romNR;
		this.romNR = romNR;
		pcs.firePropertyChange(PC_ROOMNRCHANGED, oldValue, romNR);

	}

	public String getRomType() {
		return romType;
	}
	
	/**
	 * 
	 * @param romType
	 */

	public void setRomType(String romType) {
		String oldValue = this.romType;
		this.romType = romType;
		pcs.firePropertyChange(PC_ROOMTYPECHANGED, oldValue, romType);
	}

	public String getRomInfo() {
		return romInfo;
	}
	
	/**
	 * 
	 * @param romInfo
	 */

	public void setRomInfo(String romInfo) {
		String oldValue = this.romInfo;
		this.romInfo = romInfo;
		pcs.firePropertyChange(PC_ROOMINFOCHANGED, oldValue, romInfo);
	}

	public ArrayList<Sensor> getSensorer() {
		return sensorer;
	}
	/* END SECTION OF SIMPLE GET & SET */
	
	
	/**
	 * 
	 * @param notifyListeners true if listeners should be notified
	 */
	public void addSensor(Sensor sensor) {
		int oldSize = this.sensorer.size();
		this.sensorer.add(sensor);
		sensor.addPropertyChangeListener(this);
		pcs.firePropertyChange(PC_SENSORADDED, oldSize, sensor);
	}
	
	/**
	 * Removes the given sensor
	 * @param sensor
	 */
	
	public void removeSensor(Sensor sensor) {
		int oldSize = this.sensorer.size();
		sensor.removePropertyChangeListener(this);
		if(sensorer.remove(sensor))pcs.firePropertyChange(PC_SENSOREMOVED, oldSize, sensor);
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
		s+="Room: "+romType+" "+getID()+" - "+romNR+" - "+romInfo+"\n";
		for(Sensor sens : sensorer){
			s+="\t\t"+sens.toString()+"\n";
		}
		
		return s;
		
	}


	
	
	 
}
 
