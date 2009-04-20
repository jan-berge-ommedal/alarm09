package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import apps.LAC;

/**
 * The data-model of a Sensor. Currently it also features a Thread that reduces remaining battery over time.
 * <br><br>
 * This is a JAVA-Bean, that supports PropertyChange-listening. 
 * 
 * @author Jan Berge Ommedal
 *
 */

public class Sensor {
	
	/* START DATAFELTER */
	private int id;
	private boolean alarmState;
	private Room room;
	private Timestamp installationDate;
	private int battery = 100;
	/* SLUTT DATAFELTER */
	
	private ArrayList<Event> events = new ArrayList<Event>();
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	
	
	private static int nextID = 0; 
	
	
	/**
	 * 
	 * <strong>This constructor is used to load a predefined sensor(allready exists in storage)</strong>
	 * <br>(Remember to add belonging Events after using this constructor)
	 * 
	 * @param id a predefined id
	 * @param lac The sensors' LAC
	 * @param events  of Events
	 * @param room The room containing the sensor
	 * @param installationDate Installation-date of the sensor
	 */
	
	public Sensor(int id, Room room, Timestamp installationDate){
		this.id=id;
		if(id>=nextID)nextID=id+1;
		setup(room, installationDate);
		
	}
	
	/**
	 * <strong>This constructor is used to create a new sensor when a predefined ID doesn't exist</strong>  
	 * 
	 * @param lac The sensors' LAC
	 * @param events  of Events
	 * @param room The room containing the sensor
	 * @param installationDate Installation-date of the sensor
	 */
	
	public Sensor(Room room){
		this.id = nextID++;	
		setup(room,LAC.getTime());
	}
	
	
	/**
	 * A shared subroutine for constructors. It sets fields containing alarm-state, room and installationdate.  It also creates a new startup-event
	 * 
	 * @param lac
	 * @param events
	 * @param rom
	 * @param installationDate
	 */
	private void setup(Room rom, Timestamp installationDate) {	
		alarmState=false;
		setRoom(rom);
		setInstallationDate(installationDate);
		this.events = events;
		addEvent(new Event(computeNextEventID(), Event.EventType.STARTUP,LAC.getTime()));
		
	
		Thread t = new Thread(){
			/**
	 		* A thread that decreases the remaining batterytime
			*/
			public void run(){
				while(true){
					if(getBattery()>0)setBattery(getBattery()-1);
					try {
						Thread.currentThread().sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		t.start();
	}
	
	/**
	 * This method is used when new events is created to compute the next available ID.  
	 * @return The next available ID for events
	 */
	private int computeNextEventID(){
		int i=0;
		for(Event e: events){
			if(e.getID()>=i)i=e.getID()+1;
		}
		return i;
	}
	
	
	/**
	 * 
	 * @return The predefined sensors' ID
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * 
	 * @return the date of install or replacementdate of the sensor
	 */
	public Timestamp getInstallationDate() {
		return installationDate;
	}
	
	/**
	 * This method is used when the sensor is replaced
	 * 
	 * @param installationDate a Timestamp representing when the replacement was performed
	 */
	public void setInstallationDate(Timestamp installationDate) {
		Timestamp oldValue = this.installationDate;	
		this.installationDate = installationDate;
		pcs.firePropertyChange("INSTALLATIONDATE", oldValue, installationDate);
	}
	/**
	 * 
	 * @return if the sensor has an alarm
	 */
	public boolean isAlarmState() {
		return alarmState;
	}

	public void setAlarmState(boolean alarmState) {
		if(this.alarmState!=alarmState){
			boolean oldValue = this.alarmState;
			this.alarmState = alarmState;
			pcs.firePropertyChange("INSTALLATIONDATE", oldValue, alarmState);
		}
	}
	/**
	 * 
	 * @return the room containing the sensor
	 */
	public Room getRoom() {
		return room;
	}
	
	/**
	 * Sets the room-field of the sensor. It also clears the relation with the old room and notifies PropertyChangeListeners.
	 * 
	 * @param room
	 */
	public void setRoom(Room room) {
		if(this.room!=null)this.room.removeSensor(this);
		Room oldValue = this.room;
		room.addSensor(this);
		this.room = room;
		pcs.firePropertyChange("INSTALLATIONDATE", oldValue, room);
		if(this.room!=null){
		for(PropertyChangeListener pcl : pcs.getPropertyChangeListeners())
			room.addPropertyChangeListener(pcl);
		}
		
	}
	
	/**
	 * 
	 * @return ArrayList of this sensors' events 
	 */
	public ArrayList<Event> getEvents() {
		return events;
	}

	/**
	 * Adds the event to the eventlist and notifies PropertyChangeListeners.
	 * 
	 * @param event
	 */
	
	public void addEvent(Event event) {
		int oldValue = this.events.size();
		this.events.add(event);
		pcs.firePropertyChange("SENSORS", oldValue, oldValue+1);
	}
	
	/**
	 * 
	 * @return an int representing the percentage remaining batterytime 
	 */
	public int getBattery() {
		return battery;
	}
	
	
	/**
	 * 
	 * @param batteryRemaining remaining battery - and int between 0 and 100
	 */
	public void setBattery(int batteryRemaining) {
		int oldValue = this.battery;
		this.battery=batteryRemaining;
		pcs.firePropertyChange("BATTERY", oldValue, batteryRemaining);
	}
	
	
	/**
	 * 
	 * @return an int representing the number of False alarms   
	 */
	private int computeNumberOfFailures(){
		int count=0;
		for(Event e : events){
			if(e.getEventType() == Event.EventType.FALSEALARM)count++;
		}
		return count;
	}
	
	
	/**
	 * 
	 * @return the Mean Time To Failure (MTTF) for this sensor. If the sensor has not failed yet, it returns -1  
	 */ 
	public long computeMTTF() {
		int nrOfFailures = computeNumberOfFailures();
		if(nrOfFailures==0)return -1;
		return (System.currentTimeMillis()-Timestamp.parse(installationDate.toString()))/nrOfFailures;
	}
	
	/**
	 * This method tests if the sensor has low battery or if there is an alarm
	 *
	 * @return a boolean that is true if battery is good and no alarm
	 */
	public boolean testSensor() {
		return getBattery()>10 && isAlarmState();
	}
	
	/**
	 * Adds the specified PropertyChangeListener listener to receive change-events from this sensor.
	 *  
	 * @param listener the listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener){
		pcs.addPropertyChangeListener(listener);
	}
}
 
