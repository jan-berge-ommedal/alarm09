package misc.tcp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class SimpleTcpClient extends AbstractTcpConnection{
	
	private Thread thread;

	/**
	 * Generates a CLIENT
	 * 
	 * @param hostAdress - IPadress to connect to
	 * @param port - port to connect to
	 * @param listener - TcpListener that receives incomming messages
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public SimpleTcpClient(String hostAdress, int port, TcpListener listener) throws UnknownHostException, IOException {
		Socket socket = new Socket(hostAdress,port);
		if(listener!=null)listener.connectionEstablished(this);
		this.out = new DataOutputStream(socket.getOutputStream());
		thread = new ClientThread(new BufferedReader(new InputStreamReader(socket.getInputStream())), listener, this);				
	}
	
	
	class ClientThread extends Thread{
		private BufferedReader in;
		private TcpListener listener;
		private AbstractTcpConnection connection;
		
		public ClientThread(BufferedReader in, TcpListener listener, AbstractTcpConnection connection) {
			this.setName("Tcp-Client");
			this.listener=listener;
			this.in=in;
			this.connection=connection;
			this.start();
		}
		
		public void initiateReader(BufferedReader in){
			this.in=in;
			this.start();
		}
		
		public void run(){			
			try {
				if(listener!=null){
					while(true){
						if(in.ready()){
							listener.receivedMsg(in.readLine(),connection);
						}else{
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	
		
	}
	
	
}



