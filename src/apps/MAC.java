package apps;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import connection.MACProtocol;
import connection.TCPConnection;

import database.Database;

import no.ntnu.fp.net.co.Connection;
import no.ntnu.fp.net.co.ConnectionImpl;

public class MAC {
	private Connection macConnection;
	private ArrayList<LACAdaper> adapters = new ArrayList<LACAdaper>();	
	private Database database;
	//private MACGui gui;
	
	private static int i = 0;
	
	public MAC() {
		database = new Database();
		macConnection = new TCPConnection(666);
		createNewLACAdaper();
		//gui = new MACGui(this);
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
		private boolean running;
		
		
		
		public LACAdaper(MAC mac) {
			this.mac = mac;
			running = true;
			this.setName("Connection-"+(i++));
			start();
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

	}



	

}
 
