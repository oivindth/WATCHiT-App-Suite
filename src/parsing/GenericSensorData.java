package parsing;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;


	@Root(name = "genericsensordata")
	@Namespace(reference="mirror:application:watchit:genericsensordata")
	public class GenericSensorData {
		
		@Element
		private Location location;
		
		@Element
		private Value value;

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
		
		
		
		
	}
	
	
	

