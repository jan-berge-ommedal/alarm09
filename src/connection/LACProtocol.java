package connection;

import java.io.IOException;
import java.net.ConnectException;
import java.text.ParseException;

import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import no.ntnu.fp.net.co.Connection;
import apps.LAC;

public class LACProtocol extends AbstractApplicationProtocol{


	@Override
	public void handleMSG(String msg, ModelEditController controller,Connection connection) {
		super.handleMSG(msg, controller, connection); // The abstract part of the protocol handles updateCases!
		
		LAC lac = (LAC) controller;
		Model model = controller.getModel();
		try {
			if(checkFlag(msg, INSERTROOM)){
				String roomString = removeFlag(msg, INSERTROOM);
				
				this.discardNextCommand=true;
				//The room is automatically inserted into the model by the constructor invoked by the XMLSerializer
				Room room = XmlSerializer.toRoom(roomString,controller.getModel());
				
				sendACK(connection);
				
			}
			else if(checkFlag(msg, INSERTSENSOR)){
				
				String roomString = removeFlag(msg, INSERTROOM);
				
				this.discardNextCommand=true;
				//The sensor is automatically inserted into the model by the constructor invoked by the XMLSerializer
				Sensor room = XmlSerializer.toSensor(roomString,controller.getModel());
				
				sendACK(connection);
			}
			else if(checkFlag(msg, INSERTEVENT)){
				String eventString = removeFlag(msg, INSERTEVENT);

				this.discardNextCommand=true;
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
		if(discardNextCommand){
			discardNextCommand=false;
			return;
		}
		
		System.out.println("----------\nLAC: INSERT EVENT\n\n");
		String sendString = INSERTEVENT + XmlSerializer.toEventString(event);
		System.out.println("Sending: "+sendString);
		connection.send(sendString);
		
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
		System.out.println("received updateMessage: "+updateMessage);
		if(!checkFlag(updateMessage,UPDATEEVENT)){
			sendNAK(connection);
			throw new IOException("Flags didnt match");
		}else{
			handleMSG(updateMessage, controller, connection);
		}
		
		
		//Receive last ACK from MAC, stating that the sensor was successfully created on both sides
		receiveACK(connection);
		System.out.println("End insert Event\n---------\n\n");
		
	}

	@Override
	public synchronized void insertRoom(ModelEditController controller, Connection connection, Room room) throws ConnectException, IOException {
		if(discardNextCommand){
			discardNextCommand=false;
			return;
		}
		
		System.out.println("----------\nLAC: INSERT ROOM\n\n");
		String sendString = INSERTROOM + XmlSerializer.toRoomString(room);
		System.out.println("Sending: "+sendString);
		connection.send(sendString);
		
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
		System.out.println("received updateMessage: "+updateMessage);
		if(!checkFlag(updateMessage,UPDATEROOM)){
			sendNAK(connection);
			throw new IOException("Flags didnt match");
		}else{
			handleMSG(updateMessage, controller, connection);
		}
		
		//Receive last ACK from MAC, stating that the sensor was successfully created on both sides
		receiveACK(connection);
		System.out.println("End insert Room\n---------\n\n");
		
	}

	@Override
	public synchronized void insertSensor(ModelEditController controller, Connection connection, Sensor sensor) throws ConnectException, IOException {
		if(discardNextCommand){
			discardNextCommand=false;
			return;
		}
		
		
		System.out.println("----------\nLAC: INSERT SENSOR\n\n");
		String sendString = INSERTSENSOR + XmlSerializer.toSensorString(sensor);
		System.out.println("Sending: "+sendString);
		connection.send(sendString);
		
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
		System.out.println("received updateMessage: "+updateMessage);
		if(!checkFlag(updateMessage,UPDATESENSOR)){
			sendNAK(connection);
			throw new IOException("Flags didnt match");
		}else{
			handleMSG(updateMessage, controller, connection);
		}
		
		//Receive last ACK from MAC, stating that the sensor was successfully created on both sides
		receiveACK(connection);
		System.out.println("End insert Sensor\n---------\n\n");
	}

	


	public Model receiveCompleteModel(Connection connection, int modelid, ModelEditController controller) throws ConnectException, IOException, ParseException {
		connection.send("GETMODEL"+modelid);
		String s = connection.receive();
		if(s == "-1"){
			throw new IOException("Received a NAK in LACProtocol");
		}
		return XmlSerializer.toModelComplete(s,controller);
	}


	
	
	




	
	


}