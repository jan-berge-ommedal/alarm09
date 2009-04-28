package gui;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

import model.Room;

public class ComboBoxRenderer implements ComboBoxModel {

	private Room[] rooms;	
	
	public Object getSelectedItem() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String getRoomString(Room room) {
		String result = "Nr: ";
		result += room.getRomNR() + ", Type: ";
		result += room.getRomType();
		return result;
	}

	@Override
	public void setSelectedItem(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addListDataListener(ListDataListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getElementAt(int arg0) {
		return getRoomString(rooms[arg0]);
	}

	@Override
	public int getSize() {
		return this.rooms.length;
	}

	@Override
	public void removeListDataListener(ListDataListener arg0) {
		// TODO Auto-generated method stub
		
	}

}
