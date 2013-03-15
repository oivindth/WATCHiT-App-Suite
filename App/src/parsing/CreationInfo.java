package parsing;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class CreationInfo {
	
	@Element(name = "date")
	private String date;
	@Element(name = "person")
	private String person;

	public CreationInfo() {
		
	}
	public CreationInfo(String date, String person) {
		this.date = date;
		this.person = person;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}
	
}
