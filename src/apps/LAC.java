package apps;

import gui.LACgui;
import help.AlarmHelp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.jar.JarInputStream;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import com.mysql.jdbc.jdbc2.optional.ConnectionWrapper;

import connection.AbstractApplicationProtocol;
import connection.ConnectionImplementation;
import connection.ConnectionStatusWrapper;
import connection.LACProtocol;
import connection.ModelEditController;
import connection.TCPConnection;
import connection.XmlSerializer;
import connection.ConnectionStatusWrapper.ConnectionStatus;

import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import model.Event.EventType;
import no.ntnu.fp.net.co.Connection;

/**
 * This is the logical LAC 
 * 
 * @author Jan Berge Ommedal
 *
 */


public class LAC extends ModelEditController{
	private Connection connection;
	
	private LACgui gui;

	private boolean running = true;
	
	private ListenThread thread;
	
	
	private static final int STARTPORT = 3000;
	private static String defaultAdres = "My Adresss";
	
	/**
	 * This constructor is used when creating a completly new LAC
	 * 
	 */
	public LAC(boolean useGUI) {
		super(new LACProtocol());
		if(useGUI)gui = new LACgui(this);
		connection = new ConnectionImplementation(STARTPORT);
		//connection = new TCPConnection(STARTPORT);
		connectWithRetry();
		
		try {
			connection.send("NEW"+defaultAdres);
			String reveiceID = connection.receive();
			int modelID = Integer.parseInt(reveiceID);
			Model m = new Model(this,modelID);
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		thread = new ListenThread(this);
		thread.start();
	}


	/**
	 * This constructor is used when loading a LAC for storage
	 * @param id an int, the stored id of the LAC
	 */
	public LAC(int id,boolean useGUI) {
		super(new LACProtocol());
		if(useGUI)gui = new LACgui(this);
		connectWithRetry();

		
		try {
			connection.send("ID"+id);
			String ack = connection.receive();
			if(!ack.equals("ACK")){
				System.err.println("Refused by the MAC. Exiting");
				System.exit(0);
			}
			this.getProtocol().receiveCompleteModel(connection, id,this);
			startAllSensors();
			System.out.println(model);
		} catch (ParseException e) {
			System.err.println("Could not load model");
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread = new ListenThread(this);
		thread.start();
	}
	

	private void startAllSensors() {
		for(Sensor s : model.getSensors()){
			s.startSensor();
		}
	}


	private void connectWithRetry() {
		boolean connected = false;
		while(!connected){
			try{
				connect(5);
				connected = true;
			}catch(IOException e) {
				//Try to reconnect
				try {
					this.connectionWrapper.setConnectionStatus(ConnectionStatus.DISCONNECTED);
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void connect(int i) throws IOException {
		if(connection==null)connection = new ConnectionImplementation(STARTPORT);	
		connectionWrapper.setConnectionStatus(ConnectionStatus.CONNECTING);
		while(i>0){
			try {
				connection.connect(InetAddress.getByName(MAC.MACIP), MAC.SERVERPORT);
				connectionWrapper.setConnectionStatus(ConnectionStatus.CONNECTED);
				return;
			}catch (IOException e) {
				System.err.println("Reattempting to connect ("+(i-1)+" retries left)");
				i--;
			}
		}
		throw new IOException("Could not connect to the MAC");
	}
	
	
	
	
	
	
	

	



	public static void main(String[] args) {
		boolean run = true;
		while(run){
			try{
			String s = JOptionPane.showInputDialog("Skriv inn ID til LACen som skal startes. (-1 genererer ny LAC)");
			int id = Integer.parseInt(s);
			if(id<0)
				new LAC(true);
			else
				new LAC(id,true);
			run=false;
			}catch(NumberFormatException e){
				System.err.println("Kun tall!");
			}
		}
	}
	
	
	public LACProtocol getProtocol(){
		return (LACProtocol) super.getProtocol();
	}
	
	

	class ListenThread extends Thread implements PropertyChangeListener, ActionListener{
		private ModelEditController mec;
		
		public ListenThread(ModelEditController mec) {
			this.mec=mec;
			mec.addPropertyChangeListener(this);
		}
		
		public void run(){
			while(true){
				try {
					String receivedMSG = connection.receive();
					System.out.println("LAC RECIEVED COMMAND THROUGH LISTENENING:\n------------\n"+receivedMSG);
					getProtocol().handleMSG(receivedMSG, mec, connection);
				} catch (ConnectException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent arg0) {
			//CHANGES HAS BEEN DONE IN MODEL. SUSPEND THIS THREAD SO IT DONT INTERRUPT COMMUNICATION
			this.suspend();
			Timer resumeTimer = new Timer(2000,this);
			resumeTimer.start();
			
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			this.resume();
			
		}
	}



	public void close() {
		System.out.println("This LAC is closing");
		thread.stop();
		for(Sensor s : this.getModel().getSensors()){
			new Event(-1,EventType.SHUTDOWN,this.getTime(),s);
		}
		try {
			
			getProtocol().close(connection);
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	



	@Override
	protected Connection getConnection() {
		return connection;
	}


	

}
 
