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
import javax.swing.JScrollPane;

import model.Event;
import model.Model;
import model.Room;
import model.Sensor;
import model.Event.EventType;
import model.Sensor.Alarm;

public class SensorViewPanel implements Values {
	
	private static Sensor sensor;
	private static JList eventList;
	private static JButton close;
	private static JButton checkSensor;
	private static JButton stopAlarm;
	private static JButton confirmAlarm;
	private static JButton discardAlarm;
	private static JScrollPane scrollFrame;
	
	
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
		
		stopAlarm = new JButton("Stop Alarm");
		discardAlarm = new JButton("Discard");
		confirmAlarm = new JButton("Confirm");
		
		scrollFrame = new JScrollPane(eventList);
		
		
		
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
		stopAlarm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sensor.setAlarmState(Alarm.DEACTIVATED);
				
				//sensor.addEvent(new Event(2, EventType.ALARM, new Timestamp(0), sensor));
				JOptionPane.showMessageDialog(frame,
					    "Alarm stopped!",
					    "Alarm stopped!",
					    JOptionPane.INFORMATION_MESSAGE);
				stopAlarm.setVisible(false);
				//checkSensor.setVisible(true);
				//eventList.setListData(giveArray());
				//eventList.repaint();
			}

		}
		);
		
		confirmAlarm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sensor.setAlarmState(Alarm.ACTIVATED);
				
				//sensor.addEvent(new Event(2, EventType.ALARM, new Timestamp(0), sensor));
				JOptionPane.showMessageDialog(frame,
					    "Alarm confirmed!",
					    "Alarm confirmed!",
					    JOptionPane.INFORMATION_MESSAGE);
				confirmAlarm.setVisible(false);
				discardAlarm.setVisible(false);
				stopAlarm.setVisible(true);
				checkSensor.setVisible(true);
				//eventList.setListData(giveArray());
				//eventList.repaint();
			}

		}
		);
		
		discardAlarm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sensor.setAlarmState(Alarm.DEACTIVATED);
				
				//sensor.addEvent(new Event(2, EventType.ALARM, new Timestamp(0), sensor));
				JOptionPane.showMessageDialog(frame,
					    "False Alarm!",
					    "False Alarm!",
					    JOptionPane.INFORMATION_MESSAGE);
				stopAlarm.setVisible(false);
				confirmAlarm.setVisible(false);
				discardAlarm.setVisible(false);
				checkSensor.setVisible(true);
				//eventList.setListData(giveArray());
				//eventList.repaint();
			}

		}
		);
		
		

		
		
		eventList.setBounds(LEFT_SPACE, TOP_SPACE, 400, 200);
		scrollFrame.setBounds(LEFT_SPACE, TOP_SPACE, 400, 200);
		close.setBounds(LEFT_SPACE +300, 300, BUTTON_WIDTH, BUTTON_HEIGHT);
		checkSensor.setBounds(LEFT_SPACE + 150, 300, BUTTON_WIDTH +25, BUTTON_HEIGHT);
		stopAlarm.setBounds(LEFT_SPACE, 300, BUTTON_WIDTH + 25, BUTTON_HEIGHT);
		confirmAlarm.setBounds(LEFT_SPACE, 300, BUTTON_WIDTH + 25, BUTTON_HEIGHT);
		discardAlarm.setBounds(LEFT_SPACE + BUTTON_WIDTH + LEFT_SPACE, 300, BUTTON_WIDTH + 25, BUTTON_HEIGHT);

		stopAlarm.setVisible(false);
		confirmAlarm.setVisible(false);
		discardAlarm.setVisible(false);
		checkSensor.setVisible(true);
		
		if(sensor != null) {
			if(sensor.isAlarmState() == Alarm.UNCONFIRMED) {
				checkSensor.setVisible(false);
				confirmAlarm.setVisible(true);
				discardAlarm.setVisible(true);
			}
			else if(sensor.isAlarmState() == Alarm.ACTIVATED) {
				stopAlarm.setVisible(true);
			}
		}

		
		eventList.setVisible(true);
		panel.setLayout(null);
		
		panel.add(scrollFrame);
		panel.add(checkSensor);
		panel.add(stopAlarm);
		panel.add(close);
		panel.add(confirmAlarm);
		panel.add(discardAlarm);
		
		if(sensor == null) {
			checkSensor.setVisible(false);
			stopAlarm.setVisible(false);
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
