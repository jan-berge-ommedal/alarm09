package gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import model.Model;
import model.Room;
import model.Sensor;

public class ModelListAdapter implements ListModel{
	private Model model;
	
	public ModelListAdapter(Model model) {
		this.model=model;
	}
	

	 @Override
	public void addListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
		
	}

	// @Override
	public Object getElementAt(int index) {
		return generateSensorList()[index];
	}

	@Override
	public int getSize() {
		return generateSensorList().length;
	}
	
	private Sensor[] generateSensorList(){
		ArrayList<Sensor> result = new ArrayList<Sensor>();
		for(Room r : model.getRooms()){
			result.addAll(r.getSensorer());
		}
		return result.toArray(new Sensor[result.size()]);
	}

	 @Override
	public void removeListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
		
	}




}
