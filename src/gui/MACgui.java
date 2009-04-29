package gui;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;


import log.Log;
import model.Model;

import connection.ModelEditController;

import apps.MAC;
import apps.MAC.LACAdapter;

/**
 * 
 * @author Olannon
 * 
 * denne klassen h�ndterer vinduet som presenteres fra en LAC maskin
 *
 */
@SuppressWarnings("serial")
public class MACgui extends JPanel implements Values, ActionListener, PropertyChangeListener {

	private JButton checkMarked;
	private JButton openLac;
	private JLabel lacs;
	private JList lacList;
	private JButton writeLog;
	//private JButton 'Generate and print an event report for one or more sensors for all LACs or a set of LACs'
	//private JButton Generate error report...
	//private JButton 'Hent ut log info fra en LAC

	private ConnectionStatusPanel databaseStatusPanel;

	private MAC mac;

	public MACgui(MAC mac) {
		this.mac = mac;
		mac.addAdapterListListener(this);
		Insets asdf = new Insets(0,0,0,0);
		JPanel pane = new JPanel();
		JFrame frame = new JFrame("MAC");
		frame.setSize(700, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(pane);
		frame.setVisible(true);

		// Knappene
		checkMarked = new JButton("Check Marked LACs");
		checkMarked.addActionListener(this);
		checkMarked.setMargin(asdf);
		openLac = new JButton("Open LAC view");
		openLac.addActionListener(this);
		openLac.setMargin(asdf);
		writeLog = new JButton("Write log");
		writeLog.addActionListener(this);
		writeLog.setMargin(asdf);

		lacs = new JLabel("LACs");
		Font f = new Font("Dialog", Font.PLAIN, 20);
		lacs.setFont(f);
		lacs.setVisible(true);

		pane.setLayout(null);
		pane.add(checkMarked);
		pane.add(lacs);
		pane.add(openLac);
		pane.add(writeLog);
		lacs.setBounds(LEFT_SPACE, TOP_SPACE + BUTTON_HEIGHT + 2*DEFAULT_SPACE, 100, 20);
		checkMarked.setBounds(LEFT_SPACE, 700 - TOP_SPACE - 2*BUTTON_HEIGHT, BUTTON_LONG_WIDTH, BUTTON_HEIGHT);
		openLac.setBounds(LEFT_SPACE + BUTTON_LONG_WIDTH + DEFAULT_SPACE, 700 - TOP_SPACE - 2*BUTTON_HEIGHT, BUTTON_LONG_WIDTH, BUTTON_HEIGHT);
		writeLog.setBounds(LEFT_SPACE + 3*BUTTON_LONG_WIDTH, 700 - TOP_SPACE - 2*BUTTON_HEIGHT, BUTTON_LONG_WIDTH, BUTTON_HEIGHT);

		databaseStatusPanel = new ConnectionStatusPanel(mac.getDatabaseConnectionWrapper());
		databaseStatusPanel.setBounds(500, 50, 200, 50);
		pane.add(databaseStatusPanel);

		lacList = new BlinkingList();
		JScrollPane scrollpane = new JScrollPane(lacList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane.setBounds(LEFT_SPACE, TOP_SPACE + BUTTON_HEIGHT + 3*DEFAULT_SPACE + LABEL_HEIGHT, LIST_WIDTH, LIST_HEIGHT);
		setupList();
		pane.add(scrollpane);
	}

	private void setupList() {
		lacList.setModel(mac.getLACAdapterList());
		lacList.setCellRenderer(new MACrenderer());
		lacList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lacList.setFixedCellWidth(LIST_ELEMENT_WIDTH);
		lacList.setFixedCellHeight(LIST_ELEMENT_HEIGHT);
	}

	public static void viewLog() {
		final JFrame frame = new JFrame("U FAILOR");
		JPanel panel  = new JPanel();

		//pakker frame etc
		frame.setSize(300, 110);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);

		JLabel info = new JLabel("HERE SHOULD BE LOGG0R BUT EIRIK R NOOB0R");
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


	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == checkMarked) {
			if (lacList.getSelectedIndex() != -1) {
				for (Object o : lacList.getSelectedValues()) {
					LACAdapter adaper = (LACAdapter)o;
					if(!adaper.testSensors()){
						checkFailed(adaper.getModel().getID(), adaper.getModel().getAdresse());
					}
				}
			}
			else {
				LACgui.noElementSelected();
			}

		}
		else if (e.getSource() == openLac) {
			if (lacList.getSelectedIndices().length == 1) {
				ModelEditController temp = (ModelEditController)lacList.getSelectedValue();
				if(temp != null) {
					new LACgui(temp);
				}
			}
			
			else if (lacList.getSelectedValue() == null){
				LACgui.noElementSelected();
			}
			else {
				JOptionPane.showMessageDialog(null, "Please select one LAC");
			}
		}
		else if (e.getSource() == writeLog) {
			Object[] templog = lacList.getSelectedValues();
			ModelEditController[] mecs = new ModelEditController[templog.length];
			for (int i = 0; i < mecs.length; i++) {
				mecs[i] = (ModelEditController)templog[i];
			} 
			if (templog.length != 0) { 
				Model[] tempplog = new Model[templog.length];
				for(int i = 0; i < templog.length; i++) {
					tempplog[i] = mecs[i].getModel();
				}
				if(templog != null) {				
					Log.printReport(tempplog, true);
				}
				else {
					LACgui.noElementSelected();
				}
			}
			else LACgui.noElementSelected();
		}
	}

	private void checkFailed(int id, String adress) {
		final JFrame frame = new JFrame();
		JPanel panel  = new JPanel();

		//pakker frame etc
		frame.setSize(350, 110);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);

		JLabel info = new JLabel("A sensor in LAC: " + adress + " and ID: " + id + " failed the sensorcheck");
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

	public void propertyChange(PropertyChangeEvent evt) {
		setupList();
	}


}
