package connection;

import java.io.IOException;
import java.net.ConnectException;

import no.ntnu.fp.net.co.Connection;
import model.Model;

public class LACProtocol {
	
	

	

	public static Model receiveCompleteModel(Connection connection, int modelid) throws ConnectException, IOException {
		connection.send("GETMODEL");
		System.out.println(connection.receive());
		return new Model();
	}

	public static int receiveNextModelID(Connection connection) throws ConnectException, IOException {
		connection.send("GETNEXTID");
		String s = connection.receive();
		System.out.println(s);
		return Integer.parseInt(s);
	}
	

}
