package parsing;

import java.io.File;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;

public class Parser {

	private Serializer serializer;
	private TrainingProcedure trainingProcedure;
	private File result;
	private XMLDataObject xmlObject;
	
	public Parser() {
		serializer = new Persister();
		//if (xml instanceof TrainingProcedure) trainingProcedure = (TrainingProcedure) xml; //use polymorphism instead?
		
		//trainingProcedure = new TrainingProcedure("12", "mirror:application:watchIt_Reflection_App:trainingprocedure");
	}
	
	/**
	 * Serialize(save) the simplexml object.
	 */
	public void Serialize (TrainingProcedure xml, Context context) {
		String filePath =  context.getFilesDir().getPath().toString() + "/example.txt";
		result = new File(filePath);
		//result = new File("example.xml");
		try {
			serializer.write(trainingProcedure, result);
			System.out.println("result written to : " + result.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		trainingProcedure = DeSerialize(result);
		System.out.println("XML after everything***:::   " + trainingProcedure.getId() + trainingProcedure.getTimeStamp() + trainingProcedure.getTime());
		
	}
	
	/**
	 * DeSerialize(read) the simplexml object.
	 */
	public TrainingProcedure DeSerialize (String xml) {
		System.out.println("xml:  \n " + xml);
		try {
			trainingProcedure = serializer.read(TrainingProcedure.class, xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return trainingProcedure;
	}
	/**
	 * DeSerialize(read) the simplexml object.
	 */
	public TrainingProcedure DeSerialize (File file) {
		
		try {
			trainingProcedure = serializer.read(TrainingProcedure.class, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return trainingProcedure;
	}
	
	
}
