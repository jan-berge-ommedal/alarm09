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
		this.myAddress = "192.168.1.104";
		//this.myAddress = this.getIPv4Address();
		//usedPorts.put(0, true);
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

		int connectionPort = (int)(Math.random()*60000);
		while(usedPorts.containsKey(connectionPort) || connectionPort <= 1024) {
			connectionPort = (int)(Math.random()*60000);
		}
		usedPorts.put(connectionPort, true);
		this.myPort = connectionPort;

		KtnDatagram d = this.constructInternalPacket(Flag.SYN);

		try {
			this.state = State.SYN_SENT;
			this.lastDataPacketSent = d;
			this.simplySendPacket(d);

		} catch (ClException e) {
			e.printStackTrace();
		}
		KtnDatagram b = this.receiveAck();
		if(b == null) {
			throw new SocketTimeoutException("Timeout in connect");
		}
		else if(b.getFlag() == Flag.SYN_ACK) {
			this.remoteAddress = b.getSrc_addr();
			this.remotePort = b.getSrc_port();
			this.state = State.ESTABLISHED;
			this.sendAck(b, false);
		}
		else this.state = State.CLOSED;

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

				int connectionPort = (int)(Math.random()*60000);
				while(usedPorts.containsKey(connectionPort) || connectionPort <= 1024) {
					connectionPort = (int)(Math.random()*60000);
				}
				usedPorts.put(connectionPort, true);
				this.myPort = connectionPort;

				this.remoteAddress = h.getSrc_addr();
				this.remotePort = h.getSrc_port();
				this.lastValidPacketReceived = h;

				this.state = State.SYN_RCVD;
				this.sendAck(h, true);
			}
			else {
				this.state = State.CLOSED;
				throw new IOException("SYN not recieved");
			}
		}
		else throw new IOException("ACK not recieved");

		KtnDatagram i = this.receiveAck();
		this.lastValidPacketReceived  = i;

		if(i != null) {
			if(i.getFlag() == Flag.ACK) {
				ConnectionImplementation temp = new ConnectionImplementation(this.myPort);
				temp.remoteAddress = i.getSrc_addr();
				temp.remotePort = i.getSrc_port();
				temp.state = State.ESTABLISHED;
				this.myPort = 4444;
				this.state = State.CLOSED;
				return temp;

			}
			else throw new IOException("No ACK recieved");
		}
		else throw new IOException("No ACK recieved");
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
		if(this.state == State.ESTABLISHED) {
			KtnDatagram f  = this.constructDataPacket(msg);
			KtnDatagram ack = this.sendDataPacketWithRetransmit(f);
			if(ack == null) {
				throw new IOException("No ACK Recieved in send()");
			}
			else {
				 this.lastValidPacketReceived = ack;
			}
		}
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
			this.sendAck(h, false);
			this.lastValidPacketReceived = h;
			return h.getPayload().toString();
		}
		else throw new ConnectException("No connection established");
	}

	/**
	 * Close the connection.
	 * 
	 * @see Connection#close()
	 */
	public void close() throws IOException {
		if(this.state == State.ESTABLISHED) {
			KtnDatagram h = this.constructInternalPacket(Flag.FIN);
			KtnDatagram ack = this.sendDataPacketWithRetransmit(h);
			this.state = State.FIN_WAIT_1;
			if(ack != null) {
				if(ack.getFlag() == Flag.ACK) {
					this.state = State.FIN_WAIT_2;
				}
			}
			else throw new IOException("No ACK recieved");
		}
		if(this.state == State.FIN_WAIT_2) {
			KtnDatagram g = this.receivePacket(true);
			if(g != null) {
				if(g.getFlag() == Flag.FIN) {
					this.sendAck(g, true);
					usedPorts.remove(this.myPort);
					this.state = State.CLOSED;
				}
				else throw new IOException("No FIN recieved");
			}
			else throw new IOException("No FIN recieved");
		}
		else if (this.state == State.CLOSE_WAIT) {
			this.state = State.LAST_ACK;
			KtnDatagram l = this.constructInternalPacket(Flag.FIN);
			KtnDatagram n = this.sendDataPacketWithRetransmit(l);
			if(this.state == State.LAST_ACK) {
				if(n != null) {
					if(n.getFlag() == Flag.ACK) {
						usedPorts.remove(this.myPort);
						this.state = State.CLOSED;
					}
					else throw new IOException("No ACK recieved");
				}
			}
			else throw new IOException("No ACK");
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
			if(packet.calculateChecksum() != packet.getChecksum()) return false;
			if(!packet.getSrc_addr().equals(this.remoteAddress)) return false;
			if(this.remotePort != packet.getSrc_port()) return false;
			if(this.lastValidPacketReceived.getSeq_nr() == packet.getSeq_nr()) return false;
			if(this.lastValidPacketReceived.getFlag() != Flag.SYN_ACK) {
				if((packet.getSeq_nr() - 1) != this.lastValidPacketReceived.getSeq_nr()) return false;
			}
			return true;
		}
		return true;
	}
}
