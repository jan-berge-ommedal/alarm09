package help;
import no.ntnu.fp.net.co.Connection;
import connection.AbstractApplicationProtocol;
import connection.LACProtocol;
import connection.ModelEditController;
import model.Model;
import model.Room;
import model.Sensor;
import model.Sensor.Alarm;
import apps.LAC;


public class AlarmHelp {
	
	
	public static Model getDefaultModel(ModelEditController controller){
		Model m = new Model(controller,0);
		m.setAdresse("Lidarende 1");
		Room r = new Room(0,54,"BAD","Et fint bad",m);
		new Sensor(0,Alarm.DEACTIVATED,70,LAC.getTime(),r);
		new Sensor(1,Alarm.ACTIVATED,20,LAC.getTime(),r);
		Room r2 = new Room(0,2,"Kjøkken","Storkjøkkenet i huset",m);
		new Sensor(2,Alarm.DEACTIVATED,100,LAC.getTime(),r2);
		return m;
	}

	public static ModelEditController getDefaultModelController(){
		return new DefaultModelController();
	}
	
	
	
}
