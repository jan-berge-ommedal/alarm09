package connection;

import java.io.IOException;
import java.net.ConnectException;

import com.sun.org.apache.xml.internal.serializer.ToXMLSAXHandler;

import apps.LAC;

import no.ntnu.fp.net.co.Connection;
import model.Event;
import model.Model;
import model.Room;
import model.Sensor;

public class LACProtocol {

	

	public static Model receiveCompleteModel(Connection connection, int modelid) throws ConnectException, IOException {
		connection.send("GETMODEL"+modelid);
		System.out.println("CREATED DEFAULT MODEL, but RECEIVED THIS:\n---------------------------------\n\n"+connection.receive());
		return new Model();
	}

	public static int receiveNextModelID(Connection connection, String adress) throws ConnectException, IOException {
		connection.send("GETNEXTID" + adress);
		String s = connection.receive();
		System.out.println(s);
		return Integer.parseInt(s);
	}

	public static void updateLAC(Connection connection, LAC lac) throws ConnectException, IOException {
		connection.send("UPDATELAC" + " " + Integer.toString(lac.getModel().getID()) + " " + lac.getModel().getAdresse());
	}

	public static void updateRoom(Connection connection, Room room) throws ConnectException, IOException {
		connection.send("UPDATEROOM" + XmlSerializer.toXmlRoom(room));
		
	}
	
	public static void updateSensor(Connection connection, Sensor sensor) throws ConnectException, IOException {
		connection.send("UPDATESENSOR" + XmlSerializer.toXmlSensor(sensor));
	}
	
	public static void insertRoom(Connection connection, Room room) throws ConnectException, IOException {
		connection.send("INSERTROOM" + XmlSerializer.toXmlRoom(room));
	}
	
	public static void insertSensor(Connection connection, Sensor sensor) throws ConnectException, IOException {
		connection.send("INSERTSENSOR" + XmlSerializer.toXmlSensor(sensor));
	}
	
	public static void insertEvent(Connection connection, Event event) throws ConnectException, IOException {
		connection.send("INSERTEVENT" + XmlSerializer.toXmlEvent(event));
	}

	

}
