package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;

import no.ntnu.fp.net.co.Connection;
import no.ntnu.fp.net.co.ConnectionImpl;


public class LAC {
 
	private int id;
	private String adresse;
	private ArrayList<Sensor> sensorer;
	private Connection connection;
	
	//private LACGui gui;
	
	public LAC() {
		adresse = "<adresse>";
		connection = new ConnectionImpl(500);
		try {
			connection.connect(InetAddress.getByName("192.168.0.5"), 501);
		} catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//id = Protocol.recieveData(connection,Protol.MessageType.GETNEXTLEACID).getID();
		//gui = new LACGui();
	}
	
	public LAC(int id) {
		this.id=id;
		connection = new ConnectionImpl(500);
		try {
			connection.connect(InetAddress.getByName("192.168.0.5"), 501);
		} catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		//gui = new LACGui();
	}
	
		 
	public boolean checkAlarm() {
		for (Sensor s : sensorer) {
			if(s.testSensor())return true;
		}	
		return false;
	}
	
	public void addSensor(Sensor s){
		s.setInstallationDate(new Timestamp(System.currentTimeMillis()));
		s.setLac(this);
		sensorer.add(s);
	}
	
	private static LAC parse(File f) throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		
		LAC lac = new LAC();
		lac.setID(Integer.parseInt(reader.readLine()));
		
		return null;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public ArrayList<Sensor> getSensorer() {
		return sensorer;
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

	public Timestamp getTime() {
		return new Timestamp(System.currentTimeMillis());
	}

	
	
}
 
