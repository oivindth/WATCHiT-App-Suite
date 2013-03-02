package parsing;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.util.Log;

import com.example.watchit_connect.MainApplication;

import de.imc.mirror.sdk.Space.Type;
import de.imc.mirror.sdk.android.CDMData;
import de.imc.mirror.sdk.android.CDMDataBuilder;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.DataObjectBuilder;
import de.imc.mirror.sdk.android.xml.Model;
import de.imc.mirror.sdk.android.xml.Models;
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
	
	public static GenericSensorData buildSimpleXMLObject(String watchitData, String latitude, String longitude ) {
		
		//TODO: parse watchit string
		
		GenericSensorData genericSensorData = new GenericSensorData(new Location(latitude, longitude), 
				new Value("text:", "unit", watchitData));
		
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
	   	 
	   	
	   	
	   	if (genericSensorData.getValue().getType() == ValueType.MOOD.toString()) {
	   		
	   	}
	   	
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
	
	/**
	 * Build a dummy data object for testing
	 * @return
	 */
	public static DataObject buildDummyDataObject() {
		DataObjectBuilder dataObjectBuilder =
		    	 new DataObjectBuilder("genericsensordata", "mirror:application:watchit:genericsensordata");
		Date date = new Date();
				dataObjectBuilder.addCDTCreationInfo(date, "admin", "mirror:application:watchit:genericsensordata");

				CDMDataBuilder cdmDataBuilder = new CDMDataBuilder(CDMVersion.CDM_0_1);
				cdmDataBuilder.setPublisher("admin");
				cdmDataBuilder.setTimestamp(date.toGMTString());
				CDMData cdmData = cdmDataBuilder.build();
				dataObjectBuilder.setCDMData(cdmData);
				
		    	 Map <String, String> attributes = new HashMap<String, String>();
		    	 
		    	 attributes.put("latitude", "63.381679");
		    	 attributes.put("longitude", "10.415039");
		    	 dataObjectBuilder.addElement("location", attributes, "", true);
		    	 
		    	 attributes.clear();
		    	 
		    	 attributes.put("type", "test");
		    	 attributes.put("unit", "text");
		    	 dataObjectBuilder.addElement("value", attributes , "Hello world", true);
		    	 
		    	 
		    
		    	 
		    	 DataObject dataObject = dataObjectBuilder.build();
		    	 return dataObject;
	}
	
	
	
}
