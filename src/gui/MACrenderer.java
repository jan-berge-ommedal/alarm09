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
import apps.MAC.LACAdaper;

/**
 * Klasse som skal lage listkomponenten til LACs som vises i MACvinduet. Hvert element
 * i listen vil være en MACrendererkomponent som essensielt er et JPanel med labels på
 * @author Olannon
 *
 */
public class MACrenderer extends DefaultListCellRenderer implements ListCellRenderer, Values {
	private static final int CELLHEIGHT = LABEL_HEIGHT;
	private static final int lacIDSIZE = LIST_LABEL_WIDTH;
	private static final int lacNAMESIZE = LIST_LABEL_WIDTH;
	private static final int alarmSIZE = LIST_LABEL_WIDTH ;
	private static final int statuspanelSIZE = LIST_LABEL_WIDTH;
	
	private static final Color colorOn = Color.RED;
	private static final Color colorOff = new Color(255,150,150);
	private static final Color colorSelected = new Color(0,170,255);
	@Override
	public Component getListCellRendererComponent(JList list, Object object,
			int index, boolean selected, boolean hasCellFocus) {
		
		LACAdaper adapter = (LACAdaper)object;
		
		/*
		 * Initialiserer komponenten som skal returneres som et panel med riktige mål
		 */
		JPanel panel = new JPanel();
		panel.setBounds(LEFT_SPACE, TOP_SPACE + LABEL_HEIGHT + BUTTON_HEIGHT + 3*DEFAULT_SPACE, LIST_ELEMENT_WIDTH, LIST_ELEMENT_HEIGHT);
		panel.setLayout(null);
		if(selected)panel.setBackground(colorSelected);
		
		ModelEditControll element = (ModelEditControll)object;
		Model m = element.getModel();
		
		
		//Lager connectionstatuspanel
		ConnectionStatusPanel connectionStatus = new ConnectionStatusPanel(element.getConnectionStatusWrapper());
				
		
		/*
		 * Initialiserer og legger til de JLabelene som skal være i JPanelet
		 */

		
		
		if(m!=null){

			JLabel adressLabel = new JLabel(""+adapter.getModel().getAdresse());
			
			JLabel lacIDLabel = new JLabel("ID "+(index+1));
			lacIDLabel.setBounds(0, 0, lacIDSIZE, CELLHEIGHT);
			
			JPanel alarmstLabel = new JPanel();
			
			//sjekker om alarm er ok
			if(adapter.hasAlarm())
				alarmstLabel.setBackground(((BlinkingList)list).isBlink() ? colorOn : colorOff);
			else
				alarmstLabel.setBackground(Color.GREEN);
			
			alarmstLabel.setBounds(alarmSIZE*3, 1, LIST_ELEMENT_WIDTH, LIST_ELEMENT_HEIGHT);
			connectionStatus.setBounds(statuspanelSIZE, 1, 2*LIST_ELEMENT_WIDTH, LIST_ELEMENT_HEIGHT);

			panel.add(adressLabel);
			panel.add(lacIDLabel);
			panel.add(alarmstLabel);
			panel.add(connectionStatus);
				
		} else {

			JLabel LACID = new JLabel("LAC ID: "+adapter.getModel().getID());
			LACID.setBounds(0, 0, lacIDSIZE, CELLHEIGHT);
			ConnectionStatusPanel ConnectionStatus = new ConnectionStatusPanel(adapter.getConnectionStatusWrapper());
			ConnectionStatus.setBounds(lacNAMESIZE, 0, 200, CELLHEIGHT);
			
			panel.add(LACID);
			panel.add(ConnectionStatus);
			
		}

		panel.setBackground((selected ? Color.PINK : Color.WHITE));
	
		return panel;
		
		
		
	}

}
