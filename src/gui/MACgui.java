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
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import apps.MAC;
import apps.MAC.LACAdaper;

/**
 * 
 * @author Olannon
 * 
 * denne klassen håndterer vinduet som presenteres fra en LAC maskin
 *
 */
public class MACgui extends JPanel implements Values, ActionListener, PropertyChangeListener {
	
	private JButton writeSiteSummary;
	private JButton viewLog;
	private JButton checkMarked;
	private JButton updateMarked;
	private JButton updateAll;
	private JLabel lacs;
	private JList lacList;
	
	private ConnectionStatusPanel databaseStatusPanel;
	
	private MAC mac;
	
	
	public MACgui(MAC mac) {
		this.mac=mac;
		mac.addAdapterListListener(this);
		Insets asdf = new Insets(0,0,0,0);
		JPanel pane = new JPanel();
		JFrame frame = new JFrame("MAC");
		frame.setSize(700, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(pane);
		frame.setVisible(true);
		
		// Knappene
		writeSiteSummary = new JButton("Write Site Summary");
		writeSiteSummary.addActionListener(this);
		writeSiteSummary.setMargin(asdf);
		viewLog = new JButton("View Log");
		viewLog.addActionListener(this);
		viewLog.setMargin(asdf);
		checkMarked = new JButton("Check Marked LACs");
		checkMarked.addActionListener(this);
		checkMarked.setMargin(asdf);
		updateMarked = new JButton("Update Marked LACs");
		updateMarked.addActionListener(this);
		updateMarked.setMargin(asdf);
		updateAll = new JButton("Update all LACs");
		updateAll.addActionListener(this);
		updateAll.setMargin(asdf);
		lacs = new JLabel("LACs");
		Font f = new Font("Dialog", Font.PLAIN, 20);
		lacs.setFont(f);
		lacs.setVisible(true);
	
		pane.setLayout(null);
		pane.add(writeSiteSummary);
		pane.add(viewLog);
		pane.add(checkMarked);
		pane.add(lacs);
		pane.add(updateMarked);
		pane.add(updateAll);
		writeSiteSummary.setBounds(LEFT_SPACE, TOP_SPACE, BUTTON_LONG_WIDTH, BUTTON_HEIGHT);
		viewLog.setBounds(LEFT_SPACE + BUTTON_LONG_WIDTH + DEFAULT_SPACE, TOP_SPACE, BUTTON_WIDTH, BUTTON_HEIGHT);
		lacs.setBounds(LEFT_SPACE, TOP_SPACE + BUTTON_HEIGHT + 2*DEFAULT_SPACE, 100, 20);
		checkMarked.setBounds(LEFT_SPACE, 700 - TOP_SPACE - 2*BUTTON_HEIGHT, BUTTON_LONG_WIDTH, BUTTON_HEIGHT);
		updateMarked.setBounds(LEFT_SPACE + BUTTON_LONG_WIDTH + DEFAULT_SPACE, 700 - TOP_SPACE - 2*BUTTON_HEIGHT, BUTTON_LONG_WIDTH, BUTTON_HEIGHT);
		updateAll.setBounds(LEFT_SPACE + 2*BUTTON_LONG_WIDTH + 2*DEFAULT_SPACE, 700 - TOP_SPACE - 2*BUTTON_HEIGHT, BUTTON_LONG_WIDTH, BUTTON_HEIGHT);
		
		
		databaseStatusPanel = new ConnectionStatusPanel(mac.getDatabaseConnectionWrapper());
		databaseStatusPanel.setBounds(500, 50, 200, 50);
		pane.add(databaseStatusPanel);
			
		
		lacList = new JList();
		JScrollPane scrollpane = new JScrollPane(lacList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane.setBounds(LEFT_SPACE, TOP_SPACE + BUTTON_HEIGHT + 3*DEFAULT_SPACE + LABEL_HEIGHT, LIST_WIDTH, LIST_HEIGHT);
		setupList();
		pane.add(scrollpane);
		
	}
	
	private void setupList() {
		lacList.setModel(mac.getLACAdapterList());
		lacList.setCellRenderer(new MACrenderer());
		
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
		if (e.getSource() == viewLog) {
			viewLog(); //opprett og vis en liste over events, sortert etter dato
		}
		else if (e.getSource() == checkMarked) {
			
			for (Object o : lacList.getSelectedValues()) {
				LACAdaper adaper = (LACAdaper)o;
				if(!adaper.testSensors()){
					checkFailed(adaper.getModel().getID(), adaper.getModel().getAdresse());
				}
			}
		}
		else if (e.getSource() == updateMarked) {
			//oppdater alle markerte lacs - metodekall med markerte elementer
			//TODO: Her må vi ha noe mere
			int[] selected = lacList.getSelectedIndices();
			for (int i = 0; i < selected.length; i++) {
				//TODO: Her må vi ha noe mere
			}
		}
		else if (e.getSource() == updateAll) {
			//updater alle lacs - metodekall
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
