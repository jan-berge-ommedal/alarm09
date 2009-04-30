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

import log.Log;
import model.*;
import model.Event.EventType;
import apps.LAC;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import connection.ModelEditController;

/**
 * Denne klassen håndterer vinduet som presenteres fra en LAC maskin
 * @author Olannon
 */
@SuppressWarnings("serial")
public class LACgui extends JPanel implements Values, ActionListener, PropertyChangeListener {
	
	//frame
	private JFrame frame;
	
	//knapper
	private JButton installSensor;
	private JButton saveLog;
	private JButton checkSensors;
	private JButton viewSensor;
	private JButton editRoom;
	private JButton replaceSensor;
	private JButton changeBattery;
	
	//labels
	private JLabel sensors;
	private JLabel adresse;
	private JLabel id;
	private JLabel sensorID;
	private JLabel roomname;
	private JLabel roomNUMBER;
	private JLabel sensorStatus;
	private JLabel batteryStatus;
	private JLabel date;
	
	//sensorlisten
	private JList sensorList;
	
	//diverse underliggende logikk
	private ConnectionStatusPanel csp;
	private Model model;
	private ModelEditController mec;
	
	/*
	 * Her fï¿½lger diverse konstruktï¿½rer som alle kaller initialize pï¿½ et senere tidspunkt
	 */
	
	public LACgui(ModelEditController controller){
		this.mec = controller;
		this.mec.addPropertyChangeListener(this);
		this.model = mec.getModel();
		this.initialize();
	}
	
	/**
	 * Standard getter
	 * @return Model - den underliggende modellen til LACguiet
	 */
	public Model getModel() {
		return this.model;
	}
	
	
	/**
	 * Standard setter
	 * @param model - den nye modellen til LACguiet
	 */
	/*
	public void setModel(Model model) {
		this.model = model;
		this.sensorList.setModel(new ModelListAdapter(model));
		this.frame.dispose();
		this.initialize();
	}
	*/
	
	/**
	 * Denne metoden oppfï¿½rer seg som en hjelpemetode til konstruktï¿½ren som genererer GUIet. Dersom
	 * den kalles med true vil den sette guiet til ï¿½ fï¿½lge en modell
	 * @param model - en boolean som sier noe om guiet har en modell eller ikke
	 */
	private void initialize() {
		boolean model = false;
		if(this.model!=null)model=true;
		
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
		
		adresse = new JLabel("");
		try {
			adresse.setText(this.mec.getModel().getAdresse());
		} catch (NullPointerException npe) {
			System.err.println("nullpointerex ved getAdresse");
			//npe.printStackTrace();
		}
		
		Font f = new Font("Dialog", Font.PLAIN, 20);
		sensors.setFont(f);
		adresse.setFont(f);
		adresse.setVisible(true);
		sensors.setVisible(true);
		
		//overskriftslabels for lista
		sensorID = new JLabel("Sensor ID");
		roomname = new JLabel("Room name");
		roomNUMBER = new JLabel("Room number");
		sensorStatus = new JLabel("Sensor Status");
		batteryStatus = new JLabel("Battery Status");
		date = new JLabel("Date & Time");
		
		//connectionlabel
		csp = new ConnectionStatusPanel(this.mec.getConnectionStatusWrapper());
	
		pane.setLayout(null);
		pane.add(installSensor);
		pane.add(saveLog);
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
		
		adresse.setBounds(LEFT_SPACE + 4*BUTTON_WIDTH + 4*DEFAULT_SPACE, TOP_SPACE, 2*LABEL_WIDTH, 2*LABEL_HEIGHT);
		installSensor.setBounds(LEFT_SPACE, TOP_SPACE, BUTTON_WIDTH+3*DEFAULT_SPACE, BUTTON_HEIGHT);
		saveLog.setBounds(LEFT_SPACE + BUTTON_WIDTH + DEFAULT_SPACE, 700 - TOP_SPACE - 2*BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
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
		csp.setBounds(LEFT_SPACE + 4*BUTTON_WIDTH + 4*DEFAULT_SPACE, TOP_SPACE + BUTTON_HEIGHT + 2*DEFAULT_SPACE, 2*LABEL_WIDTH, 2*LABEL_HEIGHT);
		editRoom.setBounds(LEFT_SPACE + LIST_WIDTH - BUTTON_WIDTH, 700 - TOP_SPACE - 4*BUTTON_HEIGHT - 3*DEFAULT_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		
		/*
		 * Initialiserer JListen
		 */
		sensorList = new BlinkingList();
		this.id = new JLabel("ingen ID valgt");
		if (model) { //hvis initialize kalles med en model settes listen til ï¿½ vï¿½re med elementene
			sensorList.setModel(new ModelListAdapter(this.model));
			try {
				String id = "LAC ID:  " + this.mec.getModel().getID();
				this.id = new JLabel(id);
				this.id.setFont(f);	
			} catch (NullPointerException npe) {
				System.err.println("nullpointerex nï¿½r LAC ID skulle settes: trace fï¿½lger under");
				npe.printStackTrace();
				System.out.println("-------------TRACE DONE-----------");
			}
		}
		if (!model) {
			this.id.setText("kall uten model, ingen ID");
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
	 * Her fï¿½lger diverse metoder som lager de vinduene som kommer opp dersom menyer som feks "Install Sensor" vises fra LACen
	 */
	 
	/**
	 * Metode som kalles for ï¿½ installere sensor
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
			 * Fï¿½lgende er unike for tilfeller der rom ikke skal lages
			 */
			
			Room[] rooms = new Room[model.getRooms().size()];
			int teller = 0;
			for (Room r : model.getRooms()) {
				rooms[teller] = r;
				teller++;
			}
			
			for (int i = 0; i < rooms.length; i++) {
				rooms[i] = model.getRooms().get(i);
			}
			
			final JComboBox roomsList = new JComboBox(rooms);
			ComboBoxAdapter cba = new ComboBoxAdapter(rooms);
			roomsList.setModel(cba);
			header.setText("New Sensor");
			
			
			roomsList.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (roomsList.getSelectedIndex() == 0) { //skal lages et nytt rom
						frame.dispose();
						installSensor(true);
					}
				}
			});
				
			JLabel chooseRoom = new JLabel("Choose room:");
			JButton save = new JButton("Save");
			save.addActionListener(new SensorAttributesListener(frame, this.mec, cba));
			JButton cancel = new JButton("Return");
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.dispose();
				}
			}
			);
		
			//setter bounds pï¿½ komponentene
			save.setBounds(LEFT_SPACE, SMALL_WINDOW_HEIGHT - TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
			cancel.setBounds(LEFT_SPACE + DEFAULT_SPACE + BUTTON_WIDTH, SMALL_WINDOW_HEIGHT-TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
			roomsList.setBounds(LEFT_SPACE + DEFAULT_SPACE + LABEL_WIDTH, TOP_SPACE + 5*DEFAULT_SPACE, 2*LABEL_WIDTH, LABEL_HEIGHT); //sett plassering til romma
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
			final JTextField roomNr = new JTextField();
			final JTextField roomIn = new JTextField();
			final JTextField roomTy = new JTextField();
			
			//knapper
			JButton roomSave = new JButton("Save");
			roomSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						int ronr = Integer.parseInt(roomNr.getText());
						String roin = roomIn.getText();
						String roty = roomTy.getText();
						
						Room room = new Room(-1,ronr,roty,roin,model);
						Sensor sensor = new Sensor(-1,false,100,LAC.getTime(),room);
						sensor.startSensor();
						frame.dispose();
						
					}
					catch(NullPointerException npe) {
						System.err.println("nullpointerex - skriv inn info i boksene");
					} catch (NumberFormatException nfe) {
						System.err.println("numberformatex - skriv inn tall i tallboksen");
					} 
					
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
	 * ï¿½pner vindu slik at et rom kan editeres
	 * @param sensor - en sensor som sier noe om hvilket rom det er snakk om
	 */
	public void editRoom(final Sensor sensor) {
		//oppretter frame etc
		final JFrame frame = new JFrame();
		JPanel panel  = new JPanel();
		panel.setLayout(null);
		
		//pakker frame etc
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		JLabel header = new JLabel();
		header.setBounds(2*LEFT_SPACE, TOP_SPACE, LABEL_WIDTH, LABEL_HEIGHT);
		header.setText("Room attributes");
		frame.setTitle("Edit Room");
		
		//labels for tekstfeltene der attributtene settes
		JLabel romnr = new JLabel("Room number:");
		JLabel rominfo = new JLabel("Room info:");
		JLabel romtype = new JLabel("Room type:");
		
		//tekstfelter der attributtene settes
		final JTextField roomNr = new JTextField();
		roomNr.setText("" + sensor.getRoom().getRomNR());
		final JTextField roomIn = new JTextField();
		roomIn.setText(sensor.getRoom().getRomInfo());
		final JTextField roomTy = new JTextField();
		roomTy.setText(sensor.getRoom().getRomType());
		
		//knapper
		JButton roomSave = new JButton("Save");
		roomSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { //henter ut info fra tekstfeltene og lagrer nytt rom
					int ronr = Integer.parseInt(roomNr.getText());
					String roin = roomIn.getText();
					String roty = roomTy.getText();
					
					sensor.getRoom().setRomInfo(roin);
					sensor.getRoom().setRomNR(ronr);
					sensor.getRoom().setRomType(roty);
				}
				catch (NullPointerException npe) {
					//evt gi beskjed 
					System.err.println("nullpointerex");
				}
				catch (NumberFormatException nfe) {
					//evt gi beskjed
					System.err.println("numberformatex");
				}
			}
		});
		JButton roomCancel = new JButton("Cancel");
		roomCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
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
	 * Metode som kalles nï¿½r man skal bekrefte at brannslukkingen skal starte:
	 * merk at denne metoden ikke bruker konstantene i Values for ï¿½ plassere komponentene
	 * (da vinduet er veldig lite er dette neppe nï¿½dvendig) 
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
	 * @param numbers - hvis true er det ikke skrevet tall, hvis false er tekstfeltet tomt
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
	 * Slenger opp en infoboks som bekrefter at resultatet er lagret i loggen
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
	 * Slenger opp en infoboks som bekrefter/avkrefter at sensorene er i orden
	 * @param boolean - true vil si at alt er i orden mens false vil si at det ikke er det
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
	
	/**
	 * Metode som sier fra at sensorene ikke er sjekka fordi ingen har blitt valgt/er tilgjengelige
	 */
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
			if(model != null) {
			Log.printReport(model, false);
			}
			else noElementSelected();
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
				//new Event(-1, EventType.REPLACEMENT, LAC.getTime(), sensor);
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
				editRoom((Sensor)this.sensorList.getSelectedValue());
			}
			else {
				noElementSelected();
			}
		}
	}
	
	/**
	 * indre klasse som tar for seg funksjonalitet rundt install sensor, nï¿½r save trykkes
	 * skal denne klassen legge til sensoren slik at alt oppdateres
	 * @author Olannon
	 *
	 */
	class SensorAttributesListener implements ActionListener {
		private ModelEditController mec;
		private JFrame frame;
		private ComboBoxAdapter cba;
		
		/**
		 * konstruktï¿½r for saveknappen ved install sensor
		 * @param frame
		 * @param mec
		 * @param roomsList
		 */
		public SensorAttributesListener(JFrame frame, ModelEditController mec, ComboBoxAdapter cba) {
			this.frame = frame;
			this.mec = mec; 
			this.cba = cba;
		}

		public void actionPerformed(ActionEvent e) {
			try {
				Room r = cba.getSelectedRoom();
				Sensor sensor = new Sensor(-1,false,100,LAC.getTime(),r);
				sensor.startSensor();
				System.out.println("Sensor ble lagt til logisk!"); //testlinje
				this.frame.dispose();
				System.out.println("Hovedvindu skal vises!"); //testlinje
			} catch (NullPointerException npe) {
				System.err.println("Could not create Sensor due to no rooms existing/being selected");
				noElementSelected();
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("Gui received change");
		if(evt.getSource() instanceof ModelEditController){
			model = mec.getModel();			
			// Burde muligens gjï¿½res annerledes
			this.frame.dispose();
			initialize();
		}
	}
}

