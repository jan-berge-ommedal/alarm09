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

import connection.ModelEditController;

import apps.LAC;

/**
 * The data-model of a Sensor. Currently it also features a Thread that reduces remaining battery over time.
 * <br><br>
 * This is a JAVA-Bean, that supports PropertyChange-listening. 
 * 
 * @author Jan Berge Ommedal
 *
 */

public class Sensor extends IDElement{


	public static final String PC_EVENTADDED = "EVENT_ADDED";
	public static final String PC_EVENTREMOVED = "EVENT_REMOVED";

	public static final String PC_ALARMSTATE = "EVENT_REMOVED";
	public static final String PC_INSTALLATIONDATE = "INSTALLATIONDATECHANGED";
	public static final String PC_BATTERY = "BATTERYCHANGED";

	/* START DATAFIELDS */
	private Alarm alarmState;
	private Timestamp installationDate;
	private int battery = 100;
	/* END DATAFIELDS */

	private ArrayList<Event> events = new ArrayList<Event>();

	private Room room;

	private static final int COUNTDOWNDELAY = 30000;

	public enum Alarm {
		ACTIVATED, DEACTIVATED, UNCONFIRMED
	}

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

	public Sensor(int id, Alarm alarm, int battery, Timestamp installationDate,Room room){
		super(id);
		this.alarmState=alarm;
		this.battery=battery;
		this.installationDate=installationDate;
		this.room=room;

		room.addSensor(this);



	}
	/**
	 * A thread that decreases the remaining batterytime
	 */

	public void startSensor(){

		new Event(-1,Event.EventType.STARTUP,LAC.getTime(),this);	
		
		Thread t = new Thread(){
				public void run(){

					while(true){
						if(getBattery()>0)setBattery(getBattery()-5);
						try {
							Thread.currentThread().sleep(20000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

			}
		};
		t.start();
		 
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
		pcs.firePropertyChange(PC_INSTALLATIONDATE, oldValue, installationDate);
	}
	/**
	 * 
	 * @return the alarmstate of the sensor. True=Alarm, False=No Alarm, Null=Non Confirmed 
	 */
	public Alarm getAlarmState() {
		return alarmState;

	}


	/**
	 * 
	 * 
	 * 
	 * @param alarmState true = confirmed alarmed, null= nonconfirmed alarm, false = no alarm
	 */

	public void setAlarmState(Alarm alarmState,boolean createNewEvent) {
		Alarm oldValue = this.alarmState;
		//oldValue=false;
		this.alarmState = alarmState;
		pcs.firePropertyChange(PC_ALARMSTATE, oldValue, alarmState);
		
		if(createNewEvent){
			if(this.alarmState == Alarm.ACTIVATED && oldValue == Alarm.DEACTIVATED) {
				new Event(-1,EventType.ALARM,LAC.getTime(),this);
			}
			else if(this.alarmState == Alarm.UNCONFIRMED && oldValue == Alarm.ACTIVATED) {
				//new Event(-1,EventType.ALARM,LAC.getTime(),this);
			}
			else if(this.alarmState == Alarm.UNCONFIRMED && oldValue == Alarm.DEACTIVATED) {
				//new Event(-1,EventType.FALSEALARM,LAC.getTime(),this);
				new CountdownTimer(COUNTDOWNDELAY);
				
			}
			else if(this.alarmState == Alarm.DEACTIVATED && oldValue == Alarm.UNCONFIRMED) {
				new Event(-1,EventType.FALSEALARM,LAC.getTime(),this);
			
			}
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
		int oldSize = events.size();
		event.addPropertyChangeListener(this);
		this.events.add(event);
		pcs.firePropertyChange(PC_EVENTADDED, oldSize, event);
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
	public void replaceBattery(ModelEditController mec) throws IOException {
		setBattery(100);
		new Event(-1,Event.EventType.BATTERYREPLACEMENT,LAC.getTime(),this);
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
		if(getAlarmState() != Alarm.ACTIVATED){
			result+="Alarm ";
		}

		boolean booleanResult = batteryOk && (getAlarmState() == Alarm.DEACTIVATED);
		if(booleanResult)result+="Ok, ";
		new Event(-1,(booleanResult ? EventType.SUCCESSFULTEST : EventType.FAILEDTEST),LAC.getTime(),this);
		System.out.println(result);
		return booleanResult;
	}


	public Room getRoom() {
		return room;
	}

	public String toString(){
		String s = "";
		s+="Sensor: "+getID()+" - "+alarmState+" - "+battery+" - "+installationDate+"\n";
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
		pcs.firePropertyChange(PC_BATTERY, oldValue, this.battery);
	}




	public void removeEvent(Event event) {
		int oldSize = events.size();
		if(events.remove(event))pcs.firePropertyChange(PC_EVENTREMOVED, oldSize, event);

	}

	public void activateAlarm(){
		setAlarmState(Alarm.UNCONFIRMED,true);
	}
	
	class CountdownTimer extends Timer implements ActionListener{
		public CountdownTimer(int delay) {
			super(delay,null);
			this.addActionListener(this);
			this.setRepeats(false);
			this.start();
		}

			@Override
			public void actionPerformed(ActionEvent e) {
				if(alarmState==Alarm.UNCONFIRMED);
					setAlarmState(Alarm.ACTIVATED, true);
			}
	}
}

