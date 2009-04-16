package model;

import java.sql.Timestamp;

/**
 * Data-model of an Sensor-Event 
 * <br><br>
 * It is a standard JAVA-Bean, which cannot be modified once created. 
 * 
 * @author Jan Berge Ommedal
 */

public class Event {
 
	private final int id;
	private final EventType eventType;
	private final Timestamp time; 
	private final Sensor lac;
	

	public Event(int id, EventType eventType, Timestamp time, Sensor lac) {
		this.eventType = eventType;
		this.time = time;
		this.lac = lac;
		this.id = id;
	}
	

	public int getID() {
		return id;
	}

	public EventType getEventType() {
		return eventType;
	}
	
	public Timestamp getTime() {
		return time;
	}

	public Sensor getLac() {
		return lac;
	}


	public enum EventType{
		FALSEALARM, ALARM, STARTUP, BATTERYREPLACEMENT
	}
}
 
