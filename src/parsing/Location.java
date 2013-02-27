package parsing;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class Location {
	
	@Attribute(name = "latitude")
	private String latitude;
	@Attribute(name = "longitude")
	private String longitude;

	
	public Location(String latitude, String longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}


	public String getLatitude() {
		return latitude;
	}


	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}


	public String getLongitude() {
		return longitude;
	}


	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}


	
	
	
}
