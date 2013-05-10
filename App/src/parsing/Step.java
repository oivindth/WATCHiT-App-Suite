package parsing;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root
public class Step {
	
	//@Text
	//private String time;
	
	@Element
	private String name;
	
	@Element
	private String time;

	@Attribute
	private int id;
	
	public Step(String n) {
		//trololol
	}
	
	public Step() {
	}
	
	public Step(String name, String time, int id) {
		this.name = name;
		this.time = time;
		this.id = id;
	}
	
	public String getTime() {
		return time;
	}
	public String getName() {
		return name;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setID(int id) {
		this.id = id;
	}

	
}
