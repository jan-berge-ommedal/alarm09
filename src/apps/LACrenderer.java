package apps;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import model.Sensor;

/**
 * Klasse som genererer komponentente til elementene i listen i LACvinduet (sensorene)
 * @author Olannon
 *
 */
public class LACrenderer extends DefaultListCellRenderer implements ListCellRenderer, Values {

	@Override
	public Component getListCellRendererComponent(JList list, Object object, int arg2, boolean arg3, boolean arg4) {
		JLabel comp = (JLabel)super.getListCellRendererComponent(list, object, arg2, arg3, arg4);
		
		/*
		 * Initialiserer komponenten som skal returneres som et panel med riktige mål
		 */
		
		
		//JPanel panel = new JPanel();
		//panel.setBounds(LEFT_SPACE, TOP_SPACE + LABEL_HEIGHT + BUTTON_HEIGHT + 3*DEFAULT_SPACE, LIST_ELEMENT_WIDTH, LIST_ELEMENT_HEIGHT);
		
		Sensor s = (Sensor)object;
		
		/*
		 * Initialiserer og legger til de JLabelene som skal være i JPanelet
		 */
		JLabel ROOMno = new JLabel(s.getRoom().getRomInfo());
		JLabel ROOMty = new JLabel(s.getRoom().getRomType());
		JLabel ROOMna = new JLabel(""+s.getRoom().getRomNR());
		JLabel SensorStatus = new JLabel((s.isAlarmState() ? "ALARM!" : "NO ALARM"));
		JLabel BatteryStatus = new JLabel(""+s.getBattery());
		
		comp.add(ROOMno);
		comp.add(ROOMty);
		comp.add(ROOMna);
		comp.add(SensorStatus);
		comp.add(BatteryStatus);
		
		return comp;
		
	}

}

