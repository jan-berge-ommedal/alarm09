package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import model.Event.EventType;

public class SensorViewPanel implements Values {
	private static Sensor sensor = null;
	static JList eventList;
	/**
	 * Statisk metode som genererer et vindu med sensorlogg - dvs en liste av events
	 * @param sensor
	 */
	public static void viewSensorEvents(Sensor sensorr) {
		sensor = sensorr;
		sensor.addEvent(new Event(2, EventType.ALARM, new Timestamp(0), sensor));
		final JFrame frame = new JFrame("Sensor Events");
		JPanel panel = new JPanel();
		frame.setSize(500, 400);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		JButton close = new JButton("Close");
		JButton checkSensor = new JButton("Check Sensor");
		eventList = refreshlist();
		final JButton alarm = new JButton("Stop Alarm");
		
		
		sensor.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				eventList = refreshlist();
				frame.repaint();
			}
		});
		
		//Close action listener
		close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}

		}
		);
		
		//Check sensor listener
		checkSensor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {				
				if(sensor.testSensor()) {
					JOptionPane.showMessageDialog(frame,
						    "Check successful.", 
						    "Success",
						    JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					JOptionPane.showMessageDialog(frame,
						    "Check error!",
						    "Check error!",
						    JOptionPane.ERROR_MESSAGE);
				}
			}

		}
		);
		
		//Alarm action listener
		alarm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sensor.setAlarmState(false);
				JOptionPane.showMessageDialog(frame,
					    "Alarm stopped!",
					    "Alarm stopped!",
					    JOptionPane.INFORMATION_MESSAGE);
				alarm.setVisible(false);
				eventList = refreshlist();
				frame.repaint();
			}

		}
		);
		
		

		
		
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
	
	private static JList refreshlist() {
		JList temp = new JList();
		if (sensor != null) {
			ArrayList<Event> events = sensor.getEvents();
			model.Event[] eventarray = new model.Event[events.size()];
			for(int i = 0; i < events.size(); i++) {
				eventarray[i] = events.get(i);
			}
			temp.setListData(eventarray);
			return temp;
		}
		else {
			System.err.println("Sensor var null, why?");
			return temp;
		}
	}
}
