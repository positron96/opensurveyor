package devedroid.opensurveyor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ActionBarSpinner extends BaseAdapter {
	private int[][] ids;
	private Context ctx;
	
	public ActionBarSpinner(Context ct) {
		ctx = ct;
		ids = new int[2][];
		ids[0] = new int[] { R.drawable.ic_ab_spinner_buttons, R.string.ui_name_buttons};
		ids[1] = new int[] { R.drawable.ic_ab_spinner_map, R.string.ui_name_map};
	}

	@Override
	public int getCount() {
		return ids.length;
	}

	@Override
	public Object getItem(int position) {
		return ids[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null) {
			LayoutInflater l = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = l.inflate(R.layout.actionbar_spinner_item, parent, false);
		}
		
		//ImageView iv = (ImageView) convertView.findViewById(android.R.id.icon);
		//iv.setImageResource( ids[position][0] );
		TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
		tv.setText( ids[position][1]);

		return convertView;	
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		
		if(convertView==null) {
			LayoutInflater l = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = l.inflate(R.layout.actionbar_spinner_dropdown_item, parent, false);
		}
		
		TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
		ImageView iv = (ImageView) convertView.findViewById(android.R.id.icon);
		
		iv.setImageResource( ids[position][0] );
		tv.setText( ids[position][1]);
		return convertView;		
	}


}
