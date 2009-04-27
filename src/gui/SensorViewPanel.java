package gui;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import model.Sensor;

public class SensorViewPanel {
	
	public static void viewSensorEvents(Sensor sensor) {
		final JFrame frame = new JFrame("Sensor Events");
		JPanel panel = new JPanel();
		frame.setSize(400, 400);
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
		model.Event[] events = new model.Event[sensor.getEvents().size()];
		for (int i = 0; i < events.length; i++) {
			events[i] = sensor.getEvents().get(i);
		}
		JList eventList = new JList(events);
		panel.add(eventList);
		panel.add(ok);
		
	}

}
