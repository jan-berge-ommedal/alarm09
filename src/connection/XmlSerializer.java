/*
 * Created on Oct 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package connection;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;

import model.Model;
import model.Sensor;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

/**
 * @author Simon Grøndahl
 */
public class XmlSerializer {

	/**
	 * 
	 * <strong>This method takes a model as parameter and  makes a xmlString out of it</strong>
	 * 
	 * @param aModel the model that is made to a  XmlString
	 */
	public static String toXml(Model aModel) {
		Element root = new Element("LAC");
		
		Element id = new Element("id");
		id.appendChild(Integer.toString(aModel.getID()));
		root.appendChild(id);
		
		Element adress = new Element("adress");
		adress.appendChild(aModel.getAdresse());
		root.appendChild(adress);
		
		Iterator it = aModel.iterator();
		
		while (it.hasNext()) {	
			Sensor aSensor = (Sensor)it.next();
			Element element = sensorToXml(aSensor);
			root.appendChild(element);
		}
		
		return new Document(root).toString();
	}
	
//	public Model toModel(String xmlDocument) throws ParseException {
//		Model aModle = new Model();
//		Element groupElement = xmlDocument.getRootElement();
//		Elements personElements = groupElement.getChildElements("person");
//		
//		for (int i = 0; i < personElements.size(); i++) {
//			Element childElement = personElements.get(i);
//			aProject.addPerson(assemblePerson(childElement));
//		}
//		
//		return aProject;
//	}

//    public Person toPerson(String xml) throws java.io.IOException, java.text.ParseException, nu.xom.ParsingException {
//		nu.xom.Builder parser = new nu.xom.Builder(false);
//		nu.xom.Document doc = parser.build(xml, "");
//		return assemblePerson(doc.getRootElement());
//    }
	
	private static Element sensorToXml(Sensor aSensor) {
		Element element = new Element("sensor");
		Element id = new Element("id");
		id.appendChild(Integer.toString(aSensor.getId()));
		Element alarmState = new Element("alarmState");
		if(aSensor.isAlarmState())alarmState.appendChild("true");
		else alarmState.appendChild("false");
		
		Element room = new Element("Room");
		Element roomid = new Element("id");
		roomid.appendChild(Integer.toString(aSensor.getRoom().getID()));
		Element romNR = new Element("romNR");
		romNR.appendChild(Integer.toString(aSensor.getRoom().getRomNR()));
		Element romType = new Element("rom type");
		romType.appendChild(aSensor.getRoom().getRomType());
		Element romInfo = new Element("rom info");
		romInfo.appendChild(aSensor.getRoom().getRomInfo());
		
		Iterator it = aSensor.getRoom().iterator();
		
		
		room.appendChild(aSensor.getRoom().getRomType());
		
		
		Element installationDate = new Element("installationDate");
		installationDate.appendChild(aSensor.getInstallationDate().toString());
		Element battery = new Element("battery");
		battery.appendChild(Integer.toString(aSensor.getBattery()));
		
		element.appendChild(id);
		element.appendChild(alarmState);
		element.appendChild(room);
		element.appendChild(installationDate);
		element.appendChild(battery);
		return element;
	}
	
//	private Person assemblePerson(Element personElement) throws ParseException {
//		String name = null, email = null;
//		Date date = null;
//		Element element = personElement.getFirstChildElement("name");
//		if (element != null) {
//			name = element.getValue();
//		}
//		element = personElement.getFirstChildElement("email");
//		if (element != null) {
//			email = element.getValue();
//		}
//		element = personElement.getFirstChildElement("date-of-birth");
//		if (element != null) {
//			date = parseDate(element.getValue());
//		}
//		return new Person(name, email, date);
//	}
	
	/**
	 * TODO: handle this one to avoid duplicate code
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	private Date parseDate(String date) throws ParseException {
		DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM, java.util.Locale.US);
		return format.parse(date);
	}

}

