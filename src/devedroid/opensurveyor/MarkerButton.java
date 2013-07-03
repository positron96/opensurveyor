package devedroid.opensurveyor;

import java.io.IOException;
import java.io.InputStream;

import devedroid.opensurveyor.data.Marker;
import devedroid.opensurveyor.data.POI;
import devedroid.opensurveyor.data.SessionManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;
import android.view.Gravity;
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
		if(prs.icon!=null)
			try{
				AssetManager assetManager = context.getAssets();
			    InputStream istr = assetManager.open(prs.icon);
			    Drawable dr = Drawable.createFromStream(istr, null);
			    //Utils.logd("MarkerButton", dr.toString());
			    //setCompoundDrawables(null, dr, null, null);
			    setGravity(Gravity.CENTER);
			    setPadding(getPaddingLeft(), getPaddingTop()+30, getPaddingRight(), getPaddingBottom() );
			    setCompoundDrawablePadding(-10);
				setCompoundDrawablesWithIntrinsicBounds(null, dr, null, null);
				
			} catch(IOException e) {
				Utils.loge("MarkerButton", e);
			}

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
