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
import java.util.ArrayList;

import com.mysql.jdbc.jdbc2.optional.ConnectionWrapper;

import connection.ConnectionImplementation;
import connection.ConnectionStatusWrapper;
import connection.LACProtocol;
import connection.ModelEditControll;
import connection.TCPConnection;
import connection.ConnectionStatusWrapper.ConnectionStatus;

import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import no.ntnu.fp.net.co.Connection;

/**
 * This is the logical LAC 
 * 
 * @author Jan Berge Ommedal
 *
 */


public class LAC extends ModelEditControll{
	private Connection connection;
	
	private LACgui gui;
	
	
	
	private static final int STARTPORT = 700;
	private static String defaultAdres = "My Adresss";
	
	/**
	 * This constructor is used when creating a completly new LAC
	 * 
	 */
	public LAC() {
		gui = new LACgui(this);
		try {
			connectionWrapper.setConnectionStatus(ConnectionStatus.CONNECTING);
			connect(5);
			connectionWrapper.setConnectionStatus(ConnectionStatus.CONNECTED);
			connection.send("NEW"+defaultAdres);
			String reveiceID = connection.receive();
			System.out.println(reveiceID);
			
			int modelID = Integer.parseInt(reveiceID);
			
			Model m = new Model();
			m.setID(modelID);
			m.setAdresse(defaultAdres);
			
			this.setModel(m);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * This constructor is used when loading a LAC for storage
	 * @param id an int, the stored id of the LAC
	 */
	public LAC(int id) {
		gui = new LACgui(this);
		try {
			connect(5);
			connection.send("ID"+id);
			setModel(LACProtocol.receiveCompleteModel(connection, id));
			
		} catch (IOException e) {
			System.err.println("Reattempting to connect");
		}
		
	}
	
	private void connect(int i) throws IOException {
		int port = STARTPORT;
		while(i>0){
			try {
				connection = new TCPConnection(port);
				connection.connect(InetAddress.getByName(MAC.MACIP), MAC.SERVERPORT);
				System.out.println("Connected to the MAC");
				return;
			}catch (BindException e){
				System.err.println("Port "+port+" in use, trying port "+(++port));
			}catch (IOException e) {
				System.err.println("Reattempting to connect ("+i+" retries left)");
			}finally{
				i--;
			}
		}
		throw new IOException("Could not connect to the MAC");
	}
	
	
	
	/**
	 * Returns the datamodel of the LAC
	 * @return the model
	 */
	public void setModel(Model m){
		super.setModel(m);
		gui.setModel(model);
	}
	
	/**
	 * Sets the given parameter as the datamodel of the LAC, and add
	 * @param model the new model
	 */
	
	
	

	

	/**
	 * Returns current time
	 * @return The current Timestamp
	 */
	public static Timestamp getTime() {
		return new Timestamp(System.currentTimeMillis());
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if(e.getSource() instanceof Sensor)
			try {
				LACProtocol.updateSensor(connection,((Sensor)e.getSource()));
			} catch (ConnectException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		else if(e.getSource() instanceof Room){
			try {
				LACProtocol.updateRoom(connection,((Room)e.getSource()));
			} catch (ConnectException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	
	/**
	 * @deprecated just a test;)
	 * 
	 * @param f
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	/*
	private static LAC parse(File f) throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		LAC lac = new LAC(Integer.parseInt(reader.readLine()));		
		return lac;
	}
	*/

	public static void main(String[] args) {
		new LAC();
		//new LAC(1);
		
		/*
		 if(args.length > 0){
		 
			for(String s : args){
				File f = new File(s);
				if(f.isFile()){
					LAC lac;
					try {
						lac = LAC.parse(f);
						if(lac==null)
							System.err.println("Invalid LAC Format");		
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
							
				}
			}
		}else
			new LAC();
		*/
	}

	/* MODELCONTROLLER */

	public int getNextRoomID(Room room) throws IOException {
		return LACProtocol.insertRoom(connection, room);
	}

	public int getNextSensorID(Sensor sensor) throws IOException  {
		return LACProtocol.insertSensor(connection, sensor);
	}

	public int getNextLACID(Event event) throws IOException  {
		return LACProtocol.insertEvent(connection, event);
	}

	public boolean hasAlarm() {
		for (Sensor s : model.getSensors()) {
			if(s.isAlarmState())return true;
		}	
		return false;
	}

	@Override
	public void deleteAllEvents(Sensor sensor) {
		
		
	}
	


	
	
}
 
