package model;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;


import connection.ConnectionListener;
import database.Database;

import no.ntnu.fp.net.co.Connection;
import no.ntnu.fp.net.co.ConnectionImpl;

public class MAC {
	private Connection macConnection;
	private ArrayList<LACAdaper> adapters = new ArrayList<LACAdaper>();	
	private Database database;
	
	public MAC() {
		database = new Database();
		macConnection = new ConnectionImpl(501);
	}
	
	public void createNewLACAdaper(){
		adapters.add(new LACAdaper(this));
	}
	 
	public int getNextLacId() {
		return database.getNextLACID();
	}
	
	public Connection getMainConnection() {
		return macConnection;
	}
	 
	class LACAdaper extends Thread{
		private Connection connection;
		private MAC mac;
		private boolean running;
		
		public LACAdaper(MAC mac) {
			this.mac = mac;
			running = true;
			start();
		}
		
		public void run(){
			try {
				connection = mac.getMainConnection().accept();
				while(running){
					String msg = connection.receive();
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
	}

	

}
 
