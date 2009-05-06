package testHelp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.EOFException;
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

	public ServerConnection acceptNewConnection(){
		ServerConnection connection = new ServerConnection(this);
		connections.add(connection);
		return connection;
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


	public class ServerConnection{
		private ServerConnectionThread thread;

		private ServerConnection(SimpleHost host){
			thread = new ServerConnectionThread(host);
		}

		public ServerConnectionThread getThread() {
			return thread;
		}



	}



	public ConnectionImplementation getMainConnection() {
		return mainConnection;
	}

	public class ServerConnectionThread extends Thread{
		private ServerSocket serverSocket;
		private SimpleHost host;
		private Connection connection;
		private boolean running;

		public ServerConnectionThread(SimpleHost host) {
			this.setName("Connection "+host.getConnectionThreads().size());
			running=true;
			this.host=host;
			this.start();
		}

		public void run(){									
			try {
				connection = host.getMainConnection().accept();
				host.connectionEstablished(this);
				while(running){
					host.receivedMsg(connection.receive(),this);
				}
			} catch (SocketException e) {
				host.connectionClosed(this);
				running=false;

			}
			catch (EOFException e) {
				try {
				connection.close();
				running=false;
				
				}
				catch (Exception e1) {
					
				}
			}

			catch (IOException e) {
				running=false;
			}

		}


		public Connection getConnection() {
			return connection;
		}




	}



	public ArrayList<HostListener> getConnectionThreads() {
		return listeners;
	}




}
