package parsing;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;


@Root
public class Value {
	
	
	@Attribute(name = "type")
	private String type;
	@Attribute(name = "unit", required = false)
	private String unit;
	@Text
	private String text;
	
	
	public Value() {
		
	}
	
	public Value(String type, String unit, String text) {
		this.type = type;
		this.unit = unit;
		this.text = text;
		
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


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}

	
}
