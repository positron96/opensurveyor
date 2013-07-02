package devedroid.opensurveyor.data;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import android.location.Location;

public class POI extends Marker {
	
	protected  String type;
	
	protected Map<String,String> props;
	
	public POI(String type) {
		this(null, System.currentTimeMillis(), type);
	}
	public POI(Location location, String type) {
		this(location, System.currentTimeMillis(), type);
	}
	
	public POI(Location location, long timeStamp, String type) {
		super(location, timeStamp);
		setType(type);
		props = new HashMap<String, String>();
	}
	

	public boolean isPOI() {
		return type != null;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	protected void writeDataPart(Writer w) throws IOException {
		w.append(formatProperties());
		
	}
	
	private String formatProperties() {
		StringBuilder s = new StringBuilder();
		for(Map.Entry<String, String> e: props.entrySet()) {
			s.append("\t\t<property k=\""+e.getKey()+"\" v=\""+e.getValue()+"\" />\n");
		}
		return s.toString();
	}
	@Override
	public String getDesc() {
		return type;
	}


}
