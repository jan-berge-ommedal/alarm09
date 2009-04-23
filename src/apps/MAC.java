package apps;

import gui.MACgui;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.ArrayList;

import connection.ConnectionImplementation;
import connection.MACProtocol;
import connection.ModelEditControll;
import connection.TCPConnection;

import database.Database;

import model.Model;
import model.Room;
import model.Sensor;
import no.ntnu.fp.net.co.Connection;

/**
 * This is the logical part of the MAC (the centralized alarm-central) 
 * <br><br>It handles connections to LACs, and uses a database to store the systems' current state
 * 
 * @author Jan Berge Ommedal
 *
 */

public class MAC{
	private Connection macConnection;
	private ArrayList<LACAdaper> adapters = new ArrayList<LACAdaper>();	
	private Database database;
	private MACgui gui;
	private boolean running = true;
	
	private static int i = 0;
	
	public MAC() {
		gui = new MACgui();
		try {
			database = new Database("mysql.stud.ntnu.no","janberge_admin","1234","janberge_db");
			macConnection = new TCPConnection(666);
			loadAdapters();
			startMAC();
		} catch (Exception e) {
			System.err.println("Could not connect to database");
		}
	}
	
	private void loadAdapters() {
		for(int id : database.getIDs()){
			adapters.add(new LACAdaper(this, id));
		}
		
	}

	/**
	 * This method is used to create a new {@link LACAdaper} and add it to the MAC
	 * 
	 */
	private void startMAC(){
		while(running){
			Connection newConnection;
			try {
				newConnection = macConnection.accept();
			
			String idString = newConnection.receive();
			int LACid = Integer.parseInt(idString);
			
			boolean found = false;
			for (LACAdaper adapter : adapters) {
				if(adapter.getID()==LACid){
					adapter.initializeConnection(newConnection);
					found = true;
					break;
				}
			}
			newConnection.send((found ? "OK" :"NAK"));
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
	public class LACAdaper extends ModelEditControll{
		private MAC mac;
		private LACAdapterThread thread;
		
		private int LACid;
		
		public LACAdaper(MAC mac, int id) {
			this.mac = mac;
			this.LACid=id;
		}
		
		
		public void initializeConnection(Connection newConnection) {
			thread = new LACAdapterThread(this,newConnection); 
		}


		public int getID() {
			return LACid;
		}


		/**
		 * Stops the adapter, and removes it from the MAC   
		 * 
		 */
		public void stopAdapter(){
			try {
				thread.closeConnection();
				thread.stop();
				adapters.remove(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public Connection getConnection(){
			return thread.getConnection();
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
		
	

		@Override
		public int getNextRoomID(Room room) throws IOException {
			throw new IOException("Vet ikke hvordan dette skal gjøres enda");
		}

		@Override
		public int getNextSensorID(Sensor sensor) throws IOException {
			throw new IOException("Vet ikke hvordan dette skal gjøres enda");
		}



	}
	
	class LACAdapterThread extends Thread{
		private Connection connection;
		private LACAdaper adapter;
		
		public LACAdapterThread(LACAdaper adapter, Connection connection) {
			this.adapter=adapter;
			this.connection=connection;
			this.setName("Connection-"+(i++));
			start();
		}
		
		public Connection getConnection() {
			return connection;
		}

		public void closeConnection() throws IOException {
			connection.close();
		}

		/**
		 * The tread will wait for an incomming connection. When the adapter is connected to a LAC, it will first open a new adapter and then begin listening to the Connection until stopped by the stop()-method  
		 * 
		 */
		public void run(){
			try {
				while(true){
					String msg = connection.receive();
					MACProtocol.handleMSG(adapter,msg);
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	
	public static void main(String[] args) {
		new MAC();
	}
}
 
