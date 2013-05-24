package parsing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.util.Log;


import de.imc.mirror.sdk.android.CDMData;
import de.imc.mirror.sdk.android.CDMDataBuilder;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.DataObjectBuilder;
import de.imc.mirror.sdk.cdm.CDMVersion;

/**
 * 
 * @author ¯ivind Thorvaldsen
 * @date 2013
 * Class for parsing dataobjects from reflection spaces.
 */
public class Parser {
	
	/**
	 * Convert a MIRROR dataobject receieved from a space to a simplexml object.
	 * @param dataObject
	 * @return
	 */
	public static GenericSensorData buildSimpleXMLObject(DataObject dataObject) {
		Log.d("ddd", dataObject.toString());
		String xml = dataObject.toString();
		return DeSerialize(xml);
	}
	
	public static GenericSensorDataTP buildSimpleXMLTPObject(DataObject dataObject) {
		String xml = dataObject.toString();
		return DeSerializeTPData(xml);
	}
	
	public static GenericSensorDataTP buildSimpleXMLTPObject(String procedureName, List<Step> steps) {
		GenericSensorDataTP genericSensorData = new GenericSensorDataTP(new Location("0", "0"), 
				new ValueTP("steps", procedureName, steps));
		//Log.d("Bulding simplexml:", ": " + genericSensorData.toString());
		return genericSensorData;
	}
	
	/**
	 * Build a simplexml object with data receieved from watchit and location from phone
	 * @param watchitData
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public static GenericSensorData buildSimpleXMLObject(String watchitData, String latitude, String longitude ) {
		
		GenericSensorData genericSensorData = new GenericSensorData(new Location(latitude, longitude), 
				new Value("note", "", watchitData));
		//Log.d("Bulding simplexml:", ": " + genericSensorData.toString());
		return genericSensorData;
	}
	
public static GenericSensorData buildSimpleXMLObject(String stepData, String procedureName ) {
		
		GenericSensorData genericSensorData = new GenericSensorData(new Location("0", "0"), 
				new Value("steps", procedureName, stepData));
		Log.d("Bulding simplexml:", ": " + genericSensorData.toString());
		return genericSensorData;
	}
	

	
	
	
	/**
	 * Convert a simplexml object to MIRROR DataObject for transfer to mirror space.
	 * @param genericSensorData
	 * @param user
	 * @return
	 */
	public static DataObject buildDataObjectFromSimpleXMl (GenericSensorData genericSensorData, String userJID, String userName) {
		DataObjectBuilder dataObjectBuilder =
		    	 new DataObjectBuilder("genericsensordata", "mirror:application:watchit:genericsensordata");
		Date date = new Date();
		
		dataObjectBuilder.addCDTCreationInfo(date, userName, null);
		CDMDataBuilder cdmDataBuilder = new CDMDataBuilder(CDMVersion.CDM_1_0);
		cdmDataBuilder.setPublisher(userJID);
		cdmDataBuilder.setModelVersion("0.2");
		//cdmDataBuilder.setTimestamp(date.toGMTString());
		CDMData cdmData = cdmDataBuilder.build();
		dataObjectBuilder.setCDMData(cdmData);
		
		 Map <String, String> attributes = new HashMap<String, String>();
   	 
	   	 attributes.put("latitude", genericSensorData.getLocation().getLatitude());
	   	 attributes.put("longitude", genericSensorData.getLocation().getLongitude());
	   	 dataObjectBuilder.addElement("location", attributes, "", false);
	   	 
	   	 attributes.clear();
	   	 
	   	 attributes.put("type", genericSensorData.getValue().getType());
	   	 attributes.put("unit", genericSensorData.getValue().getUnit());
	   	// Log.d("building object", "text?:" +genericSensorData.getValue().getText());
	   	 //dataObjectBuilder.addElement("value", attributes , genericSensorData.getValue().getText(), false);
	   	 dataObjectBuilder.addElement("value", attributes , genericSensorData.getValue().getText(), false);
	   	 
	   	 DataObject dataObject = dataObjectBuilder.build();
	   	 //Log.d("Parser", dataObject.toString());
	   	 return dataObject;
		
	}
	
	public static Procedure DeSerializeProcedure(String xml) {
		Serializer serializer = new Persister();
		Procedure data = null;
		try {
			data = serializer.read(Procedure.class, xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	/**
	 * DeSerialize(read) xml from a string and return a simpleXML object
	 */
	public static GenericSensorData DeSerialize (String xml) {
		Serializer serializer = new Persister();
		GenericSensorData data = null;
		try {
			data = serializer.read(GenericSensorData.class, xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	
	/**
	 * DeSerialize training procedure xml
	 */
	public static GenericSensorDataTP DeSerializeTPData (String xml) {
		Serializer serializer = new Persister();
		GenericSensorDataTP data = null;
		try {
			data = serializer.read(GenericSensorDataTP.class, xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	
	public static List<GenericSensorData> convertDataObjectsToGenericSensordataObjects (List<de.imc.mirror.sdk.DataObject> dataObjects) {
		List<GenericSensorData> genericSensorDataObjects = new ArrayList<GenericSensorData>();
		for (de.imc.mirror.sdk.DataObject dataObject : dataObjects) {
			 genericSensorDataObjects.add(buildSimpleXMLObject((DataObject) dataObject));
		}
		return genericSensorDataObjects;
	}


	public static String buildCustomStringFromSteps(List<Step> steps) {
		StringBuilder customStepsXML = new StringBuilder();
		for (Step step : steps) {
			customStepsXML.append("sx" + step.getTime());
		}
		return customStepsXML.toString();
	}
	
	
	
	public static List<Step> buildStepList(String steps) {
		List<Step> temp = new ArrayList<Step>();
		int max = steps.length();
		for (int i = 0; i < max; i++) {
			if (steps.charAt(i) == 'x') {
				int start = i+1;
				int end = start+5;
				String s = steps.substring(start, end);		
				Step step = new Step();
				Log.d("timetag", s);
				step.setTime(s);
				temp.add(step);
			}
		}
		return temp;
	}
	
	/**
	 * Builds integer from a string of the format xx:xx
	 * EX: 01:25(1 minute 25 seconds is converted to 85 seconds.
	 * @param time
	 * @return
	 */
	public static int convertStringTimeToSeconds(String time) {
		Log.d("kalk", "string tid: " + time);
		Log.d("kalk", "orvtorv: " + time.substring(0, 1));
		int hour = Integer.parseInt(time.substring(0, 1));
		int minutes = Integer.parseInt(time.substring(1, 2));
		int seconds = Integer.parseInt(time.substring(3, 4));
		int second = Integer.parseInt(time.substring(4,5));
		int counter = 0;
				if (hour != '0') {
					counter+= hour*60*60; //convert hour to seconds.
					Log.d("kalk", "etter hours: " + seconds);
				}
				if (minutes != '0') {
					counter += minutes*60; // convert minutes to seconds.
					Log.d("kalk", "etter hour2: " + seconds);
				}
				if (seconds != '0') {
					counter += seconds*10 ; // convert ten seconds to seconds.......
					Log.d("kalk", "etter 10sekunds: " + seconds);
				}
				counter += second; //add the last seconds.
				Log.d("kalk", "etter siste: " + seconds );
				return counter;	
		}
	
		public static String convertSecondsToString(int secondsIN) {
			
			int milliseconds = secondsIN*1000;
			
			int seconds = (int) (milliseconds / 1000) % 60 ;
			int minutes = (int) ((milliseconds / (1000*60)) % 60);
			int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
			
			String sseconds = "" + seconds;
			
			if (seconds == 0) {
				sseconds ="00";
			}
			
			String s ="" + hours + minutes + ":" + sseconds;
			Log.d("troll", "formatert: " + s );
			return s;
			
		}
	

}
