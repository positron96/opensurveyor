package devedroid.opensurveyor.data;

import java.io.IOException;
import java.io.Writer;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import android.content.res.Resources;
import android.location.Location;
import devedroid.opensurveyor.presets.TextPreset;

public class TextMarker extends Marker {
	
	protected  String text;
	
	public TextMarker(TextPreset t) {
		super(t);
	}
	
	private TextMarker(Location location, long timeStamp, String text) {
		super(location, timeStamp);
		setText(text);
	}
	
	public TextMarker(IGeoPoint location, String text) {
		super(System.currentTimeMillis() );
		setLocation(location);
		setText(text);
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	//TODO: escape text part correctly
	@Override
	protected void writeDataPart(Writer w) throws IOException {
		if(text==null || text.length()==0) 
			w.append("\t\t<text generated=\"yes\">").append(generatedText).append("</text>\n");
		else
			w.append("\t\t<text>").append(text).append("</text>\n");
	}
	@Override
	public String getDesc(Resources res) {
		String v = (text==null || text.length()==0) ? generatedText : text; 
		return v;// + (hasDirection() ? " "+dir.dirString() : "");
	}

	@Override
	public void addProperty(PropertyDefinition key, String value) {
		//Utils.logd(this, "adding "+key+"="+value);
		if(TextPreset.PROP_VALUE.equals(key)) text=value;
		else throw new IllegalArgumentException("TextMarker contains only text property (\""+key+"\" requested)");
	}

	@Override
	public String getProperty(PropertyDefinition name) {
		if(TextPreset.PROP_VALUE.equals(name)) return text;
		else throw new IllegalArgumentException("TextMarker contains only text property (\""+name+"\" requested)");
	}

}
