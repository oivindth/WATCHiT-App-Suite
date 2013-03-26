package listeners;

public interface LayersChangeListener {
	
	/**
	 * Implement this interface from a fragment in an activity to change the data that shall be viewed.
	 * @param showPersonsFound
	 * @param showMoods
	 * @param notes
	 * @param onlyShowCurrentLocalUser
	 */
	public void onLayersChanged(boolean showPersonsFound, boolean showMoods, boolean notes, boolean onlyShowCurrentLocalUserDataPoints);
	
}
