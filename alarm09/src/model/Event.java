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
	
	/* START DATAFELTER */
	private final int id;
	private final EventType eventType;
	private final Timestamp time; 
	/* SLUTT DATAFELTER */
	
	public Event(int id, EventType eventType, Timestamp time) {
		this.eventType = eventType;
		this.time = time;
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



	public enum EventType{
		FALSEALARM, ALARM, STARTUP, BATTERYREPLACEMENT
	}
}
 
