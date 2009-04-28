package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AbstractPropertyChangeBean implements PropertyChangeListener{
	
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);


	/**
	 * Adds the specified PropertyChangeListener listener to receive change-events from this sensor.
	 *  
	 * @param listener the listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener){
		pcs.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener){
		pcs.removePropertyChangeListener(listener);
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		for(PropertyChangeListener listener : pcs.getPropertyChangeListeners()){
			listener.propertyChange(evt);
		}
		
	}

}
