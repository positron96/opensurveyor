package devedroid.opensurveyor;

import devedroid.opensurveyor.data.Marker;
import devedroid.opensurveyor.data.POI;
import devedroid.opensurveyor.data.SessionManager;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;

public class MarkerButton extends Button implements OnClickListener {
	private Preset prs = null;
	private SessionManager sm;

	public MarkerButton(Context context) {
		super(context);
	}
	
	public MarkerButton(Context context, AttributeSet set) {
		super(context, set);
	}
	
	public MarkerButton(Context context, AttributeSet set, int defStyle) {
		super(context, set, defStyle);
	}
	
	public MarkerButton(Context context, Preset prs, SessionManager sm) {
		super(context);
		this.prs=prs;
		this.sm=sm;
		
		setText(prs.title);
		
		setOnClickListener(this);
	}

	
	public void setDirectionEnabled(boolean enabled) {
		
	}

	@Override
	public void onClick(View v) {
		Marker mm = Marker.createFromPreset(prs);
		
		sm.addMarker(mm);
	}

}
