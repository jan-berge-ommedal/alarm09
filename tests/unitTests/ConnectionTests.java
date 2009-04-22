package unitTests;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import unitTests.SimpleHost.ServerConnectionThread;

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
		
		SimpleHost host;
		try {
			host = new SimpleHost(800);
			
			
			host.addHostListener(this);
			host.acceptNewConnection();
			
			Connection client1 = new ConnectionImplementation(900);
			client1.connect(InetAddress.getByName("localhost"), 800);
			String aMessage = "TestMessage";
			nextMessage = aMessage;
			client1.send(aMessage);
		}catch (IOException e) {
			assertEquals("You failed Start DEBUGGING", true, false);
		}

		
		
		
	
	}
	
	/**
	 * Requirement 10
	 * Denne testen skal sjekke at implementasjonen er reliable 
	 */
	//FIXME Jan: Correctness-test
	public void testCorrectness() {
	
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
		assertEquals("Expected Message didnt match", nextMessage, msg);
		nextMessage=null;
		
	}
	
	

		
	
		
}
