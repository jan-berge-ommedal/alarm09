package guiAddons;

import javax.swing.JFrame;

import help.AlarmHelp;
import apps.LAC;
import apps.MAC;
import model.Model;


//FIXME Eirik: Alarm Activator
/**
 * This class will be used to test Requirement 14.
 * 
 * It contains a simple gui that can activate alarms on sensors of the given model
 * 
 * 
 * 
 * @author Jan Berge Ommedal, ......
 *
 */


public class AlarmActivator extends JFrame{

	
	public AlarmActivator(Model m) {
		// TODO Auto-generated constructor stub
	}
	
	
	public static void main(String[] args) {
		MAC mac = new MAC();
		LAC lac = new LAC();
		Model m = AlarmHelp.getDefaultModel();
		lac.setModel(m);
		new AlarmActivator(m);
		
	}
}
