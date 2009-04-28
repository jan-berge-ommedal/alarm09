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

public class Model extends AbstractPropertyChangeBean{
 
	private static final String PC_ROOMADDED = null;
	private static final String PC_ROOMREMOVED = null;
	/* START DATAFIELDS */
	private int id;
	private String adress = "<adress>";
	/* END DATAFIELDS */
	
	
	private ArrayList<Room> rooms = new ArrayList<Room>();
	private ArrayList<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public Model() {
		pcs.addPropertyChangeListener(this);
	}
	
	/* SECTION OF SIMPLE GET & SET */
	
	public int getID() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	
	public void setID(int id) {
		int oldValue = this.id;
		this.id = id;
		pcs.firePropertyChange("LACID", oldValue, id);
	}
	
	public String getAdresse() {
		return adress;
	}
	
	/**
	 * 
	 * @param adresse
	 */

	public void setAdresse(String adresse) {
		String oldValue = this.adress;
		this.adress = adresse;		
		pcs.firePropertyChange("ADDRESS", oldValue, adresse);
	}
	
	
	/**
	 * 
	 * @param room
	 */
	
	
	public void addRoom(Room room){
		// TODO SJEKK AT DENNE FUNKER
		room.addPropertyChangeListener(this);
		rooms.add(room);
		pcs.firePropertyChange(PC_ROOMADDED, null, room);
	}
	
	/**
	 * 
	 * @param room
	 */
	
	public void removeRoom(Room room){
		room.removePropertyChangeListener(this);
		if(rooms.remove(room))pcs.firePropertyChange(PC_ROOMREMOVED, null, room);
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


	
	public String toString(){
		String result = "Model\n---------------\n";
		result+="ID: "+id+"\n";
		result+="Adresse: "+adress+"\n";
		for(Room r : rooms){
			result+="\t"+r.toString()+"\n";
		}
		return result;
	}


	 @Override
	public void propertyChange(PropertyChangeEvent e) {
		for(PropertyChangeListener pcl : listeners){
			pcl.propertyChange(e);
		}
		
		
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
	
	public static void main(String[] args) {
		Model m = new Model();
		m.addPropertyChangeListener(new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println(evt.getSource());
			}
			
		});
		Room r = new Room(0,53,"sdfg","swddfsg",m);
		m.addRoom(r);
		r.setRomInfo("sdfssdd");
		r.setRomInfo("sdfssdd");
		r.setRomInfo("sdfssdd2");
	}

	public Sensor getSensor(int i) {
		Sensor result = null;
		for(Sensor s : this.getSensors()){
			if(s.getID()==i)result=s;
		}
		return result;
	}

	public Room getRoom(int romID) {
		Room result = null;
		for(Room r : rooms){
			if(r.getID()==romID)result=r;
		}
		return result;
	}
}
 

