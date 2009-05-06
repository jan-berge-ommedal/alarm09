package unitTests;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import junit.framework.TestCase;

import no.ntnu.fp.net.co.Connection;
import connection.ConnectionImplementation;

public class ConnectionTest extends TestCase {
	
	public void testConnection() throws InterruptedException {
		Reciever receiver = new Reciever();
		receiver.start();
		
		Sender sender = new Sender();
		sender.start();
		sender.join();
		
	}

	public static void main(String[] args) {

		ConnectionTest test = new ConnectionTest();
		
		
		
	}
	
	
	public class Sender extends Thread{
		
		public void run(){
			Connection connection = new ConnectionImplementation(4000);
			try {
				connection.connect(InetAddress.getByName("localhost"), 3000);
				connection.send("This is a test-string");
				connection.close();
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	public class Reciever extends Thread{
		public void run(){
			ConnectionImplementation imp = new ConnectionImplementation(3000);
			Connection connection = null;
			String received = "";
			try {
				connection = imp.accept();
				received = connection.receive();
				connection.receive();
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (EOFException e){
				try {
					connection.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			System.err.println("Sender received: "+received);
		}
		
		
	}
}
