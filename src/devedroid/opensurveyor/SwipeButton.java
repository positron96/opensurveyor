package devedroid.opensurveyor;	

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SwipeButton extends Button {

	private AbsoluteLayout popupLayer;
	
	public SwipeButton(Context context) {
		super(context);
		
	}
	
	public SwipeButton(Context context, AttributeSet aset) {
		super(context, aset);
	}
	
	private int dir;
	
	public int getSelectedDirection() {
		return dir;
	}
	
	public void setPopupLayer(AbsoluteLayout l) {
		popupLayer = l;
	}

	private boolean down;
	private TextView popupl, popupr;
	
	@Override
	public boolean onTouchEvent(MotionEvent t) {
		switch(t.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: 
				dir = 0;
				down = true;
				popupl = new TextView(getContext());
				popupl.setBackgroundColor(Color.BLUE);
				popupl.setText("LEFT");
				AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(
						60, getHeight(),
						getLeft()-60, getTop()	);
				popupLayer.addView(popupl, lp);
				
				popupr = new TextView(getContext());
				popupr.setBackgroundColor(Color.BLUE);
				popupr.setText("RIGHT");
				AbsoluteLayout.LayoutParams lpr = new AbsoluteLayout.LayoutParams(
						60, getHeight(),
						getLeft()+getWidth(), getTop()	);
				popupLayer.addView(popupr, lpr);
				break;
			case MotionEvent.ACTION_UP: 
				down = false;		
				//setText("down="+down);
				int[] rr = new int[2];
				popupl.getLocationOnScreen(rr);
				Rect r = new Rect(rr[0], rr[1], rr[0]+popupl.getWidth(), rr[1]+popupl.getHeight());
				popupr.getLocationOnScreen(rr);
				Rect r2 = new Rect(rr[0], rr[1], rr[0]+popupr.getWidth(), rr[1]+popupr.getHeight());
				Log.i("opensurveyor", "button " + r);
				Log.i("opensurveyor", "evt " +t.getRawX()+"/"+t.getRawY());
				popupLayer.removeView(popupl);
				popupLayer.removeView(popupr);
				if( r.contains( (int)t.getRawX(), (int)t.getRawY())) {
					dir = 1;
					Log.i("opensurveyor", "perform l click ");
					performClick();
					//t.setLocation(t.getX()-100, t.getY());
					setPressed(false);
					return true;
				} else if(r2.contains((int)t.getRawX(), (int)t.getRawY())) {
					dir = -1;
					Log.i("opensurveyor", "perform r click ");
					performClick();
					t.setLocation(t.getX()+100, t.getY());
					setPressed(false);
					return true;
				}
				
				break;
			case MotionEvent.ACTION_MOVE:				
				if(down) {
					//((RelativeLayout.LayoutParams)getLayoutParams()).rightMargin += 
					//		t.getHistoricalX(0)-t.getX();
					//requestLayout();
					//layout( getLeft()+5, getTop(), getRight()+5, getBottom());
					//requestLayout();
				}
		}
		//setText("down="+down);		
		return super.onTouchEvent(t);
	}
}
