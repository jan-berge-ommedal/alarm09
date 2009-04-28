package connection;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.Timestamp;

import connection.ConnectionStatusWrapper.ConnectionStatus;

import model.Event;
import model.Model;
import model.Room;
import model.Sensor;



public abstract class ModelEditControll implements PropertyChangeListener {
	protected Model model;
	protected ConnectionStatusWrapper connectionWrapper = new ConnectionStatusWrapper(ConnectionStatus.DISCONNECTED);
	
	/**
	 * This method tests all sensors 
	 * @return a boolean that is false when some of the sensors doesn't pass the test
	 */ 
	public boolean testSensors() {
		for (Sensor s : model.getSensors()) {
			if(!s.testSensor())return false;
		}	
		return true;
	}
	
	
	public Model getModel(){
		return model;
	}
	
	public void setModel(Model model){
		if(this.model!=null)this.model.removePropertyChangeListener(this);
		this.model = model;
		if(model!=null)model.addPropertyChangeListener(this);
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
	
	public ConnectionStatusWrapper getConnectionStatusWrapper(){
		return connectionWrapper;
	}
	

	/**
	 * Returns current time
	 * @return The current Timestamp
	 */
	public static Timestamp getTime() {
		return new Timestamp(System.currentTimeMillis());
	}
	
	
	public abstract Room insertRoom(int modelID, int roomNr, String roomType, String roomInfo) throws IOException;
	public abstract Sensor insertSensor(int roomID, boolean alarmState, int batteyStatus) throws IOException;
	public abstract Model insertModel(String adress) throws IOException;
	public abstract Event insertEvent(Event.EventType eventType);

	public abstract void deleteAllEvents(Sensor sensor);



}
