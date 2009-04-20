package connection;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import no.ntnu.fp.net.co.Connection;

public class ConnectionImplementation implements Connection{

	@Override
	public Connection accept() throws IOException, SocketTimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connect(InetAddress remoteAddress, int remotePort)
			throws IOException, SocketTimeoutException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String receive() throws ConnectException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void send(String msg) throws ConnectException, IOException {
		// TODO Auto-generated method stub
		
	}

}
