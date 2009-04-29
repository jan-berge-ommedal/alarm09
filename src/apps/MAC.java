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

import connection.ConnectionImplementation;
import connection.ConnectionStatusWrapper;
import connection.LACProtocol;
import connection.MACProtocol;
import connection.ModelEditController;
import connection.TCPConnection;
import connection.ConnectionStatusWrapper.ConnectionStatus;

import database.Database;

import model.Event;
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
	
	private static final int TIMEOUT = 600000;
	
	private final MACProtocol protocol = new MACProtocol();
	
	
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
	public class LACAdapter extends ModelEditController implements ActionListener{
		
		private MAC mac;
		private LACAdapterThread thread;
		
		private Timer timer;
		
		
		public LACAdapter(MAC mac, int id) {
			this.mac = mac;
			database.getLACModel(id,this);
			timer = new Timer(TIMEOUT,this);
		}
		
		
		public void initializeConnection(Connection newConnection) {
			thread = new LACAdapterThread(this,newConnection);
			connectionWrapper.setConnectionStatus(ConnectionStatus.CONNECTED);
			timer.start();
		}
		
		public void resetTimeout(){
			timer.restart();
		}


		/**
		 * Stops the adapter, and removes it from the MAC   
		 * 
		 */
		public void stopAdapter(){
			close();
			adapters.remove(this);
			
		}
	
		
		@Override
		public void close() {
			// Does nothing
		}
	
	

		public void propertyChange(PropertyChangeEvent e) {
			super.propertyChange(e);
			if(e.getSource() instanceof Sensor){
				Sensor sensor = (Sensor) e.getSource();
				if(e.getPropertyName().equals(Sensor.PC_EVENTADDED)){
					Event event = (Event) e.getNewValue();
					try {
						protocol.insertEvent(this, thread.connection,event);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					event.setID(database.insertEvent(event.getID(), event.getEventType()));				
				}else{
					try {
						protocol.updateSensor(this, thread.connection,sensor);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}			
			}else if(e.getSource() instanceof Room){
				Room  room = (Room)e.getSource();
					if(e.getPropertyName().equals(Room.PC_SENSORADDED)){
						Sensor sensor = (Sensor) e.getNewValue();
						try {
							protocol.insertSensor(this, thread.connection,sensor);
						} catch (IOException e1) {
							e1.printStackTrace();
						}

					}else{
						try {
							protocol.updateRoom(this,thread.connection,room);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
			}else if(e.getSource() instanceof Model){
				Model model = (Model) e.getSource();
				if(e.getPropertyName().equals(Model.PC_ROOMADDED)){
					Room room = (Room) e.getNewValue();
					try {
						protocol.insertRoom(this, thread.connection,room);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					try {
						room.setID(database.insertRoom(model.getID(), room.getRomNR(), room.getRomType(), room.getRomInfo()));
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}else{
					try {
						protocol.updateModel(this,thread.connection);
					} catch (ConnectException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		}


		@Override
		public void deleteAllEvents(Sensor sensor) {
			database.removeSensorsEvents(sensor.getID());
			sensor.deleteAllEvents();
			System.err.println("Delete not implemented in AppProtocol");
			/*
			try {
				//protocol.deleteAllEvents(thread.connection,sensor);
			} catch (IOException e) {
				e.printStackTrace();
			}
			*/
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource()==timer){
				System.out.println("LACAdapter "+getModel().getID()+": Connection timed out");
				connectionWrapper.setConnectionStatus(ConnectionStatus.DISCONNECTED);
				try {
					thread.closeConnection();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				thread.stop();
				timer.stop();
			}
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
					protocol.handleMSG(msg,this.adapter,this.connection);
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
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
							//IF THE GIVEN ID FOUND AND FREE, RETURN "ACK" ELSE RETURN "NAK"
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
						try {
							int returnid = database.insertLAC(adress);
							newConnection.send(""+returnid);
							LACAdapter adapter = new LACAdapter(mac,returnid);
							Model m = new Model(adapter);
							m.setID(returnid);
							adapter.initializeConnection(newConnection);
							adapters.add(adapter);
						} catch (ConnectException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}finally{
							newConnection.send("NAK");
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
	
	
}
 
