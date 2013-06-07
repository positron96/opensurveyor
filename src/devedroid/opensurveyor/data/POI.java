package devedroid.opensurveyor.data;


import java.io.IOException;
import java.io.Writer;

import android.location.*;

/** A basic POI class for storing POI's time, title (if any), LatLon location (if any).
 * Subclasses should implement extra functionality. */
public abstract class POI {
	
	protected  Location location;
	
	protected  long timeStamp;
	
	protected  String title;
	
	public POI() {
		this(null, System.currentTimeMillis(), "");
	}
	
	public POI(String title) {
		this(null, System.currentTimeMillis(), title);
	}
	
	public POI(long timeStamp, String title) {
		this(null, timeStamp, title);
	}
	
	public POI(Location location, long timeStamp, String title) {
		this.setLocation(location);
		this.setTimestamp(timeStamp);
		this.setTitle(title);
	}

	public boolean hasLocation() {
		return location != null;
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public long getTimestamp() {
		return timeStamp;
	}

	public void setTimestamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public boolean hasTitle() {
		return title != null;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
	public abstract void writeXML(Writer w)  throws IOException;


}
