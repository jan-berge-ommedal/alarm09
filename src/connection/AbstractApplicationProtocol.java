package connection;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.sql.Timestamp;

import apps.MAC.MACProtocol;

import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import model.Sensor.Alarm;
import no.ntnu.fp.net.co.Connection;

public abstract class AbstractApplicationProtocol {
	
	protected static final String INSERTSENSOR = "INSERTSENSOR";
	protected static final String UPDATESENSOR = "UPDATESENSOR";
	
	protected static final String INSERTROOM = "INSERTROOM";
	protected static final String UPDATEROOM = "UPDATEROOM";
	
	protected static final String INSERTEVENT = "INSERTEVENT";
	protected static final String UPDATEEVENT = "UPDATEEVENT";
	
	protected static final String UPDATEMODEL = "UPDATEMODEL";
	
	public static final String ELEMENT_ID = "<ID>";
	
	public static final String ELEMENT_MODEL_ADDRESS = "<MODELADDRESS>";
	
	public static final String ELEMENT_ROOM_ROOMNR = "<ROOMNR>";
	public static final String ELEMENT_ROOM_ROOMTY = "<ROOMTYPE>";
	public static final String ELEMENT_ROOM_ROOMINFO = "<ROOMINFO>";
	
	public static final String ELEMENT_SENSOR_ALARMSTATE = "<ALARM>";
	public static final String ELEMENT_SENSOR_INSTALLATIONDATE = "<INSTALLATIONDATE>";
	public static final String ELEMENT_SENSOR_BATTERY = "<BATTERY>";
	
	public static final String CLOSE = "CLOSE";
	
	
	protected boolean discardNextCommand = false;
	
	
	protected static void receiveACK(Connection connection) throws IOException {
		System.out.println("waiting for ACK");
		if(!connection.receive().equals("ACK")){
			System.out.println("got NAK");
			throw new IOException("Received a NAK");
		}
		System.out.println("got ACK");
	}
	
	
	
	protected static void sendACK(Connection connection) {
		try {
			connection.send("ACK");
			System.out.println("sent ACK");
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	protected static void sendNAK(Connection connection) {
		try {
			connection.send("NAK");
			System.out.println("sent NAK");
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

	/**
	 * Denne metoden er overlagret i både lacprotokollen og macprotokollen. Denne metoden brukes her
	 * for å håndtere updates, dens overlagrede versjoner vil håndtere insert. Dersom protokollklassene 
	 * skal inserte noe kaller de denne supermetoden.
	 * @param msg
	 * @param controller
	 * @param connection
	 * @throws IOException 
	 */
	public void handleMSG(String msg, ModelEditController controller,Connection connection){
		System.out.println("got Command: "+msg);
	
		Model model = controller.getModel();
		
		if(checkFlag(msg, UPDATESENSOR)){
			this.discardNextCommand=true;
			System.out.println("Will discard the propagation of the updateEvent");
			String sensorString = removeFlag(msg, UPDATESENSOR);
			
			String[] updateInfo = deconstructUpdateHeader(sensorString);
			
			boolean ok = true;
			if(updateInfo[0].equals(ELEMENT_ID)){
				Sensor sensor = model.getSensor(Integer.parseInt(updateInfo[2]));//Need to use the old ID to find the sensor	
				if(sensor==null){
					System.err.println("Didnt find id");
					System.err.println(sensorString);
				}else{
					sensor.setID(Integer.parseInt(updateInfo[3]));
				}	
			}else{
				Sensor sensor = model.getSensor(Integer.parseInt(updateInfo[1]));
				if(updateInfo[0].equals(ELEMENT_SENSOR_ALARMSTATE)){
					sensor.setAlarmState(Alarm.valueOf(updateInfo[3]),false);
				}else if(updateInfo[0].equals(ELEMENT_SENSOR_BATTERY)){
					sensor.setBattery(Integer.parseInt(updateInfo[3]));
				}else if(updateInfo[0].equals(ELEMENT_SENSOR_INSTALLATIONDATE)){
					sensor.setInstallationDate(Timestamp.valueOf(updateInfo[3]));
				}else{
					ok=false;
				}
				
				if(this instanceof MACProtocol){
					MACProtocol macProtocol = (MACProtocol) this;
					try {
						macProtocol.getDatabase().updateSensor(sensor.getID(), sensor.getAlarmState(), sensor.getBattery(), sensor.getInstallationDate());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			
			if(ok)
				sendACK(connection);
			else{
				sendNAK(connection);
				System.err.println("Dette skal aldri skje");
			}
		}
		
		else if(checkFlag(msg, UPDATEROOM)){
			this.discardNextCommand=true;
			System.out.println("Will discard the propagation of the updateEvent");

			String roomString = removeFlag(msg, UPDATEROOM);

			String[] updateInfo = deconstructUpdateHeader(roomString);
			
			boolean ok = true;
			if(updateInfo[0].equals(ELEMENT_ID)){
				Room room = model.getRoom(Integer.parseInt(updateInfo[2]));//Need to use the old ID to find the room
				if(room==null){
					System.err.println("Didnt find id");
					System.err.println(roomString);
				}else{
					room.setID(Integer.parseInt(updateInfo[3]));
				}	
			}else{
				Room room = model.getRoom(Integer.parseInt(updateInfo[1]));
				if(updateInfo[0].equals(ELEMENT_ROOM_ROOMINFO)){
					room.setRomInfo(updateInfo[3]);
				}else if(updateInfo[0].equals(ELEMENT_ROOM_ROOMNR)){
					room.setRomNR(Integer.parseInt(updateInfo[3]));
				}else if(updateInfo[0].equals(ELEMENT_ROOM_ROOMTY)){
					room.setRomType(updateInfo[3]);
				}else{
					ok=false;
				}
			}
			if(ok)
				sendACK(connection);
			else{
				sendNAK(connection);
				System.err.println("Dette skal aldri skje");
			}
		}else if(checkFlag(msg, UPDATEEVENT)){
			this.discardNextCommand=true;
			System.out.println("Will discard the propagation of the updateEvent");

			String eventString = removeFlag(msg, UPDATEEVENT);
						
			String[] updateInfo = deconstructUpdateHeader(eventString);
			
			boolean ok = true;
			if(updateInfo[0].equals(ELEMENT_ID)){
				Event event = model.getEvent(Integer.parseInt(updateInfo[2]));//Need to use the old ID to find the room
				if(event==null){
					System.err.println("Didnt find id");
					System.err.println(eventString);
				}else{
					event.setID(Integer.parseInt(updateInfo[3]));
				}
			}else{
				ok=false;
			}
			
			if(ok)
				sendACK(connection);
			else{
				sendNAK(connection);
				System.err.println("Dette skal aldri skje");
			}
		}else if(checkFlag(msg, UPDATEMODEL)){
			this.discardNextCommand=true;
			System.out.println("Will discard the propagation of the updateEvent");

			String modelString = removeFlag(msg, UPDATEMODEL);
			
			String[] updateInfo = deconstructUpdateHeader(modelString);
			
			boolean ok = true;
			if(updateInfo[0].equals(ELEMENT_ID)){
				this.discardNextCommand=false;
				System.out.println("Will not discard the propagation of the updateEvent");
				ok=false;
			}else if(updateInfo[0].equals(ELEMENT_MODEL_ADDRESS)){
				model.setAdresse(updateInfo[3]);
			}else{
				ok=false;
			}
			
			if(ok)
				sendACK(connection);
			else{
				sendNAK(connection);
				System.err.println("Dette skal aldri skje");
			}
		}
		else if(msg.equals(CLOSE)){
			sendACK(connection);
			try {
				connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected String constructUpdateHeader(int id, String elementTag, String oldValue, String newValue) {
		return elementTag+"<"+id+"><"+oldValue+"><"+newValue+">";
	}
	
	/**
	 * 
	 * 
	 * 
	 * @param stringOfParts A string on the format 'tag'<elementTag><oldValue><newValue>
	 * @return an array with four elements: 0-the tag 1-the id of the element 2-the old value 3-the new value
	 */
	

	protected String[] deconstructUpdateHeader(String stringOfParts) {
		String[] result = new String[4];
		boolean[] removeTags = new boolean[]{false,true,true,true};
		for(int i=0; i<result.length;i++){
			int end = stringOfParts.indexOf(">");
			result[i]  = stringOfParts.substring((removeTags[i] ? 1 : 0),(removeTags[i] ? end : end+1));
			stringOfParts = stringOfParts.substring(end+1);
		}
		return result;
		
	}
	

	protected String getAndRemoveValue(String stringStartingWithValue){
		int end = stringStartingWithValue.indexOf(">");
		String result = stringStartingWithValue.substring(1,end);
		stringStartingWithValue = stringStartingWithValue.substring(end);
		return result;
	}
	
	protected static String removeFlag(String msg, String flag){
		return msg.substring(flag.length());
	}
	
	protected static boolean checkFlag(String msg, String flag){
		return msg.startsWith(flag);
	}
	
	public abstract void insertSensor(ModelEditController controller, Connection connection, Sensor sensor) throws ConnectException, IOException;
	public abstract void insertRoom(ModelEditController controller, Connection connection, Room room) throws ConnectException, IOException;
	public abstract void insertEvent(ModelEditController controller, Connection connection, Event event) throws ConnectException, IOException;

	public synchronized void updateEvent(Connection connection, int id, String elementTag, String oldValue, String newValue) throws ConnectException, IOException {
		if(discardNextCommand){
			discardNextCommand=false;
			return;
		}
		System.out.println("Running Event-Update");
		String eventString = UPDATEEVENT + constructUpdateHeader(id, elementTag, oldValue, newValue);
		connection.send(eventString);
		receiveACK(connection);
		System.out.println("End Event-Update");
		
	}


	public synchronized void updateModel(Connection connection, int id, String elementTag, String oldValue, String newValue) throws ConnectException, IOException {
		if(discardNextCommand){
			discardNextCommand=false;
			return;
		}
		System.out.println("Running Model-Update");
		String eventString = UPDATEMODEL + constructUpdateHeader(id, elementTag, oldValue, newValue);
		connection.send(eventString);
		receiveACK(connection);
		System.out.println("End Model-Update");
	}


	public synchronized void updateRoom(Connection connection, int id, String elementTag, String oldValue, String newValue) throws IOException {
		if(discardNextCommand){
			discardNextCommand=false;
			return;
		}
		System.out.println("Running Room-Update");
		String eventString = UPDATEROOM + constructUpdateHeader(id, elementTag, oldValue, newValue);
		connection.send(eventString);
		receiveACK(connection);
		System.out.println("End Room-Update");
	}


	public synchronized void updateSensor(Connection connection, int id, String elementTag, String oldValue, String newValue) throws IOException {
		if(discardNextCommand){
			discardNextCommand=false;
			return;
		}
		System.out.println("Running Sensor-Update");
		String eventString = UPDATESENSOR + constructUpdateHeader(id, elementTag, oldValue, newValue);
		connection.send(eventString);
		receiveACK(connection);
		System.out.println("End Sensor-Update");
	}
	
	
	
}
