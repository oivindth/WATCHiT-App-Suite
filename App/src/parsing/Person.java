package parsing;

/**
 * author: ¯ivind Thorvaldsen
 */
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

public class Person {
	
	@Attribute(name = "cdt:person")
	private String person;
	@Text
	private String text;
	
	public Person(String person, String text) {
		this.person = person;
		this.text = text;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	
}
