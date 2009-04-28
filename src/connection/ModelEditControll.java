package connection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.Timestamp;

import connection.ConnectionStatusWrapper.ConnectionStatus;

import model.AbstractPropertyChangeBean;
import model.Event;
import model.Model;
import model.Room;
import model.Sensor;



public abstract class ModelEditControll extends AbstractPropertyChangeBean {
	public static final String PC_MODELCHANGE = "MODELCHANGE";
	protected Model model;
	
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private ConnectionStatusWrapper connectionStatusWrapper;
	
	public ModelEditControll(ConnectionStatusWrapper connectionStatusWrapper) {
		this.connectionStatusWrapper = connectionStatusWrapper;
	}
	
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
	

	/**
	 * Sets the given parameter as the datamodel of the LAC, and add
	 * @param model the new model
	 */
	
	
	public void setModel(Model model){
		Model oldValue = this.model;
		if(this.model!=null)this.model.removePropertyChangeListener(this);
		this.model = model;
		if(model!=null)model.addPropertyChangeListener(this);
		pcs.firePropertyChange(PC_MODELCHANGE, oldValue, this.model);
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
		return connectionStatusWrapper;
	}
	

	/**
	 * Returns current time
	 * @return The current Timestamp
	 */
	public static Timestamp getTime() {
		return new Timestamp(System.currentTimeMillis());
	}
	
	/**
	 * Inserts a room into the ModelEditControllers' model, and ensures that persistent model is updated.
	 * 
	 * 
	 * @param modelID
	 * @param roomNr
	 * @param roomType
	 * @param roomInfo
	 * @return
	 * @throws IOException
	 */
	
	public abstract Room insertRoom(int modelID, int roomNr, String roomType, String roomInfo) throws IOException;
	
	/**
	 * Inserts a sensor into the ModelEditControllers' model, and ensures that persistent model is updated.
	 * 
	 * 
	 * @param modelID
	 * @param roomNr
	 * @param roomType
	 * @param roomInfo
	 * @return
	 * @throws IOException
	 */
	public abstract Sensor insertSensor(int roomID, boolean alarmState, int batteyStatus) throws IOException;
	
	/**
	 * Inserts a event into the ModelEditControllers' model, and ensures that persistent model is updated.
	 * 
	 * 
	 * @param modelID
	 * @param roomNr
	 * @param roomType
	 * @param roomInfo
	 * @return
	 * @throws IOException
	 */
	public abstract Event insertEvent(int roomID, int sensorID, Event.EventType eventType) throws IOException;

	public abstract void deleteAllEvents(Sensor sensor);

	
	public abstract void close();

}
