package devedroid.opensurveyor;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import devedroid.opensurveyor.data.Marker;
import devedroid.opensurveyor.data.PropertyDefinition;
import devedroid.opensurveyor.data.TextMarker;

public class PropertyWindow extends RelativeLayout {
	//private PropAdapter ad;

	private LinearLayout propList;
	private Button btPropClose;

	private ButtonUIFragment parent;

	private Timer timeoutTimer = new Timer("PropWin timeout timer");
	private TimeoutTask timeoutTask;

	private Marker marker;
	private BasePreset prs;
	
	
	private OnTouchListener genericCancelTimeoutListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			cancelTimeoutTimer();
			return false;
		}
	};

	public PropertyWindow(Context context, AttributeSet set) {
		super(context, set);
	}

	public void setParent(ButtonUIFragment parent) {
		this.parent = parent;

		//ad = new PropAdapter(getContext());
		propList = (LinearLayout) this.findViewById(R.id.prop_list);
		//propList.setItemsCanFocus(true);
		//propList.setAdapter(ad);
		propList.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

		btPropClose = (Button) findViewById(R.id.btPropsClose);
		btPropClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PropertyWindow.this.parent.hideEditPropWin();
			}
		});

		setOnTouchListener(genericCancelTimeoutListener);
	}

	public void setMarker(Marker m) {
		this.marker = m;
		this.prs = m.getPreset();
		fillProps();
		if(marker instanceof TextMarker) {
			//* show keyboard
			InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
			View w = propList.findViewById(R.id.prop_value);
			//Utils.logd(this, "found edittext "+w );
			w.requestFocus();
			imm.showSoftInput(w, InputMethodManager.SHOW_IMPLICIT);
			cancelTimeoutTimer();
//			w.setOnFocusChangeListener( new OnFocusChangeListener() {
//				
//				@Override
//				public void onFocusChange(View v, boolean hasFocus) {
//					if(!hasFocus) imm.hide
//					
//				}
//			});
		} else rearmTimeoutTimer();
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		if (changedView == this)
			if (visibility == View.VISIBLE) {

			} else {
//				if(ad!=null) {
//					Utils.logd(this, "Clearing adapter and replacing");
//					ad.clear();
//					propList.setAdapter(null);
//					propList.setAdapter(ad);
//				}
				if(propList!=null)
					propList.removeAllViews();
				marker = null;
				cancelTimeoutTimer();
			}
	}

	private void fillProps() {
		//ad.clear();
		for (PropertyDefinition t : prs.getProperties()) {
			Utils.logd(this, "added prop entry "+t);
			//ad.add(new PropEntry(t, null));
			View w = loadPropsView(t);
			propList.addView(w);
		}
	}

	public void saveProps() {
		for (int i = 0; i < propList.getChildCount(); i++) {
			LinearLayout item = (LinearLayout)propList.getChildAt(i);
			String val = null;
			View ctl = item.findViewById(R.id.prop_value);
			PropertyDefinition def = (PropertyDefinition)ctl.getTag();
			switch(def.type) {
			case String:
			case Number:
				val = ((EditText)ctl).getText().toString();
				break;
			case Boolean:
				val = ((CheckBox)ctl).isChecked() ? "yes" : "no";
				break;
			case Choice:
				val = ((PropertyDefinition.ChoiceEntry)((Spinner)ctl).getSelectedItem()).value;
				break;
			}
			Utils.logd(this, "Saving "+def.key+" from control "+ ctl+" val="+val );
			if(val!=null && val.length()>0)
				marker.addProperty(def, val);
		}
	}

	void cancelTimeoutTimer() {
		//Utils.logd(this, "cancelTimeoutTimer");
		if (timeoutTask != null) {
			timeoutTask.cancel();
			//Utils.logd(this, "canceled timer");
			setPropButton(-1);
		}
	}

	void rearmTimeoutTimer() {
		cancelTimeoutTimer();
		timeoutTask = new TimeoutTask();
		timeoutTimer.schedule(timeoutTask, 0, 1000);
		//Utils.logd(this, "rearmed timer");
	}

	private void setPropButton(final int left) {
		parent.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (left != -1)
					btPropClose.setText("OK (" + left + ")");
				else
					btPropClose.setText("OK");
			}
		});
	}

	private class TimeoutTask extends TimerTask {
		private int timeoutDelay = 5;

		@Override
		public void run() {
			if (timeoutDelay == 0) {
				parent.hideEditPropWin();
				cancel();
			} else
				setPropButton(timeoutDelay);
			timeoutDelay--;
		}
	}

	private class PropEntry {
		final PropertyDefinition def;
		View control;

		public PropEntry(PropertyDefinition key, View editText) {
			this.def = key;
			this.control = editText;
		}
	}

	private View loadPropsView(PropertyDefinition def) {
		LinearLayout itemView;

		final String propTitle = def.title;

		LayoutInflater vi = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		itemView = (LinearLayout)vi.inflate(R.layout.item_prop, propList, false);
		View control =null;
		
		switch (def.type) {
			case String:
				EditText et = new EditText(itemView.getContext() );
				control = et;
				break;
			case Number:
				EditText ent = new EditText(itemView.getContext() );
				ent.setInputType( InputType.TYPE_CLASS_NUMBER);
				control = ent;
				break;
			case Choice:
				Spinner sp = new Spinner(itemView.getContext() );
				ArrayAdapter<PropertyDefinition.ChoiceEntry> spa
					= new ArrayAdapter<PropertyDefinition.ChoiceEntry>(
						itemView.getContext(),
				        android.R.layout.simple_spinner_item,
			            def.choices);
				spa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp.setAdapter(spa);
				control = sp;
				break;
			case Boolean:
				CheckBox cb = new CheckBox(itemView.getContext());
				control = cb;
				break;
		}
		control.setTag(def);
		control.setId(R.id.prop_value);
		
		FrameLayout container = (FrameLayout)itemView.findViewById(R.id.prop_value_container);
		//Utils.logd(this, "created "+def.key+" from control "+ control+", parent="+itemView);
		//LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		//lp.weight = 0.7f;
		//itemView.addView( control, lp );
		container.addView(control);
		control.setOnTouchListener(genericCancelTimeoutListener );
		
		TextView tv = (TextView) itemView.findViewById(R.id.prop_name);
		tv.setText(propTitle+":");
		tv.setOnTouchListener( genericCancelTimeoutListener );
		

		return itemView;
	}

	private class PropAdapter extends ArrayAdapter<PropEntry> {

		public PropAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View cView, ViewGroup parent) {
			TextView tv;
			PropEntry item = getItem(position);
			Utils.logd(this, "getView "+position+"; item="+item.def.key+"; cView="+cView+" control="+item.control);
			
			final String propTitle = item.def.title;

			if (cView == null) {
				if(item.control!=null) {
					cView = (View)item.control.getParent();
				} else {
					LayoutInflater vi = (LayoutInflater) parent.getContext()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					cView = vi.inflate(R.layout.item_prop, parent, false);
					
						switch (item.def.type) {
							case String:
								EditText et = new EditText(cView.getContext() );
								item.control = et;
								break;
							case Choice:
								Spinner sp = new Spinner(cView.getContext() );
								SpinnerAdapter spa = new ArrayAdapter<PropertyDefinition.ChoiceEntry>(cView.getContext(),
								        android.R.layout.simple_spinner_dropdown_item,
							            item.def.choices);
								sp.setAdapter(spa);
								item.control = sp;
								break;
							case Boolean:
								CheckBox cb = new CheckBox(cView.getContext());
								item.control = cb;
								break;
						}
					item.control.setTag(item.def);
					Utils.logd(this, "creating "+item.def.key+" from control "+ item.control+", parent="+cView);
					LinearLayout l = ((LinearLayout)cView);
					if(l.getChildCount() > 1) l.removeViewAt(1);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,LayoutParams.WRAP_CONTENT);
					lp.weight = 0.7f;
					l.addView( item.control, lp );
					item.control.setOnFocusChangeListener(new OnFocusChangeListener() {
						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							//Utils.logd(this, "onFocusChange " + hasFocus);
							if (hasFocus) {
								cancelTimeoutTimer();
							} else {
								if (marker != null)
									;//marker.addProperty(c, ((EditText) v).getText().toString());
							}
						}
					});
				}
			}
			
			tv = (TextView) cView.findViewById(R.id.prop_name);
			tv.setText(propTitle);
			
			//ev = (EditText) cView.findViewById(R.id.prop_value);
			//String v = marker.getProperty(c);
//			if (v != null)
//				ev.setText(v);

			return cView;
		}

	}

}
