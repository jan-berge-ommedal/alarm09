package help;
import model.Model;
import model.Room;
import model.Sensor;
import apps.LAC;


public class AlarmHelp {
	
	public static Model getDefaultModel(){
		Model m = new Model();
		m.setAdresse("Lidarende 1");
		Room r = new Room(0,54,"BAD","Et fint bad");
		r.addSensor(new Sensor(0,false,70,LAC.getTime(),r));
		r.addSensor(new Sensor(1,true,20,LAC.getTime(),r));
		Room r2 = new Room(0,2,"Kj�kken","Storkj�kkenet i huset");
		r2.addSensor(new Sensor(2,false,100,LAC.getTime(),r2));
		m.addRoom(r);
		m.addRoom(r2);
		m.setID(1);
		return m;
	}

}