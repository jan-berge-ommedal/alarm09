package apps;

import gui.MACgui;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.ArrayList;

import connection.MACProtocol;
import connection.TCPConnection;

import database.Database;

import model.Model;
import no.ntnu.fp.net.co.Connection;
import no.ntnu.fp.net.co.ConnectionImpl;

/**
 * This is the logical part of the MAC (the centralized alarm-central) 
 * <br><br>It handles connections to LACs, and uses a database to store the systems' current state
 * 
 * @author Jan Berge Ommedal
 *
 */

public class MAC {
	private Connection macConnection;
	private ArrayList<LACAdaper> adapters = new ArrayList<LACAdaper>();	
	private Database database;
	private MACgui gui;
	
	private static int i = 0;
	
	public MAC() {
		gui = new MACgui();
		try {
			database = new Database("mysql.stud.ntnu.no","janberge_admin","1234","janberge_db");
			macConnection = new TCPConnection(666);
			createNewLACAdaper();
		} catch (Exception e) {
			System.err.println("Could not connect to database");
		}
	}
	
	/**
	 * This method is used to create a new {@link LACAdaper} and add it to the MAC
	 * 
	 */
	private void createNewLACAdaper(){
		adapters.add(new LACAdaper(this));
	}
	
	
	/**
	 * The MAC contains a {@link no.ntnu.fp.net.co.Connection Connection} that is used to create new connections through the accept()-method.   
	 * @return the MACs' main connection
	 */
	public Connection getMainConnection() {
		return macConnection;
	}
	
	public Database getDatabase() {
		return database;
	}
	
	public ArrayList<LACAdaper> getLACAdapters(){
		return adapters;
	}
	

	/**
	 * This adapter is a thread, that handles a connection from the MAC to a LAC. The MAC will create an instance of this class for each LAC it communicates with 
	 * 
	 * @author Jan Berge Ommedal
	 *
	 */
	public class LACAdaper extends Thread{
		private Connection connection;
		private MAC mac;
		private Model model;
		private boolean running;
		
		public LACAdaper(MAC mac) {
			this.mac = mac;
			running = true;
			this.setName("Connection-"+(i++));
			start();
		}
		
		/**
		 * The tread will wait for an incomming connection. When the adapter is connected to a LAC, it will first open a new adapter and then begin listening to the Connection until stopped by the stop()-method  
		 * 
		 */
		public void run(){
			try {
				connection = mac.getMainConnection().accept();
				mac.createNewLACAdaper();
				while(running){
					String msg = connection.receive();
					MACProtocol.handleMSG(this,msg);
					System.out.println("asdfsadf");
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Stops the adapter, and removes it from the MAC   
		 * 
		 */
		public void stopAdapter(){
			running=false;
			try {
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.stop();
			adapters.remove(this);
		}
		
		public Connection getConnection(){
			return connection;
		}

		public MAC getMAC() {
			return mac;
		}

		public boolean hasModel() {
			return model!=null;
		}
		
		public void setModel(Model model){
			this.model=model;
		}
		
		public Model getModel() {
			return model;
		}

	}

	
	public static void main(String[] args) {
		new MAC();
	}
}
 
