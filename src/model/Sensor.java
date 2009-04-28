package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.Timer;

import model.Event.EventType;

import connection.ModelEditControll;

import apps.LAC;

/**
 * The data-model of a Sensor. Currently it also features a Thread that reduces remaining battery over time.
 * <br><br>
 * This is a JAVA-Bean, that supports PropertyChange-listening. 
 * 
 * @author Jan Berge Ommedal
 *
 */

public class Sensor extends AbstractPropertyChangeBean{
	
	public static final String PC_EVENTADDED = "EVENT_ADDED";
	
	/* START DATAFIELDS */
	private int id;
	private Boolean alarmState;
	private Timestamp installationDate;
	private int battery = 100;
	/* END DATAFIELDS */
	
	private ArrayList<Event> events = new ArrayList<Event>();

	private Room room;
	
	public final static int ALARMCOUNDOWN = 15000;
	
	/**
	 * 
	 * <strong>This constructor is used to load a predefined sensor(already exists in storage)</strong>
	 * <br>(Remember to add belonging Events after using this constructor)
	 * 
	 * @param id a predefined id
	 * @param lac The sensors' LAC
	 * @param events  of Events
	 * @param room The room containing the sensor
	 * @param installationDate Installation-date of the sensor
	 */
	
	public Sensor(int id, boolean alarm, int battery, Timestamp installationDate,Room r, boolean startSensor){
		this.id=id;
		this.alarmState=alarm;
		this.battery=battery;
		this.installationDate=installationDate;
		this.room=r;
		if(startSensor){
			addEvent(new Event(Event.EventType.STARTUP,this));	
			Thread t = new Thread(){
				/**
		 		* A thread that decreases the remaining batterytime
				*/
				public void run(){
					while(true){
						if(getBattery()>0)setBattery(getBattery()-5);
						try {
							Thread.currentThread().sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			t.start();
		}
	}
	
	
	
	/**
	 * 
	 * @return The predefined sensors' ID
	 */
	public int getID() {
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
	 * @return the alarmstate of the sensor. True=Alarm, False=No Alarm, Null=Non Confirmed 
	 */
	public Boolean isAlarmState() {
		return alarmState;
		
	}
	
	
	/**
	 * 
	 * 
	 * 
	 * @param alarmState true = confirmed alarmed, null= nonconfirmed alarm, false = no alarm
	 */

	public void setAlarmState(Boolean alarmState) {
		if(this.alarmState!=alarmState){
			if(alarmState==null){
				//TODO TEST SENSOR!!
				
				//Alarmen settes til Non Confirmed. Følgende timer setter den til true etter angitt tid 
				Timer t = new Timer(ALARMCOUNDOWN,new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						setAlarmState(true);
					}
				});
				t.setRepeats(false);
				t.start();
			}
			Boolean oldValue = this.alarmState;
			this.alarmState = alarmState;
			// TODO FIX THIS
			EventType type = (alarmState==null ? EventType.DETECTED : (alarmState ? EventType.ALARM : EventType.FALSEALARM));
			this.events.add(new Event(0,type,LAC.getTime(),this));
			pcs.firePropertyChange("INSTALLATIONDATE", oldValue, alarmState);
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
		this.events.add(event);
		pcs.firePropertyChange(PC_EVENTADDED, null, event);
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
	 * @throws IOException 
	 */
	public void replaceBattery(ModelEditControll mec) throws IOException {
		setBattery(100);
		this.addEvent(new Event(Event.EventType.BATTERYREPLACEMENT,this));
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
		Timestamp t;
		return (System.currentTimeMillis()-installationDate.getTime())/nrOfFailures;
	}
	
	/**
	 * This method tests if the sensor has low battery or if there is an alarm
	 *
	 * @return a boolean that is true if battery is good and no alarm
	 */
	public boolean testSensor() {
		String result = "Sensortest for Sensor "+getID()+": ";
		
		boolean batteryOk = getBattery()>20;
		if(batteryOk)result+="Low battery ";
		if(!isAlarmState())result+="Alarm ";
		
		boolean booleanResult = batteryOk && !isAlarmState();
		if(booleanResult)result+="Ok, ";
		this.addEvent(new Event((booleanResult ? EventType.SUCCESSFULTEST : EventType.FAILEDTEST),this));
		System.out.println(result);
		return booleanResult;
	}
	

	public Room getRoom() {
		return room;
	}
	
	public String toString(){
		String s = "";
		s+="Sensor: "+id+" - "+alarmState+" - "+battery+" - "+installationDate+"\n";
		for(Event e : events){
			s+="\t\t\t"+e.toString()+"\n";
		}
		
		return s;
	}

	public void deleteAllEvents() {
		int oldValue = events.size();
		events.removeAll(events);
		pcs.firePropertyChange("EVENTS", oldValue, 0);
	}

	/**
	 * 
	 * 	@param battery
	 */
	
	public void setBattery(int battery) {
		int oldValue = this.battery;
		this.battery=battery;
		pcs.firePropertyChange("BATTERY", oldValue, this.battery);
	}
}
 
