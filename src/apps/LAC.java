package apps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;

import connection.LACProtocol;
import connection.TCPConnection;

import model.Model;
import model.Sensor;
import no.ntnu.fp.net.co.Connection;
import no.ntnu.fp.net.co.ConnectionImpl;


public class LAC {
	private Model model;
	private Connection connection;
	
	//private LACGui gui;
	private static int connectionint = 1;
	
	public LAC() {
		try {
			connection = new TCPConnection(666+connectionint);
			connection.connect(InetAddress.getByName("localhost"), 666);
			int modelID = LACProtocol.receiveNextModelID(connection);
			model = new Model();
			model.setID(modelID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			

		
	}
	
	public LAC(int id) {
		try {
			connection = new TCPConnection(500);
			connection.connect(InetAddress.getByName("192.168.0.5"), 501);
			model = LACProtocol.receiveCompleteModel(connection, id);
			//gui = new LACGui();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
		 
	public boolean checkAlarm() {
		for (Sensor s : model.getSensors()) {
			if(s.testSensor())return true;
		}	
		return false;
	}
	
	public void installSensor(Sensor s){
		s.setInstallationDate(new Timestamp(System.currentTimeMillis()));
		model.addSensor(s);
	}
	
	private static LAC parse(File f) throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		LAC lac = new LAC(Integer.parseInt(reader.readLine()));		
		return lac;
	}

	public static void main(String[] args) {
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
							
				}
			}
		}else
			new LAC();
	}

	public Model getModel() {
		return model;
	}

	public Timestamp getTime() {
		return new Timestamp(System.currentTimeMillis());
	}

	
	
}
 
