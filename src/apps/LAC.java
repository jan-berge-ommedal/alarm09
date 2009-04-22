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

import connection.ConnectionImplementation;
import connection.LACProtocol;
import connection.TCPConnection;

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


public class LAC implements PropertyChangeListener {
	private Model model;
	private Connection connection;
	
	private LACgui gui;
	
	private static int connectionint = 1;
	private static final int SERVERPORT = 666;
	
	/**
	 * This constructor is used when creating a completly new LAC
	 * 
	 */
	public LAC() {
		gui = new LACgui(this);
		try {
			connect(5);
			String defaulfAddress = "My Adresss";
			int modelID = LACProtocol.receiveNextModelID(connection, defaulfAddress);
			Model m = new Model();
			m.setID(modelID);
			m.setAdresse(defaulfAddress);
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
		gui = new LACgui();
		try {
			connect();
			setModel(LACProtocol.receiveCompleteModel(connection, id));
		} catch (IOException e) {
			System.err.println("Reattempting to connect");
		}
		
	}
	
	private void connect(int i) throws IOException {
		while(i>0){
			try {
				connect();
				System.out.println("Connected to the MAC");
				return;
			} catch(BindException e){
				throw new IOException("Port in use");
			}catch (IOException e) {
				System.err.println("Reattempting to connect ("+i+" retries left)");
			}finally{
				i--;
			}
		}
		throw new IOException("Could not connect to the MAC");
	}
	
	private void connect() throws SocketTimeoutException, UnknownHostException, IOException{
			connection = new TCPConnection(SERVERPORT+connectionint++);
			connection.connect(InetAddress.getByName("localhost"), SERVERPORT);
	}
	
	/**
	 * Returns the datamodel of the LAC
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}
	
	/**
	 * Sets the given parameter as the datamodel of the LAC, and add
	 * @param model the new model
	 */
	public void setModel(Model model){
		if(this.model!=null)this.model.removePropertyChangeListener(this);
		this.model = model;
		gui.setModel(model);
		if(model!=null)model.addPropertyChangeListener(this);
	}
	
	
	/**
	 * This method checks if some sensors has an alarm 
	 * @return a boolean that is true when some of the LACs' sensors have an alarm
	 */ 
	public boolean checkAlarm() {
		for (Sensor s : model.getSensors()) {
			if(s.isAlarmState())return true;
		}	
		return false;
	}
	
	/**
	 * This method tests all sensors 
	 * @return a boolean that is false when some of the LACs' doesn't pass the test
	 */ 
	public boolean testSensors() {
		for (Sensor s : model.getSensors()) {
			if(!s.testSensor())return false;
		}	
		return true;
	}
	

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

	public int getNextRoomID(Room room) throws IOException {
		return LACProtocol.insertRoom(connection, room);
	}

	public int getNextSensorID(Sensor sensor) throws IOException  {
		return LACProtocol.insertSensor(connection, sensor);
	}

	public int getNextLACID(Event event) throws IOException  {
		return LACProtocol.insertEvent(connection, event);
	}

	

	
	
}
 
