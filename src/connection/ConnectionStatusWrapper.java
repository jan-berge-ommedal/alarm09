package connection;

import java.util.ArrayList;

public class ConnectionStatusWrapper {
	private ConnectionStatus connectionStatus;
	private ArrayList<ConnectionStatusListener> listeners = new ArrayList<ConnectionStatusListener>();
	
	public ConnectionStatusWrapper(ConnectionStatus status){
		connectionStatus = status;
	}
	
	
	public ConnectionStatus getConnectionStatus() {
		return connectionStatus;
	}


	public void setConnectionStatus(ConnectionStatus connectionStatus) {
		this.connectionStatus = connectionStatus;
		for (ConnectionStatusListener listener : listeners) {
			listener.connectionStatusChanged(connectionStatus);
		}
	}


	public void addConnectionStatusListener(ConnectionStatusListener listener){
		listeners.add(listener);
	}
	
	public void removeConnectionStatusListener(ConnectionStatusListener listener){
		listeners.remove(listener);
	}

	public 	enum ConnectionStatus{
		DISCONNECTED, CONNECTING, CONNECTED, ACCEPTING
	}
	
}
