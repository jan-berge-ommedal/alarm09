package testHelp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;


import no.ntnu.fp.net.co.Connection;

import connection.ConnectionImplementation;

public class SimpleHost implements HostListener{
	private ArrayList<ServerConnection> connections = new ArrayList<ServerConnection>();
	private ArrayList<HostListener> listeners = new ArrayList<HostListener>();
	private ConnectionImplementation mainConnection;
	
	
	public SimpleHost(int port) throws UnknownHostException, IOException {
			mainConnection = new ConnectionImplementation(port);
	}
	
	public void addHostListener(HostListener listener){
		listeners.add(listener);
	}
	
	public void acceptNewConnection(){
		connections.add(new ServerConnection(this));
	}
	
	@Override
	public void connectionClosed(ServerConnectionThread thread) {
		connections.remove(thread);
		for(HostListener listener : listeners){
			listener.connectionClosed(thread);
		}
	}

	@Override
	public void connectionEstablished(ServerConnectionThread thread) {
		for(HostListener listener : listeners){
			listener.connectionEstablished(thread);
		}
		
	}

	@Override
	public void receivedMsg(String msg, ServerConnectionThread thread) {
		for(HostListener listener : listeners){
			listener.receivedMsg(msg,thread);
		}
	}


	class ServerConnection{
	
		private ServerConnection(SimpleHost host){
			Thread thread = new ServerConnectionThread(host);
		}
		
	}
	
	

	public ConnectionImplementation getMainConnection() {
		return mainConnection;
	}
	
	public class ServerConnectionThread extends Thread{
		private ServerSocket serverSocket;
		private SimpleHost host;
		private Connection connection;
			
			public ServerConnectionThread(SimpleHost host) {
				this.setName("Connection "+host.getConnectionThreads().size());
				this.host=host;
				this.start();
			}

			public void run(){									
						try {
							connection = host.getMainConnection().accept();
							host.connectionEstablished(this);
							while(true){
								host.receivedMsg(connection.receive(),this);
							}
						} catch (SocketException e) {
								host.connectionClosed(this);
						
						}catch (IOException e) {
							e.printStackTrace();
						}
		
						
					
						
					
					
				
			}
			
			
	}



	public ArrayList<HostListener> getConnectionThreads() {
		return listeners;
	}


	

}
