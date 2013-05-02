package parsing;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root
public class Step {
	
	@Text
	private String time;
	
	
	public Step() {
		
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Step(String time) {
		this.time = time;
	}
	
}
