package connection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.sql.Timestamp;

import apps.MAC.MACProtocol;

import no.ntnu.fp.net.co.Connection;

import connection.ConnectionStatusWrapper.ConnectionStatus;

import model.AbstractPropertyChangeSupport;
import model.Event;
import model.IDElement;
import model.Model;
import model.Room;
import model.Sensor;



public abstract class ModelEditController extends AbstractPropertyChangeSupport {
	public static final String PC_MODELCHANGE = "MODELCHANGE";
	protected Model model;
	
	protected ConnectionStatusWrapper connectionWrapper = new ConnectionStatusWrapper(ConnectionStatus.DISCONNECTED);
	
	private AbstractApplicationProtocol protocol;
	
	public ModelEditController(AbstractApplicationProtocol protocol) {
		this.protocol=protocol;
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
		if(this.model!=null)this.model.addPropertyChangeListener(this);
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
		return connectionWrapper;
	}
	
	protected abstract Connection getConnection();
	

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
	


	public abstract void deleteAllEvents(Sensor sensor);

	
	public abstract void close();
	


	public void propertyChange(PropertyChangeEvent e) {
		super.propertyChange(e);
		if(e.getSource() instanceof Sensor){
			Sensor sensor = (Sensor) e.getSource();
			if(e.getPropertyName().equals(Sensor.PC_EVENTADDED)){
				Event event = (Event) e.getNewValue();
				try {
					protocol.insertEvent(this, getConnection(),event);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if(protocol instanceof MACProtocol){
					MACProtocol macProtocol = (MACProtocol) protocol;
					event.setID(macProtocol.getDatabase().insertEvent(event.getID(), event.getEventType()));
				}
								
			}else{
				//FINN UT HVILKET ELEMENT SOM ER ENDRET
				String propteryName =  e.getPropertyName();
				if(propteryName.equals(IDElement.PC_IDCHANGED)){
					try {
						protocol.updateSensor(getConnection(),sensor.getID(),AbstractApplicationProtocol.ELEMENT_ID, e.getOldValue().toString(),e.getNewValue().toString());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}else if(propteryName.equals(Sensor.PC_ALARMSTATE)){
					Boolean newState = (Boolean) e.getNewValue();
					if(newState!=null){
						try {
							protocol.updateSensor(getConnection(), sensor.getID(), AbstractApplicationProtocol.ELEMENT_SENSOR_ALARMSTATE, e.getOldValue().toString(), e.getNewValue().toString());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}else if(propteryName.equals(Sensor.PC_INSTALLATIONDATE)){
					try {
						protocol.updateSensor(getConnection(), sensor.getID(), AbstractApplicationProtocol.ELEMENT_SENSOR_INSTALLATIONDATE, e.getOldValue().toString(), e.getNewValue().toString());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else if(propteryName.equals(Sensor.PC_BATTERY)){
					try {
						protocol.updateSensor(getConnection(), sensor.getID(), AbstractApplicationProtocol.ELEMENT_SENSOR_BATTERY, e.getOldValue().toString(), e.getNewValue().toString());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				
			}			
		}else if(e.getSource() instanceof Event){
				Event event = (Event) e.getSource();
				String propteryName =  e.getPropertyName();
				if(propteryName.equals(IDElement.PC_IDCHANGED)){
						try {
							protocol.updateEvent(getConnection(),event.getID(),AbstractApplicationProtocol.ELEMENT_ID, e.getOldValue().toString(),e.getNewValue().toString());
						} catch (ConnectException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}
				
				
		
		}else if(e.getSource() instanceof Room){
			Room  room = (Room)e.getSource();
				if(e.getPropertyName().equals(Room.PC_SENSORADDED)){
					Sensor sensor = (Sensor) e.getNewValue();
					try {
						protocol.insertSensor(this, getConnection(),sensor);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					if(protocol instanceof MACProtocol){
						MACProtocol macProtocol = (MACProtocol) protocol;
						sensor.setID(macProtocol.getDatabase().insertSensor(sensor.getRoom().getID(), sensor.isAlarmState(), sensor.getBattery()));
					}

				}else{
					try {
						String propteryName =  e.getPropertyName();
						if(propteryName.equals(IDElement.PC_IDCHANGED)){
							protocol.updateRoom(getConnection(),room.getID(),AbstractApplicationProtocol.ELEMENT_ID, e.getOldValue().toString(),e.getNewValue().toString());
						}else if(propteryName.equals(Room.PC_ROOMINFOCHANGED)){
							try {
								protocol.updateSensor(getConnection(), room.getID(), AbstractApplicationProtocol.ELEMENT_ROOM_ROOMINFO, e.getOldValue().toString(), e.getNewValue().toString());
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}else if(propteryName.equals(Room.PC_ROOMNRCHANGED)){
							try {
								protocol.updateSensor(getConnection(), room.getID(), AbstractApplicationProtocol.ELEMENT_ROOM_ROOMNR, e.getOldValue().toString(), e.getNewValue().toString());
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}else if(propteryName.equals(Room.PC_ROOMTYPECHANGED)){
							try {
								protocol.updateSensor(getConnection(), room.getID(), AbstractApplicationProtocol.ELEMENT_ROOM_ROOMTY, e.getOldValue().toString(), e.getNewValue().toString());
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
		}else if(e.getSource() instanceof Model){
			Model model = (Model) e.getSource();
			String propteryName =  e.getPropertyName();
			if(propteryName.equals(Model.PC_ROOMADDED)){
				Room room = (Room) e.getNewValue();
				try {
					protocol.insertRoom(this, getConnection(),room);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					if(protocol instanceof MACProtocol){
						MACProtocol macProtocol = (MACProtocol) protocol;
						room.setID(macProtocol.getDatabase().insertRoom(model.getID(), room.getRomNR(), room.getRomType(), room.getRomInfo()));
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}else if(propteryName.equals(Model.PC_ROOMREMOVED)){
				System.err.println("Functionallity to remove components not implemented");
				
			}else{
				if(propteryName.equals(IDElement.PC_IDCHANGED)){
					System.err.println("ModelEditController line 198: not implemented (Should never be needed;))");
				}else if(propteryName.equals(Model.PC_ADDRESS)){
					try {
						protocol.updateModel(this.getConnection(), model.getID(), AbstractApplicationProtocol.ELEMENT_MODEL_ADDRESS, e.getOldValue().toString(), e.getNewValue().toString());
					} catch (ConnectException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
		
			}
		}
		
	}

}
