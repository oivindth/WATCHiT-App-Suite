package enums;

/**
 * Not in use at the moment
 * @author oivindth
 *
 */
public enum ValueType {
	
	
	MOOD, PERSON, SAD, HAPPY, NEUTRAL;
	
	
	public static ValueType getValue(String value) {
		
		if (value.equals("I rescued someone")) return PERSON;
		
		return MOOD;
		
	}
	
	
}
