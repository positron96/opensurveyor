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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SwipeButton extends Button {

	private GestureDetector gd = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			float v = e1.getX()-e2.getX();
			double ang = Math.toDegrees(Math.atan(Math.abs(e1.getY() - e2.getY())/v) );
			if(ang<30 ) {
				if(Math.abs(v)> 10) {
					SwipeButton.this.dir = (int) Math.signum(v);
					SwipeButton.this.performClick();
					SwipeButton.this.dir=0;
					return true;
				}
			}
			return false;
		}

	});
	
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

	private boolean down;
	private TextView popupl, popupr;
	
	@Override
	public boolean onTouchEvent(MotionEvent t) {
		switch(t.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: 
				dir = 0;
				down = true;
				popupl = new TextView(getContext());
				popupl.setBackgroundColor(Color.BLUE);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.LEFT_OF, getId() );
				lp.addRule(RelativeLayout.ALIGN_BASELINE, getId() );
				popupl.setText("LEFT");
				((RelativeLayout)getParent()).addView(popupl, lp);
				popupr = new TextView(getContext());
				popupr.setBackgroundColor(Color.BLUE);
				RelativeLayout.LayoutParams lpr = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpr.addRule(RelativeLayout.RIGHT_OF, getId() );
				lpr.addRule(RelativeLayout.ALIGN_BASELINE, getId() );
				popupr.setText("RIGHT");
				((RelativeLayout)getParent()).addView(popupr, lpr);
				break;
			case MotionEvent.ACTION_UP: 
				down = false;				
				int[] rr = new int[2];
				popupl.getLocationOnScreen(rr);
				Rect r = new Rect(rr[0], rr[1], rr[0]+popupl.getWidth(), rr[1]+popupl.getHeight());
				popupr.getLocationOnScreen(rr);
				Rect r2 = new Rect(rr[0], rr[1], rr[0]+popupr.getWidth(), rr[1]+popupr.getHeight());
				Log.i("opensurveyor", "button " + r);
				Log.i("opensurveyor", "evt " +t.getRawX()+"/"+t.getRawY());
				((RelativeLayout)getParent()).removeView(popupl);
				((RelativeLayout)getParent()).removeView(popupr);
				if( r.contains( (int)t.getRawX(), (int)t.getRawY())) {
					dir = 1;
					Log.i("opensurveyor", "perform l click ");
					performClick();
					return true;
				} else if(r2.contains((int)t.getRawX(), (int)t.getRawY())) {
					dir = -1;
					Log.i("opensurveyor", "perform r click ");
					performClick();
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
		setText("down="+down);		
		return super.onTouchEvent(t);
	}
}
