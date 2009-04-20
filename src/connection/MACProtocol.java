package connection;

import java.io.IOException;
import java.net.ConnectException;

import model.Model;

import apps.MAC.LACAdaper;

public class MACProtocol {

	public static void handleMSG(LACAdaper adaper, String receive) {
		if(receive.startsWith("GETMODEL")){
			try {
				if(!adaper.hasModel()){
					Model m = adaper.getMAC().getDatabase().getLACModel(Integer.parseInt(receive.substring(8)));
					adaper.setModel(m);
					adaper.getConnection().send(XmlSerializer.toXml(m));
				}else{
					adaper.getConnection().send(XmlSerializer.toXml(adaper.getModel()));
				}
			} catch (ConnectException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(receive.equals("GETNEXTID")){
			try {
				adaper.getConnection().send(""+adaper.getMAC().getDatabase().getNextLACID());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
