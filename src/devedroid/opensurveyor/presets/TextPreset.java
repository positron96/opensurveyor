package devedroid.opensurveyor.presets;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import devedroid.opensurveyor.R;
import devedroid.opensurveyor.data.PropertyDefinition;
import devedroid.opensurveyor.data.SessionManager;
import devedroid.opensurveyor.data.TextMarker;

public class TextPreset extends BasePreset {
	
	public TextPreset(Resources res) {
		super( res.getString(R.string.preset_text));
	}

	@Override
	public boolean isToggleButton() {
		return false;
	}

	@Override
	public boolean isDirected() {
		return true;
	}

	@Override
	public Button createButton(Context context, final SessionManager sm) {
		final Button res;
		res = new Button(context);
		res.setTag(this);
		res.setText(title);
		final ButtonTouchListener btl = new ButtonTouchListener(res);
		if (isDirected())
			res.setOnTouchListener(btl);
		
		res.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextMarker mm = new TextMarker(TextPreset.this); 
				if (isDirected())
					mm.setDirection( btl.dir );
				sm.addMarker(mm);
			}
		});
		return res;
	}
	
	public static final String PROP_NAME = "Note";
	public static final String PROP_KEY = "note";
	public static final PropertyDefinition PROP_VALUE = PropertyDefinition.stringProperty(PROP_NAME, PROP_KEY) ; 
	private static final ArrayList<PropertyDefinition> PROPS = new ArrayList<PropertyDefinition>(1); 
	static {
		PROPS.add(PROP_VALUE);
	}

	@Override
	public List<PropertyDefinition> getProperties() {
		return PROPS;
	}

}
