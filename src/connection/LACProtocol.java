package connection;

import java.io.IOException;
import java.net.ConnectException;

import apps.LAC;

import no.ntnu.fp.net.co.Connection;
import model.Model;
import model.Room;
import model.Sensor;

public class LACProtocol {

	

	public static Model receiveCompleteModel(Connection connection, int modelid) throws ConnectException, IOException {
		connection.send("GETMODEL"+modelid);
		System.out.println("CREATED DEFAULT MODEL, but RECEIVED THIS:\n---------------------------------\n\n"+connection.receive());
		return new Model();
	}

	public static int receiveNextModelID(Connection connection) throws ConnectException, IOException {
		connection.send("GETNEXTID");
		String s = connection.receive();
		System.out.println(s);
		return Integer.parseInt(s);
	}

	public static void updateMAC(Connection connection, LAC lac, Sensor sensor) {
		// TODO Auto-generated method stub
		
	}

	public static void updateMAC(Connection connection, LAC lac, Room room) {
		// TODO Auto-generated method stub
		
	}
	

}
