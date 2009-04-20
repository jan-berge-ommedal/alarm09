package apps;

import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * @author Olannon
 * 
 * denne klassen håndterer vinduet som presenteres fra en LAC maskin
 *
 */
public class LACgui extends JPanel implements Values {
	
	private boolean fromMac;
	private JButton installSensor;
	private JButton saveLog;
	private JButton checkSensors;
	private JButton returnMAC;
	private JLabel sensors;
	
	public LACgui() {
		Insets asdf = new Insets(0,0,0,0);
		JPanel pane = new JPanel();
		JFrame frame = new JFrame("LAC");
		frame.setSize(700, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(pane);
		frame.setVisible(true);
		
		installSensor = new JButton("Install Sensor");
		installSensor.setMargin(asdf);
		saveLog = new JButton("Save Log");
		saveLog.setMargin(asdf);
		checkSensors = new JButton("Check Sensors");
		checkSensors.setMargin(asdf);
		returnMAC = new JButton("Return to MAC");
		returnMAC.setMargin(asdf);
		//returnMAC.setVisible(false);
		sensors = new JLabel("Sensors");
		Font f = new Font("Dialog", Font.PLAIN, 20);
		sensors.setFont(f);
		sensors.setVisible(true);
	
		pane.setLayout(null);
		pane.add(installSensor);
		pane.add(saveLog);
		pane.add(checkSensors);
		pane.add(sensors);
		pane.add(returnMAC);
		installSensor.setBounds(LEFT_SPACE, TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		saveLog.setBounds(LEFT_SPACE + BUTTON_WIDTH + DEFAULT_SPACE, TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		sensors.setBounds(LEFT_SPACE, TOP_SPACE + BUTTON_HEIGHT + 2*DEFAULT_SPACE, 100, 20);
		checkSensors.setBounds(LEFT_SPACE, 700 - TOP_SPACE - 2*BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
		returnMAC.setBounds(LEFT_SPACE + BUTTON_WIDTH + DEFAULT_SPACE, 700 - TOP_SPACE - 2*BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
		
		
		
	}
	
	/**
	 * Public metode som endrer booleanverdien som sier noe om LACgrensesnittet er generert fra MAC eller ikke
	 * @param fromMac
	 */
	public void setFromMac(boolean fromMac) {
		this.fromMac = fromMac;
	}
	
	public static void main(String[] args) {
		//LACgui window = new LACgui();
		MACgui window2 = new MACgui();
	}

}
