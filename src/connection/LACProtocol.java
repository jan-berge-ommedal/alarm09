package connection;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.Timestamp;
import java.text.ParseException;

import com.sun.org.apache.xml.internal.serializer.ToXMLSAXHandler;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;

import apps.LAC;

import no.ntnu.fp.net.co.Connection;
import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import model.Event.EventType;

public class LACProtocol extends AbstractApplicationProtocol{



	@Override
	public void handleMSG(String msg, ModelEditController controller,Connection connection) {
		LAC lac = (LAC) controller;
		
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
			else if(checkFlag(msg, UPDATEROOM)){
				String eventString = removeFlag(msg, UPDATEROOM);

				
				System.err.println("LAC: UPDATEROOM, Dette må handles, men sender ACK, slik at vi kan kjøre");
				
				sendACK(connection);
			}
			else if(checkFlag(msg, UPDATESENSOR)){
				String eventString = removeFlag(msg, INSERTEVENT);
				
				System.err.println("LAC: UPDATESENSOR, Dette må handles, men sender ACK, slik at vi kan kjøre");
				
				sendACK(connection);
			}
			else if(checkFlag(msg, UPDATEEVENT)){
				String eventString = removeFlag(msg, INSERTEVENT);
				
				System.err.println("LAC: UPDATEEVENT, Dette må handles, men sender ACK, slik at vi kan kjøre");
				
				sendACK(connection);
			}
			else if(checkFlag(msg, UPDATEMODEL)){
				String eventString = removeFlag(msg, INSERTEVENT);
				
				System.err.println("LAC: UPDATEMODEL, Dette må handles, men sender ACK, slik at vi kan kjøre");
				
				sendACK(connection);
			}
			
			else if(false){
				// DELETE EVENT MBY???
			
			}
		} catch (Exception e) {
			try {
				connection.send("NAK");
			} catch (ConnectException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	
	@Override
	public void insertEvent(ModelEditController controller, Connection connection, Event event) throws ConnectException, IOException {
		connection.send(INSERTEVENT + XmlSerializer.toEventString(event));
		
		//Recieve and discard insertCommand from MAC, send ACK
		String insertStringFromMAC = connection.receive();
		if(!checkFlag(insertStringFromMAC,INSERTEVENT)){
			sendNAK(connection);
			throw new IOException("Flags didnt match");
		}else
			sendACK(connection);
		
		//Receive and handle updateCommand from MAC (But check the flag)
		String updateMessage = connection.receive();
		if(!checkFlag(insertStringFromMAC,UPDATEEVENT)){
			sendNAK(connection);
			throw new IOException("Flags didnt match");
		}else{
			handleMSG(updateMessage, controller, connection);
		}
		
		
		//Receive last ACK from MAC, stating that the sensor was successfully created on both sides
		receiveACK(connection);
		
	}

	@Override
	public void insertRoom(ModelEditController controller, Connection connection, Room room) throws ConnectException, IOException {
		connection.send(INSERTROOM + XmlSerializer.toRoomString(room));
		
		//Recieve and discard insertCommand from MAC, send ACK
		String insertStringFromMAC = connection.receive();
		checkFlag(insertStringFromMAC,INSERTROOM);
		sendACK(connection);
		
		//Receive and handle updateCommand from MAC (But check the flag)
		String updateMessage = connection.receive();
		checkFlag(insertStringFromMAC,UPDATEROOM);
		handleMSG(updateMessage, controller, connection);
		
		//Receive last ACK from MAC, stating that the sensor was successfully created on both sides
		receiveACK(connection);
		
	}

	@Override
	public void insertSensor(ModelEditController controller, Connection connection, Sensor sensor) throws ConnectException, IOException {
		connection.send(INSERTSENSOR + XmlSerializer.toSensorString(sensor));
		
		//Recieve and discard insertCommand from MAC, send ACK
		String insertStringFromMAC = connection.receive();
		checkFlag(insertStringFromMAC,INSERTSENSOR);
		sendACK(connection);
		
		//Receive and handle updateCommand from MAC (But check the flag)
		String updateMessage = connection.receive();
		checkFlag(insertStringFromMAC,INSERTSENSOR);
		handleMSG(updateMessage, controller, connection);
		
		//Receive last ACK from MAC, stating that the sensor was successfully created on both sides
		receiveACK(connection);
		
	}

	

	

	
	@Override
	public void updateEvent(ModelEditController controller, Connection connection ,  Event event) throws ConnectException, IOException {
		connection.send(UPDATEMODEL + XmlSerializer.toEventString(event));
		receiveACK(connection);
	}

	@Override
	public void updateModel(ModelEditController controller, Connection connection) throws ConnectException, IOException {
		Model model = controller.getModel();
		connection.send(UPDATEMODEL + XmlSerializer.toModelString(model));
		receiveACK(connection);
	}

	@Override
	public void updateRoom(ModelEditController controller, Connection connection, Room room) throws IOException {
		Model model = controller.getModel();
		connection.send(UPDATEROOM + XmlSerializer.toRoomString(room));
		receiveACK(connection);
		
	}

	@Override
	public void updateSensor(ModelEditController controller, Connection connection, Sensor sensor) throws IOException {
		connection.send("UPDATESENSOR" + XmlSerializer.toSensorString(sensor));
		receiveACK(connection);
		// TODO Auto-generated method stub
		
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
		try{
			connection.send("CHECK");
			if(connection.receive().equals("CHECK")){
				return true;
			}
		}
		catch (Exception e) {
		}
		return false;
	}
	

	
	


}
