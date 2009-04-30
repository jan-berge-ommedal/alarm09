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
	
	protected boolean discardNextCommand = false;
	
	
	protected static void receiveACK(Connection connection) throws IOException {
		System.out.println("waiting for ACK");
		if(!connection.receive().equals("ACK")){
			System.out.println("got NAK");
			throw new IOException("Received a NAK");
		}
		System.out.println("got ACK");
	}
	
	private void receiveACKOrFlag(Connection connection, String flag) throws ConnectException, IOException {
		System.out.println("waiting for ACK or correct flag");
		String receive = connection.receive();
		if(! (checkFlag(receive, flag) || receive.equals("ACK"))){
			System.out.println("got NAK");
			throw new IOException("Received a NAK");
		}
		System.out.println("got ACK (or flag)");
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
	
	

	
	public void handleMSG(String msg, ModelEditController controller,Connection connection){
		System.out.println("got Command: "+msg);
	
		Model model = controller.getModel();
		
		if(checkFlag(msg, UPDATESENSOR)){
			String eventString = removeFlag(msg, UPDATESENSOR);
			
			String[] updateInfo = deconstructUpdateHeader(eventString);
			
			boolean ok = true;
			if(updateInfo[0].equals(ELEMENT_ID)){
				Sensor sensor = model.getSensor(Integer.parseInt(updateInfo[3]));//Try to find the sensor with the new ID
				if(sensor==null){//The ID needs update
					sensor = model.getSensor(Integer.parseInt(updateInfo[2]));//Need to use the old ID to find the sensor
					sensor.setID(Integer.parseInt(updateInfo[3]));
				}	
			}else{
				Sensor sensor = model.getSensor(Integer.parseInt(updateInfo[1]));
				if(updateInfo[0].equals(ELEMENT_SENSOR_ALARMSTATE)){
					sensor.setAlarmState(Boolean.parseBoolean(updateInfo[3]));
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
						macProtocol.getDatabase().updateSensor(sensor.getID(), sensor.isAlarmState(), sensor.getBattery(), sensor.getInstallationDate());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			
			if(ok)
				sendACK(connection);
			else
				sendNAK(connection);
		}
		
		else if(checkFlag(msg, UPDATEROOM)){
			String roomString = removeFlag(msg, UPDATEROOM);

			String[] updateInfo = deconstructUpdateHeader(roomString);
			
			boolean ok = true;
			if(updateInfo[0].equals(ELEMENT_ID)){
				Room room = model.getRoom(Integer.parseInt(updateInfo[3]));//Try to find the room with the new ID
				if(room==null){//The ID needs update
					room = model.getRoom(Integer.parseInt(updateInfo[2]));//Need to use the old ID to find the room
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
			else
				sendNAK(connection);
			
		}else if(checkFlag(msg, UPDATEEVENT)){
			String eventString = removeFlag(msg, INSERTEVENT);
						
			String[] updateInfo = deconstructUpdateHeader(eventString);
			
			boolean ok = true;
			if(updateInfo[0].equals(ELEMENT_ID)){
				Event event = model.getEvent(Integer.parseInt(updateInfo[3]));//Try to find the room with the new ID
				if(event==null){//The ID needs update
					event = model.getEvent(Integer.parseInt(updateInfo[2]));//Need to use the old ID to find the room
					event.setID(Integer.parseInt(updateInfo[3]));
				}
			}else{
				ok=false;
			}
			
			if(ok)
				sendACK(connection);
			else
				sendNAK(connection);
		}else if(checkFlag(msg, UPDATEMODEL)){
			String modelString = removeFlag(msg, INSERTEVENT);
			
			String[] updateInfo = deconstructUpdateHeader(modelString);
			
			boolean ok = true;
			if(updateInfo[0].equals(ELEMENT_ID)){
				// An model-id should not be updated
				ok=false;
			}else if(updateInfo[0].equals(ELEMENT_MODEL_ADDRESS)){
				model.setAdresse(updateInfo[3]);
			}else{
				ok=false;
			}
			
			if(ok)
				sendACK(connection);
			else
				sendNAK(connection);
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
		System.out.println("Running Sensor-Update");
		String eventString = UPDATEEVENT + constructUpdateHeader(id, elementTag, oldValue, newValue);
		connection.send(eventString);
		receiveACKOrFlag(connection,UPDATEEVENT);
		
	}


	public synchronized void updateModel(Connection connection, int id, String elementTag, String oldValue, String newValue) throws ConnectException, IOException {
		if(discardNextCommand){
			discardNextCommand=false;
			return;
		}
		System.out.println("Running Sensor-Update");
		String eventString = UPDATEMODEL + constructUpdateHeader(id, elementTag, oldValue, newValue);
		connection.send(eventString);
		receiveACKOrFlag(connection,UPDATEMODEL);
	}


	public synchronized void updateRoom(Connection connection, int id, String elementTag, String oldValue, String newValue) throws IOException {
		if(discardNextCommand){
			discardNextCommand=false;
			return;
		}
		System.out.println("Running Sensor-Update");
		String eventString = UPDATEROOM + constructUpdateHeader(id, elementTag, oldValue, newValue);
		connection.send(eventString);
		receiveACKOrFlag(connection,UPDATEROOM);
	}


	public synchronized void updateSensor(Connection connection, int id, String elementTag, String oldValue, String newValue) throws IOException {
		if(discardNextCommand){
			discardNextCommand=false;
			return;
		}
		System.out.println("Running Sensor-Update");
		String eventString = UPDATESENSOR + constructUpdateHeader(id, elementTag, oldValue, newValue);
		connection.send(eventString);
		receiveACKOrFlag(connection,UPDATESENSOR);
	}

	
	
}
