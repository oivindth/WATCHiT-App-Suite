package parsing;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root(name = "trainingprocedure")
@Namespace(reference="mirror:application:watchIt_Reflection_App:trainingprocedure")
public class TrainingProcedure {
	
	
	@Element(name = "Time", required = false)
	private String time;
	@Element(name ="step1", required = false)
	private String step;
	
	@Attribute(name = "id")
	private String id;
	@Attribute(name = "timestamp")
	private String timestamp;
	

	
	public TrainingProcedure () {
	}
	
	
	public TrainingProcedure (String time, String step, String id, String timestamp) {
		this.step = step;
		this.time = time;
		this.id = id;
		this.timestamp = timestamp;
	}
	
	public String getTime() {
		return time;
	}
	
	public String getText () {
		return step;
	}

	public String getId () {
		return id;
	}
	public String getTimeStamp () {
		return timestamp;
	}
	
	public String toString () {
		
		String s =  " id: " + id + " timestamp: " + timestamp + " Time: " + time + " step1 " + step;
		return s;
		
	}
	
}
