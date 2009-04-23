package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import model.Model;
import model.Sensor;

import connection.ModelEditControll;

import apps.LAC;

/**
 * Klasse som skal lage listkomponenten til LACs som vises i MACvinduet. Hvert element
 * i listen vil være en MACrendererkomponent som essensielt er et JPanel med labels på
 * @author Olannon
 *
 */
public class MACrenderer extends DefaultListCellRenderer implements ListCellRenderer, Values {
	private static final int CELLHEIGHT = 25;
	private static final int lacIDSIZE = 50;
	private static final int lacNAMESIZE = 100;
	private static final int alarmSIZE = 50;
	
	private static final Color colorOn = Color.RED;
	private static final Color colorOff = new Color(255,150,150);
	private static final Color colorSelected = new Color(0,170,255);
	@Override
	public Component getListCellRendererComponent(JList list, Object object,
			int index, boolean selected, boolean hasCellFocus) {
		
		/*
		 * Initialiserer komponenten som skal returneres som et panel med riktige mål
		 */
		JPanel panel = new JPanel();
		panel.setBounds(LEFT_SPACE, TOP_SPACE + LABEL_HEIGHT + BUTTON_HEIGHT + 3*DEFAULT_SPACE, LIST_ELEMENT_WIDTH, LIST_ELEMENT_HEIGHT);
		panel.setLayout(null);
		if(selected)panel.setBackground(colorSelected);
		
		ModelEditControll element = (ModelEditControll)object;
		Model m = element.getModel();
		
		Sensor s = (Sensor)object;
		
		
		/*
		 * Initialiserer og legger til de JLabelene som skal være i JPanelet
		 */
		JLabel ID = new JLabel("ID");
		JLabel LOC = new JLabel("Location");
		JLabel ALARMst = new JLabel("Alarm Status");
		
		
		
		JLabel lacIDLabel = new JLabel("ID "+(index+1));
		lacIDLabel.setBounds(0, 0, lacIDSIZE, CELLHEIGHT);
		
		JLabel LOCLabel = new JLabel(m.getAdresse());
		LOCLabel.setBounds(lacNAMESIZE, 0, 100, CELLHEIGHT);
		
		JLabel alarmstLabel = new JLabel();
		
		if(s.isAlarmState())
			alarmstLabel.setBackground(((BlinkingList)list).isBlink() ? colorOn : colorOff);
				else
					alarmstLabel.setBackground(Color.GREEN);
		alarmstLabel.setBounds(alarmSIZE, 1, LIST_ELEMENT_WIDTH, LIST_ELEMENT_HEIGHT); 
		
		panel.add(ID);
		panel.add(LOC);
		panel.add(ALARMst);
		panel.add(lacIDLabel);
		panel.add(LOCLabel);
		panel.add(alarmstLabel);
		
		
		return panel;
		
		
		
	}

}
