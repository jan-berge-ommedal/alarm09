package connection;

import java.io.IOException;

import model.Model;
import model.Room;
import model.Sensor;



public abstract class ModelEditControll {
	protected Model model;
	
	/**
	 * This method tests all sensors 
	 * @return a boolean that is false when some of the LACs' doesn't pass the test
	 */ 
	public boolean testSensors() {
		for (Sensor s : model.getSensors()) {
			if(s.isAlarmState())return true;
		}	
		return false;
	}
	
	
	public Model getModel(){
		return model;
	}
	
	/**
	 * This method checks if some sensors has an alarm 
	 * @return a boolean that is true when some of the LACs' sensors have an alarm
	 */ 
	public boolean hasAlarm() {
		for (Sensor s : model.getSensors()) {
			if(s.isAlarmState())return true;
		}	
		return false;
	}
	
	public abstract int getNextRoomID(Room room) throws IOException;
	public abstract int getNextSensorID(Sensor sensor) throws IOException;

}
