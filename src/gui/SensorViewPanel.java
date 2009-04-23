package gui;

import javax.swing.JPanel;

import model.Sensor;

public class SensorViewPanel extends JPanel {
	
	private Sensor sensor;
	
	public SensorViewPanel(Sensor sensor) {
		this.sensor = sensor;
	}

}
