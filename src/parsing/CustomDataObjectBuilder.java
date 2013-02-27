package parsing;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.simpleframework.xml.Serializer;
import de.imc.mirror.sdk.android.CDMData;
import de.imc.mirror.sdk.android.CDMDataBuilder;
import de.imc.mirror.sdk.android.DataObject;
import de.imc.mirror.sdk.android.DataObjectBuilder;
import de.imc.mirror.sdk.cdm.CDMVersion;

public class CustomDataObjectBuilder {
	

	
	public static DataObject buildDataObjectFromSimpleXMl (GenericSensorData genericSensorData, String user) {
	
		
		DataObjectBuilder dataObjectBuilder =
		    	 new DataObjectBuilder("genericsensordata", "mirror:application:watchit:genericsensordata");
		Date date = new Date();
		dataObjectBuilder.addCDTCreationInfo(date, user, "mirror:application:watchit:genericsensordata");
		CDMDataBuilder cdmDataBuilder = new CDMDataBuilder(CDMVersion.CDM_0_1);
		cdmDataBuilder.setPublisher(user);
		cdmDataBuilder.setTimestamp(date.toGMTString());
		CDMData cdmData = cdmDataBuilder.build();
		dataObjectBuilder.setCDMData(cdmData);
		
		 Map <String, String> attributes = new HashMap<String, String>();
   	 
	   	 attributes.put("latitude", genericSensorData.getLocation().getLatitude());
	   	 attributes.put("longitude", genericSensorData.getLocation().getLongitude());
	   	 dataObjectBuilder.addElement("location", attributes, "", true);
	   	 
	   	 attributes.clear();
	   	 
	   	 attributes.put("type", genericSensorData.getValue().getType());
	   	 attributes.put("unit", genericSensorData.getValue().getUnit());
	   	 dataObjectBuilder.addElement("value", attributes , genericSensorData.getValue().getText(), true);

	   	 //dataObjectBuilder.addCDTCreationInfo(date, connectionHandler.getCurrentUser().getBareJID(), application);
	   	 
	   	 DataObject dataObject = dataObjectBuilder.build();
	   	 return dataObject;
		
	}
	
	/**
	 * DeSerialize(read) xml from a string and return a simpleXML object
	 */
	public static GenericSensorData DeSerialize (String xml) {
		Serializer serializer = null;
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
