package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import model.Sensor;

public class SensorViewPanel implements Values {
	
	/**
	 * Statisk metode som genererer et vindu med sensorlogg - dvs en liste av events
	 * @param sensor
	 */
	public static void viewSensorEvents(Sensor sensor) {
		final JFrame frame = new JFrame("Sensor Events");
		JPanel panel = new JPanel();
		frame.setSize(300, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
			
		}
		);
		JList eventList = new JList();
		if (sensor != null) {
			model.Event[] events = new model.Event[sensor.getEvents().size()];
			for (int i = 0; i < events.length; i++) {
				events[i] = sensor.getEvents().get(i);
			}
			eventList.setListData(events);
		}
		else {
			System.err.println("Sensor var null, why?");
		}
		eventList.setBounds(LEFT_SPACE, TOP_SPACE, 200, 200);
		ok.setBounds(2*LEFT_SPACE, 300, BUTTON_WIDTH, BUTTON_HEIGHT);
		ok.setVisible(true);
		eventList.setVisible(true);
		panel.setLayout(null);
		panel.add(eventList);
		panel.add(ok);
		frame.repaint();
		
	}
	
	/**
	 * main for testing av sensorviewpanel
	 * @param args
	 */
	public static void main(String[] args) {
		viewSensorEvents(null);
	}

}
