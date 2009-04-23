package guiAddons;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import gui.BlinkingList;
import gui.LACrenderer;
import gui.Values;
import help.AlarmHelp;
import apps.LAC;
import apps.MAC;
import model.Model;
import model.Sensor;


//FIXME Eirik: Alarm Activator
/**
 * This class will be used to test Requirement 14.
 * 
 * It contains a simple gui that can activate alarms on sensors of the given model
 * 
 * 
 * 
 * @author Jan Berge Ommedal, ......
 *
 */


public class AlarmActivator extends JFrame implements Values{

	private JButton checkAlarm;
	private JLabel sensors;
	private JList sensorList;
	private Model model;
	private JLabel status;
	private JLabel statusFelt;
	
	
	
	public AlarmActivator(Model model){
		this.model = model;
		JPanel pane = new JPanel();
		this.setSize(300, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(pane);
		this.setVisible(true);
		
		
		sensors = new JLabel("Sensors");
		sensors.setBounds(18, DEFAULT_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		status = new JLabel("Status");
	
		
		statusFelt = new JLabel("Alarmstatus");
		statusFelt.setBounds(DEFAULT_SPACE+BUTTON_WIDTH+DEFAULT_SPACE+8, DEFAULT_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		
		pane.setLayout(null);
		pane.add(sensors);
		pane.add(status);
		pane.add(statusFelt);
		
		Sensor[] sensors = model.getSensors();
		for(int i=0; i<sensors.length;i++){
			final Sensor s = sensors[i];
			JPanel elementPanel = new JPanel();			
			
			JLabel label = new JLabel("Sensor "+s.getId());
			JButton alarmButton = new JButton("Start alarm");
			alarmButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					s.setAlarmState(true);
				}
			});
			
			elementPanel.add(label);
			elementPanel.add(alarmButton);
			elementPanel.setBounds(18, 50+i*30, 200, 30);
			this.add(elementPanel);
			
			
		}
		
	
	
	
	this.setContentPane(pane);
	}
	
	
	public static void main(String[] args) {
		LAC lac = new LAC(1);
		new AlarmActivator(lac.getModel());
		
	}


}

	


