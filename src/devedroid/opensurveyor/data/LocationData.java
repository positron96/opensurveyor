package devedroid.opensurveyor.data;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Locale;

public class LocationData implements Serializable{
	
	public final double lat, lon, heading;
	
	public LocationData(LocationData loc) {
		lat = loc.lat;
		lon = loc.lon;
		heading = loc.heading;
		
	}
	
	public LocationData(android.location.Location loc) {
		lat = loc.getLatitude();
		lon = loc.getLongitude();
		if(loc.hasBearing())
			heading = loc.getBearing();
		else
			heading = Double.NaN;
	}
	
	public boolean hasHeading() { return !Double.isNaN(heading); }
	
	
	public void writeLocationTag(Writer w) throws IOException {
		w.append(String.format(Locale.US, 
				"\t\t<position lat=\"%.5f\" lon=\"%.4f\" ", 
				lat, lon));
		if(hasHeading()) w.append(String.format(Locale.US, 
				"heading=\"%.2f\" ",heading));
		w.append("/>\n");
	}

}
