package connection;

import connection.ConnectionStatusWrapper.ConnectionStatus;

public interface ConnectionStatusListener {
	
	public void connectionStatusChanged(ConnectionStatus newStatus);

}
