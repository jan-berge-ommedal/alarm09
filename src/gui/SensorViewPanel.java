package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import model.Model;
import model.Room;
import model.Sensor;

public class SensorViewPanel implements Values {

	/**
	 * Statisk metode som genererer et vindu med sensorlogg - dvs en liste av events
	 * @param sensor
	 */
	public static void viewSensorEvents(Sensor sensor) {
		final JFrame frame = new JFrame("Sensor Events");
		JPanel panel = new JPanel();
		frame.setSize(500, 400);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		JButton close = new JButton("Close");
		JButton checkSensor = new JButton("Check Sensor");
		JButton alarm = new JButton("Stop Alarm");
		close.addActionListener(new ActionListener() {

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
		eventList.setBounds(LEFT_SPACE, TOP_SPACE, 400, 200);
		close.setBounds(LEFT_SPACE +300, 300, BUTTON_WIDTH, BUTTON_HEIGHT);
		checkSensor.setBounds(LEFT_SPACE + 150, 300, BUTTON_WIDTH +25, BUTTON_HEIGHT);
		alarm.setBounds(LEFT_SPACE, 300, BUTTON_WIDTH + 25, BUTTON_HEIGHT);

		if(sensor != null) {
			if(!sensor.isAlarmState()) {
				alarm.setVisible(false);
			}
		}

		eventList.setVisible(true);
		panel.setLayout(null);
		panel.add(eventList);

		panel.add(checkSensor);
		panel.add(alarm);
		panel.add(close);
		
		if(sensor == null) {
			checkSensor.setVisible(false);
			alarm.setVisible(false);
			eventList.setVisible(false);
			JLabel noSensor = new JLabel("No sensor loaded!");
			noSensor.setBounds(LEFT_SPACE, TOP_SPACE, 400, 200);
			panel.add(noSensor);
		}
		
		frame.repaint();

	}

	/**
	 * main for testing av sensorviewpanel
	 * @param args
	 */
	public static void main(String[] args) {
		viewSensorEvents(new Sensor(2, true, 50, new Timestamp(0), new Room(2, 32, "Hus", "Stort",  new Model()), true));
	}

}
