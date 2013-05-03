package parsing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;


import de.imc.mirror.sdk.android.CDMData;
import de.imc.mirror.sdk.android.CDMDataBuilder;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.DataObjectBuilder;
import de.imc.mirror.sdk.cdm.CDMVersion;

/**
 * 
 * @author �ivind Thorvaldsen
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
		String xml = dataObject.toString();
		return DeSerialize(xml);
	}
	
	public static GenericSensorDataTP buildSimpleXMLTPObject(DataObject dataObject) {
		String xml = dataObject.toString();
		return DeSerializeTPData(xml);
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
	
public static GenericSensorData buildSimpleXMLObject(String watchitData ) {
		
		GenericSensorData genericSensorData = new GenericSensorData(new Location(), 
				new Value("steps", "", watchitData));
		//Log.d("Bulding simplexml:", ": " + genericSensorData.toString());
		return genericSensorData;
	}
	
	/**
	 * Build a simplexml object with training procedure data.
	 * @param watchitData
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public static GenericSensorDataTP buildSimpleXMLObject(List<Step> steps) {
		
		GenericSensorDataTP gstp = new GenericSensorDataTP(new Location(), new ValueTP("steps", "", steps));
		//Log.d("Bulding simplexml:", ": " + genericSensorData.toString());
		return gstp;
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

	public static String buildCustomStringXmlFromSteps(List<Step> steps) {
		StringBuilder customStepsXML = new StringBuilder();
		customStepsXML.append("<steps" + "\n");
		
		for (Step step : steps) {
			customStepsXML.append("<step>" + step + "</step>");
			customStepsXML.append("/n");
		}
		customStepsXML.append("</steps>");
		
		return customStepsXML.toString();
	}
	
	
}
