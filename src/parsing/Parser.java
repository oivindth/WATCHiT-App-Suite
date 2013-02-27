package parsing;
import java.io.File;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;




public class Parser {
	
	
	
	private Serializer serializer;
	private File result;
	
	public Parser() {
		serializer = new Persister();
		//if (xml instanceof TrainingProcedure) trainingProcedure = (TrainingProcedure) xml; //use polymorphism instead?
		
		//trainingProcedure = new TrainingProcedure("12", "mirror:application:watchIt_Reflection_App:trainingprocedure");
	}
	
	/**
	 * Serialize(save) the simplexml object.
	 */
	public void Serialize (GenericSensorData xmlObject) {
		File result = new File("example.xml");
		try {
			serializer.write(xmlObject, result);
			System.out.println("result written to : " + result.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * DeSerialize(read) xml from a string and return a simpleXML object
	 */
	public GenericSensorData DeSerialize (String xml) {
		GenericSensorData data = null;
		try {
			data = serializer.read(GenericSensorData.class, xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	
	/**
	 * DeSerialize(read) the simplexml and return object from file
	 */
	public GenericSensorData DeSerialize (File file) {
		GenericSensorData data = null;
		try {
			data = serializer.read(GenericSensorData.class, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
}
