package connection;

import java.io.IOException;
import java.net.ConnectException;

import model.Model;

import apps.MAC.LACAdaper;
import apps.MAC.LACAdapter;

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
		}else if(receive.substring(0, 9).equals("GETNEXTID")){
			try {
				adaper.getConnection().send(""+adaper.getMAC().getDatabase().insertLAC(receive.substring(9)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(receive.substring(0, 9).equals("UPDATELAC")){
			try{
				String[] s = receive.split(" ");
				adaper.getMAC().getDatabase().updateLAC(Integer.parseInt(s[1]), s[2]);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(receive.substring(0, 9).equals("UPDATEROOM")){
			try{
				String[] s = receive.split(" ");
				adaper.getMAC().getDatabase().updateRoom(Integer.parseInt(s[1]), Integer.parseInt(s[2]), s[3], s[4]);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(receive.substring(0, 9).equals("UPDATESENSOR")){
			try{
				String[] s = receive.split(" ");
				boolean b = (s[2].equals("true")) ? true : false;
				adaper.getMAC().getDatabase().updateSensor(Integer.parseInt(s[1]), b, batteryStatus, installationDate)
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
