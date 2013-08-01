package devedroid.opensurveyor.presets;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;
import devedroid.opensurveyor.data.POI;
import devedroid.opensurveyor.data.PropertyDefinition;
import devedroid.opensurveyor.data.SessionManager;

public class POIPreset extends BasePreset {

	public final String type;
	public final String icon;
	private boolean toggle = false;
	private boolean directed = false;

	private final List<PropertyDefinition> props;

	public POIPreset(String title, String type, String icon, boolean toggle) {
		super(title);
		if (type == null)
			this.type = title.toLowerCase();
		else
			this.type = type;
		this.icon = icon;
		this.props = new ArrayList<PropertyDefinition>();
		this.toggle = toggle;
	}

	public POIPreset(String title, String type, String icon) {
		this(title, type, icon, false);
	}

	public POIPreset(String title, String type) {
		this(title, type, null, false);
	}

	public POIPreset(String title) {
		this(title, null, null, false);
	}

	public void addProperty(PropertyDefinition p) {
		props.add(p);
	}

	static float buttonTextSize = Float.NaN;

	public void setToggleButton(boolean v) {
		toggle = v;
	}

	@Override
	public boolean isToggleButton() {
		return toggle;
	}

	public void setDirected(boolean v) {
		directed = v;
	}

	@Override
	public boolean isDirected() {
		return directed;
	}
	
	//public Marker createMarker() {
	//}

	@Override
	public Button createButton(Context context, final SessionManager sm) {
		final Button res;
		if (isToggleButton()) {
			final ToggleButton tres = new ToggleButton(context);
			tres.setTextOn(title);
			tres.setTextOff(title);
			if (Float.isNaN(buttonTextSize))
				buttonTextSize = new Button(context).getTextSize();
			tres.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonTextSize);
			res = tres;
		} else {
			res = new Button(context);
		}
		res.setTag(this);
		res.setText(title);
		final ButtonTouchListener btl = new ButtonTouchListener(res);
		if (isDirected())
			res.setOnTouchListener(btl);
		res.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				POI mm = new POI(POIPreset.this);
				if (isToggleButton())
					mm.addProperty(PROP_LINEAR,
							((ToggleButton) res).isChecked() ? "start" : "end");
				if (isDirected())
					mm.setDirection( btl.dir );
				sm.addMarker(mm);

			}
		});
		// if(icon!=null) {
		// int rid = context.getResources().getIdentifier("marker_"+icon,
		// "drawable",
		// context.getApplicationInfo().packageName);
		// if(rid != 0) {
		// Drawable dr = context.getResources().getDrawable(rid);
		// //Utils.logd("MarkerButton", dr.toString());
		// //setCompoundDrawables(null, dr, null, null);
		// //res.setGravity(Gravity.CENTER);
		// //res.setPadding(res.getPaddingLeft(), res.getPaddingTop()+30,
		// res.getPaddingRight(), res.getPaddingBottom() );
		// //res.setCompoundDrawablePadding(-10);
		// res.setCompoundDrawablesWithIntrinsicBounds(null, dr, null, null);
		// }
		// }

		return res;
	}
	
	@Override
	public List<PropertyDefinition> getProperties() {
		return props;
	}

}
