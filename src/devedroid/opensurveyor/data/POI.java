package devedroid.opensurveyor.data;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import devedroid.opensurveyor.BasePreset;
import devedroid.opensurveyor.POIPreset;

import android.location.Location;

public class POI extends Marker {
	
	protected String type;
	
	protected Map<PropertyDefinition,String> props;
	
	public POI(POIPreset prs) {
		super(prs);
		this.type = prs.type;
		props = new HashMap<PropertyDefinition, String>();
	}
	private POI(String type) {
		this(null, System.currentTimeMillis(), type);
	}
	private POI(Location location, String type) {
		this(location, System.currentTimeMillis(), type);
	}
	
	private POI(Location location, long timeStamp, String type) {
		super(location, timeStamp);
		setType(type);
		props = new HashMap<PropertyDefinition, String>();
	}
	
	@Override
	public void addProperty(PropertyDefinition key, String value) {
		props.put(key,value);
	}
	
	@Override
	public String getProperty(PropertyDefinition name) {
		return props.get(name);
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
		w.append("\t\t<poi type=\"").append(type).append("\"/>\n");
		if(generatedText!=null)
			w.append("\t\t<text generated=\"yes\">").append(generatedText).append("</text>\n");
		w.append(formatProperties());
	}
	
	private String formatProperties() {
		StringBuilder s = new StringBuilder();
		for(Map.Entry<PropertyDefinition, String> e: props.entrySet()) {
			s.append("\t\t<property k=\""+e.getKey().key+"\" v=\""+e.getValue()+"\" />\n");
		}
		return s.toString();
	}
	
	@Override
	public String getDesc() {
		String misc = "";//(hasDirection() ? " "+dir.dirString() : "");
		//StringBuilder misc = new StringBuilder();
		if(!props.isEmpty()) {
			misc += " (";
			for(PropertyDefinition p: props.keySet() ) {
				if(p.equals(BasePreset.PROP_LINEAR)) {
					misc += props.get(p).equals("start") ? "start" : "end";
				} else 
					misc += p.title.toLowerCase()+": "+ p.formatValue(props.get(p));
				misc += "; ";
			}
			misc = misc.substring(0, misc.length()-2) +")";
		}
		return (generatedText==null ? type : generatedText) +misc;
	}
	@Override
	public boolean containsExternals() {
		return false;
	}

	
}
