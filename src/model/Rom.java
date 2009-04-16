package model;

import java.util.ArrayList;

public class Rom {
 
	private int ID;
	private int romNR;
	private String romType;
	private String romInfo;	 
	private ArrayList<Sensor> sensorer = new ArrayList<Sensor>();
	
	

	public Rom(int id, int romNR, String romType, String romInfo) {
		ID = id;
		this.romNR = romNR;
		this.romType = romType;
		this.romInfo = romInfo;
	}

	public int getID() {
		return ID;
	}

	public void setID(int id) {
		ID = id;
	}

	public int getRomNR() {
		return romNR;
	}

	public void setRomNR(int romNR) {
		this.romNR = romNR;
	}

	public String getRomType() {
		return romType;
	}

	public void setRomType(String romType) {
		this.romType = romType;
	}

	public String getRomInfo() {
		return romInfo;
	}

	public void setRomInfo(String romInfo) {
		this.romInfo = romInfo;
	}

	public ArrayList<Sensor> getSensorer() {
		return sensorer;
	}

	public void addSensor(Sensor sensor) {
		this.sensorer.add(sensor);
	}
	
	
	
	 
}
 
