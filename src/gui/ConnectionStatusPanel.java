package gui;

/**
 * This is a sick component imo.
 * 
 * @author SimonGrøndahl
 *
 */

import javax.swing.*;

import connection.ConnectionStatusListener;
import connection.ConnectionStatusWrapper;
import connection.ConnectionStatusWrapper.ConnectionStatus;

public class ConnectionStatusPanel extends JLabel implements ConnectionStatusListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImageIcon ikon;
	
	
	public ConnectionStatusPanel(ConnectionStatusWrapper wrapper) {
		wrapper.addConnectionStatusListener(this);
		connectionStatusChanged(wrapper.getConnectionStatus());
	}
	
	public void connectionStatusChanged(ConnectionStatus newStatus) {
		
		this.setText(newStatus.toString());
		if(newStatus.toString().equals("CONNECTED")){
			ikon = new ImageIcon("sirkelgronn.jpg");
		}
		else if (newStatus.toString().equals("CONNECTING")){
			ikon = new ImageIcon("sirkelgul.jpg");
		}
		else if (newStatus.toString().equals("DISCONNECTED")){
			ikon = new ImageIcon("sirkelrod.jpg");
		}
		setIcon(ikon);
		
	}
}
