package model;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import apps.LAC;

/**
 * The data-model of a LAC. 
 * <br><br>
 * This is a JAVA-Bean, that supports PropertyChange-listening. 
 * @author Jan Berge Ommedal
 *
 */

public class Model implements ListModel, PropertyChangeListener {
 
	/* START DATAFIELDS */
	private int id;
	private String adress = "<adress>";
	/* END DATAFIELDS */
	
	
	private ArrayList<Room> rooms = new ArrayList<Room>();
	private ArrayList<Sensor> sensors = new ArrayList<Sensor>();
	private ArrayList<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public Model() {
		pcs.addPropertyChangeListener(this);
	}
	
	/* SECTION OF SIMPLE GET & SET */
	
	public int getID() {
		return id;
	}

	public void setID(int id) {
		int oldValue = this.id;
		this.id = id;
		pcs.firePropertyChange("LACID", oldValue, id);
	}
	
	public String getAdresse() {
		return adress;
	}

	public void setAdresse(String adresse) {
		String oldValue = this.adress;
		this.adress = adresse;		
		pcs.firePropertyChange("ADDRESS", oldValue, adresse);
	}
	
	
	public void addRoom(Room r){
		r.addPropertyChangeListener(this);
		rooms.add(r);
		for(Sensor s : r.getSensorer()){
			sensors.add(s);
		}
	}
	
	public void removeRoom(Room r){
		r.removePropertyChangeListener(this);
		rooms.remove(this);
	}


	public ArrayList<Room> getRooms() {
		return rooms;
	}
	
	/* END SECTION OF SIMPLE GET & SET */
	
	
	
	/**
	 * Adds the specified PropertyChangeListener listener to receive change-events from this model.
	 *  
	 * @param listener the listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener){
		listeners.add(listener);
	}
	
	/**
	 * Stops the given PropertyChangeListener from listening to this model.
	 *  
	 * @param listener the listener
	 */
	public void removePropertyChangeListener(Object object) {
		listeners.remove(object);
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		for(PropertyChangeListener pcl : listeners){
			pcl.propertyChange(e);
		}
		System.out.println("Change in model");
		
	}

	public Sensor[] getSensors() {
		ArrayList<Sensor> list = new ArrayList<Sensor>();
		for(Room r : getRooms()){
			for(Sensor s : r.getSensorer()){
				list.add(s);
			}
		}
		return list.toArray(new Sensor[list.size()]);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getElementAt(int index) {
		return sensors.get(index);
	}

	@Override
	public int getSize() {
		return sensors.size();
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
		
	}
	
	public String toString(){
		String result = "Model\n---------------\n";
		result+="ID: "+id+"\n";
		result+="Adresse: "+adress+"\n";
		for(Room r : rooms){
			result+="\t"+r.toString()+"\n";
		}
		return result;
	}


	public static void main(String[] args) {
		Model m = new Model();
		Room r = new Room(0,51,"sdf","sdfsd",m);
		m.addRoom(r);
		r.setRomInfo("NEW INFO");
		
	}

	
	
}
 

