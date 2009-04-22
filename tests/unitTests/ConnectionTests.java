package unitTests;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import testHelp.HostListener;
import testHelp.SimpleHost;
import testHelp.SimpleHost.ServerConnection;
import testHelp.SimpleHost.ServerConnectionThread;

import no.ntnu.fp.net.co.Connection;
import connection.ConnectionImplementation;
import junit.framework.TestCase;
import junit.framework.TestResult;


/**
 * 
 * 
 * The test is executed with the following setup of "settings.xml":


//FIXME Jan: Sjekk med A2-dokumentasjon at denne konfigurasjonen genererer alle mulige feil
<?xml version="1.0" encoding="UTF-8"?>
<root>
  <errors>true</errors>
  <loss>0.7</loss>
  <delay>0.7</delay>
  <ghost>0.8</ghost>
  <payload>0.7</payload>
  <header>0.7</header>
  <onlydata>true</onlydata>
  <serverAddress>localhost</serverAddress>
  <serverport>10500</serverport>
  <simpleConnection>false</simpleConnection>
</root>


 * 
 * 
 * 
 *
 *
 */

public class ConnectionTests extends TestCase implements HostListener{
	private String nextMessage;

	/**
	 * Requirement 10
	 * Denne testen skal sjekke at det opprettes en forbindelse mellom sender og mottaker.
	 * Dette betyr at det ikke skal forekomme forstyrrelser fra andre forbindelser og at applikasjonlaget gjøres oppmerksom på forbindelsesbrudd 
	 */
	//FIXME Jan: Connection-test
	public void testConnection() {
		
		try {
			typicalTransmit();
		} catch (IOException e) {
			assertEquals("Her er det noe galt", false,true);
		}
		
		connectIssues();
		
		closeIssues();
	
	}
	
	private void closeIssues() {
		// TODO Auto-generated method stub
		
	}

	private void connectIssues() {
		SimpleHost host;
		ServerConnection hostConnection1 = null;
		
		try {
			host = new SimpleHost(810);
			host.addHostListener(this);
			hostConnection1 = host.acceptNewConnection();
		} catch (IOException e) {
			assertEquals("Her er det noe galt", false,true);
		}
						
		Connection client1 = new ConnectionImplementation(900);
		
		try {
			client1.send("Dette går ikke ann");
			assertEquals("Skal ikke kunne sende uten connecta", false, true);
		}catch (IOException e) {}
		
		
		try {
			client1.connect(InetAddress.getByName("192.563.74.86"), 800);
		} catch (SocketTimeoutException e1) {
		} catch (UnknownHostException e1) {
		} catch (IOException e1) {
			assertEquals("Kast unkownHostException eller SocketTimeout", false, true);
		}
		
		try {
			hostConnection1.getThread().getConnection().send("Dette går ikke ann");
			assertEquals("Skal ikke kunne sende uten å ha receiva data (Skal være threeway)", false, true);
		}catch (IOException e) {}
		
		
	}

	private void typicalTransmit() throws UnknownHostException, IOException {
		SimpleHost host = new SimpleHost(800);
		ServerConnection hostConnection = host.acceptNewConnection();
		Connection con = new ConnectionImplementation(900);
		con.connect(InetAddress.getByName("localhost"), 800);
		con.close();
		hostConnection.getThread().stop();		
	}

	/**
	 * Requirement 10
	 * Denne testen skal sjekke at implementasjonen er reliable 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws SocketTimeoutException 
	 */
	//FIXME Jan: Correctness-test
	public void testCorrectness() {
		
		SimpleHost host;
		try {
			host = new SimpleHost(820);
		
			ServerConnection c = host.acceptNewConnection();
			host.addHostListener(this);
		
			Connection sender = new ConnectionImplementation(920);
			sender.connect(InetAddress.getByName("localhost"), 820);
			
			
			String complexMessage = "gg34æøÅ2riuf\"dq32fuh3§!#¤%&#Ãz&/()=?=)(/&%¤#‡!25ityqioQE¤WT¤%WGFAWyeæ56gtQE¤WTYE%RGTSUIT(&OWT%E¤&IUEHYERW#¤T%DFGDSFGDFYER&UYHAswergfrju=J";
			for(int i=0; i<1000 ; i++){
				nextMessage=complexMessage;
				sender.send(complexMessage);
			}
			
			sender.close();
			c.getThread().stop();
		
		} catch (IOException e) {
			assertEquals("Her er det noe galt", false);
		}
	
	}
	
	/**
	 * Requirement 10
	 * Denne testen skal sjekke at alle pakker leveres in-order 
	 */
	//FIXME Jan: InOrder-test
	public void testInOrder() {
	
	}
	
	

	@Override
	public void connectionClosed(ServerConnectionThread thread) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionEstablished(ServerConnectionThread thread) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receivedMsg(String msg, ServerConnectionThread thread) {
		assertEquals("Expected Message didnt match", nextMessage.equals(msg));
		nextMessage=null;
	}

}
