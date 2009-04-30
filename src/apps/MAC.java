package apps;

import gui.MACgui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ListModel;
import javax.swing.Timer;
import javax.swing.event.ListDataListener;

import connection.AbstractApplicationProtocol;
import connection.ConnectionImplementation;
import connection.ConnectionStatusWrapper;
import connection.ModelEditController;
import connection.TCPConnection;
import connection.XmlSerializer;
import connection.ConnectionStatusWrapper.ConnectionStatus;

import database.Database;

import model.Event;
import model.IDElement;
import model.Model;
import model.Room;
import model.Sensor;
import model.Event.EventType;
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
	
	public static final int SERVERPORT = 666;
	public static final String MACIP = "localhost";
	
	
	
	
	public MAC(boolean useGUI) {
		if(useGUI)gui = new MACgui(this);
		boolean connectedToDatabase=false;
		while(!connectedToDatabase){
			try {
				databaseConnectionWrapper.setConnectionStatus(ConnectionStatus.CONNECTING);
				database = new Database("mysql.stud.ntnu.no","janberge_admin","1234","janberge_db");
				databaseConnectionWrapper.setConnectionStatus(ConnectionStatus.CONNECTED);
				loadAdapters();
				connectedToDatabase=true;
			} catch (Exception e) {
				System.err.println("Could not connect to database");
				databaseConnectionWrapper.setConnectionStatus(ConnectionStatus.DISCONNECTED);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
			}
		}
		macConnection = new TCPConnection(SERVERPORT);
		RunThread thread = new RunThread(this);
		thread.start();
	}
	
	public void addAdapterListListener(PropertyChangeListener listener){
		adapters.addAdapterListListener(listener);
	}
	
	private void loadAdapters() {
		for(int id : database.getIDs()){
			LACAdapter adapter = new LACAdapter(this,id);
			adapters.add(adapter);
		}
	}
	
	public ConnectionStatusWrapper getDatabaseConnectionWrapper(){
		return databaseConnectionWrapper;
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
	public class LACAdapter extends ModelEditController{
		
		private MAC mac;
		private LACAdapterThread thread;
		
		
		
		public LACAdapter(MAC mac, int id) {
			super(new MACProtocol());
			this.mac = mac;
			database.getLACModel(id,this);
		}
		
		
		public void initializeConnection(Connection newConnection) {
			thread = new LACAdapterThread(this,newConnection);
			connectionWrapper.setConnectionStatus(ConnectionStatus.CONNECTED);
		}
		


		/**
		 * Stops the adapter, and removes it from the MAC   
		 * 
		 */
		public void stopAdapter(){
			thread.stop();
			try {
				thread.closeConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			adapters.remove(this);
			
		}
	
	

	


		public Connection getConnection() {
			return thread.connection;
		}


		


	




	}
	
	class LACAdapterThread extends Thread{
		private Connection connection;
		private LACAdapter adapter;
		
		public LACAdapterThread(LACAdapter adapter, Connection connection) {
			this.adapter=adapter;
			this.connection=connection;
			this.setName("LACAdapter-"+adapter.getModel().getID());
			start();
		}
	
		private void closeConnection() throws IOException {
			adapter.getConnectionStatusWrapper().setConnectionStatus(ConnectionStatus.DISCONNECTED);
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
					this.adapter.getProtocol().handleMSG(msg,this.adapter,this.connection);
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				try {
					this.closeConnection();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
	}
	
	public class LacAdapterList implements ListModel{
		private ArrayList<LACAdapter> lacAdapters = new ArrayList<LACAdapter>();
		
		private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

		public void addListDataListener(ListDataListener l) {
			// TODO Auto-generated method stub
			
		}
		
		public void addAdapterListListener(PropertyChangeListener listener){
			pcs.addPropertyChangeListener(listener);
		}

		public void remove(LACAdapter adaper) {
			int oldValue = lacAdapters.size();
			lacAdapters.remove(adaper);
			pcs.firePropertyChange("LACADAPTERS", oldValue, lacAdapters.size());
			
		}

		public void add(LACAdapter adaper) {
			int oldValue = lacAdapters.size();
			lacAdapters.add(adaper);
			pcs.firePropertyChange("LACADAPTERS", oldValue, lacAdapters.size());
			
		}

		public Object getElementAt(int index) {
			return lacAdapters.get(index);
		}
		
		public LACAdapter getElementLACAdapterAt(int index) {
			return lacAdapters.get(index);
		}

		public int getSize() {
			return lacAdapters.size();
		}

		public void removeListDataListener(ListDataListener l) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class RunThread extends Thread{
		public MAC mac;
		
		
		
			
		public RunThread(MAC mac) {
			this.mac = mac;
		}


		public void run(){
			while(true){
				Connection newConnection = null;
		
				try {
					newConnection = macConnection.accept();
					
					
					String idString = newConnection.receive();
					if(idString.startsWith("ID")){
						// THE LAC REQUESTED TO LOAD MODEL FROM DB
						int LACid = Integer.parseInt(idString.substring(2));
						boolean found = false;
						for (LACAdapter adapter : adapters.lacAdapters) {
							if(adapter.getModel().getID()==LACid){
								if(adapter.getConnectionStatusWrapper().getConnectionStatus() == ConnectionStatus.DISCONNECTED){
									adapter.initializeConnection(newConnection);
									found = true;
								}
								break;
							}
						}
						try {
							//IF THE GIVEN ID FOUND AN-D FREE, RETURN "ACK" ELSE RETURN "NAK"
							newConnection.send((found ? "ACK" :"NAK"));
						} catch (ConnectException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if(idString.startsWith("NEW")){//NY LAC, ber om ID
						//THE LAC CREATE A NEW MODEL IN DB
						String adress = idString.substring(3);
						int returnid =-1;
						try {
							returnid = database.insertLAC(adress);
							newConnection.send(""+returnid);
							
						} catch (ConnectException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(returnid!=-1){
							LACAdapter adapter = new LACAdapter(mac,returnid);
							adapter.initializeConnection(newConnection);
							adapters.add(adapter);
						}
					}
				} catch (BindException e1) {
					System.err.println("The MAC is unable to open port. It will now close");
					System.exit(0);
				} catch (SocketTimeoutException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
						
		}
		}
	}

	
	public static void main(String[] args) {
		new MAC(true);
	}
	


	public class MACProtocol extends AbstractApplicationProtocol {
		
		public MACProtocol() {
			System.out.println("Constructed MACProtocol");
		}

		@Override
		public void handleMSG(String msg, ModelEditController controller, Connection connection){
			super.handleMSG(msg, controller, connection);
			LACAdapter adapter = (LACAdapter)controller;
			
				if(msg.startsWith("GETMODEL")){
					try {
						connection.send(XmlSerializer.toXmlComplete(adapter.getModel()));
					} catch (ConnectException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if(checkFlag(msg, INSERTROOM)){
					
					String roomstring = removeFlag(msg, INSERTROOM);
					
					Room room = XmlSerializer.toRoom(roomstring, controller.getModel());
					
					sendACK(connection);
					
				}
				else if(checkFlag(msg, INSERTSENSOR)){

					String sensorstring = removeFlag(msg, INSERTSENSOR);
					
					Sensor sensor = XmlSerializer.toSensor(sensorstring,controller.getModel());
					
					sendACK(connection);
				}
				else if(checkFlag(msg, INSERTEVENT)){
					
					String eventstring = removeFlag(msg, INSERTEVENT);
					
					Event event = XmlSerializer.toEvent(eventstring,controller.getModel());
					
					sendACK(connection);
				}
				

			
		}

		@Override
		public synchronized void insertEvent(ModelEditController controller, Connection connection, Event event) throws ConnectException, IOException {
			System.out.println("----------\nMAC: INSERT EVENT\n\n");
			String eventString = XmlSerializer.toEventString(event);
			System.out.println("Sending: "+eventString);
			connection.send(INSERTEVENT + eventString);
			receiveACK(connection);
			System.out.println("MAC: End insertEvent()\n------------\n\n");
			
		}

		@Override
		public synchronized void insertRoom(ModelEditController controller, Connection connection, Room room) throws ConnectException, IOException {
			System.out.println("----------\n\nMAC: INSERT ROOM\n");
			String roomString = XmlSerializer.toRoomString(room);
			System.out.println("Sending: "+roomString);
			connection.send(INSERTROOM + roomString);
			receiveACK(connection);
			System.out.println("MAC: End insertRoom()\n------------\n\n");
			
		}

		@Override
		public synchronized void insertSensor(ModelEditController controller, Connection connection, Sensor sensor) throws ConnectException, IOException {
			System.out.println("----------\nMAC: INSERT SENSOR\n\n");
			String sensorString = XmlSerializer.toSensorString(sensor);
			System.out.println("Sending: "+sensorString);
			connection.send(INSERTSENSOR + sensorString);
			receiveACK(connection);
			System.out.println("MAC: End insertSensor()\n------------\n\n");
		}
		
		public Database getDatabase(){
			return database;
		}

		

	}

	
}
 
