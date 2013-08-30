package devedroid.opensurveyor.data;


import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Date;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import android.content.res.Resources;
import android.location.Location;
import devedroid.opensurveyor.Utils;
import devedroid.opensurveyor.presets.BasePreset;

/** A basic POI class for storing POI's time and type(if null then it's supposed to be a text note), LatLon location (if any).
 * Subclasses should implement extra functionality. */
public abstract class Marker implements Serializable {
	
	protected LocationData location;
	
	protected String generatedText;
	
	protected transient BasePreset prs = null;
	
	public enum Direction {
		LEFT, RIGHT, FRONT, BACK;
		public String getXMLName() {
			return this.toString().toLowerCase();
		}
		public String dirString() {
			switch(this) {
				case LEFT: return "to the left";
				case RIGHT: return "to the right";
				case FRONT: return "in front";
				case BACK: return "behind";
				default: return "unknown";
			}
		}
		public float getAngle() {
			switch(this) {
				case LEFT: return 180;
				case RIGHT: return 0;
				case FRONT: return -90;
				case BACK: return 90;
				default: return 0;
			}
		}
	}
	
	protected  Direction dir = null;
	
	protected  long timeStamp;
	
	protected Marker(BasePreset prs) {
		this(null, System.currentTimeMillis());
		this.prs = prs;
		this.generatedText = prs.title;
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
	public LocationData getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		if(location==null) 
			this.location = null; 
		else
			this.location = new LocationData(location);
	}
	
	public void setLocation(LocationData location) {
		if(location==null) 
			this.location = null; 
		else
			this.location = new LocationData(location);
	}
	public void setLocation(IGeoPoint location) {
		if(location==null) 
			this.location = null; 
		else
			this.location = new LocationData(location);
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
	public void setDirection(Direction dir) {
		this.dir = dir;
	}
	
	public boolean hasHeading() {
		return location!=null && location.hasHeading();
	}	
	public double getHeading() {
		return location.heading;
	}
	
	public abstract String getDesc(Resources res);
	
	protected abstract void writeDataPart(Writer w) throws IOException;
	
	public void writeXML(Writer w)  throws IOException {
		w.append("\t<point time=\"").append(Utils.formatISOTime(new Date(getTimestamp()))).append("\" ");
		if(hasDirection()) 
			w.append("dir=\"").append(getDirection().getXMLName()).append("\" ");
		w.append(">\n");
		if(hasLocation())	location.writeLocationTag(w);
		writeDataPart(w);
		
		w.append("\t</point>\n");
	}

	public BasePreset getPreset() {
		return prs;
	}
	
	public abstract void addProperty(PropertyDefinition key, String value) ;
	
	public abstract String getProperty(PropertyDefinition name);
	

}
