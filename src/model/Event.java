package model;

import java.sql.Timestamp;

public class Event {
 
	private int id;
	private EventType eventType;
	private Timestamp time; 
	private Sensor lac;
	
	private static int nextID;
	
	public Event(EventType eventType, Timestamp time, Sensor lac) {
		this.eventType = eventType;
		this.time = time;
		this.lac = lac;
		id = nextID++;
	}
	
	public Event(int id, EventType eventType, Timestamp time, Sensor lac) {
		this.eventType = eventType;
		this.time = time;
		this.lac = lac;
		this.id = id;
	}
	
	public static void setNextID(int id){
		nextID=id;
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


	enum EventType{
		FALSEALARM, ALARM
		
	}
}
 
