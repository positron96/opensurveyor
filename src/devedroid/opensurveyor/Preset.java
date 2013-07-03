package devedroid.opensurveyor;

import java.util.ArrayList;
import java.util.List;

public class Preset {
	
	public final String title;
	public final String type;
	public final String icon;
	
	public final List<String> propsNames;
	
	public Preset(String title, String type, String icon) {
		this.title = title;
		if(type==null) 
			this.type = title.toLowerCase();
		else
			this.type = type;
		this.icon = icon;
		this.propsNames = new ArrayList<String>();
	}
	
	public Preset(String title, String type) {
		this.title = title;
		this.type = type;
		this.icon = null;
		this.propsNames = new ArrayList<String>();
	}
	
	public Preset(String title) {
		this.title = title;
		this.type = title.toLowerCase();
		this.icon = null;
		this.propsNames = new ArrayList<String>();
	}
	
	public void addProperty(String p) {
		propsNames.add(p);
	}

}
