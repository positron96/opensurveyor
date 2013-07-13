package devedroid.opensurveyor;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class PropertyWindow extends RelativeLayout {
	//private PropAdapter ad;

	private LinearLayout propList;
	private Button btPropClose;

	private ButtonUIFragment parent;

	private Timer timeoutTimer = new Timer("PropWin timeout timer");
	private TimeoutTask timeoutTask;

	private Marker marker;
	private BasePreset prs;

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

		OnClickListener ll = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.logd(this, "propsWin.onClick");
				cancelTimeoutTimer();
			}
		};
		setOnClickListener(ll);
	}

	public void setMarker(Marker m) {
		this.marker = m;
		this.prs = m.getPreset();
		fillProps();
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
			View ctl = item.getChildAt(1);
			PropertyDefinition def = (PropertyDefinition)ctl.getTag();
			switch(def.type) {
			case String:
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
				marker.addProperty(def.key, val);
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

	private TextWatcher tw = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			// Utils.logd(this, "afterTextChanged "+s);
			// p.addProperty(c, s.toString());
			// parent.cancelTimeoutTimer();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// Utils.logd(this, "beforeTextChanged "+s);
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// Utils.logd(this, "onTextChanged "+s);
		}

	};

	private View loadPropsView(PropertyDefinition def) {
		LinearLayout itemView;

		final String propTitle = def.title;

		TextView tv;
		
		LayoutInflater vi = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		itemView = (LinearLayout)vi.inflate(R.layout.item_prop, propList, false);
		View control =null;
		
		switch (def.type) {
			case String:
				EditText et = new EditText(itemView.getContext() );
				control = et;
				break;
			case Choice:
				Spinner sp = new Spinner(itemView.getContext() );
				SpinnerAdapter spa = new ArrayAdapter<PropertyDefinition.ChoiceEntry>(itemView.getContext(),
				        android.R.layout.simple_spinner_dropdown_item,
			            def.choices);
				sp.setAdapter(spa);
				control = sp;
				break;
			case Boolean:
				CheckBox cb = new CheckBox(itemView.getContext());
				control = cb;
				break;
		}
		control.setTag(def);
		Utils.logd(this, "created "+def.key+" from control "+ control+", parent="+itemView);
		if(itemView.getChildCount() > 1) {
			itemView.removeViewAt(1);
			Utils.logd(this, "removing child");
		}
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,LayoutParams.WRAP_CONTENT);
		lp.weight = 0.7f;
		itemView.addView( control, lp );
		control.setOnFocusChangeListener(new OnFocusChangeListener() {
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
		
		tv = (TextView) itemView.findViewById(R.id.prop_name);
		tv.setText(propTitle);
		
		//ev = (EditText) cView.findViewById(R.id.prop_value);
		//String v = marker.getProperty(c);
//		if (v != null)
//			ev.setText(v);

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
