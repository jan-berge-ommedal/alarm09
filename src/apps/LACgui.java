package apps;

import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
	private JList sensorList;
	
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
		sensors.setBounds(LEFT_SPACE, TOP_SPACE + BUTTON_HEIGHT + 2*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		checkSensors.setBounds(LEFT_SPACE, 700 - TOP_SPACE - 2*BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
		returnMAC.setBounds(LEFT_SPACE + BUTTON_WIDTH + DEFAULT_SPACE, 700 - TOP_SPACE - 2*BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
		
		/*
		 * Initialiserer JListen
		 */
		sensorList = new JList(new String[]{"lol"});
		sensorList.setCellRenderer(new LACrenderer());
		sensorList.setVisibleRowCount(7); 
		sensorList.setVisible(true);
		pane.add(sensorList);
		sensorList.setBounds(LEFT_SPACE, TOP_SPACE + BUTTON_HEIGHT + 3*DEFAULT_SPACE + LABEL_HEIGHT, LIST_WIDTH, LIST_HEIGHT);
		sensorList.setFixedCellWidth(LIST_ELEMENT_WIDTH);
		sensorList.setFixedCellHeight(LIST_ELEMENT_HEIGHT);
	}
	
	/**
	 * Public metode som endrer booleanverdien som sier noe om LACgrensesnittet er generert fra MAC eller ikke
	 * @param fromMac
	 */
	public void setFromMac(boolean fromMac) {
		this.fromMac = fromMac;
	}
	
	/*
	 * Her følger diverse metoder som lager de vinduene som kommer opp dersom menyer som feks "Install Sensor" vises fra LACen
	 */
	 
	/**
	 * Kalles når en sensor skal innstalleres og lukker vinduet samt åpner vinduet for sensorinnstallering
	 */
	public static void sensorAttributes(boolean install) {
		JFrame frame = new JFrame("Sensor attributes");
		JPanel panel  = new JPanel();
		
		//pakker frame etc
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel header = new JLabel();
		if (install) {
			header.setText("New Sensor");
		}
		else {
			header.setText("Edit Sensor");
		}
		header.setBounds(2*LEFT_SPACE, TOP_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		//forskjellige tekstfelter og labels
		JLabel ROOMid = new JLabel("Room-ID:");
		JLabel ROOMna = new JLabel("Room name:");
		JLabel ROOMty = new JLabel("Room type:");
		JTextField id = new JTextField();
		JTextField name = new JTextField();
		JTextField type = new JTextField();
		ROOMid.setBounds(LEFT_SPACE, TOP_SPACE + LABEL_HEIGHT + DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		ROOMna.setBounds(LEFT_SPACE, TOP_SPACE + 2*LABEL_HEIGHT + 2*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		ROOMty.setBounds(LEFT_SPACE, TOP_SPACE + 3*LABEL_HEIGHT + 3*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		id.setBounds(LEFT_SPACE + LABEL_WIDTH + DEFAULT_SPACE, TOP_SPACE + LABEL_HEIGHT + DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		name.setBounds(LEFT_SPACE + LABEL_WIDTH + DEFAULT_SPACE, TOP_SPACE + 2*LABEL_HEIGHT + 2*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		type.setBounds(LEFT_SPACE + LABEL_WIDTH + DEFAULT_SPACE, TOP_SPACE + 3*LABEL_HEIGHT + 3*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		
		//knappene
		JButton save = new JButton("Save");
		JButton cancel = new JButton("Return");
		save.setBounds(LEFT_SPACE, SMALL_WINDOW_HEIGHT-TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		cancel.setBounds(LEFT_SPACE + DEFAULT_SPACE + BUTTON_WIDTH, SMALL_WINDOW_HEIGHT-TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		
		//legger til elementene på jpanel
		panel.add(header);
		panel.add(ROOMid);
		panel.add(ROOMna);
		panel.add(ROOMty);
		panel.add(id);
		panel.add(name);
		panel.add(type);
		panel.add(save);
		panel.add(cancel);
		frame.repaint();
	}
	
	/**
	 * Metode som kalles når man skal bekrefte at brannslukkingen skal starte:
	 * merk at denne metoden ikke bruker konstantene i Values for å plassere komponentene
	 * (da vinduet er veldig lite er dette neppe nødvendig) 
	 */
	public static void fireFightConfirm() {
		JFrame frame = new JFrame("Confirmation needed");
		JPanel panel  = new JPanel();
		
		//pakker frame etc
		frame.setSize(450, 135);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel info = new JLabel("Are you sure you want to initiate the automatic fire-fighting system?");
		JButton y = new JButton("Yes");
		JButton n = new JButton("No");
		panel.add(info);
		panel.add(y);
		panel.add(n);
	}
	
	/**
	 * Slenger opp en informasjonsboks som informerer om at brannslukkingen har startet.
	 * I likhet med fireFightConfirm bruker den ikke verdier fra Values
	 */
	public static void fireFightConfirmed() {
		JFrame frame = new JFrame();
		JPanel panel  = new JPanel();
		
		//pakker frame etc
		frame.setSize(350, 110);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel info = new JLabel("You have initiated the automatic fire-fighting system!");
		JButton y = new JButton("OK");
		panel.add(info);
		panel.add(y);
	}
	
	/**
	 * Slenger opp en infoboks som bekrefter at resultatet er lagret i loggen. Bruker ikke values
	 */
	public static void logSaved() {
		JFrame frame = new JFrame("Congratulations");
		JPanel panel  = new JPanel();
		
		//pakker frame etc
		frame.setSize(300, 110);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel info = new JLabel("The result has been written to the local log file!");
		JButton y = new JButton("OK");
		panel.add(info);
		panel.add(y);
	}
	
	/**
	 * main for testmetoder
	 * @param args
	 */
	public static void main(String[] args) {
		sensorAttributes(false); //blir lol hvis den kalles med true som parameter wtf
		MACgui window2 = new MACgui();
		LACgui window = new LACgui();
		fireFightConfirm();
		fireFightConfirmed();
		logSaved();
	}

}
