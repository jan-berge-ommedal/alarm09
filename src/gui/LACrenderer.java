package gui;

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
	private static final int CELLHEIGHT = LABEL_HEIGHT;
	private static final int roomTYPESIZE = LIST_LABEL_WIDTH;
	private static final int roomNRSIZE = LIST_LABEL_WIDTH;
	private static final int roomNAMESIZE = LIST_LABEL_WIDTH;
	private static final int batterySIZE = LIST_LABEL_WIDTH;
	private static final int sensorStatusSIZE = LIST_LABEL_WIDTH;
	private static final int timeStampSIZE = 2*LIST_LABEL_WIDTH;
	
	private static final Color colorOn = Color.RED;
	private static final Color colorOff = new Color(255,150,150);
	private static final Color colorSelected = new Color(0,170,255);
	

	@Override
	public Component getListCellRendererComponent(JList list, Object object, int index, boolean selected, boolean hasCellFocus) {
		//JLabel label = (JLabel)super.getListCellRendererComponent(list, object, arg2, arg3, arg4);
		
		JPanel comp = new JPanel();
		comp.setLayout(null);
		if(selected)comp.setBackground(colorSelected);
		
		/*
		 * Initialiserer komponenten som skal returneres som et panel med riktige mål
		 */
		
		
		//JPanel panel = new JPanel();
		//panel.setBounds(LEFT_SPACE, TOP_SPACE + LABEL_HEIGHT + BUTTON_HEIGHT + 3*DEFAULT_SPACE, LIST_ELEMENT_WIDTH, LIST_ELEMENT_HEIGHT);
		
		Sensor s = (Sensor)object;
		
		/*
		 * Initialiserer og legger til de JLabelene som skal være i JPanelet
		 */

		JLabel sensorNameLabel = new JLabel("Sensor "+(index+1));
		sensorNameLabel.setBounds(0, 0, roomNAMESIZE, CELLHEIGHT);
		
		JLabel roomTypeLabel = new JLabel(s.getRoom().getRomType());
		roomTypeLabel.setBounds(roomNAMESIZE, 0, roomTYPESIZE, CELLHEIGHT);
		JLabel roomNrLAbel = new JLabel(""+s.getRoom().getRomNR());
		roomNrLAbel.setBounds(roomNAMESIZE+roomTYPESIZE, 0, roomNRSIZE, CELLHEIGHT);
		JPanel SensorStatus = new JPanel();
		
		if(s.isAlarmState())
			SensorStatus.setBackground(((BlinkingList)list).isBlink() ? colorOn : colorOff);
		else
			SensorStatus.setBackground(Color.GREEN);
		
		SensorStatus.setBounds(roomNAMESIZE+roomNRSIZE+roomTYPESIZE, 0, sensorStatusSIZE, CELLHEIGHT);
		//JLabel BatteryStatus = new JLabel(""+s.getBattery());
		BatteryIndicator BatteryStatus = new BatteryIndicator(s.getBattery());
		BatteryStatus.setBounds(roomNAMESIZE+roomNRSIZE+roomTYPESIZE+sensorStatusSIZE+20, 0, batterySIZE, CELLHEIGHT);
		
		JLabel timestampLabel = new JLabel(s.getInstallationDate().toString());
		timestampLabel.setBounds(roomNAMESIZE+roomNRSIZE+roomTYPESIZE+sensorStatusSIZE+batterySIZE+25, 0, timeStampSIZE, CELLHEIGHT);
		
		
		comp.add(sensorNameLabel);
		comp.add(roomTypeLabel);
		comp.add(roomNrLAbel);
		comp.add(SensorStatus);
		comp.add(BatteryStatus);
		comp.add(timestampLabel);
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
			g.fillRect(1, 1, (this.getWidth()-2)*batteryLevel/100, this.getHeight()-2);
		}
		
		
	}
	
	
		
		

}

