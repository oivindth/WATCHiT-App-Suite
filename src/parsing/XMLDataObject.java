package parsing;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public class XMLDataObject {
	
	@Attribute(name = "xmlns")
	protected String xmlns;
	@Attribute(name = "id")
	protected String id;
	@Attribute(name = "timestamp")
	protected String timestamp;
	
	
	public XMLDataObject (String xmlns, String id, String timestamp) {
		this.xmlns = xmlns;
		this.id = id;
		this.timestamp = timestamp;
	}
	public XMLDataObject () {
		super();
	}
	
	
	public String getXMLns () {
		return xmlns;
	}
	public String getId () {
		return id;
	}
	public String getTimeStamp () {
		return timestamp;
	}
	
}
