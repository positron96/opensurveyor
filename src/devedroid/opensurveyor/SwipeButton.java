package devedroid.opensurveyor;	

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SwipeButton extends Button {

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
	private TextView popup;
	
	@Override
	public boolean onTouchEvent(MotionEvent t) {
		switch(t.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: 
				dir = 0;
				down = true;
				popup = new TextView(getContext());
				popup.setBackgroundColor(Color.BLUE);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.LEFT_OF, getId() );
				lp.addRule(RelativeLayout.ALIGN_BASELINE, getId() );
				popup.setText("LEFT");
//				popup.setOnTouchListener( new OnTouchListener() {
//					@Override
//					public boolean onTouch(View v, MotionEvent evt) {
//						Log.i("opensurveyor", "popup " + evt);
//						if(evt.getActionMasked() == MotionEvent.ACTION_UP) {
//							dir = 1;
//							
//							//Toast.makeText(getContext(), "LEFT selected", Toast.LENGTH_SHORT).show();
//							SwipeButton.this.onTouchEvent(evt);
//						}
//						return false;
//					}
//				});
				((RelativeLayout)getParent()).addView(popup, lp);
				break;
			case MotionEvent.ACTION_UP: 
				down = false;				
				int[] rr = new int[2];
				popup.getLocationOnScreen(rr);
				
				Rect r = new Rect(rr[0], rr[1], rr[0]+popup.getWidth(), rr[1]+popup.getHeight());
				Log.i("opensurveyor", "button " + r);
				Log.i("opensurveyor", "evt " +t.getRawX()+"/"+t.getRawY());
				((RelativeLayout)getParent()).removeView(popup);
				if( r.contains( (int)t.getRawX(), (int)t.getRawY())) {
					dir = 1;
					Log.i("opensurveyor", "perform click ");
					performClick();
					return true;
				}
				
				break;
			case MotionEvent.ACTION_MOVE:				
				if(down) {
					//((RelativeLayout.LayoutParams)getLayoutParams()).rightMargin += 
					//		t.getHistoricalX(0)-t.getX();
					//requestLayout();
					layout( getLeft()+5, getTop(), getRight()+5, getBottom());
					requestLayout();
				}
		}
		setText("down="+down);		
		return super.onTouchEvent(t);
	}
}
