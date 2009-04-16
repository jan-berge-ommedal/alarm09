package connection;

import java.io.IOException;
import java.net.ConnectException;

import apps.MAC.LACAdaper;

public class MACProtocol {

	public static void handleMSG(LACAdaper adaper, String receive) {
		if(receive.equals("GETMODEL")){
			
		}else if(receive.equals("GETNEXTID")){
			try {
				adaper.getConnection().send(""+adaper.getMAC().getDatabase().getNextLACID());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
