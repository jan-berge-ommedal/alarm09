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

import connection.AbstractApplicationProtocol;
import connection.ConnectionImplementation;
import connection.ConnectionStatusWrapper;
import connection.ModelEditController;
import connection.TCPConnection;
import connection.XmlSerializer;
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
	
	private final LACProtocol protocol = new LACProtocol();
	
	private static final int STARTPORT = 700;
	private static String defaultAdres = "My Adresss";
	
	/**
	 * This constructor is used when creating a completly new LAC
	 * 
	 */
	public LAC(boolean useGUI) {
		super(new LACProtocol());
		if(useGUI)gui = new LACgui(this);
		connection = new TCPConnection(STARTPORT);
		connectWithRetry();
		
		try {
			connection.send("NEW"+defaultAdres);
			String reveiceID = connection.receive();
			int modelID = Integer.parseInt(reveiceID);
			Model m = new Model(this,modelID);
			m.setAdresse(defaultAdres);
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		thread = new RunThread();
		thread.start();
	}


	/**
	 * This constructor is used when loading a LAC for storage
	 * @param id an int, the stored id of the LAC
	 */
	public LAC(int id,boolean useGUI) {
		super(new LACProtocol());
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
			startAllSensors();
			System.out.println(model);
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
	

	private void startAllSensors() {
		for(Sensor s : model.getSensors()){
			s.startSensor();
		}
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


	@Override
	protected Connection getConnection() {
		return connection;
	}


	public static class LACProtocol extends AbstractApplicationProtocol{



		@Override
		public void handleMSG(String msg, ModelEditController controller,Connection connection) {
			super.handleMSG(msg, controller, connection); // The abstract part of the protocol handles updateCases!
			
			LAC lac = (LAC) controller;
			Model model = controller.getModel();
			try {
				if(checkFlag(msg, INSERTROOM)){
					String roomString = removeFlag(msg, INSERTROOM);
					
					//The room is automatically inserted into the model by the constructor invoked by the XMLSerializer
					Room room = XmlSerializer.toRoom(roomString,controller.getModel());
					
					sendACK(connection);
					
				}
				else if(checkFlag(msg, INSERTSENSOR)){
					
					String roomString = removeFlag(msg, INSERTROOM);
					
					//The sensor is automatically inserted into the model by the constructor invoked by the XMLSerializer
					Sensor room = XmlSerializer.toSensor(roomString,controller.getModel());
					
					sendACK(connection);
				}
				else if(checkFlag(msg, INSERTEVENT)){
					String eventString = removeFlag(msg, INSERTEVENT);

					Event event = XmlSerializer.toEvent(eventString,controller.getModel());
					
					sendACK(connection);

				}
				
				
				else if(false){
					// DELETE EVENT MBY???
				
				}
			} catch (Exception e) {
				e.printStackTrace();
					sendNAK(connection);
			
			}
			
		}
		
		
		@Override
		public synchronized void insertEvent(ModelEditController controller, Connection connection, Event event) throws ConnectException, IOException {
			System.out.println("LAC: INSERT EVENT\n----------\n");
			connection.send(INSERTEVENT + XmlSerializer.toEventString(event));
			
			//Recieve and discard insertCommand from MAC, send ACK
			System.out.println("Next Command will be discarded");
			String insertStringFromMAC = connection.receive();
			System.out.println("Got: "+insertStringFromMAC);
			if(!checkFlag(insertStringFromMAC,INSERTEVENT)){
				sendNAK(connection);
				throw new IOException("Flags didnt match");
			}else
				sendACK(connection);
			
			//Receive and handle updateCommand from MAC (But check the flag)
			System.out.println("Next Command will be handled");
			String updateMessage = connection.receive();
			if(!checkFlag(updateMessage,UPDATEEVENT)){
				sendNAK(connection);
				throw new IOException("Flags didnt match");
			}else{
				handleMSG(updateMessage, controller, connection);
			}
			
			
			//Receive last ACK from MAC, stating that the sensor was successfully created on both sides
			receiveACK(connection);
			
		}

		@Override
		public synchronized void insertRoom(ModelEditController controller, Connection connection, Room room) throws ConnectException, IOException {
			System.out.println("LAC: INSERT ROOM\n----------\n");
			connection.send(INSERTROOM + XmlSerializer.toRoomString(room));
			
			//Recieve and discard insertCommand from MAC, send ACK
			System.out.println("Next Command will be discarded");
			String insertStringFromMAC = connection.receive();
			if(!checkFlag(insertStringFromMAC,INSERTROOM)){
				sendNAK(connection);
				throw new IOException("Flags didnt match");
			}else
				sendACK(connection);
			
			//Receive and handle updateCommand from MAC (But check the flag)
			System.out.println("Next Command will be handled");
			String updateMessage = connection.receive();
			if(!checkFlag(updateMessage,UPDATEROOM)){
				sendNAK(connection);
				throw new IOException("Flags didnt match");
			}else{
				handleMSG(updateMessage, controller, connection);
			}
			
			//Receive last ACK from MAC, stating that the sensor was successfully created on both sides
			receiveACK(connection);
			
		}

		@Override
		public synchronized void insertSensor(ModelEditController controller, Connection connection, Sensor sensor) throws ConnectException, IOException {
			System.out.println("LAC: INSERT SENSOR\n----------\n");
			connection.send(INSERTSENSOR + XmlSerializer.toSensorString(sensor));
			
			//Recieve and discard insertCommand from MAC, send ACK
			System.out.println("Next Command will be discarded");
			String insertStringFromMAC = connection.receive();
			if(!checkFlag(insertStringFromMAC,INSERTSENSOR)){
				sendNAK(connection);
				throw new IOException("Flags didnt match");
			}else
				sendACK(connection);
			
			//Receive and handle updateCommand from MAC (But check the flag)
			System.out.println("Next Command will be handled");
			String updateMessage = connection.receive();
			if(!checkFlag(updateMessage,UPDATESENSOR)){
				sendNAK(connection);
				throw new IOException("Flags didnt match");
			}else{
				handleMSG(updateMessage, controller, connection);
			}
			
			//Receive last ACK from MAC, stating that the sensor was successfully created on both sides
			receiveACK(connection);
			
		}

		


		public static Model receiveCompleteModel(Connection connection, int modelid, ModelEditController controller) throws ConnectException, IOException, ParseException {
			connection.send("GETMODEL"+modelid);
			String s = connection.receive();
			if(s == "-1"){
				throw new IOException("Received a NAK in LACProtocol");
			}
			return XmlSerializer.toModelComplete(s,controller);
		}

		
		
		

		
		public static boolean connectionCheck(Connection connection) {
			try {
				connection.send("CHECK");
				receiveACK(connection);
			} catch (IOException e) {
				return false;
			}
			return true;
		
		}




		
		


	}

}
 
