package gui;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import model.*;
import model.Event.EventType;
import apps.LAC;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataListener;

import connection.ModelEditControll;
import help.AlarmHelp;

/**
 * 
 * @author Olannon
 * 
 * denne klassen håndterer vinduet som presenteres fra en LAC maskin
 *
 */
@SuppressWarnings("serial")
public class LACgui extends JPanel implements Values, ActionListener, PropertyChangeListener {
	
	private JFrame frame;
	private JButton installSensor;
	private JButton saveLog;
	private JButton checkSensors;
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
	private JButton viewSensor;
	private JButton editRoom;
	
	/*
	 * Her følger diverse konstruktører som alle kaller initialize på et senere tidspunkt
	 */
	
	public LACgui(ModelEditControll controller){
		this.mec = controller;
		this.mec.addPropertyChangeListener(this);
		this.model = mec.getModel();
		this.initialize(false);
		
		
	}
	
	public Model getModel() {
		return this.model;
	}
	
	public void setModel(Model model) {
		this.model = model;
		this.sensorList.setModel(model);
		this.frame.dispose();
		this.initialize(true);
	}
	
	/**
	 * 
	 * @param model - en boolean som sier noe om guiet har en modell eller ikke
	 */
	private void initialize(boolean model) {
		
		Insets asdf = new Insets(0,0,0,0);
		JPanel pane = new JPanel();
		frame = new JFrame("LAC");
		frame.setSize(700, 700);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(pane);
		frame.setVisible(true);
		
		
		frame.addWindowListener(new WindowAdapter()
		{
		      public void windowClosing(WindowEvent e)
		      {
		         mec.close();
		      }
		});
		
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
		replaceSensor = new JButton("Replace Sensor");
		replaceSensor.addActionListener(this);
		replaceSensor.setMargin(asdf);
		replaceSensor.setVisible(true);
		changeBattery = new JButton("Change Battery");
		changeBattery.addActionListener(this);
		changeBattery.setMargin(asdf);
		changeBattery.setVisible(true);
		editRoom = new JButton("Edit Room");
		editRoom.addActionListener(this);
		editRoom.setMargin(asdf);
		editRoom.setVisible(true);
		viewSensor = new JButton("View Sensor");
		viewSensor.addActionListener(this);
		viewSensor.setMargin(asdf);
		viewSensor.setVisible(true);
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
		pane.add(adresse);
		pane.add(sensorID);
		pane.add(roomname);
		pane.add(roomNUMBER);
		pane.add(sensorStatus);
		pane.add(batteryStatus);
		pane.add(date);
		pane.add(csp);
		pane.add(replaceSensor);
		pane.add(changeBattery);
		pane.add(viewSensor);
		pane.add(editRoom);
		installSensor.setBounds(LEFT_SPACE, TOP_SPACE, BUTTON_WIDTH+3*DEFAULT_SPACE, BUTTON_HEIGHT);
		//saveLog.setBounds(LEFT_SPACE + BUTTON_WIDTH + DEFAULT_SPACE, TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		sensors.setBounds(LEFT_SPACE, TOP_SPACE + BUTTON_HEIGHT + 2*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		checkSensors.setBounds(LEFT_SPACE, 700 - TOP_SPACE - 2*BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
		viewSensor.setBounds(LEFT_SPACE, 700 - TOP_SPACE - 4*BUTTON_HEIGHT - 3*DEFAULT_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		replaceSensor.setBounds(LEFT_SPACE + DEFAULT_SPACE + BUTTON_WIDTH, 700 - TOP_SPACE - 4*BUTTON_HEIGHT - 3*DEFAULT_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		changeBattery.setBounds(LEFT_SPACE + 2*DEFAULT_SPACE + 2*BUTTON_WIDTH, 700 - TOP_SPACE - 4*BUTTON_HEIGHT - 3*DEFAULT_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		sensorID.setBounds(LEFT_SPACE, TOP_SPACE + 2*BUTTON_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		roomname.setBounds(LEFT_SPACE + LIST_LABEL_WIDTH, TOP_SPACE + 2*BUTTON_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		roomNUMBER.setBounds(LEFT_SPACE + 2*LIST_LABEL_WIDTH, TOP_SPACE + 2*BUTTON_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		sensorStatus.setBounds(LEFT_SPACE + 3*LIST_LABEL_WIDTH, TOP_SPACE + 2*BUTTON_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		batteryStatus.setBounds(LEFT_SPACE + 3*LIST_LABEL_WIDTH + LABEL_WIDTH, TOP_SPACE + 2*BUTTON_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		date.setBounds(LEFT_SPACE + 3*LABEL_WIDTH+2*LIST_LABEL_WIDTH, TOP_SPACE + 2*BUTTON_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		csp.setBounds(LEFT_SPACE + 4*BUTTON_WIDTH + 4*DEFAULT_SPACE, TOP_SPACE, 2*LABEL_WIDTH, 2*LABEL_HEIGHT);
		editRoom.setBounds(LEFT_SPACE + LIST_WIDTH - BUTTON_WIDTH, 700 - TOP_SPACE - 4*BUTTON_HEIGHT - 3*DEFAULT_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		
		/*
		 * Initialiserer JListen
		 */
		sensorList = new BlinkingList();
		if (model) { //hvis initialize kalles med en model settes listen til å være med elementene
			sensorList.setModel(this.mec.getModel());
			String id = "LAC ID:  " + this.mec.getModel().getID();
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
	
	/*
	 * Her følger diverse metoder som lager de vinduene som kommer opp dersom menyer som feks "Install Sensor" vises fra LACen
	 */
	 
	/**
	 * Kalles når en sensor skal innstalleres og lukker vinduet samt åpner vinduet for sensorinnstallering
	 */
	public void installSensor(boolean makeRoom) {
		/*
		 * Felles for metoden uavhengig av makeRoom
		 */

		//oppretter frame etc
		final JFrame frame = new JFrame("New Sensor");
		JPanel panel  = new JPanel();
		panel.setLayout(null);
		
		//pakker frame etc
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel header = new JLabel();
		header.setBounds(2*LEFT_SPACE, TOP_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		
		if (!makeRoom) { //skal ikke lages nytt rom
			/*
			 * Følgende er unike for tilfeller der rom ikke skal lages
			 */
			Room[] rooms = new Room[model.getRooms().size()];
			for (int i = 0; i < rooms.length; i++) {
				rooms[i] = model.getRooms().get(i);
			}
			final JComboBox roomsList = new JComboBox(rooms);
			roomsList.setModel(new ComboBoxRenderer());
			header.setText("New Sensor");
			
			roomsList.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (roomsList.getSelectedIndex() == 0) {
						//popp opp nytt gui for å mekke nytt rom
						//legg til rom i lista
					}
				}
			});
				
			JLabel chooseRoom = new JLabel("Choose room:");
			JButton save = new JButton("Save");
			save.addActionListener(new SensorAttributesListener(frame, this.mec, roomsList));
			JButton cancel = new JButton("Return");
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.dispose();
				}
			}
			);
		
			//setter bounds på komponentene
			save.setBounds(LEFT_SPACE, SMALL_WINDOW_HEIGHT - TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
			cancel.setBounds(LEFT_SPACE + DEFAULT_SPACE + BUTTON_WIDTH, SMALL_WINDOW_HEIGHT-TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
			roomsList.setBounds(LEFT_SPACE + DEFAULT_SPACE + LABEL_WIDTH, TOP_SPACE + 5*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT); //sett plassering til romma
			chooseRoom.setBounds(LEFT_SPACE, TOP_SPACE + 5*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
			
			//legger til elementene i panel
			panel.add(header);
			panel.add(save);
			panel.add(cancel);
			panel.add(roomsList);
			panel.add(chooseRoom);
			
		}
		else { //rom skal lages
			header.setText("New Room");
			frame.setTitle("New Room");
			
			//labels for tekstfeltene der attributtene settes
			JLabel romnr = new JLabel("Room number:");
			JLabel rominfo = new JLabel("Room info:");
			JLabel romtype = new JLabel("Room type:");
			
			//tekstfelter der attributtene settes
			JTextField roomNr = new JTextField();
			JTextField roomIn = new JTextField();
			JTextField roomTy = new JTextField();
			
			//knapper
			JButton roomSave = new JButton("Save");
			roomSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//rommet skal lagres	
				}
			});
			JButton roomCancel = new JButton("Cancel");
			roomCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.dispose();
					installSensor(false);
				}
			});
			
			//plassering
			romnr.setBounds(LEFT_SPACE, TOP_SPACE + LABEL_HEIGHT + 2*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
			roomNr.setBounds(LEFT_SPACE + LABEL_WIDTH + DEFAULT_SPACE, TOP_SPACE + LABEL_HEIGHT + 2*DEFAULT_SPACE, TEXTFIELD_LENGTH, LABEL_HEIGHT);
			
			rominfo.setBounds(LEFT_SPACE, TOP_SPACE + 2*LABEL_HEIGHT + 3*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
			roomIn.setBounds(LEFT_SPACE + LABEL_WIDTH + DEFAULT_SPACE, TOP_SPACE + 2*LABEL_HEIGHT + 3*DEFAULT_SPACE, TEXTFIELD_LENGTH, LABEL_HEIGHT);
			
			romtype.setBounds(LEFT_SPACE, TOP_SPACE + 3*LABEL_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
			roomTy.setBounds(LEFT_SPACE + LABEL_WIDTH + DEFAULT_SPACE, TOP_SPACE + 3*LABEL_HEIGHT + 4*DEFAULT_SPACE, TEXTFIELD_LENGTH, LABEL_HEIGHT);
			
			roomSave.setBounds(LEFT_SPACE, SMALL_WINDOW_HEIGHT - TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
			roomCancel.setBounds(LEFT_SPACE + DEFAULT_SPACE + BUTTON_WIDTH, SMALL_WINDOW_HEIGHT-TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
			
			//legger til elementene i panelet
			panel.add(romnr);
			panel.add(rominfo);
			panel.add(romtype);
			
			panel.add(roomNr);
			panel.add(roomIn);
			panel.add(roomTy);
			
			panel.add(roomSave);
			panel.add(roomCancel);
			
			panel.add(header);
		}
		frame.repaint();
	}
	
	/**
	 * Åpner vindu slik at rom kan editeres
	 */
	public void editRoom() {
		//oppretter frame etc
		final JFrame frame = new JFrame("New Sensor");
		JPanel panel  = new JPanel();
		panel.setLayout(null);
		
		//pakker frame etc
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel header = new JLabel();
		header.setBounds(2*LEFT_SPACE, TOP_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		header.setText("New Room");
		frame.setTitle("New Room");
		
		//labels for tekstfeltene der attributtene settes
		JLabel romnr = new JLabel("Room number:");
		JLabel rominfo = new JLabel("Room info:");
		JLabel romtype = new JLabel("Room type:");
		
		//tekstfelter der attributtene settes
		JTextField roomNr = new JTextField();
		JTextField roomIn = new JTextField();
		JTextField roomTy = new JTextField();
		
		//knapper
		JButton roomSave = new JButton("Save");
		roomSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//rommet skal lagres	
			}
		});
		JButton roomCancel = new JButton("Cancel");
		roomCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				installSensor(false);
			}
		});
		
		//plassering
		romnr.setBounds(LEFT_SPACE, TOP_SPACE + LABEL_HEIGHT + 2*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		roomNr.setBounds(LEFT_SPACE + LABEL_WIDTH + DEFAULT_SPACE, TOP_SPACE + LABEL_HEIGHT + 2*DEFAULT_SPACE, TEXTFIELD_LENGTH, LABEL_HEIGHT);
		
		rominfo.setBounds(LEFT_SPACE, TOP_SPACE + 2*LABEL_HEIGHT + 3*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		roomIn.setBounds(LEFT_SPACE + LABEL_WIDTH + DEFAULT_SPACE, TOP_SPACE + 2*LABEL_HEIGHT + 3*DEFAULT_SPACE, TEXTFIELD_LENGTH, LABEL_HEIGHT);
		
		romtype.setBounds(LEFT_SPACE, TOP_SPACE + 3*LABEL_HEIGHT + 4*DEFAULT_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		roomTy.setBounds(LEFT_SPACE + LABEL_WIDTH + DEFAULT_SPACE, TOP_SPACE + 3*LABEL_HEIGHT + 4*DEFAULT_SPACE, TEXTFIELD_LENGTH, LABEL_HEIGHT);
		
		roomSave.setBounds(LEFT_SPACE, SMALL_WINDOW_HEIGHT - TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		roomCancel.setBounds(LEFT_SPACE + DEFAULT_SPACE + BUTTON_WIDTH, SMALL_WINDOW_HEIGHT-TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		
		//legger til elementene i panelet
		panel.add(romnr);
		panel.add(rominfo);
		panel.add(romtype);
		
		panel.add(roomNr);
		panel.add(roomIn);
		panel.add(roomTy);
		
		panel.add(roomSave);
		panel.add(roomCancel);
		
		panel.add(header);
		
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
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel info = new JLabel("Are you sure you want to initiate the automatic fire-fighting system?");
		JButton y = new JButton("Yes");
		y.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				fireFightConfirmed();
			}
		}
		);
		JButton n = new JButton("No");
		n.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
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
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel info = new JLabel("You have initiated the automatic fire-fighting system!");
		JButton y = new JButton("OK");
		y.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		}
		);
		panel.add(info);
		panel.add(y);
	}
	
	/**
	 * Lager et infovindu som sier fra dersom sensorattributtene ikke
	 * er kompatible
	 * @param numbers - hvis true er talla lol, hvis false nullpointer
	 */
	public static void sensorAttributeError(boolean numbers) {
		final JFrame frame = new JFrame();
		JPanel panel  = new JPanel();
		
		//pakker frame etc
		frame.setSize(350, 110);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel info = new JLabel();
		if (numbers) {
			info.setText("Skriv tall i stedet for bokstaver");
		}
		else {
			info.setText("Nullpointer - finner ikke romobjekt som info skal lagres til");
		}
		JButton y = new JButton("OK");
		y.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
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
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel info = new JLabel("The result has been written to the local log file!");
		JButton y = new JButton("OK");
		y.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
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
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
				frame.dispose();
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
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel info = new JLabel();
		info.setText("No sensors checked");
		JButton y = new JButton("OK");
		y.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
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
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel info = new JLabel();
		info.setText("No element selected");
		JButton y = new JButton("OK");
		y.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
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
			installSensor(false);
		}
		else if (evt.getSource() == checkSensors) {
			if (this.mec != null && this.sensorList.getModel().getSize() > 0) {
				sensorsChecked(this.mec.testSensors());
			}
			else {
				sensorsChecked();
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
				try {
					((Sensor)sensorList.getSelectedValue()).replaceBattery(mec);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else { //liste har ikke selected item
				noElementSelected();
			}
		}
		else if (evt.getSource() == viewSensor) {
			if (this.sensorList.getSelectedIndex() != -1) { //sjekk om jlist har selected item
				SensorViewPanel.viewSensorEvents((Sensor)sensorList.getSelectedValue());
			}
			else { //liste har ikke selected item
				noElementSelected();
			}
		}
		else if (evt.getSource() == editRoom) {
			if (this.sensorList.getSelectedIndex() != -1) { //sjekk om jlist har selected item
				editRoom();
			}
			else {
				noElementSelected();
			}
		}
	}
	
	/**
	 * indre klasse som tar for seg funksjonalitet rundt install sensor
	 * @author Olannon
	 *
	 */
	class SensorAttributesListener implements ActionListener {
		private ModelEditControll mec;
		private JFrame frame;
		private Room room;
		
		/**
		 * konstruktør for saveknappen ved install sensor
		 * @param frame
		 * @param mec
		 * @param roomsList
		 */
		public SensorAttributesListener(JFrame frame, ModelEditControll mec, JComboBox roomsList) {
			this.frame = frame;
			this.mec = mec; 
			if (roomsList.getSelectedIndex() != -1 ) { //et rom er valgt
				this.room = (Room)roomsList.getSelectedItem();
			}
			else {
				this.room = null;
			}
		}

		public void actionPerformed(ActionEvent e) {
			try {
				mec.insertSensor(this.room.getID(), false, 100);
				this.frame.dispose();
			} catch (IOException e1) {
				System.err.println("Could not create Sensor due to an IO-error");
			} catch (NullPointerException npe) {
				System.err.println("Could not create Sensor due to no rooms existing/being selected");
				noElementSelected();
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		this.frame.dispose();
		model = mec.getModel();
		initialize(true);
	}
}
