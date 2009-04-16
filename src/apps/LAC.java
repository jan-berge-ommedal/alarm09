package apps;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;

import connection.LACProtocol;
import connection.TCPConnection;

import model.Model;
import model.Room;
import model.Sensor;
import no.ntnu.fp.net.co.Connection;
import no.ntnu.fp.net.co.ConnectionImpl;


public class LAC implements PropertyChangeListener {
	private Model model;
	private Connection connection;
	
	private LACgui gui;
	
	private static int connectionint = 1;
	private static final int SERVERPORT = 666;
	
	public LAC() {
		try {
			connect();
			int modelID = LACProtocol.receiveNextModelID(connection);
			Model m = new Model();
			m.setID(modelID);
			
			gui = new LACgui();
		} catch (IOException e) {
			e.printStackTrace();
		}
			

		
	}
	
	public LAC(int id) {
		
		try {
			connect();
			setModel(LACProtocol.receiveCompleteModel(connection, id));
			gui = new LACgui();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void connect() throws SocketTimeoutException, UnknownHostException, IOException{
			connection = new TCPConnection(SERVERPORT+connectionint++);
			connection.connect(InetAddress.getByName("localhost"), SERVERPORT);
	}
	
	public Model getModel() {
		return model;
	}
	
	private void setModel(Model m){
		model = m;
		m.addPropertyChangeListener(this);
	}
	
	
		 
	public boolean checkAlarm() {
		for (Sensor s : model.getSensors()) {
			if(s.testSensor())return true;
		}	
		return false;
	}
	


	public static Timestamp getTime() {
		return new Timestamp(System.currentTimeMillis());
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if(e.getSource() instanceof Sensor)
			LACProtocol.updateMAC(connection,this,((Sensor)e.getSource()));
		else if(e.getSource() instanceof Room){
			LACProtocol.updateMAC(connection,this,((Room)e.getSource()));
		}
		
	}
	
	
	private static LAC parse(File f) throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		LAC lac = new LAC(Integer.parseInt(reader.readLine()));		
		return lac;
	}

	public static void main(String[] args) {
		new LAC();
		new LAC(1);
		
		/*
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
		*/
	}

	

	
	
}
 
