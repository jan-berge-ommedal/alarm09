package apps;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * Klasse som genererer komponentente til elementene i listen i LACvinduet (sensorene)
 * @author Olannon
 *
 */
public class LACrenderer extends JPanel implements ListCellRenderer, Values {

	@Override
	public Component getListCellRendererComponent(JList arg0, Object arg1,
			int arg2, boolean arg3, boolean arg4) {
		
		/*
		 * Initialiserer komponenten som skal returneres som et panel med riktige mål
		 */
		JPanel panel = new JPanel();
		panel.setBounds(LEFT_SPACE, TOP_SPACE + LABEL_HEIGHT + BUTTON_HEIGHT + 3*DEFAULT_SPACE, LIST_ELEMENT_WIDTH, LIST_ELEMENT_HEIGHT);
		
		/*
		 * Initialiserer og legger til de JLabelene som skal være i JPanelet
		 */
		JLabel ID = new JLabel("ID");
		JLabel ROOMno = new JLabel("Room-number");
		JLabel ROOMty = new JLabel("Room-type");
		JLabel ROOMna = new JLabel("Room name");
		JLabel SensorStatus = new JLabel("Sensor Status");
		JLabel BatteryStatus = new JLabel("Battery Status");

		panel.add(ID);
		panel.add(ROOMno);
		panel.add(ROOMty);
		panel.add(ROOMna);
		panel.add(SensorStatus);
		panel.add(BatteryStatus);
		
		return panel;
		
	}

}

