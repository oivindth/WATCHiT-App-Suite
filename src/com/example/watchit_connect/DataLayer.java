package com.example.watchit_connect;

import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.SpaceHandler;

public class DataLayer {
	
	SpaceHandler spaceHandler;
	DataHandler dataHandler;
	

	public Space getSpace(String spaceId) {
		
		return spaceHandler.getSpace(spaceId);
	}
	
	
}
