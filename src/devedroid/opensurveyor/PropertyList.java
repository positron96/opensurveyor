package devedroid.opensurveyor;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import devedroid.opensurveyor.data.Marker;

public class PropertyList extends ListView {
	private PropAdapter ad;

	private ButtonUIFragment parent;

	public PropertyList(Context context) {
		super(context);
		ad = new PropAdapter(context);
		
		this.setItemsCanFocus(true);
		this.setAdapter(ad);
		this.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
	}
	
	public PropertyList(Context context, ButtonUIFragment parent) {
		super(context);
		//this.setOrientation( LinearLayout.VERTICAL );
		ad = new PropAdapter(context);
		this.setItemsCanFocus(true);
		this.setAdapter(ad);
		this.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		this.parent = parent;
	}

	private Marker p;
	private BasePreset prs;

	private void setPreset(BasePreset prs) {
		this.prs = prs;
		fillProps();
	}

	public void setMarker(Marker m) {
		this.p = m;
		setPreset(m.getPreset() );

	}

	private void fillProps() {
		for (String t : prs.getPropertyNames()) {
			ad.add(new PropEntry(t, null));
			
			//View v = loadRow(getContext(), t);
			
			//addView(v,new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
	}

	public void saveProps() {
		for (int i = 0; i < ad.getCount(); i++) {
			PropEntry e = ad.getItem(i);
			if (e.editText != null)
				p.addProperty(e.key, e.editText.getText().toString());
			else
				Utils.logw(this, "saveProps: EditText is null for" + e.key);
		}
	}
	
	private View loadRow(Context ctx, final String c) {
		View cView;
		TextView tv;
		EditText ev;
		
		LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		cView = vi.inflate(R.layout.item_prop, null);
		ev = (EditText) cView.findViewById(R.id.prop_value);
		ev.addTextChangedListener( tw);
		ev.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Utils.logd(this, "onFocusChange");
				if(!hasFocus) {
					p.addProperty(c, ((EditText) v).getText().toString());
				} else 
					PropertyList.this.parent.cancelTimeoutTimer();
			}
		});
		
		tv = (TextView) cView.findViewById(R.id.prop_name);
		tv.setText(c);

		String v = p.getProperty(c);
		if(v!=null)
			ev.setText(v);
		
		return cView;
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
			Utils.logd(this, "afterTextChanged "+s);
			//p.addProperty(c, s.toString());
			//PropertyList.this.parent.cancelTimeoutTimer();
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start,int count, int after) {	
			//Utils.logd(this, "beforeTextChanged "+s);
		}
		@Override
		public void onTextChanged(CharSequence s, int start,int before, int count) { 
			//Utils.logd(this, "onTextChanged "+s);
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
			final String c = item.key;
			
			if (cView == null) {
				LayoutInflater vi = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				cView = vi.inflate(R.layout.item_prop, null);
				ev = (EditText) cView.findViewById(R.id.prop_value);
				item.editText = ev;
				ev.addTextChangedListener( tw);
				ev.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						Utils.logd(this, "onFocusChange");
						if(!hasFocus) {
							p.addProperty(c, ((EditText) v).getText().toString());
						} else 
							PropertyList.this.parent.cancelTimeoutTimer();
					}
				});
			}
			
			tv = (TextView) cView.findViewById(R.id.prop_name);
			tv.setText(c);
			ev = (EditText) cView.findViewById(R.id.prop_value);
			

			ev.setText(p.getProperty(c));
			
			
			return cView;

		}

	}

}
