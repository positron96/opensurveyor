package devedroid.opensurveyor;

import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;
import devedroid.opensurveyor.data.POI;
import devedroid.opensurveyor.data.SessionManager;

public abstract class BasePreset {
	
	public final String title;

	public BasePreset(String title) {
		this.title = title;
	}

	public abstract boolean isToggleButton() ;

	public abstract boolean isDirected() ;

	public abstract Button createButton(Context context, final SessionManager sm);
	
	public abstract List<String> getPropertyTitles();

}