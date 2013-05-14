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
	
	@Attribute
	private int id;
	

	
	public Step() {
	}
	
	public Step(String name, int id) {
		this.name = name;
		this.id = id;
	}
	public Step(String name) {
		this.name = name;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	


	
}
