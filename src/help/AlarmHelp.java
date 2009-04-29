package help;
import connection.ModelEditController;
import unitTests.XMLParsingTests.DefaultModelEditController;
import model.Model;
import model.Room;
import model.Sensor;
import apps.LAC;


public class AlarmHelp {
	
	
	public static Model getDefaultModel(ModelEditController controller){
		Model m = new Model(controller);
		m.setAdresse("Lidarende 1");
		Room r = new Room(0,54,"BAD","Et fint bad",m);
		new Sensor(0,false,70,LAC.getTime(),r,true);
		new Sensor(1,true,20,LAC.getTime(),r,true);
		Room r2 = new Room(0,2,"Kjøkken","Storkjøkkenet i huset",m);
		new Sensor(2,false,100,LAC.getTime(),r2,true);
		return m;
	}


}
