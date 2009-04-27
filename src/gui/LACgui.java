package gui;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import model.*;
import model.Event.EventType;
import apps.LAC;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import connection.ModelEditControll;
import help.AlarmHelp;

/**
 * 
 * @author Olannon
 * 
 * denne klassen håndterer vinduet som presenteres fra en LAC maskin og
 * inneholder mainmetoden som skal brukes for å teste guienhetene
 *
 */
@SuppressWarnings("serial")
public class LACgui extends JPanel implements Values, ActionListener {
	
	private boolean fromMac;
	private JFrame frame;
	private JButton installSensor;
	private JButton saveLog;
	private JButton checkSensors;
	private JButton returnMAC;
	private JButton sensorView;
	private JLabel sensors;
	private JList sensorList;
	private Model model;
	private JLabel adresse;
	private JLabel id;
	private JLabel sensorID;
	private JLabel roomname;
	private JLabel roomNUMBER;
	private JLabel sensorStatus;
	private JLabel batteryStatus;
	private JLabel date;
	private ConnectionStatusPanel csp;
	private JButton replaceSensor;
	private JButton changeBattery;
	private ModelEditControll mec;
	
	/*
	 * Her følger diverse konstruktører som alle kaller initialize på et senere tidspunkt
	 */
	
	public LACgui(ModelEditControll controller){
		this.mec = controller;
		this.initialize(false, false);
	}
	
	public Model getModel() {
		return this.model;
	}
	
	public void setModel(Model model) {
		this.model = model;
		this.sensorList.setModel(model);
		this.frame.dispose();
		this.initialize(true, false);
	}
	
	/**
	 * 
	 * @param model - en boolean som sier noe om guiet har en modell eller ikke
	 * @param fromMac - en boolean som sier om lac er startet fra mac
	 */
	private void initialize(boolean model, boolean fromMac) {
		Insets asdf = new Insets(0,0,0,0);
		JPanel pane = new JPanel();
		frame = new JFrame("LAC");
		frame.setSize(700, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(pane);
		frame.setVisible(true);
		
		/*
		 * Buttons
		 */
		installSensor = new JButton("Install New Sensor");
		installSensor.addActionListener(this);
		installSensor.setMargin(asdf);
		saveLog = new JButton("Save Log");
		saveLog.addActionListener(this);
		saveLog.setMargin(asdf);
		checkSensors = new JButton("Check Sensors");
		checkSensors.addActionListener(this);
		checkSensors.setMargin(asdf);
		
		//knapper som kun gjelder dersom et listelement er valgt
		sensorView = new JButton("View Sensor");
		sensorView.addActionListener(this);
		sensorView.setMargin(asdf);
		sensorView.setVisible(true);
		replaceSensor = new JButton("Replace Sensor");
		replaceSensor.addActionListener(this);
		replaceSensor.setMargin(asdf);
		replaceSensor.setVisible(true);
		changeBattery = new JButton("Change Battery");
		changeBattery.addActionListener(this);
		changeBattery.setMargin(asdf);
		changeBattery.setVisible(true);
		
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
		
		//overskriftslabels for lista
		sensorID = new JLabel("Sensor ID");
		roomname = new JLabel("Room name");
		roomNUMBER = new JLabel("GET A ROOM!");
		sensorStatus = new JLabel("Sensor Status");
		batteryStatus = new JLabel("Battery Status");
		date = new JLabel("Date & Time");
		
		//connectionlabel
		csp = new ConnectionStatusPanel(this.mec.getConnectionStatusWrapper());
	
		pane.setLayout(null);
		pane.add(installSensor);
		//pane.add(saveLog);
		pane.add(checkSensors);
		pane.add(sensors);
		pane.add(returnMAC);
		pane.add(adresse);
		pane.add(sensorView);
		pane.add(sensorID);
		pane.add(roomname);
		pane.add(roomNUMBER);
		pane.add(sensorStatus);
		pane.add(batteryStatus);
		pane.add(date);
		pane.add(csp);
		pane.add(replaceSensor);
		pane.add(changeBattery);
		installSensor.setBounds(LEFT_SPACE, TOP_SPACE, BUTTON_WIDTH+3*DEFAULT_SPACE, BUTTON_HEIGHT);
		//saveLog.setBounds(LEFT_SPACE + BUTTON_WIDTH + DEFAULT_SPACE, TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		sensors.setBounds(LEFT_SPACE, TOP_SPACE + BUTTON_HEIGHT + 2*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		checkSensors.setBounds(LEFT_SPACE, 700 - TOP_SPACE - 2*BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
		returnMAC.setBounds(LEFT_SPACE + BUTTON_WIDTH + DEFAULT_SPACE, 700 - TOP_SPACE - 2*BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
		sensorView.setBounds(LEFT_SPACE, 700 - TOP_SPACE - 4*BUTTON_HEIGHT - 3*DEFAULT_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		replaceSensor.setBounds(LEFT_SPACE + DEFAULT_SPACE + BUTTON_WIDTH, 700 - TOP_SPACE - 4*BUTTON_HEIGHT - 3*DEFAULT_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		changeBattery.setBounds(LEFT_SPACE + 2*DEFAULT_SPACE + 2*BUTTON_WIDTH, 700 - TOP_SPACE - 4*BUTTON_HEIGHT - 3*DEFAULT_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		sensorID.setBounds(LEFT_SPACE, TOP_SPACE + 2*BUTTON_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		roomname.setBounds(LEFT_SPACE + LIST_LABEL_WIDTH, TOP_SPACE + 2*BUTTON_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		roomNUMBER.setBounds(LEFT_SPACE + 2*LIST_LABEL_WIDTH, TOP_SPACE + 2*BUTTON_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		sensorStatus.setBounds(LEFT_SPACE + 3*LIST_LABEL_WIDTH, TOP_SPACE + 2*BUTTON_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		batteryStatus.setBounds(LEFT_SPACE + 3*LIST_LABEL_WIDTH + LABEL_WIDTH, TOP_SPACE + 2*BUTTON_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		date.setBounds(LEFT_SPACE + 3*LABEL_WIDTH+2*LIST_LABEL_WIDTH, TOP_SPACE + 2*BUTTON_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		csp.setBounds(LEFT_SPACE + 4*BUTTON_WIDTH + 4*DEFAULT_SPACE, TOP_SPACE, 2*LABEL_WIDTH, 2*LABEL_HEIGHT);
		
		
		/*
		 * Initialiserer JListen
		 */
		sensorList = new BlinkingList();
		if (model) { //hvis initialize kalles med en model settes listen til å være med elementene
			sensorList.setModel(this.model);
			String id = "LAC ID:  " + this.model.getID();
			this.id = new JLabel(id);
			this.id.setFont(f);
		}
		if (!model) {
			this.id = new JLabel("ingen id valgt");
		}
		this.id.setVisible(true);
		pane.add(this.id);
		this.id.setBounds(LEFT_SPACE + BUTTON_WIDTH + 4*DEFAULT_SPACE, TOP_SPACE, BUTTON_WIDTH+DEFAULT_SPACE, BUTTON_HEIGHT);
		sensorList.setCellRenderer(new LACrenderer());
		sensorList.setVisibleRowCount(7); 
		sensorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sensorList.setVisible(true);
		pane.add(sensorList);
		sensorList.setBounds(LEFT_SPACE, TOP_SPACE + BUTTON_HEIGHT + 5*DEFAULT_SPACE + 3*LABEL_HEIGHT, LIST_WIDTH, LIST_HEIGHT);
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
	 * Her følger diverse metoder som lager de vinduene som kommer opp dersom menyer som feks "Install Sensor" vises fra LACen
	 */
	 
	/**
	 * Kalles når en sensor skal innstalleres og lukker vinduet samt åpner vinduet for sensorinnstallering
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
		if (!install && model != null) { //den skal editeres, feltene skal ha info
			name.setText("lol"); //må hente ut sensorinfo her. how?
			type.setText("owned");
			number.setText("omg");
		}
		ROOMna.setBounds(LEFT_SPACE, TOP_SPACE + 2*LABEL_HEIGHT + 2*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		ROOMty.setBounds(LEFT_SPACE, TOP_SPACE + 3*LABEL_HEIGHT + 3*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		ROOMnu.setBounds(LEFT_SPACE, TOP_SPACE + 4*LABEL_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		name.setBounds(LEFT_SPACE + LABEL_WIDTH + DEFAULT_SPACE, TOP_SPACE + 2*LABEL_HEIGHT + 2*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		type.setBounds(LEFT_SPACE + LABEL_WIDTH + DEFAULT_SPACE, TOP_SPACE + 3*LABEL_HEIGHT + 3*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		number.setBounds(LEFT_SPACE + LABEL_WIDTH + DEFAULT_SPACE, TOP_SPACE + 4*LABEL_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		
		//knappene
		JButton save = new JButton("Save");
		save.addActionListener(new SensorAttributesListener(frame, this.mec, number, type, name));
		JButton cancel = new JButton("Return");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		}
		);
		save.setBounds(LEFT_SPACE, SMALL_WINDOW_HEIGHT-TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		cancel.setBounds(LEFT_SPACE + DEFAULT_SPACE + BUTTON_WIDTH, SMALL_WINDOW_HEIGHT-TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		
		//legger til elementene på jpanel
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
	 * Metode som kalles når man skal bekrefte at brannslukkingen skal starte:
	 * merk at denne metoden ikke bruker konstantene i Values for å plassere komponentene
	 * (da vinduet er veldig lite er dette neppe nødvendig) 
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
			info.setText("Skriv tall i stedet for bokstaver");
		}
		else {
			info.setText("Nullpointer - finner ikke sensorobjekt som info skal lagres til");
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
	 * Metode som slenger opp infoboks dersom man vil editere listeelementer som ikke er valgt
	 */
	public static void noElementSelected() {
		final JFrame frame = new JFrame();
		JPanel panel  = new JPanel();
		
		//pakker frame etc
		frame.setSize(170, 110);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel info = new JLabel();
		info.setText("No element selected");
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

	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == saveLog) {
			logSaved(); //TODO skulle vi lagt inn mulighet for feilmelding dersom loggen ikke lagres?
		}
		else if (evt.getSource() == installSensor) {
			sensorAttributes(true, this.model);
		}
		else if (evt.getSource() == checkSensors) {
			if (this.mec != null && this.sensorList.getModel().getSize() > 0) {
				sensorsChecked(this.mec.testSensors());
			}
			else {
				sensorsChecked();
			}
		}
		else if (evt.getSource() == sensorView) {
			if (this.sensorList.getSelectedIndex() != -1) { //sjekk om jlist har selected item
				//sensorvindu med events dukker opp
				
			}
			else { //liste har ikke selected item
				noElementSelected();
			}
			
		}
		else if (evt.getSource() == replaceSensor) {
			if (this.sensorList.getSelectedIndex() != -1) { //sjekk om jlist har selected item
				//sensorvindu dukker opp for utbytting
				Sensor sensor = (Sensor)sensorList.getSelectedValue();
				sensor.setBattery(100);
				sensor.setInstallationDate(LAC.getTime());
				sensor.setAlarmState(false);
				mec.deleteAllEvents(sensor);
				sensor.addEvent(new Event(0, EventType.STARTUP, LAC.getTime(), sensor));
			}
			else { //liste har ikke selected item
				noElementSelected();
			}
		}
		else if (evt.getSource() == changeBattery) {
			if (this.sensorList.getSelectedIndex() != -1) { //sjekk om jlist har selected item
				((Sensor)sensorList.getSelectedValue()).setBattery(100);
			}
			else { //liste har ikke selected item
				noElementSelected();
			}
		}
		else if (evt.getSource() == returnMAC) {
			//returner til macen
		}
		
	}
	
	class SensorAttributesListener implements ActionListener{
		private ModelEditControll mec;
		private JTextField romid;
		private JTextField romnummer;
		private JTextField romtype;
		private JTextField rominfo;
		private JFrame frame;
		
		public SensorAttributesListener(JFrame frame, ModelEditControll mec, JTextField romnummer, JTextField romtype, JTextField rominfo) {
			this.frame = frame;
			this.mec = mec; 
			this.romnummer = romnummer;
			this.romtype = romtype;
			this.rominfo = rominfo;
		}
		
		public void actionPerformed(ActionEvent e) {
			int romNUMMER = -1;
			String romTYPE = "";
			String romINFO = "";
			Room room = null;
		
			boolean didCreateRoom = false;
			try {
				romNUMMER = Integer.parseInt(romnummer.getText());			
				romTYPE = romtype.getText();
				romINFO = rominfo.getText();
				room = new Room(this.mec, romNUMMER, romTYPE, romINFO);
				didCreateRoom=true;
			}
			catch (NumberFormatException nfe) {
				sensorAttributeError(true);
			}catch (NullPointerException npe) {
				sensorAttributeError(false); //TODO fiks sensorobjekt
			}catch (IOException ioe) {
				System.err.println("Could not create Room due to an IO-error");
			}
			if(didCreateRoom){
				Sensor sensor;
				try {
					sensor = new Sensor(this.mec, room);
					room.addSensor(sensor);
					this.frame.setVisible(false);
					if (sensor.getRoom() != null) {
						this.mec.getModel().addRoom(room);
					}
				} catch (IOException e1) {
					System.err.println("Could not create Sensor due to an IO-error");
				}
			}
		}
	}
}
