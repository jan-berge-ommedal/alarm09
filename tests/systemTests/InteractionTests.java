package systemTests;

import junit.framework.TestCase;
import connection.ModelEditControll;
import apps.LAC;
import apps.MAC;

public class InteractionTests extends TestCase{
	
	
	/**
	 * This test verifies requirement 12
	 * 
	 */
	//FIXME Oddy MAC & LAC interaction
	public void testInteraction(){

		int lacid = 1;

		MAC mac = new MAC(false);
		LAC lac = new LAC(lacid,false);
		
		
		ModelEditControll sender = lac;
		ModelEditControll receiver = mac.getLACAdapterList().getElementLACAdapterAt(lacid);
		
		
		assertEquals(receiver.getModel().getID(), sender.getModel().getID());
		
		assertEquals(receiver.getModel().toString(), sender.getModel().toString());
		
		
		
		
		
		
		
	}

}
