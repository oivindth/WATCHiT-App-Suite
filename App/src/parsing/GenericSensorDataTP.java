package parsing;

/**
 * author: ¯ivind Thorvaldsen
 */
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;


	@Root(name = "genericsensordata")
	@Namespace(reference="mirror:application:watchit:genericsensordata")
	public class GenericSensorDataTP {
		
		@Attribute
		private String publisher;
		@Attribute
		private String modelVersion;
		@Attribute
		private String cdmVersion;
		@Attribute
		private String id;
		@Attribute
		private String timestamp;

		@Attribute
		private String schemaLocation;
		
		@Element
		private CreationInfo creationInfo;
		
		@Element
		private Location location;
		
		@Element
		private ValueTP value;

		
		public CreationInfo getCreationInfo() {
			return creationInfo;
		}
	
		public GenericSensorDataTP() {
			
		}
	
		
		public GenericSensorDataTP(String publisher, String modelVersion,
				String cdmVersion, String id, String timestamp,
				String schemaLocation, @Element(name="location")Location location, @Element(name="value")ValueTP value) {
			super();
			this.publisher = publisher;
			this.modelVersion = modelVersion;
			this.cdmVersion = cdmVersion;
			this.id = id;
			this.timestamp = timestamp;
			this.schemaLocation = schemaLocation;
			this.location = location;
			this.value = value;
		}

		public GenericSensorDataTP(Location location, ValueTP value) {
			this.location = location;
			this.value = value;
		}
		
	
		

		public Location getLocation() {
			return location;
		}

		public void setLocation(Location location) {
			this.location = location;
		}

		public ValueTP getValue() {
			return value;
		}

		public void setValueTP(ValueTP value) {
			this.value = value;
		}

		public String getPublisher() {
			return publisher;
		}

		public void setPublisher(String publisher) {
			this.publisher = publisher;
		}

		public String getModelVersion() {
			return modelVersion;
		}

		public void setModelVersion(String modelVersion) {
			this.modelVersion = modelVersion;
		}

		public String getCdmVersion() {
			return cdmVersion;
		}

		public void setCdmVersion(String cdmVersion) {
			this.cdmVersion = cdmVersion;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}

		public String getSchemaLocation() {
			return schemaLocation;
		}

		public void setSchemaLocation(String schemaLocation) {
			this.schemaLocation = schemaLocation;
		}
		
		
		
		
	}
