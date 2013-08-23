package devedroid.opensurveyor;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import devedroid.opensurveyor.data.AudioRecordMarker;
import devedroid.opensurveyor.data.Marker;
import devedroid.opensurveyor.data.PropertyDefinition;
import devedroid.opensurveyor.data.TextMarker;
import devedroid.opensurveyor.presets.AudioRecordPreset;
import devedroid.opensurveyor.presets.BasePreset;

public class PropertyWindow extends RelativeLayout {
	//private PropAdapter ad;

	private LinearLayout propList;
	private Button btPropClose;

	private ButtonUIFragment parent;

	private Timer timeoutTimer = new Timer("PropWin timeout timer");
	private TimeoutTask timeoutTask;
	private AudioRecordTask audioTask;

	private Marker marker;
	private BasePreset prs;
	
	private OnClickListener closeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			PropertyWindow.this.parent.hideEditPropWin();
		}
	};
	
	
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
		btPropClose.setOnClickListener(closeListener);

		setOnTouchListener(genericCancelTimeoutListener);
	}

	public void setMarker(Marker m) {
		this.marker = m;
		this.prs = m.getPreset();
		fillProps();
		fillPropValues();
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
		} else
		if(marker instanceof AudioRecordMarker) {
			rearmTimeoutTimer();
		} else {
			rearmTimeoutTimer();
		}
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
		if(propList!=null)
			propList.removeAllViews();
		
		if(prs instanceof AudioRecordPreset) {
			LayoutInflater vi = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = vi.inflate(R.layout.item_prop_audio, propList, true);
			//propList.setLayoutParams( new LayoutParams)
			//propList.getLayoutParams().height = 200;//LayoutParams.MATCH_PARENT;
			propList.requestLayout();
			TextView dur = (TextView)itemView.findViewById(R.id.duration);
			final Button btStop = (Button)itemView.findViewById(R.id.stop);
			if( ((AudioRecordMarker)marker).isRecordFinished() ) {
				btStop.setVisibility(View.GONE);
				long d = ((AudioRecordMarker)marker).getDuration()/1000;
				dur.setText( String.format("%02d:%02d", d/60, d%60) );
				//TODO: play audio file here 
			} else {
				btStop.setOnClickListener( new OnClickListener() {
					@Override
					public void onClick(View v) {
						//btStop.setText("Stop");
						btStop.setVisibility(View.INVISIBLE);
						cancelTimeoutTimer();
						//btStop.setOnClickListener( closeListener );
					}
				});
				
				if(audioTask!=null) audioTask.cancel();
				audioTask = new AudioRecordTask(dur, marker.getTimestamp() );
				timeoutTimer.schedule(audioTask, 0, 1000);
			}
		} else {
			for (PropertyDefinition t : prs.getProperties()) {
				Utils.logd(this, "added prop entry "+t);
				//ad.add(new PropEntry(t, null));
				View w = loadPropsView(t);
				propList.addView(w);
			}
		}
	}
	
	public void fillPropValues() {
		int i=0;
		for(PropertyDefinition prop: prs.getProperties()) {
			String v = marker.getProperty( prop );
			if (v!=null) {
				View item = propList.getChildAt(i);
				View ctl = item.findViewById(R.id.prop_value);
				PropertyDefinition def = (PropertyDefinition)ctl.getTag();
				switch(def.type) {
				case String:
				case Number:
					((EditText)ctl).setText( prop.formatValue(v, getResources() ));
					break;
				case Boolean:
					((CheckBox)ctl).setChecked( v.equals(PropertyDefinition.VALUE_YES) );
					break;
				case Choice:
					((Spinner)ctl).setSelection( prop.findChoiceByValue(v) );
					break;
				}
			}
			i++;
		}
	}

	public void savePropValues() {
		if(prs instanceof AudioRecordPreset) {
			AudioRecordPreset p = ((AudioRecordPreset)prs);
			if(p.isRecording()) p.stopRecord();
			if(audioTask!=null) audioTask.cancel();
			audioTask = null;
		} else {
			for (int i = 0; i < propList.getChildCount(); i++) {
				View item = propList.getChildAt(i);
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
	}

	void cancelTimeoutTimer() {
		//Utils.logd(this, "cancelTimeoutTimer");
		if (timeoutTask != null) {
			timeoutTask.cancel();
			//Utils.logd(this, "canceled timer");
		}
		setPropButton(-1);
	}

	void rearmTimeoutTimer() {
		cancelTimeoutTimer();
		timeoutTask = new TimeoutTask();
		timeoutTimer.schedule(timeoutTask, 0, 1000);
		//Utils.logd(this, "rearmed timer");
	}

	private void setPropButton(final int left) {
		this.post(new Runnable() {
			@Override
			public void run() {
				String s = getContext().getString(R.string.str_ok);
				if (left != -1)
					btPropClose.setText(s+" (" + left + ")");
				else
					btPropClose.setText(s);
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
	
	private class AudioRecordTask extends TimerTask {
		private TextView label;
		private long start;
		public AudioRecordTask(TextView label, long start) {
			this.label = label;
			this.start = start/1000;
		}

		@Override
		public void run() {
			post(new Runnable() {
				@Override
				public void run() {
					long d = System.currentTimeMillis()/1000 - start;
					label.setText( String.format("%02d:%02d", d/60, d%60) );
				}
			});
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
		View itemView;

		final String propTitle = def.title;

		LayoutInflater vi = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		itemView = vi.inflate(R.layout.item_prop, propList, false);
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

}
