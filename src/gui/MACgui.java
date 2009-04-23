package gui;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import apps.MAC;

/**
 * 
 * @author Olannon
 * 
 * denne klassen håndterer vinduet som presenteres fra en LAC maskin
 *
 */
public class MACgui extends JPanel implements Values, ActionListener {
	
	private JButton writeSiteSummary;
	private JButton viewLog;
	private JButton checkMarked;
	private JButton updateMarked;
	private JButton updateAll;
	private JLabel lacs;
	private JList lacList;
	
	
	public MACgui() {
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
		
		lacList = new JList();
		lacList.setCellRenderer(new MACrenderer());
		lacList.setVisibleRowCount(7); 
		lacList.setVisible(true);
		pane.add(lacList);
		lacList.setBounds(LEFT_SPACE, TOP_SPACE + BUTTON_HEIGHT + 3*DEFAULT_SPACE + LABEL_HEIGHT, LIST_WIDTH, LIST_HEIGHT);
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
	
	public static void main(String[] args) {
		MACgui mac = new MACgui();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == viewLog) {
			viewLog(); //opprett og vis en liste over events, sortert etter dato
		}
		else if (e.getSource() == checkMarked) {
			//sjekk markerte lacs - dvs sjekk alle alarmene til alle sjekka lacs. metodekall
		}
		else if (e.getSource() == updateMarked) {
			//oppdater alle markerte lacs - metodekall med markerte elementer
		}
		else if (e.getSource() == updateAll) {
			//updater alle lacs - metodekall
		}	
	}
}
