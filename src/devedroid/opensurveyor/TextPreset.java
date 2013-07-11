package devedroid.opensurveyor;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;
import devedroid.opensurveyor.data.POI;
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
		res.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sm.addMarker(new TextMarker(TextPreset.this));
			}
		});
		return res;
	}
	
	public static final String PROP_NAME = "Note";
	private static final ArrayList<String> PROPS = new ArrayList<String>(1); 
	static {
		PROPS.add(PROP_NAME);
	}

	@Override
	public List<String> getPropertyNames() {
		return PROPS;
	}

}
