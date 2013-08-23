package devedroid.opensurveyor.data;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Locale;

import org.osmdroid.util.GeoPoint;

public class LocationData implements Serializable{
	
	public final double lat, lon, heading, alt;
	
	public LocationData(LocationData loc) {
		lat = loc.lat;
		lon = loc.lon;
		heading = loc.heading;
		alt = loc.alt;
		
	}
	
	public LocationData(android.location.Location loc) {
		lat = loc.getLatitude();
		lon = loc.getLongitude();
		if(loc.hasBearing())
			heading = loc.getBearing();
		else
			heading = Double.NaN;
		if(loc.hasAltitude())
			alt = loc.getAltitude();
		else
			alt = Double.NaN;
	}
	
	public boolean hasHeading() { return !Double.isNaN(heading); }
	
	public boolean hasAltitude() { return !Double.isNaN(alt); }
	
	
	public void writeLocationTag(Writer w) throws IOException {
		w.append(String.format(Locale.US, 
				"\t\t<position lat=\"%.5f\" lon=\"%.4f\" ", 
				lat, lon));
		if(hasHeading()) w.append(String.format(Locale.US, 
				"heading=\"%.2f\" ",heading));
		if(hasAltitude()) w.append(String.format(Locale.US, 
				"alt=\"%.2f\" ",alt));
		w.append("/>\n");
	}
	
	public GeoPoint getGeoPoint() {
		GeoPoint res = new GeoPoint(lat,lon);
		if(hasAltitude()) res.setAltitude((int) alt);
		return res;
	}

}
