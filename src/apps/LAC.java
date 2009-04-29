package apps;

import gui.LACgui;
import help.AlarmHelp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.jar.JarInputStream;

import javax.swing.JOptionPane;

import com.mysql.jdbc.jdbc2.optional.ConnectionWrapper;

import connection.ConnectionImplementation;
import connection.ConnectionStatusWrapper;
import connection.LACProtocol;
import connection.ModelEditController;
import connection.TCPConnection;
import connection.ConnectionStatusWrapper.ConnectionStatus;

import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import model.Event.EventType;
import no.ntnu.fp.net.co.Connection;

/**
 * This is the logical LAC 
 * 
 * @author Jan Berge Ommedal
 *
 */


public class LAC extends ModelEditController{
	private Connection connection;
	
	private LACgui gui;

	private boolean running = true;
	
	private RunThread thread;
	
	
	
	private static final int STARTPORT = 700;
	private static String defaultAdres = "My Adresss";
	
	/**
	 * This constructor is used when creating a completly new LAC
	 * 
	 */
	public LAC(boolean useGUI) {
		if(useGUI)gui = new LACgui(this);
		connection = new TCPConnection(STARTPORT);
		connectWithRetry();
		
		try {
			createNewLAC();
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		thread = new RunThread();
		thread.start();
	}


	private void createNewLAC() throws ConnectException, IOException {
		connection.send("NEW"+defaultAdres);
		String reveiceID = connection.receive();
		System.out.println(reveiceID);
		int modelID = Integer.parseInt(reveiceID);
		Model m = new Model(this);
		m.setID(modelID);
		m.setAdresse(defaultAdres);
	}
	
	/**
	 * This constructor is used when loading a LAC for storage
	 * @param id an int, the stored id of the LAC
	 */
	public LAC(int id,boolean useGUI) {
		if(useGUI)gui = new LACgui(this);
		connectWithRetry();

		
		try {
			connection.send("ID"+id);
			String ack = connection.receive();
			if(!ack.equals("ACK")){
				System.err.println("Refused by the MAC. Exiting");
				System.exit(0);
			}
			LACProtocol.receiveCompleteModel(connection, id,this);
		} catch (ParseException e) {
			System.err.println("Could not load model");
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread = new RunThread();
		thread.start();
	}
	

	private void connectWithRetry() {
		boolean connected = false;
		while(!connected){
			try{
				connect(5);
				connected = true;
			}catch(IOException e) {
				//Try to reconnect
				try {
					this.connectionWrapper.setConnectionStatus(ConnectionStatus.DISCONNECTED);
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void connect(int i) throws IOException {
		if(connection==null)connection = new TCPConnection(STARTPORT);	
		connectionWrapper.setConnectionStatus(ConnectionStatus.CONNECTING);
		while(i>0){
			try {
				connection.connect(InetAddress.getByName(MAC.MACIP), MAC.SERVERPORT);
				connectionWrapper.setConnectionStatus(ConnectionStatus.CONNECTED);
				return;
			}catch (IOException e) {
				System.err.println("Reattempting to connect ("+(i-1)+" retries left)");
				i--;
			}
		}
		throw new IOException("Could not connect to the MAC");
	}
	
	
	
	
	
	
	

	


	// @Override
	public void propertyChange(PropertyChangeEvent e) {
		super.propertyChange(e);
		if(e.getSource() instanceof Sensor){
			Sensor sensor = (Sensor) e.getSource();
			if(e.getPropertyName().equals(Sensor.PC_EVENTADDED)){
				Event event = (Event) e.getNewValue();
				try {
					LACProtocol.insertEvent(connection, event,this);
				} catch (IOException e1) {
					System.err.println("LAC: The event was not inserted sucessfully on th MAC. Removing it on the LAC..");
					model.removePropertyChangeListener(this);
					sensor.removeEvent(event);
					model.addPropertyChangeListener(this);
				}
				
			}else{
				try {
					LACProtocol.updateSensor(connection, sensor);
				} catch (ConnectException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}			
		}else if(e.getSource() instanceof Room){
			Room  room = (Room)e.getSource();
				if(e.getPropertyName().equals(Room.PC_SENSORADDED)){
					Sensor sensor = (Sensor)e.getNewValue();
					try {
						LACProtocol.insertSensor(connection, sensor,this);
					} catch (IOException e1) {
						System.err.println("LAC: The sensor was not inserted sucessfully on th MAC. Removing it on the LAC..");
						model.removePropertyChangeListener(this);
						room.removeSensor(sensor);
						model.addPropertyChangeListener(this);
					}
				}else if(e.getPropertyName().equals(Room.PC_SENSORADDED)){
				
					
				}else{
				
					try {
						LACProtocol.updateRoom(connection, room);
					} catch (ConnectException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
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
					LACProtocol.insertRoom(connection, room,this);
				} catch (IOException e1) {
					System.err.println("LAC: The room was not inserted sucessfully on th MAC. Removing it on the LAC.. ");
					model.removePropertyChangeListener(this);
					model.removeRoom(room);
					model.addPropertyChangeListener(this);
					
				}
			}else{
				try {
					LACProtocol.updateLAC(connection, model);
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
	


	public static void main(String[] args) {
		boolean run = true;
		while(run){
			try{
			String s = JOptionPane.showInputDialog("Skriv inn ID til LACen som skal startes. (-1 genererer ny LAC)");
			int id = Integer.parseInt(s);
			if(id<0)
				new LAC(true);
			else
				new LAC(id,true);
			run=false;
			}catch(NumberFormatException e){
				System.err.println("Kun tall!");
			}
		}
	}
	
	@Override
	public void deleteAllEvents(Sensor sensor) {
		sensor.deleteAllEvents();
		LACProtocol.deleteSensorEvents(sensor);
	}

	
	

	class ListenThread extends Thread{
		
		public void run(){
			try {
				String receivedMSG = connection.receive();
			} catch (ConnectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


/*

	@Override
	public Sensor insertSensor(int roomID, boolean alarmState, int batteyStatus)
			throws IOException {
		Room r = null;
		for(Room room : this.getModel().getRooms()){
			if(room.getID()==roomID)r=room;
		}
		if(r==null)throw new IOException("Could not find room");
		int receiveID = LACProtocol.insertSensor(roomID, alarmState, batteyStatus);
		Sensor sensor = new Sensor(receiveID,alarmState,batteyStatus,LAC.getTime(),r,true);
		return sensor;
	}

	

	@Override
	public Room insertRoom(int modelID, int roomNr, String roomType,String roomInfo) throws IOException {
		if(modelID!=this.getModel().getID())throw new IOException("Error");
		
		int receiveID = LACProtocol.insertRoom(connection, roomNr,roomType,roomInfo);
		Room r = new Room(receiveID,roomNr,roomType,roomInfo,this.getModel());
		
		
		return r;
	}

	@Override
	public Event insertEvent(int roomID, int sensorID, EventType eventType)	throws IOException {
		
		for()
		LACProtocol.insertEvent(connection, roomID, sensorID, eventType);
		
		Event e = new Event(eventType.add)
		return null;
	}

*/
	@Override
	public void close() {
		try {
			System.out.println("This LAC is closing");
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	

	class RunThread extends Thread{
		public RunThread() {
			this.setName("Run Thread");
		}
		
		public void run(){
			//FIXME
			/*
			while(false){
				
				if(!LACProtocol.connectionCheck(connection)){
					connectionWrapper.setConnectionStatus(ConnectionStatus.DISCONNECTED);
					try {
						connection.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					connectWithRetry();
				}
				
				else{
				
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
					
			}
			*/
		}
	}
}
 
