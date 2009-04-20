package unitTests;

import junit.framework.TestCase;
import junit.framework.TestResult;


/**
 * 
 * 
 * The test is executed with the following setup of "settings.xml":


//FIXME LOOKUP: Sjekk med A2-dokumentasjon at denne konfigurasjonen genererer alle mulige feil
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

public class ConnectionTests extends TestCase{
	
	
	/**
	 * Requirement 10
	 * Denne testen skal sjekke at det opprettes en forbindelse mellom sender og mottaker.
	 * Dette betyr at det ikke skal forekomme forstyrrelser fra andre forbindelser og at applikasjonlaget gjøres oppmerksom på forbindelsesbrudd 
	 */
	//FIXME UnitTest: Connection-test
	public void testConnection() {
	
	}
	
	/**
	 * Requirement 10
	 * Denne testen skal sjekke at implementasjonen er reliable 
	 */
	//FIXME UnitTest: Correctness-test
	public void testCorrectness() {
	
	}
	
	/**
	 * Requirement 10
	 * Denne testen skal sjekke at alle pakker leveres in-order 
	 */
	//FIXME UnitTest: InOrder-test
	public void testInOrder() {
	
	}
	
}
