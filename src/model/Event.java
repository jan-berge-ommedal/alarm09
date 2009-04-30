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

public class Event extends IDElement{
 
	/* START DATAFELTER */
	private final EventType eventType;
	private final Timestamp time; 
	/* SLUTT DATAFELTER */
	
	private Sensor sensor;
	

	
	
	
	public Event(int id, EventType eventType, Timestamp time,Sensor sensor) {
		super(id);
		this.eventType = eventType;
		this.time = time;
		this.sensor=sensor;
		
		this.sensor.addEvent(this);
	}
	
	

	public Sensor getSensor() {
		return sensor;
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
		s+="Event: "+getID()+" - "+eventType+" - "+time;
		return s;
	}

	
}
 
