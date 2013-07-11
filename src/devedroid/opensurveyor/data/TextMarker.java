package devedroid.opensurveyor.data;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import devedroid.opensurveyor.TextPreset;
import devedroid.opensurveyor.Utils;

import android.location.Location;

public class TextMarker extends Marker {
	
	protected  String text;
	
	public TextMarker(TextPreset t) {
		super(t);
	}
	
	private TextMarker(Location location, long timeStamp, String text) {
		super(location, timeStamp);
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
		w.append("\t\t<text>").append(text).append("</text>\n");
		
	}
	@Override
	public String getDesc() {
		if(text==null || text.length()==0) return prs.title;
		return text;
	}

	@Override
	public void addProperty(String key, String value) {
		Utils.logd(this, "adding "+key+"="+value);
		if(TextPreset.PROP_NAME.equals(key)) text=value;
		else throw new IllegalArgumentException("TextMarker contains only text property (\""+key+"\" requested)");
	}

	@Override
	public String getProperty(String name) {
		if(TextPreset.PROP_NAME.equals(name)) return text;
		else throw new IllegalArgumentException("TextMarker contains only text property (\""+name+"\" requested)");
	}

}
