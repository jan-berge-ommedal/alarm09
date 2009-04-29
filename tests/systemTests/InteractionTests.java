package systemTests;

import model.Model;
import model.Room;
import model.Sensor;
import junit.framework.TestCase;
import connection.ModelEditController;
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
		
		
		ModelEditController sender = lac;
		ModelEditController receiver = mac.getLACAdapterList().getElementLACAdapterAt(lacid-1);
		
		standardTimeout();
		assertEquals(receiver.getModel().getID(), sender.getModel().getID());
		assertEquals(receiver.getModel().toString(), sender.getModel().toString());
		
		Model senderModel = sender.getModel();
		Model receiverModel = receiver.getModel();
		

		String testString = "This is a test";
 		senderModel.setAdresse(testString);
 		
 		standardTimeout();
 		assertEquals(senderModel.getAdresse(),receiverModel.getAdresse());
		
 		Room room = new Room(-1, 0, "testString", "testString", senderModel);
 		
 		senderModel.addRoom(room);
 		
 		standardTimeout();
 		//IDen skal endres 
 		assertEquals(room.getID()!=-1,true);
 		assertEquals(receiver.getModel().toString(), sender.getModel().toString());
		
 		
 		room.setRomInfo("newInfo");
 		
 		standardTimeout();
 		assertEquals(receiver.getModel().toString(), sender.getModel().toString());
 		
 		room.setRomNR(56);
 		standardTimeout();
 		assertEquals(receiver.getModel().toString(), sender.getModel().toString());
 		
 		room.setRomType("asdfsadfsafd");
 		standardTimeout();
 		assertEquals(receiver.getModel().toString(), sender.getModel().toString());
	
 		senderModel.removeRoom(room);
 		standardTimeout();
 		assertEquals(receiver.getModel().toString(), sender.getModel().toString());
 		
 		Sensor s = new Sensor(-1,true,100,LAC.getTime(),room,false);
 		room.addSensor(s);
 		
 		senderModel.addRoom(room);
 		standardTimeout();
 		//Når et rom med tilhørende sensorer addes, må dette skje på begge sider. Iden til sensor må også settes
 		assertEquals(senderModel.getSensor(-1)!=null, true);
 		assertEquals(senderModel.getSensor(-1).getID()!=-1, true);
 		assertEquals(receiver.getModel().toString(), sender.getModel().toString());
 		
 	 		
 		
	}

	private void standardTimeout() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
