package interfaces;

public interface LocationChangedListener {
	
	/**
	 * 
	 * @param lat
	 * @param lng
	 */
	public void onLocationChanged(double lat, double lng);
	
}
