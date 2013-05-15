package parsing;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class ValueTP {
	
	
	@Attribute(name = "type")
	private String type;
	@Attribute(name = "unit", required = false)
	private String unit;
	
	
	@ElementList
	private List<Step> steps;
	
	
	
	public ValueTP() {
		
	}
	
	public ValueTP(String type, String unit, List<Step> steps) {
		this.type = type;
		this.unit = unit;
		this.steps = steps;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getUnit() {
		return unit;
	}


	public void setUnit(String unit) {
		this.unit = unit;
	}


	public List<Step> getSteps() {
		return steps;
	}


	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	
}

