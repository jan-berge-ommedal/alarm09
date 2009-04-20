package connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;


import no.ntnu.fp.net.co.Connection;

/**
 * This is a simple implement of {@link no.ntnu.fp.net.co.Connection Connection} using the TCP-protocol
 * 
 * @author Jan Berge Ommedal
 *
 */

public class TCPConnection implements Connection{
	private ServerSocket server;
	private Socket socket;
	
	private DataOutputStream out;
	private BufferedReader in;
	
	public TCPConnection(int port) throws IOException{
		server = new ServerSocket(port);
	}
	
	public TCPConnection(Socket socket) throws IOException{
		this.socket=socket;
		out = new DataOutputStream(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	@Override
	public Connection accept() throws IOException, SocketTimeoutException {
		return new TCPConnection(server.accept());
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connect(InetAddress remoteAddress, int remotePort)throws IOException, SocketTimeoutException {
		socket = new Socket(remoteAddress,remotePort);
		out = new DataOutputStream(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	@Override
	public String receive() throws ConnectException, IOException {
		while(true){
			if(in.ready())
				return in.readLine();
			else{
				try {
					Thread.currentThread().sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void send(String msg) throws ConnectException, IOException {
		out.writeBytes(msg+"\n");
	}

}
