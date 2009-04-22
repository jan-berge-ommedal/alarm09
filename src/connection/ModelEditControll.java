package connection;

import model.Model;



public interface ModelEditControll {

	public boolean testSensors();
	public Model getModel();
	public boolean hasAlarm();

}
