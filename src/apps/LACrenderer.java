package apps;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
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
	private static final int roomNRSIZE = 50;
	private static final int roomNAMESIZE = 150;
	private static final int batterySIZE = 50;
	private static final int sensorStatusSIZE = 50;
	
	private static final Color colorOn = Color.RED;
	private static final Color colorOff = Color.BLACK; 

	@Override
	public Component getListCellRendererComponent(JList list, Object object, int index, boolean selected, boolean hasCellFocus) {
		//JLabel label = (JLabel)super.getListCellRendererComponent(list, object, arg2, arg3, arg4);
		
		JPanel comp = new JPanel();
		comp.setLayout(null);
		
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
		ROOMno.setBounds(0, 0, roomNRSIZE, 20);
		JLabel ROOMty = new JLabel(s.getRoom().getRomType());
		JLabel ROOMna = new JLabel(""+s.getRoom().getRomNR());
		ROOMna.setBounds(roomNRSIZE, 0, roomNAMESIZE, 20);
		JPanel SensorStatus = new JPanel();
		
		if(s.isAlarmState())
			SensorStatus.setBackground(((BlinkingList)list).isBlink() ? colorOn : colorOff);
		else
			SensorStatus.setBackground(Color.GREEN);
		
		SensorStatus.setBounds(roomNRSIZE+roomNAMESIZE, 0, sensorStatusSIZE, 20);
		//JLabel BatteryStatus = new JLabel(""+s.getBattery());
		BatteryIndicator BatteryStatus = new BatteryIndicator(s.getBattery());
		BatteryStatus.setBounds(roomNRSIZE+roomNAMESIZE+sensorStatusSIZE, 0, batterySIZE, 20);
		
		comp.add(ROOMno);
		//comp.add(ROOMty);
		comp.add(ROOMna);
		comp.add(SensorStatus);
		comp.add(BatteryStatus);
		
		return comp;
		
	}
	
	class BatteryIndicator extends JPanel{
		private int batteryLevel;
		
		public BatteryIndicator(int batteryLevel) {
			super();
			this.batteryLevel=batteryLevel;
			this.setMinimumSize(new Dimension(10, 100));
		}
		
		public void paintComponent(Graphics g){
			super.paintComponents(g);
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
			g.setColor((batteryLevel/100.0>0.20 ? Color.GREEN : Color.RED));
			g.fillRect(1, 1, (this.getWidth()-3)*batteryLevel/100, this.getHeight()-3);
		}
		
		
	}
	
	
		
		

}

