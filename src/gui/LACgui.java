package gui;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import model.*;
import apps.LAC;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import help.AlarmHelp;

/**
 * 
 * @author Olannon
 * 
 * denne klassen h�ndterer vinduet som presenteres fra en LAC maskin og
 * inneholder mainmetoden som skal brukes for � teste guienhetene
 *
 */
public class LACgui extends JPanel implements Values, ActionListener {
	
	private boolean fromMac;
	private JButton installSensor;
	private JButton saveLog;
	private JButton checkSensors;
	private JButton returnMAC;
	private JLabel sensors;
	private JList sensorList;
	private Model model;
	private JLabel adresse;
	private LAC lac;
	private MACgui macgui;
	
	/*
	 * Her f�lger diverse konstrukt�rer som alle kaller initialize p� et senere tidspunkt
	 */
	
	public LACgui(Model model) {
		this.model = model;
		this.initialize(true, false);
	}
	
	public LACgui(LAC lac) {
		this.lac = lac;
		this.initialize(true, false);
	}
	
	public LACgui(MACgui macgui) {
		this.macgui = macgui;
		this.initialize(false, true);
	}
	
	public LACgui() {
		this.initialize(false, false);
	}
	
	public Model getModel() {
		return this.model;
	}
	
	public void setModel(Model model) {
		this.model = model;
		this.sensorList.setModel(model);
		this.repaint();
	}
	
	public void setMACgui(MACgui macgui) {
		this.macgui = macgui;
	}
	
	public MACgui getMACgui() {
		return this.macgui;
	}
	
	/**
	 * 
	 * @param model - en boolean som sier noe om guiet har en modell eller ikke
	 * @param fromMac - en boolean som sier om lac er startet fra mac
	 */
	private void initialize(boolean model, boolean fromMac) {
		Insets asdf = new Insets(0,0,0,0);
		JPanel pane = new JPanel();
		JFrame frame = new JFrame("LAC");
		frame.setSize(700, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(pane);
		frame.setVisible(true);
		
		/*
		 * Buttons
		 */
		installSensor = new JButton("Install Sensor");
		installSensor.addActionListener(this);
		installSensor.setMargin(asdf);
		saveLog = new JButton("Save Log");
		saveLog.addActionListener(this);
		saveLog.setMargin(asdf);
		checkSensors = new JButton("Check Sensors");
		checkSensors.addActionListener(this);
		checkSensors.setMargin(asdf);
		//Merk at returnMAC m� h�ndteres dynamisk for � unng� at den vises n�r LACvinduet
		//ikke er aksessert fra MAC
		returnMAC = new JButton("Return to MAC");
		returnMAC.addActionListener(this);
		returnMAC.setMargin(asdf);
		if (!fromMac) {
			returnMAC.setVisible(false);
		}
		sensors = new JLabel("Sensors");
		if (model && this.model.getAdresse() != null) {
			adresse = new JLabel(this.model.getAdresse());
		}
		else {
			adresse = new JLabel("Ikke valgt");
		}
		Font f = new Font("Dialog", Font.PLAIN, 20);
		sensors.setFont(f);
		adresse.setFont(f);
		adresse.setVisible(true);
		sensors.setVisible(true);
	
		pane.setLayout(null);
		pane.add(installSensor);
		pane.add(saveLog);
		pane.add(checkSensors);
		pane.add(sensors);
		pane.add(returnMAC);
		pane.add(adresse);
		installSensor.setBounds(LEFT_SPACE, TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		saveLog.setBounds(LEFT_SPACE + BUTTON_WIDTH + DEFAULT_SPACE, TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		sensors.setBounds(LEFT_SPACE, TOP_SPACE + BUTTON_HEIGHT + 2*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		checkSensors.setBounds(LEFT_SPACE, 700 - TOP_SPACE - 2*BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
		returnMAC.setBounds(LEFT_SPACE + BUTTON_WIDTH + DEFAULT_SPACE, 700 - TOP_SPACE - 2*BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
		//adresse.setBounds()
		
		/*
		 * Initialiserer JListen
		 */
		sensorList = new BlinkingList();
		if (model) { //hvis initialize kalles med en model settes listen til � v�re med elementene
			sensorList.setModel(this.model);
		}
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
		if (fromMac) {
			this.returnMAC.setVisible(true);
		}
		else {
			this.returnMAC.setVisible(false);
		}
	}
	
	/*
	 * Her f�lger diverse metoder som lager de vinduene som kommer opp dersom menyer som feks "Install Sensor" vises fra LACen
	 */
	 
	/**
	 * Kalles n�r en sensor skal innstalleres og lukker vinduet samt �pner vinduet for sensorinnstallering
	 */
	public void sensorAttributes(boolean install, Model model) {
		final JFrame frame = new JFrame("Sensor attributes");
		JPanel panel  = new JPanel();
		panel.setLayout(null);
		
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
		JLabel ROOMna = new JLabel("Room info:");
		JLabel ROOMty = new JLabel("Room type:");
		JLabel ROOMnu = new JLabel("Room number:");
		final JTextField name = new JTextField();
		final JTextField type = new JTextField();
		final JTextField number = new JTextField();
		if (!install && model != null) {
			name.setText("noob");
			type.setText("owned");
		}
		ROOMna.setBounds(LEFT_SPACE, TOP_SPACE + 2*LABEL_HEIGHT + 2*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		ROOMty.setBounds(LEFT_SPACE, TOP_SPACE + 3*LABEL_HEIGHT + 3*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		ROOMnu.setBounds(LEFT_SPACE, TOP_SPACE + 4*LABEL_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		name.setBounds(LEFT_SPACE + LABEL_WIDTH + DEFAULT_SPACE, TOP_SPACE + 2*LABEL_HEIGHT + 2*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		type.setBounds(LEFT_SPACE + LABEL_WIDTH + DEFAULT_SPACE, TOP_SPACE + 3*LABEL_HEIGHT + 3*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		number.setBounds(LEFT_SPACE + LABEL_WIDTH + DEFAULT_SPACE, TOP_SPACE + 4*LABEL_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		
		//knappene
		JButton save = new JButton("Save");
		save.addActionListener(new SensorAttributesListener(this.lac, number, type, name));
		JButton cancel = new JButton("Return");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		}
		);
		save.setBounds(LEFT_SPACE, SMALL_WINDOW_HEIGHT-TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		cancel.setBounds(LEFT_SPACE + DEFAULT_SPACE + BUTTON_WIDTH, SMALL_WINDOW_HEIGHT-TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		
		//legger til elementene p� jpanel
		panel.add(header);
		panel.add(ROOMna);
		panel.add(ROOMty);
		panel.add(ROOMnu);
		panel.add(name);
		panel.add(type);
		panel.add(number);
		panel.add(save);
		panel.add(cancel);
		frame.repaint();
	}
	
	/**
	 * Metode som kalles n�r man skal bekrefte at brannslukkingen skal starte:
	 * merk at denne metoden ikke bruker konstantene i Values for � plassere komponentene
	 * (da vinduet er veldig lite er dette neppe n�dvendig) 
	 */
	public static void fireFightConfirm() {
		final JFrame frame = new JFrame("Confirmation needed");
		JPanel panel  = new JPanel();
		
		//pakker frame etc
		frame.setSize(450, 135);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel info = new JLabel("Are you sure you want to initiate the automatic fire-fighting system?");
		JButton y = new JButton("Yes");
		y.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				fireFightConfirmed();
			}
		}
		);
		JButton n = new JButton("No");
		n.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		}
		);
		panel.add(info);
		panel.add(y);
		panel.add(n);
	}
	
	/**
	 * Slenger opp en informasjonsboks som informerer om at brannslukkingen har startet.
	 * I likhet med fireFightConfirm bruker den ikke verdier fra Values
	 */
	public static void fireFightConfirmed() {
		final JFrame frame = new JFrame();
		JPanel panel  = new JPanel();
		
		//pakker frame etc
		frame.setSize(350, 110);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel info = new JLabel("You have initiated the automatic fire-fighting system!");
		JButton y = new JButton("OK");
		y.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		}
		);
		panel.add(info);
		panel.add(y);
	}
	
	/**
	 * Mekker gui
	 * @param numbers - hvis true er talla lol, hvis false nullpointer
	 */
	public static void sensorAttributeError(boolean numbers) {
		final JFrame frame = new JFrame();
		JPanel panel  = new JPanel();
		
		//pakker frame etc
		frame.setSize(350, 110);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel info = new JLabel();
		if (numbers) {
			info.setText("Please insert integers l0lzcak0r");
		}
		else {
			info.setText("herreguuud sett stats f�r save mb?!??!?!??!");
		}
		JButton y = new JButton("OK");
		y.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		}
		);
		panel.add(info);
		panel.add(y);
	}
	
	/**
	 * Slenger opp en infoboks som bekrefter at resultatet er lagret i loggen. Bruker ikke values
	 */
	public static void logSaved() {
		final JFrame frame = new JFrame("Congratulations");
		JPanel panel  = new JPanel();
		
		//pakker frame etc
		frame.setSize(300, 110);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel info = new JLabel("The result has been written to the local log file!");
		JButton y = new JButton("OK");
		y.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		}
		);
		panel.add(info);
		panel.add(y);
	}
	
	/**
	 * Slenger opp en infoboks som bekrefter at sensorene er i orden
	 * 
	 */
	public static void sensorsChecked(boolean ok) {
		final JFrame frame = new JFrame();
		JPanel panel  = new JPanel();
		
		//pakker frame etc
		frame.setSize(100, 110);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel info = new JLabel();
		if (ok) {
			info.setText("Sensors are ok!");
		}
		else {
			info.setText("Sensors are fail0r");
		}
		JButton y = new JButton("OK");
		y.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		}
		);
		panel.add(info);
		panel.add(y);
	}
	
	public static void sensorsChecked() {
		final JFrame frame = new JFrame();
		JPanel panel  = new JPanel();
		
		//pakker frame etc
		frame.setSize(170, 110);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel info = new JLabel();
		info.setText("No sensors checked");
		JButton y = new JButton("OK");
		y.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		}
		);
		panel.add(info);
		panel.add(y);
	}
	
	/**
	 * main for GUI testmetoder
	 * @param args
	 */
	public static void main(String[] args) {
		//sensorAttributes(false);
		//sensorsChecked();
		//MACgui window2 = new MACgui();
		LACgui window = new LACgui(AlarmHelp.getDefaultModel()); //skulle gjerne hatt en LAC her
		//fireFightConfirm();
		//fireFightConfirmed();
		//logSaved();
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == saveLog) {
			logSaved(); //skulle vi lagt inn mulighet for feilmelding dersom loggen ikke lagres?
		}
		else if (evt.getSource() == installSensor) {
			sensorAttributes(true, this.model);
		}
		else if (evt.getSource() == checkSensors) {
			if (this.lac != null) {
				sensorsChecked(this.lac.testSensors());
			}
			else {
				sensorsChecked();
			}
		}
		else if (evt.getSource() == returnMAC) {
			//returner til macen
		}
		
	}
	
	class SensorAttributesListener implements ActionListener{
		private LAC lac;
		private JTextField romid;
		private JTextField romnummer;
		private JTextField romtype;
		private JTextField rominfo;
		
		public SensorAttributesListener(LAC lac, JTextField romnummer, JTextField romtype, JTextField rominfo) {
			this.lac=lac;
			this.romid = romid;
			this.romnummer = romnummer;
			this.romtype = romtype;
			this.rominfo = rominfo;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int romNUMMER = -1;
			String romTYPE = "";
			String romINFO = "";
			Room room = null;
			try {
				romNUMMER = Integer.parseInt(romnummer.getText());			
			}
			catch (NumberFormatException nfe) {
				sensorAttributeError(true);
			}
			catch (NullPointerException npe) {
				sensorAttributeError(false);
			}
			try {
				romTYPE = romtype.getText();
				romINFO = rominfo.getText();
			}
			catch (NullPointerException npe) {
				sensorAttributeError(true);
			}
			try {
				// opprett room = new Room(romNUMMER, romTYPE, romINFO, lac.getModel());
			}
			catch (NullPointerException npe) {
				//trenger ikke gj�re noe;
			}
			Sensor sensor = new Sensor(room);
			if (sensor.getRoom() != null) {
				this.lac.getModel(); //jan ordner imo
			}
		}
	}
}
