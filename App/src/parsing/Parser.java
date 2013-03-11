package parsing;

import java.util.HashMap;
import java.util.Map;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.util.Log;

import de.imc.mirror.sdk.android.CDMData;
import de.imc.mirror.sdk.android.CDMDataBuilder;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.DataObjectBuilder;
import de.imc.mirror.sdk.cdm.CDMVersion;
import enums.ValueType;

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
		
		Log.d("Bulding simplexml:", ": " + genericSensorData.toString());
		
		return genericSensorData;
	}
	
	/**
	 * Convert a simplexml object to MIRROR DataObject for transfer to mirror space.
	 * @param genericSensorData
	 * @param user
	 * @return
	 */
	public static DataObject buildDataObjectFromSimpleXMl (GenericSensorData genericSensorData, String userJID) {
		DataObjectBuilder dataObjectBuilder =
		    	 new DataObjectBuilder("genericsensordata", "mirror:application:watchit:genericsensordata");
		//Date date = new Date();
		//dataObjectBuilder.addCDTCreationInfo(date, user, "mirror:application:watchit:genericsensordata");
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
	   	 Log.d("building object", "text?:" +genericSensorData.getValue().getText());
	   	 //dataObjectBuilder.addElement("value", attributes , genericSensorData.getValue().getText(), false);
	   	dataObjectBuilder.addElement("value", attributes , genericSensorData.getValue().getText(), false);
	   	 
	   	
	   	 DataObject dataObject = dataObjectBuilder.build();
	   	 Log.d("Parser", dataObject.toString());
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
	
}
