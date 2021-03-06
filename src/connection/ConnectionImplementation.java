/*
 * Created on Oct 27, 2004
 */
package connection;

import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import no.ntnu.fp.net.admin.Log;
import no.ntnu.fp.net.cl.ClException;
import no.ntnu.fp.net.cl.ClSocket;
import no.ntnu.fp.net.cl.KtnDatagram;
import no.ntnu.fp.net.cl.KtnDatagram.Flag;
import no.ntnu.fp.net.co.AbstractConnection;
import no.ntnu.fp.net.co.Connection;
import no.ntnu.fp.net.co.SimpleConnection;

/**
 * Implementation of the Connection-interface. <br>
 * <br>
 * This class implements the behaviour in the methods specified in the interface
 * {@link Connection} over the unreliable, connectionless network realised in
 * {@link ClSocket}. The base class, {@link AbstractConnection} implements some
 * of the functionality, leaving message passing and error handling to this
 * implementation.
 * 
 * @author Sebj�rn Birkeland and Stein Jakob Nordb�
 * @see no.ntnu.fp.net.co.Connection
 * @see no.ntnu.fp.net.cl.ClSocket
 */
public class ConnectionImplementation extends AbstractConnection {

	/** Keeps track of the used ports for each server port. */
	private static Map<Integer, Boolean> usedPorts = Collections.synchronizedMap(new HashMap<Integer, Boolean>());

	/**
	 * Initialise initial sequence number and setup state machine.
	 * 
	 * @param myPort
	 *            - the local port to associate with this connection
	 */
	public ConnectionImplementation(int myPort) {
		super();
		this.myPort = myPort;
		this.myAddress = this.getIPv4Address();
	}

	private String getIPv4Address() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		}
		catch (UnknownHostException e) {
			return "127.0.0.1";
		}
	}

	/**
	 * Establish a connection to a remote location.
	 * 
	 * @param remoteAddress
	 *            - the remote IP-address to connect to
	 * @param remotePort
	 *            - the remote portnumber to connect to
	 * @throws IOException
	 *             If there's an I/O error.
	 * @throws java.net.SocketTimeoutException
	 *             If timeout expires before connection is completed.
	 * @see Connection#connect(InetAddress, int)
	 */
	public void connect(InetAddress remoteAddress, int remotePort) throws IOException,
	SocketTimeoutException {

		this.remoteAddress = remoteAddress.getHostAddress();
		this.remotePort = remotePort;

		//Lager en ny port for klienten, ikke i bruk til vanlig
		//this.myPort = this.createPort();
		//usedPorts.put(this.myPort, true);

		KtnDatagram d = this.constructInternalPacket(Flag.SYN);

		try {
			this.state = State.SYN_SENT;
			this.simplySendPacket(d);

		} catch (ClException e) {
			e.printStackTrace();
		}
		KtnDatagram b = this.receiveAck();
		if(b == null) {
			this.state = State.CLOSED;
			throw new SocketTimeoutException("No packet received in connect(), expected SYN_ACK");
		}
		else if(b.getFlag() == Flag.SYN_ACK) {
			//this.lastValidPacketReceived = b;
			this.remoteAddress = b.getSrc_addr();
			this.remotePort = b.getSrc_port();
			this.state = State.ESTABLISHED;
			this.sendAck(b, false);
		}
		else {
			this.state = State.CLOSED;
			throw new SocketTimeoutException("No SYN_ACK flag in packet received connect()");
		}
	}



	/**
	 * Listen for, and accept, incoming connections.
	 * 
	 * @return A new ConnectionImpl-object representing the new connection.
	 * @see Connection#accept()
	 */
	public Connection accept() throws IOException, SocketTimeoutException {

		this.state = State.LISTEN;

		KtnDatagram h = this.receivePacket(true);

		if (h != null) {
			if(h.getFlag() == Flag.SYN) {

				//Lager en ny port for denne tilkoblingen til en klient
				this.myPort = this.createPort();
				usedPorts.put(this.myPort, true);


				this.remoteAddress = h.getSrc_addr();
				this.remotePort = h.getSrc_port();

				this.state = State.SYN_RCVD;
				this.sendAck(h, true);
			}
			else {
				this.state = State.CLOSED;
				throw new IOException("SYN not recieved in accept()");
			}
		}
		else {
			this.state = State.CLOSED;
			throw new SocketTimeoutException("No packet recieved in accept()");
		}

		KtnDatagram i = this.receiveAck();

		if(i != null) {
			if(i.getFlag() == Flag.ACK) {
				ConnectionImplementation temp = new ConnectionImplementation(this.myPort);
				temp.remoteAddress = i.getSrc_addr();
				temp.remotePort = i.getSrc_port();
				temp.state = State.ESTABLISHED;
				//temp.nextSequenceNo = this.nextSequenceNo;
				//temp.lastValidPacketReceived = i;
				this.myPort = 4444;
				this.state = State.CLOSED;
				return temp;

			}
			else {
				this.state = State.CLOSED;
				throw new IOException("Wrong Flag recieved in accept()");
			}
		}
		else {
			this.state = State.CLOSED;
			throw new SocketTimeoutException("No ACK recieved in accept()");
		}
	}

	/**
	 * Send a message from the application.
	 * 
	 * @param msg
	 *            - the String to be sent.
	 * @throws ConnectException
	 *             If no connection exists.
	 * @throws IOException
	 *             If no ACK was received.
	 * @see AbstractConnection#sendDataPacketWithRetransmit(KtnDatagram)
	 * @see no.ntnu.fp.net.co.Connection#send(String)
	 */
	public void send(String msg) throws ConnectException, IOException {
		//Sjekker connection
		if(this.state == State.ESTABLISHED) {
			KtnDatagram f  = this.constructDataPacket(msg);
			KtnDatagram ack = this.sendDataPacketWithRetransmit(f);
			if(ack == null) {
				throw new IOException("No ACK recieved in send()");
			}
			//Sjekker om ACK er godkjent, og legger denne i last valid packet om den er det
			if(this.isValid(ack)) {
				this.lastValidPacketReceived = ack;
			}

			else throw new IOException("Wrong ACK recieved in send()");
		}
		else throw new ConnectException("There is no connection");
	}

	/**
	 * Wait for incoming data.
	 * 
	 * @return The received data's payload as a String.
	 * @see Connection#receive()
	 * @see AbstractConnection#receivePacket(boolean)
	 * @see AbstractConnection#sendAck(KtnDatagram, boolean)
	 */
	public String receive() throws ConnectException, IOException {
		if(this.state == State.ESTABLISHED) {
			KtnDatagram h = this.receivePacket(false);
			if(h == null) {
				throw new IOException("No packet recieved in recieve()");
			}
			else if(this.isValid(h)) {
				this.sendAck(h, false);
				this.lastValidPacketReceived = h;
				return h.getPayload().toString();
			}

			//Brukes til � sende ACK til en duplikat-pakke
			//if(this.isDuplicate(h)) {
			//	this.sendAck(h, false);
			//}

			else throw new IOException("Invalid packet recieved");
		}
		else throw new ConnectException("No connection established");
	}

	/**
	 * Close the connection.
	 * 
	 * @see Connection#close()
	 */
	public void close() throws IOException {

		//Sjekker om det er mottaker eller sender av close(),
		//ved � se om det er mottatt en FIN-flag pakke i disconnectRequest
		if(this.disconnectRequest != null) {
			if(this.disconnectRequest.getFlag() == Flag.FIN) {
				this.state = State.CLOSE_WAIT;
			}
		}

		//Sender sin close() prosedyre
		if(this.state == State.ESTABLISHED) {
			//System.out.println("Client closing");
			KtnDatagram f = this.constructInternalPacket(Flag.FIN);
			try {
				this.simplySendPacket(f);
				this.state = State.FIN_WAIT_1;
			}
			catch (ClException e) {
			}
			KtnDatagram ack = this.receiveAck();
			if(ack == null) throw new IOException("No ACK received in close()");
			if(ack.getFlag() == Flag.ACK) {
				this.state = State.FIN_WAIT_2;
			}
			else throw new IOException("No ACK reveived in close()");
			KtnDatagram fin = this.receivePacket(true);
			if(fin != null) {
				if(fin.getFlag() == Flag.FIN) {
					this.sendAck(fin, false);
					usedPorts.remove(this.myPort);
					this.state = State.CLOSED;
					//System.out.println("Connection to server " + fin.getDest_addr() +" is closed");
				}
				else throw new IOException("No FIN received in close()");
			}
			else throw new IOException("No FIN received in close()");
		}

		//Mottaker sin close() prosedyre
		else if(this.state == State.CLOSE_WAIT) {
			//System.out.println("Server closing");
			this.state = State.LAST_ACK;
			this.sendAck(this.disconnectRequest, false);
			KtnDatagram fin = this.constructInternalPacket(Flag.FIN);
			try {
				this.simplySendPacket(fin);
			}
			catch (ClException e) {

			}
			KtnDatagram ack = this.receiveAck();
			if(ack != null) {
				if(ack.getFlag() == Flag.ACK) {
					usedPorts.remove(this.myPort);
					this.state = State.CLOSED;
					//System.out.println("Connection to client " + ack.getDest_addr() +" is closed");
				}
				else throw new IOException("No ACK recieved in close()");
			}
			else throw new IOException("No ACK recieved in close()");
		}
	}

	/**
	 * Test a packet for transmission errors. This function should only called
	 * with data or ACK packets in the ESTABLISHED state.
	 * 
	 * @param packet
	 *            Packet to test.
	 * @return true if packet is free of errors, false otherwise.
	 */
	protected boolean isValid(KtnDatagram packet) {
		if(this.state == State.ESTABLISHED) {
			if(packet.calculateChecksum() != packet.getChecksum()){
				System.err.println("Checksum error!");
				return false;
			}
			if(!packet.getSrc_addr().equals(this.remoteAddress)){
				System.err.println("Ghostpacket error! Wrong address");
				return false;
			}
			if(this.remotePort != packet.getSrc_port()){
				System.err.println("Ghostpacket error! Wrong port");
				return false;
			}
			if(this.lastValidPacketReceived != null) {
				if(packet.getSeq_nr() != (this.lastValidPacketReceived.getSeq_nr() +1)) {
					System.err.println("Wrong sequence number [Expected: " + (this.lastValidPacketReceived.getSeq_nr() +1) + "] [Received: " + packet.getSeq_nr() + "]");
					return false;
				}
			}
			return true;
		}
		else return false;
	}


	private int createPort() {
		//Her genereres det en port p� server-siden,
		//paramatererene settes i while-l�kken
		int connectionPort = (int)(Math.random()*60000);
		while(usedPorts.containsKey(connectionPort) || connectionPort > 55000 || connectionPort < 40000) {
			connectionPort = (int)(Math.random()*60000);
		}
		return connectionPort;
	}


	private boolean isDuplicate(KtnDatagram packet) {
		//Brukes kun dersom man vil ACK'e p� en duplikat pakke,
		//alts� ikke i bruk
		if(this.lastValidPacketReceived.equals(packet)) {
			return true;
		}
		else return false;
	}
}
