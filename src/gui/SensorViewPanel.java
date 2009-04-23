package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.Sensor;

public class SensorViewPanel extends JPanel {
	
	private Sensor sensor;
	private JFrame frame;
	
	public SensorViewPanel(Sensor sensor) {
		this.sensor = sensor;
		frame = new JFrame("Sensor Events");
	}

}
