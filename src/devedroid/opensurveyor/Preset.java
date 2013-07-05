package devedroid.opensurveyor;

import java.util.ArrayList;
import java.util.List;

import devedroid.opensurveyor.data.Marker;
import devedroid.opensurveyor.data.POI;
import devedroid.opensurveyor.data.SessionManager;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public class Preset {
	
	public final String title;
	public final String type;
	public final String icon;
	private boolean toggle = false;
	private boolean directed = false;
	
	public final List<String> propsNames;
	
	public Preset(String title, String type, String icon, boolean toggle) {
		this.title = title;
		if(type==null) 
			this.type = title.toLowerCase();
		else
			this.type = type;
		this.icon = icon;
		this.propsNames = new ArrayList<String>();
		this.toggle = toggle;
	}
	
	public Preset(String title, String type, String icon) {
		this(title, type, icon, false);
	}
	
	public Preset(String title, String type) {
		this(title, type, null, false);
	}
	
	public Preset(String title) {
		this(title, null,null,false);
	}
	
	public void addProperty(String p) {
		propsNames.add(p);
	}
	
	public boolean isToggleButton() {
		return toggle;
	}

	public boolean isDirected() {
		return directed;
	}
	
	static float buttonTextSize=Float.NaN;
	
	public Button createButton(Context context, final SessionManager sm) {
		Button res;
		if(isToggleButton()) {
			final ToggleButton tres = new ToggleButton(context);
			tres.setTextOn(title);
			tres.setTextOff(title);
			if(Float.isNaN(buttonTextSize)) buttonTextSize = new Button(context).getTextSize();
			tres.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonTextSize) ;
			tres.setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					POI mm = Marker.createPOIFromPreset(Preset.this);
					mm.addProperty("linear", tres.isChecked() ? "start" : "end");
					sm.addMarker(mm);
				}
			});
			res = tres;
		} else {
			res = new Button(context);
			
			res.setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					POI mm = Marker.createPOIFromPreset(Preset.this);
					sm.addMarker(mm);
				}
			});
		}
		res.setTag(this);
		res.setText(title);
		
		return res;
	}

}
