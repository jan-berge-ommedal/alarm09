package apps;

import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * @author Olannon
 * 
 * denne klassen håndterer vinduet som presenteres fra en LAC maskin
 *
 */
public class MACgui extends JPanel implements Values {
	
	private JButton writeSiteSummary;
	private JButton viewLog;
	private JButton checkMarked;
	private JButton updateMarked;
	private JButton updateAll;
	private JLabel lacs;
	
	
	public MACgui() {
		Insets asdf = new Insets(0,0,0,0);
		JPanel pane = new JPanel();
		JFrame frame = new JFrame("MAC");
		frame.setSize(700, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(pane);
		frame.setVisible(true);
		
		writeSiteSummary = new JButton("Write Site Summary");
		writeSiteSummary.setMargin(asdf);
		viewLog = new JButton("View Log");
		viewLog.setMargin(asdf);
		checkMarked = new JButton("Check Marked LACs");
		checkMarked.setMargin(asdf);
		updateMarked = new JButton("Update Marked LACs");
		updateMarked.setMargin(asdf);
		updateAll = new JButton("Update all LACs");
		updateAll.setMargin(asdf);
		//returnMAC.setVisible(false);
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
		
	}

}
