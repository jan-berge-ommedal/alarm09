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
				Room room = XmlSerializer.toRoom(roomString);
				
				sendACK(connection);
				
			}
			else if(checkFlag(msg, INSERTSENSOR)){
				
				String sensorString = removeFlag(msg, INSERTROOM);
				
				//The sensor is automatically inserted into the model by the constructor invoked by the XMLSerializer
				Sensor room = XmlSerializer.toSensor(sensorString);
				
				sendACK(connection);
			}
			else if(checkFlag(msg, INSERTEVENT)){
				String sensorString = removeFlag(msg, INSERTEVENT);
				
				//The sensor is automatically inserted into the model by the constructor invoked by the XMLSerializer
				
				int id = Integer.parseInt(s[1]);
				EventType eventtype = null;
				for (EventType e : EventType.values()) {
					if(e.toString().equals(s[2])) eventtype = e;
				}
				Timestamp installationDate = XmlSerializer.makeTimestamp(s[3]);
				Room room = null;
				Sensor sensor = null;
				for (Room r : lac.getModel().getRooms()) {
					if(r.getID() == Integer.parseInt(s[4])) room = r;
				}
				for (Sensor se : room.getSensorer()) {
					if(se.getID() == Integer.parseInt(s[5])) sensor = se;
				}
				sensor.addEvent(new Event(id, eventtype,installationDate, sensor));
			}
			else if(s[0].equals("UPDATEROOM")){
				int id = Integer.parseInt(s[1]);
				Room room = null;
				for (Room r : lac.getModel().getRooms()) {
					if(r.getID() == id) room = r;
				}
	
				room.setRomNR(Integer.parseInt(s[2]));
				room.setRomType(s[3]);
				room.setRomInfo(s[4]);
			}
			else if(s[0].equals("UPDATESENSOR")){
				int id = Integer.parseInt(s[1]);
				Sensor sensor = null;
				for (Sensor se : lac.getModel().getSensors()) {
					if(se.getID() == id) sensor = se;
				}
				sensor.setAlarmState((true ? s[2].equals("true"): false));
				sensor.setInstallationDate(XmlSerializer.makeTimestamp(s[3]));
				sensor.setBattery(Integer.parseInt(s[4]));
			}
			
			else if(s[0].equals("DELETEALLEVENTS")){
				Room room = null;
				for (Room r : lac.getModel().getRooms()) {
					if (r.getID() == Integer.parseInt(s[2])){
						room = r;
					}
				}
				for(Sensor se : room.getSensorer()){
					if(se.getID() == Integer.parseInt(s[1])){
						se.deleteAllEvents();
					}
				}
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
		connection.send(INSERTEVENT + XmlSerializer.toXml(event));
		
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
		connection.send(INSERTROOM + XmlSerializer.toXmlRoom(room));
		
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
		connection.send(INSERTSENSOR + XmlSerializer.toXmlSensor(sensor));
		
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
		connection.send(UPDATEMODEL + XmlSerializer.toXml(event));
		receiveACK(connection);
	}

	@Override
	public void updateModel(ModelEditController controller, Connection connection) throws ConnectException, IOException {
		Model model = controller.getModel();
		connection.send(UPDATEMODEL + XmlSerializer.toXml(model));
		receiveACK(connection);
	}

	@Override
	public void updateRoom(ModelEditController controller, Connection connection, Room room) throws IOException {
		Model model = controller.getModel();
		connection.send(UPDATEROOM + XmlSerializer.toXmlRoom(room));
		receiveACK(connection);
		
	}

	@Override
	public void updateSensor(ModelEditController controller, Connection connection, Sensor sensor) throws IOException {
		connection.send("UPDATESENSOR" + XmlSerializer.toXmlSensor(sensor));
		receiveACK(connection);
		// TODO Auto-generated method stub
		
	}
	

	public static Model receiveCompleteModel(Connection connection, int modelid, ModelEditController controller) throws ConnectException, IOException, ParseException {
		connection.send("GETMODEL"+modelid);
		String s = connection.receive();
		if(s == "-1"){
			throw new IOException("Received a NAK in LACProtocol");
		}
		return XmlSerializer.toModel(s,controller);
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
