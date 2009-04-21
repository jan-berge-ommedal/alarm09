package connection;

import java.io.IOException;
import java.net.ConnectException;

import model.Model;

import apps.MAC.LACAdapter;

public class MACProtocol {

	public static void handleMSG(LACAdapter adaper, String receive) {
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
		}else if(receive.substring(0, 9).equals("GETNEXTID")){
			try {
				adaper.getConnection().send(""+adaper.getMAC().getDatabase().insertLAC(receive.substring(9)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(receive.substring(0, 9).equals("UPDATELAC")){
			try{
				// Mangler matchende databasemetode
				adaper.getConnection().send("" + adaper.getMAC().getDatabase(). );
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(receive.substring(0, 9).equals("UPDATEROOM")){
			try{
				
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(receive.substring(0, 9).equals("UPDATESENSOR")){
			try{

			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(receive.substring(0, 9).equals("INSERTROOM")){
			try{

			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(receive.substring(0, 9).equals("INSERTSENSOR")){
			try{

			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(receive.substring(0, 9).equals("INSERTEVENT")){
			try{

			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}

}
