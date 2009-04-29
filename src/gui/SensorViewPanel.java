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
	
	private static Sensor sensor;
	private static JList eventList;
	private static JButton close;
	private static JButton checkSensor;
	private static JButton alarm;
	
	
	/**
	 * Statisk metode som genererer et vindu med sensorlogg - dvs en liste av events
	 * @param sensor
	 */
	public static void viewSensorEvents(Sensor sensorr) {
		
		sensor = sensorr;
		//sensor.addEvent(new Event(2, EventType.ALARM, new Timestamp(0), sensor));
		
		final JFrame frame = new JFrame("Sensor Events");
		
		JPanel panel = new JPanel();
		
		frame.setSize(500, 400);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		close = new JButton("Close");
		checkSensor = new JButton("Check Sensor");
		eventList = new JList();
		alarm = new JButton("Stop Alarm");
		
		eventList.setListData(giveArray());
		
		sensor.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				eventList.setListData(giveArray());
				//eventList.repaint();
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
				//sensor.addEvent(new Event(2, EventType.ALARM, new Timestamp(0), sensor));
				JOptionPane.showMessageDialog(frame,
					    "Alarm stopped!",
					    "Alarm stopped!",
					    JOptionPane.INFORMATION_MESSAGE);
				alarm.setVisible(false);
				//eventList.setListData(giveArray());
				//eventList.repaint();
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

	
	
	private static model.Event[] giveArray() {
		if (sensor != null) {
			ArrayList<Event> events = sensor.getEvents();
			model.Event[] eventarray = new model.Event[events.size()];
			for(int i = 0; i < events.size(); i++) {
				eventarray[i] = events.get(i);
			}
			return eventarray;
		}
		else {
			System.err.println("Sensor var null, why?");
			return null;
		}
	}
}
