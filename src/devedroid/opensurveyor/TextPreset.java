package devedroid.opensurveyor;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;
import devedroid.opensurveyor.BasePreset.ButtonTouchListener;
import devedroid.opensurveyor.data.POI;
import devedroid.opensurveyor.data.PropertyDefinition;
import devedroid.opensurveyor.data.SessionManager;
import devedroid.opensurveyor.data.TextMarker;

public class TextPreset extends BasePreset {
	
	public TextPreset() {
		super("Text note");
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
	private static final ArrayList<PropertyDefinition> PROPS = new ArrayList<PropertyDefinition>(1); 
	static {
		PROPS.add(PropertyDefinition.stringProperty(PROP_NAME, PROP_NAME) );
	}

	@Override
	public List<PropertyDefinition> getProperties() {
		return PROPS;
	}

}
