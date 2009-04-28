package gui;

import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

import model.Room;

public class ComboBoxRenderer implements ComboBoxModel {

	private Room[] rooms = new Room[0];	
	private int selectedIndex = -1;
	
	private ArrayList<ListDataListener> listeners = new ArrayList<ListDataListener>();
	
	public ComboBoxRenderer(Room[] rooms) {
		this.rooms = rooms;
	}
	
	public Object getSelectedItem() {
		if (selectedIndex == -1) return null;
		return getElementAt(selectedIndex);
	}
	
	
	@Override
	public void setSelectedItem(Object arg0) {
		for (int i = 0; i < this.getSize(); i++) {
			if (arg0 == getElementAt(i)) {
				selectedIndex = i;
			}
		}	
	}

	@Override
	public void addListDataListener(ListDataListener arg0) {
		listeners.add(arg0);
		
	}

	@Override
	public Object getElementAt(int index) {
		if (index == 0) {
			return "<Create New Room>";
		}
		Room room = rooms[index-1];
		String result = "Nr: ";
		result += room.getRomNR() + ", Type: ";
		result += room.getRomType();
		return result;		
	}

	@Override
	public int getSize() {
		return this.rooms.length+1;
		
	}

	@Override
	public void removeListDataListener(ListDataListener arg0) {
		listeners.remove(arg0);
	}

}
