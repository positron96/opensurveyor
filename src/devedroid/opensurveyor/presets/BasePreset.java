package devedroid.opensurveyor.presets;

import java.util.List;

import android.content.Context;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import devedroid.opensurveyor.data.Marker;
import devedroid.opensurveyor.data.PropertyDefinition;
import devedroid.opensurveyor.data.SessionManager;

public abstract class BasePreset {
	
	public final String title;

	public BasePreset(String title) {
		this.title = title;
	}

	public abstract boolean isToggleButton() ;

	public abstract boolean isDirected() ;

	public abstract Button createButton(Context context, final SessionManager sm);
	
	//public abstract Marker createMarker();
	
	public static final PropertyDefinition PROP_LINEAR = PropertyDefinition.stringProperty("linear", "linear");
	
	public boolean needsPropertyWindow() {
		return getProperties().size() > 0;
	}
	
	public abstract List<PropertyDefinition> getProperties();
	
	protected static class ButtonTouchListener 
			implements View.OnTouchListener {
		
		private float sx,sy;
		private Button res;
		Marker.Direction dir = null;
		
		public ButtonTouchListener(Button bt) {
			res = bt;
		}
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					sx = event.getX();
					sy = event.getY();
					break;
				case MotionEvent.ACTION_UP:
					sx = event.getX() - sx;
					sy = event.getY() - sy;
					float len = FloatMath.sqrt( sx*sx + sy*sy);
					if(len< res.getWidth()/3) {
						dir = null;
						return false;
					}
					double ang = Math.toDegrees( Math.atan2(sy, sx) );
					if( ang < -135 || ang > 135) dir = Marker.Direction.LEFT;//Utils.logd(res, "left");
					if( ang > -135 && ang < -45) dir = Marker.Direction.FRONT;//Utils.logd(res, "front");
					if( ang > -45 && ang < 45) dir = Marker.Direction.RIGHT;//Utils.logd(res, "right");
					if( ang > 45 && ang < 135) dir = Marker.Direction.BACK;//Utils.logd(res, "back");
					
					//Utils.logd(this, event.toString() );
					if(event.getX() < 0 || event.getX()>=res.getWidth()
							|| event.getY()<0 || event.getY()>res.getHeight())
						res.performClick();
			}

			return false;
		}
	}

}