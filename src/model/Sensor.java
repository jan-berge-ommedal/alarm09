package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class Sensor {
 
	private int id;
	private Timestamp installationDate;
	private boolean alarmState;
	private LAC lac;
	private Rom rom;
	private ArrayList<Event> events = new ArrayList<Event>();
	
	 
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Timestamp getInstallationDate() {
		return installationDate;
	}

	public void setInstallationDate(Timestamp installationDate) {
		this.installationDate = installationDate;
	}

	public boolean isAlarmState() {
		return alarmState;
	}

	public void setAlarmState(boolean alarmState) {
		this.alarmState = alarmState;
	}

	public LAC getLac() {
		return lac;
	}

	public void setLac(LAC lac) {
		this.lac = lac;
	}

	public Rom getRom() {
		return rom;
	}

	public void setRom(Rom rom) {
		rom.addSensor(this);
		this.rom = rom;
		
	}

	public ArrayList<Event> getEvents() {
		return events;
	}

	public void addEvent(Event event) {
		this.events.add(event);
	}

	public int checkBattery() {
		return 47;
	}
	 
	public Timestamp computeMTTF() {
		return new Timestamp(System.currentTimeMillis()-Timestamp.parse(installationDate.toString()));
	}
	 
	public boolean testSensor() {
		boolean result = (Math.random()>0.5 ? true : false);
		
		if(result)
			alarmState=true;
		else
			alarmState=false;
		
		//CREATE EVENT BASED ON RESULT
		if(result)
			events.add(new Event(Event.EventType.ALARM,lac.getTime(),this));
		else
			events.add(new Event(Event.EventType.FALSEALARM,lac.getTime(),this));
		
		return result;
	}	 
}
 
