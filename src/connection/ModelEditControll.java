package connection;

import model.Model;
import model.Room;
import model.Sensor;



public interface ModelEditControll {

	public boolean testSensors();
	public Model getModel();
	public boolean hasAlarm();
	public int getNextRoomID(Room room);
	public int getNextSensorID(Sensor sensor);

}
