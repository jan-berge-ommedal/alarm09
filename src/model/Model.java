package model;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import connection.ModelEditController;

import apps.LAC;

/**
 * The data-model of a LAC. 
 * <br><br>
 * This is a JAVA-Bean, that supports PropertyChange-listening. 
 * @author Jan Berge Ommedal
 *
 */

public class Model extends IDElement{
 
	public static final String PC_ROOMADDED = "ROOMADDED";
	public static final String PC_ROOMREMOVED = "ROOMREMOVED";
	
	public static final String PC_ADDRESS = "ADDRESSCHANGE";
	
	
	/* START DATAFIELDS */

	private String adress = "An Address";
	/* END DATAFIELDS */
	
	
	private ArrayList<Room> rooms = new ArrayList<Room>();
	
	public Model(ModelEditController controller, int id) {
		super(id);
		controller.setModel(this);
	}
	
	/* SECTION OF SIMPLE GET & SET */
	
	
	
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
		pcs.firePropertyChange(PC_ADDRESS, oldValue, adresse);
	}
	
	
	/**
	 * 
	 * @param room
	 */
	
	
	public void addRoom(Room room){
		int oldSize = rooms.size();
		room.addPropertyChangeListener(this);
		rooms.add(room);
		pcs.firePropertyChange(PC_ROOMADDED, oldSize, room);
	}
	
	/**
	 * 
	 * @param room
	 */
	
	public void removeRoom(Room room){
		int oldSize = rooms.size();
		room.removePropertyChangeListener(this);
		if(rooms.remove(room))pcs.firePropertyChange(PC_ROOMREMOVED, oldSize, room);
	}


	public ArrayList<Room> getRooms() {
		return rooms;
	}
	
	/* END SECTION OF SIMPLE GET & SET */
	
	
	


	
	public String toString(){
		String result = "Model\n---------------\n";
		result+="ID: "+getID()+"\n";
		result+="Adresse: "+adress+"\n";
		for(Room r : rooms){
			result+="\t"+r.toString()+"\n";
		}
		result+="\n\n";
		return result;
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
	
	

	public Sensor getSensor(int i) {
		for(Sensor s : this.getSensors()){
			if(s.getID()==i)return s;
		}
		return null;
	}

	public Room getRoom(int romID) {
		for(Room r : rooms){
			if(r.getID()==romID)return r;
		}
		return null;
	}
	
	public Event[] getEvents(){
		ArrayList<Event> list = new ArrayList<Event>();
		for(Sensor s : getSensors()){
			for(Event e : s.getEvents()){
				list.add(e);
			}
		}
		return list.toArray(new Event[list.size()]);
	}

	public Event getEvent(int eventID) {
		for(Event e : getEvents()){
			if(e.getID()==eventID)return e;
		}
		return null;
	}
}
 

