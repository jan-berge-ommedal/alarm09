package guiAddons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import gui.BlinkingList;
import gui.LACrenderer;
import gui.Values;
import help.AlarmHelp;
import apps.LAC;
import apps.MAC;
import model.Model;


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


public class AlarmActivator extends JFrame implements Values, ActionListener{

	private JButton checkAlarm;
	private JLabel sensors;
	private JList sensorList;
	private Model model;
	private JLabel status;
	private JLabel statusFelt;
	
	
	
	public AlarmActivator(Model model){
		this.model = model;
		JPanel pane = new JPanel();
		JFrame frame = new JFrame("Alarm check");
		frame.setSize(700, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(pane);
		frame.setVisible(true);
		
		
		checkAlarm = new JButton("Check alarm");
		checkAlarm.addActionListener(this);
		
		sensors = new JLabel("Sensors");
		status = new JLabel("Status");
		
		pane.setLayout(null);
		pane.add(sensors);
		pane.add(status);
		pane.add(statusFelt);
		
	JLabel[] statusf = new JLabel[20];
		
	for (int i = 0; i < statusf.length; i++) {
			statusf[i] = new JLabel();
			statusf[i].setBounds(100, 40+(i*10), 30, 30);
	}
		
		
	sensorList = new BlinkingList();
	
	sensorList.setCellRenderer(new LACrenderer());
	sensorList.setVisibleRowCount(40); 
	sensorList.setVisible(true);
	pane.add(sensorList);
	sensorList.setBounds(LEFT_SPACE, TOP_SPACE + BUTTON_HEIGHT + 3*DEFAULT_SPACE + LABEL_HEIGHT, LIST_WIDTH, LIST_HEIGHT);
	sensorList.setFixedCellWidth(LIST_ELEMENT_WIDTH);
	sensorList.setFixedCellHeight(LIST_ELEMENT_HEIGHT);
	}
	
	
	public static void main(String[] args) {
		LAC lac = new LAC();
		Model m = AlarmHelp.getDefaultModel();
		lac.setModel(m);
		new AlarmActivator(m);
		
	}

	public void actionPerformed(ActionEvent e) {
		
	}
}
