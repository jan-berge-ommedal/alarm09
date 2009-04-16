package apps;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.ArrayList;

import connection.MACProtocol;
import connection.TCPConnection;

import database.Database;

import model.Model;
import no.ntnu.fp.net.co.Connection;
import no.ntnu.fp.net.co.ConnectionImpl;

public class MAC {
	private Connection macConnection;
	private ArrayList<LACAdaper> adapters = new ArrayList<LACAdaper>();	
	private Database database;
	private MACgui gui;
	
	private static int i = 0;
	
	public MAC() {
		try {
			database = new Database("mysql.stud.ntnu.no","janberge_admin","1234","janberge_db");
			macConnection = new TCPConnection(666);
			createNewLACAdaper();
			gui = new MACgui();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void createNewLACAdaper(){
		adapters.add(new LACAdaper(this));
	}
	 
	public int getNextLacId() {
		return database.getNextLACID();
	}
	
	public Connection getMainConnection() {
		return macConnection;
	}
	
	public Database getDatabase() {
		return database;
	}
	
	public static void main(String[] args) {
		new MAC();
	}
	 
	public class LACAdaper extends Thread{
		private Connection connection;
		private MAC mac;
		private Model model;
		private boolean running;
		
		
		
		public LACAdaper(MAC mac) {
			this.mac = mac;
			running = true;
			this.setName("Connection-"+(i++));
			start();
		}
		
		public void setModel(Model model){
			this.model=model;
		}
		
		public void run(){
			try {
				connection = mac.getMainConnection().accept();
				mac.createNewLACAdaper();
				while(running){
					String msg = connection.receive();
					MACProtocol.handleMSG(this,msg);
					System.out.println("asdfsadf");
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public Connection getConnection(){
			return connection;
		}
		
		public void stopAdapter(){
			running=false;
			try {
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public MAC getMAC() {
			return mac;
		}

		public boolean hasModel() {
			return model!=null;
		}

		public Model getModel() {
			return model;
		}

	}



	

}
 
