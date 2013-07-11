package devedroid.opensurveyor.data;


import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import devedroid.opensurveyor.BasePreset;
import devedroid.opensurveyor.POIPreset;
import devedroid.opensurveyor.TextPreset;
import devedroid.opensurveyor.Utils;

import android.location.*;

/** A basic POI class for storing POI's time and type(if null then it's supposed to be a text note), LatLon location (if any).
 * Subclasses should implement extra functionality. */
public abstract class Marker {
	
	protected  Location location;
	
	protected volatile BasePreset prs = null;
	
	public enum Direction {
		LEFT, RIGHT, FRONT, BACK;
		public String getXMLName() {
			return this.toString().toLowerCase();
		}
	}
	
	protected  Direction dir = null;
	
	protected  long timeStamp;
	
	protected Marker(BasePreset prs) {
		this(null, System.currentTimeMillis());
		this.prs = prs;
	}	
	
	public Marker() {
		this(null, System.currentTimeMillis());
	}
	
	public Marker(long timeStamp) {
		this(null, timeStamp);
	}
	
	public Marker(Location location, long timeStamp) {
		this.setLocation(location);
		this.setTimestamp(timeStamp);
	}

	public boolean hasLocation() {
		return location != null;
	}	
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		if(location==null) 
			this.location = null; 
		else
			this.location = new Location(location);
	}

	public long getTimestamp() {
		return timeStamp;
	}
	public void setTimestamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public boolean hasDirection() {
		return dir != null;
	}	
	public Direction getDirection() {
		return dir;
	}
	
	public boolean hasHeading() {
		return location!=null && location.hasBearing();
	}	
	public float getHeading() {
		return location.getBearing();
	}
	
	public abstract String getDesc();
	
	protected abstract void writeDataPart(Writer w) throws IOException;
	
	public void writeXML(Writer w)  throws IOException {
		w.append("\t<point time=\"").append(Utils.formatISOTime(new Date(getTimestamp()))).append("\" ");
		if(hasDirection()) 
			w.append("dir=\"").append(getDirection().getXMLName()).append("\" ");
		w.append(">\n");
		if(hasLocation())
			w.append(formatLocationTag()).append("\n");
		writeDataPart(w);
		
		w.append("\t</point>\n");
	}
	
	private String formatLocationTag() {
		StringBuilder s = new StringBuilder();
		s.append(String.format("\t\t<position lat=\"%.5f\" lon=\"%.4f\" ", location.getLatitude(), location.getLongitude()));
		if(hasHeading()) s.append(String.format("heading=\"%.2f\" ", getHeading()));
		s.append("/>");
		return s.toString();
	}


	public static Marker createMarkerFromPreset(BasePreset prs) {
		Marker m;
		if(prs instanceof POIPreset)
			m = new POI((POIPreset)prs);
		else
			m = new TextMarker((TextPreset)prs);
		
		return m;
	}

	public BasePreset getPreset() {
		return prs;
	}
	
	public abstract void addProperty(String key, String value) ;
	
	public abstract String getProperty(String name);

}
