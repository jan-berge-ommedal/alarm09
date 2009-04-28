package log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import model.Model;

public class Log {
	public static void printReport(Model model, boolean mac) {
		Date dato = new Date();
		if(mac){
			try{
				// Create file 
				FileWriter fstream = new FileWriter("Log/Maclog-" + dato.getDate() + "." + dato.getMonth() + "-" + dato.getHours() + dato.getMinutes() + ".log");
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(model.toString());
				JOptionPane.showMessageDialog(new JFrame(), 
					    "MAC log written!",
					    "Success!",
					    JOptionPane.INFORMATION_MESSAGE);

				//Close the output stream
				out.close();
			}catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
				JOptionPane.showMessageDialog(new JFrame(), 
					    "Error when making MAC log!",
					    "File error!",
					    JOptionPane.ERROR_MESSAGE);
			}
		}
		else {
			try{
				// Create file 
				FileWriter fstream = new FileWriter("Log/Laclog-" + dato.getDate() + "." + dato.getMonth() + "-" + dato.getHours() + dato.getMinutes() + ".log");
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(model.toString());
				JOptionPane.showMessageDialog(new JFrame(), 
					    "LAC log written!",
					    "Success!",
					    JOptionPane.INFORMATION_MESSAGE);

				//Close the output stream
				out.close();
			}catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
				JOptionPane.showMessageDialog(new JFrame(), 
					    "Error when making LAC log!",
					    "File error!",
					    JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
