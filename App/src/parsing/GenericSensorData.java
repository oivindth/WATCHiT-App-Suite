package parsing;

/**
 * Copyrigth 2013 ¯ivind Thorvaldsen
 * This file is part of Reflection App

    Reflection App is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * author: ¯ivind Thorvaldsen
 */
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

	@Root(name = "genericsensordata")
	@Namespace(reference="mirror:application:watchit:genericsensordata")
	public class GenericSensorData {
		
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
		private Value value;

		
		public CreationInfo getCreationInfo() {
			return creationInfo;
		}
		
		
		public GenericSensorData() {
			
		}
		
		public GenericSensorData(String publisher, String modelVersion,
				String cdmVersion, String id, String timestamp,
				String schemaLocation, @Element(name="location")Location location, @Element(name="value")Value value) {
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

		public GenericSensorData(Location location, Value value) {
			this.location = location;
			this.value = value;
		}
		
	
		public Location getLocation() {
			return location;
		}

		public void setLocation(Location location) {
			this.location = location;
		}

		public Value getValue() {
			return value;
		}

		public void setValue(Value value) {
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