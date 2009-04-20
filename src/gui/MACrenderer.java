package gui;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * Klasse som skal lage listkomponenten til LACs som vises i MACvinduet. Hvert element
 * i listen vil være en MACrendererkomponent som essensielt er et JPanel med labels på
 * @author Olannon
 *
 */
public class MACrenderer extends JPanel implements ListCellRenderer, Values {

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
		JLabel LOC = new JLabel("Location");
		JLabel ALARMst = new JLabel("Alarm Status");
		

		panel.add(ID);
		panel.add(LOC);
		panel.add(ALARMst);
	
		
		return panel;
		
	}

}
