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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import devedroid.opensurveyor.data.Marker;

public class PropertyWindow extends RelativeLayout {
	private PropAdapter ad;

	private ListView propList;
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

		ad = new PropAdapter(getContext());
		propList = (ListView) this.findViewById(R.id.prop_list);
		propList.setItemsCanFocus(true);
		propList.setAdapter(ad);
		propList.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

		btPropClose = (Button) findViewById(R.id.btPropsClose);
		btPropClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveProps();
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
				if(ad!=null) {
					ad.clear();
					propList.setAdapter(null);
					propList.setAdapter(ad);
				}
				marker = null;
				cancelTimeoutTimer();
			}
	}

	private void fillProps() {
		ad.clear();
		for (String t : prs.getPropertyTitles()) {
			ad.add(new PropEntry(t, null));

			// View v = loadRow(getContext(), t);

			// addView(v,new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,
			// LayoutParams.WRAP_CONTENT));
		}
	}

	public void saveProps() {
		for (int i = 0; i < ad.getCount(); i++) {
			PropEntry e = ad.getItem(i);
			if (e.editText != null)
				marker.addProperty(e.key, e.editText.getText().toString());
			else
				Utils.logw(this, "saveProps: EditText is null for" + e.key);
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
		String key;
		EditText editText;

		public PropEntry(String key, EditText editText) {
			super();
			this.key = key;
			this.editText = editText;
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

	private class PropAdapter extends ArrayAdapter<PropEntry> {

		public PropAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View cView, ViewGroup parent) {
			TextView tv;
			EditText ev;
			PropEntry item = getItem(position);
			//Utils.logd(this, "getView "+position+"; "+cView);
			final String c = item.key;

			if (cView == null) {
				LayoutInflater vi = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				cView = vi.inflate(R.layout.item_prop, null);
				ev = (EditText) cView.findViewById(R.id.prop_value);
				item.editText = ev;
				// ev.addTextChangedListener( tw);
				ev.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						//Utils.logd(this, "onFocusChange " + hasFocus);
						if (hasFocus) {
							cancelTimeoutTimer();
						} else {
							if (marker != null)
								marker.addProperty(c, ((EditText) v).getText().toString());
						}
					}
				});
			}

			tv = (TextView) cView.findViewById(R.id.prop_name);
			tv.setText(c);

			ev = (EditText) cView.findViewById(R.id.prop_value);
			String v = marker.getProperty(c);
			if (v != null)
				ev.setText(v);

			return cView;
		}

	}

}
