package devedroid.opensurveyor;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;
import devedroid.opensurveyor.data.POI;
import devedroid.opensurveyor.data.SessionManager;
import devedroid.opensurveyor.presets.POIPreset;

public class MarkerButton extends ToggleButton implements OnClickListener {
	private POIPreset prs = null;
	private SessionManager sm;
	
	private boolean toggle = false;
	private boolean directed = false;

	public MarkerButton(Context context) {
		super(context);
	}
	
	public MarkerButton(Context context, AttributeSet set) {
		super(context, set);
	}
	
	public MarkerButton(Context context, AttributeSet set, int defStyle) {
		super(context, set, defStyle);
	}
	
	public MarkerButton(Context context, POIPreset prs, SessionManager sm) {
		super(context);
		this.prs=prs;
		this.sm=sm;
		
		toggle = prs.isToggleButton();
		directed = prs.isDirected();
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

	public void setToggleEnabled(boolean enabled) {
		
	}
	public void setDirectionEnabled(boolean enabled) {
		
	}

	@Override
	public void onClick(View v) {
		
		POI mm = new POI(prs);//Marker.createPOIFromPreset(prs);
		if( toggle) {
			throw new UnsupportedOperationException();
			//mm.addProperty("linear", !isPressed() ? "start" : "end");
			
			//postDelayed(new Runnable() {public void run() {
			//setPressed( true );} }, 400);
			

			//setFocusable( true );
		}
		sm.addMarker(mm);
		
	}

}
