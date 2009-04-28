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
import connection.ConnectionStatusWrapper.ConnectionStatus;

import apps.LAC;
import apps.MAC.LACAdapter;

/**
 * Klasse som skal lage listkomponenten til LACs som vises i MACvinduet. Hvert element
 * i listen vil v�re en MACrendererkomponent som essensielt er et JPanel med labels p�
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
		
		LACAdapter adapter = (LACAdapter)object;
		
		/*
		 * Initialiserer komponenten som skal returneres som et panel med riktige m�l
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
		 * Initialiserer og legger til de JLabelene som skal v�re i JPanelet
		 */

		
		
		if(m!=null){

			JLabel adressLabel = new JLabel(""+adapter.getModel().getAdresse());
			adressLabel.setBounds(statuspanelSIZE*5, 1, LIST_ELEMENT_WIDTH, LIST_ELEMENT_HEIGHT);
			
			JLabel lacIDLabel = new JLabel("ID "+(index+1));
			lacIDLabel.setBounds(0, 0, lacIDSIZE, CELLHEIGHT);
			
			JPanel alarmstLabel = new JPanel();
			
			//sjekker om alarm er ok
			if(adapter.hasAlarm())
				alarmstLabel.setBackground(((BlinkingList)list).isBlink() ? colorOn : colorOff);
			else
				alarmstLabel.setBackground(Color.GREEN);
			
			if(element.getConnectionStatusWrapper().getConnectionStatus() == ConnectionStatus.DISCONNECTED) {
				alarmstLabel.setBackground(Color.YELLOW);
			}
			
			alarmstLabel.setBounds(alarmSIZE*3, 1, LIST_ELEMENT_WIDTH, LIST_ELEMENT_HEIGHT);
			connectionStatus.setBounds(statuspanelSIZE, 1, 2*LIST_ELEMENT_WIDTH, LIST_ELEMENT_HEIGHT);

			panel.add(adressLabel);
			panel.add(lacIDLabel);
			panel.add(alarmstLabel);
			panel.add(connectionStatus);
				
		} 

		panel.setBackground((selected ? Color.PINK : Color.WHITE));
	
		return panel;
		
		
		
	}

}
