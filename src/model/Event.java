package model;

import java.io.IOException;
import java.sql.Timestamp;

import connection.ModelEditController;

import apps.LAC;

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
	
	private Sensor sensor;
	
	private static int nextEventID = 0;
	
	
	
	
	public Event(int id, EventType eventType, Timestamp time,Sensor sensor) {
		this.eventType = eventType;
		this.time = time;
		this.id = id;
		this.sensor=sensor;
		if(id>=nextEventID)nextEventID=id+1;
		
		this.sensor.addEvent(this);
	}
	
	public void setID(int insertEvent) {
		// TODO Auto-generated method stub
		
	}

	public Sensor getSensor() {
		return sensor;
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
		FALSEALARM, ALARM, STARTUP, BATTERYREPLACEMENT, DETECTED, SUCCESSFULTEST, FAILEDTEST
	}
	
	public String toString(){
		String s = "";
		s+="Event: "+id+" - "+eventType+" - "+time;
		return s;
	}

	
}
 
