package enums;

/**
 * Not in use at the moment
 * @author oivindth
 *
 */
public enum ValueType {
	
	
	MOOD, PERSON, MOOD_SAD, MOOD_HAPPY, MOOD_NEUTRAL, NOTES;
	
	
	public static ValueType getValue(String value) {
		
		if (value.equals("I rescued someone")) return PERSON;
		
		if (value.equals("I'm happy")) return MOOD_HAPPY;
		
		if (value.equals("I'm sad")) return MOOD_SAD;
		
		if (value.equals("I'm so and so")) return MOOD_NEUTRAL;
		
		return NOTES;
		
	}
	
	
}
