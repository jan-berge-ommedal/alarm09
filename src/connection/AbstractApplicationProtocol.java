package connection;

import java.io.IOException;
import java.net.ConnectException;

import model.Event;
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
	
	protected static void receiveACK(Connection connection) throws IOException {
		if(!connection.receive().equals("ACK")){
			System.err.println("LACProtocol Received NAK, throwing exception");
			throw new IOException("Received a NAK in LACProtocol");
		}
	}
	
	protected static void sendACK(Connection connection) {
		try {
			connection.send("ACK");
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	protected static void sendNAK(Connection connection) {
		try {
			connection.send("ACK");
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

	public abstract void updateModel(ModelEditController controller, Connection connection) throws ConnectException, IOException;
	public abstract void updateRoom(ModelEditController controller, Connection connection, Room room) throws IOException;
	public abstract void updateSensor(ModelEditController controller, Connection connection, Sensor sensor) throws IOException;
	public abstract void updateEvent(ModelEditController controller, Connection connection, Event event) throws ConnectException, IOException;
	
	public abstract void handleMSG(String msg, ModelEditController controller,Connection connection);
}
