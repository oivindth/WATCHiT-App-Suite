package parsing;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "trainingprocedure")
public class TrainingProcedure {
	
	
	@Element(name = "Time")
	private String time;
	@Element(name ="step1")
	private String step;
	
	@Attribute(name = "xmlns")
	private String xmlns;
	@Attribute(name = "id")
	private String id;
	@Attribute(name = "timestamp")
	private String timestamp;
	

	
	public TrainingProcedure () {
	}
	
	public TrainingProcedure (String time, String step, String xmlns, String id, String timestamp) {
		this.step = step;
		this.time = time;
		this.xmlns = xmlns;
		this.id = id;
		this.timestamp = timestamp;
	}
	
	public String getTime() {
		return time;
	}
	
	public String getText () {
		return step;
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
