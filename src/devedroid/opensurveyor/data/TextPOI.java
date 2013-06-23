package devedroid.opensurveyor.data;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import devedroid.opensurveyor.Utils;

import android.location.Location;

public class TextPOI extends POI {
	
	private String desc;

	public TextPOI(String title, String desc) {
		super(title);
		this.setDesc(desc);
	}

	public TextPOI(long timeStamp, String title, String desc) {
		super(timeStamp, title);
		this.setDesc(desc);
	}

	public TextPOI(Location location, long timeStamp, String title, String desc) {
		super(location, timeStamp, title);
		this.setDesc(desc);
	}
	
	public boolean hasDesc() {
		return desc!=null;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Override
	public void writeXML(Writer w) throws IOException {
		w.append("<text ");
		w.append("time=\""+Utils.formatISOTime( new Date(getTimestamp()))+"\" ");
		if(hasLocation()) w.append("lat=\""+location.getLatitude()+"\" lon=\""+location.getLongitude()+"\" ");
		if(hasTitle()) w.append("title=\""+title+"\" "); //TODO: escape title correctly
		if(hasDesc()) w.append("desc=\""+desc+"\" "); //TODO: same for desc
		
		w.append("/>\n");
	}

}
