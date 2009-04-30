package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class IDElement extends AbstractPropertyChangeSupport{
	public static final String PC_IDCHANGED = "ID_CHANGED";
	
	private int id;
	
	public IDElement(int id) {
		this.id=id;
		
	}


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
		if(id==0){
			System.out.println("and the sinner is....");
		}
		
		pcs.firePropertyChange(PC_IDCHANGED, oldValue, id);
	}
}
