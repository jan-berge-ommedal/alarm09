package apps;

import gui.MACgui;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import connection.ConnectionImplementation;
import connection.ConnectionStatusWrapper;
import connection.MACProtocol;
import connection.ModelEditControll;
import connection.TCPConnection;
import connection.ConnectionStatusWrapper.ConnectionStatus;

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
	private LacAdapterList adapters = new LacAdapterList();	
	private Database database;
	private ConnectionStatusWrapper databaseConnectionWrapper = new ConnectionStatusWrapper(ConnectionStatus.DISCONNECTED);
	
	private MACgui gui;
	private boolean running = true;
	
	private static int i = 0;
	
	public static final int SERVERPORT = 666;
	
	public MAC() {
		gui = new MACgui();
		try {
			databaseConnectionWrapper.setConnectionStatus(ConnectionStatus.CONNECTING);
			database = new Database("mysql.stud.ntnu.no","janberge_admin","1234","janberge_db");
			databaseConnectionWrapper.setConnectionStatus(ConnectionStatus.CONNECTED);
			loadAdapters();
			
			System.out.println("MAC is running");
			startMAC();
		} catch (Exception e) {
			e.printStackTrace();
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
		try {
			macConnection = new TCPConnection(SERVERPORT);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		while(running){
			Connection newConnection = null;
	
			try {
					newConnection = macConnection.accept();
				
				String idString = newConnection.receive();
				if(idString.startsWith("ID")){
					int LACid = Integer.parseInt(idString.substring(2));
					boolean found = false;
					for (LACAdaper adapter : adapters.lacAdapters) {
						if(adapter.getID()==LACid){
							adapter.initializeConnection(newConnection);
							found = true;
							break;
						}
					}
					try {
						newConnection.send((found ? "OK" :"NAK"));
					} catch (ConnectException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if(idString.startsWith("NEW")){//NY LAC, ber om ID
					String adress = idString.substring(3);
					int returnid = database.insertLAC(adress);
					try {
						newConnection.send(""+returnid);
						Model m = new Model();
						m.setID(returnid);
						LACAdaper adapter = new LACAdaper(this,returnid);
						adapter.setModel(m);
						adapter.initializeConnection(newConnection);
						adapters.add(adapter);
					} catch (ConnectException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
			}catch (SocketTimeoutException e1) {
					// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
	
	public LacAdapterList getLACAdapterList(){
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
			connectionWrapper.setConnectionStatus(ConnectionStatus.CONNECTED);
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


		@Override
		public void propertyChange(PropertyChangeEvent arg0) {
			System.err.println("Handle This");
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
	
	class LacAdapterList implements ListModel{
		private ArrayList<LACAdaper> lacAdapters = new ArrayList<LACAdaper>();

		@Override
		public void addListDataListener(ListDataListener l) {
			// TODO Auto-generated method stub
			
		}

		public void remove(LACAdaper adaper) {
			lacAdapters.remove(adaper);
			
		}

		public void add(LACAdaper adaper) {
			lacAdapters.add(adaper);
			
		}

		@Override
		public Object getElementAt(int index) {
			return lacAdapters.get(index);
		}

		@Override
		public int getSize() {
			return lacAdapters.size();
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			// TODO Auto-generated method stub
			
		}
		
	}

	
	public static void main(String[] args) {
		new MAC();
	}
	
	
}
 
