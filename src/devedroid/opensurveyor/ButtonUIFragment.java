package devedroid.opensurveyor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apmem.tools.layouts.FlowLayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import devedroid.opensurveyor.data.Marker;
import devedroid.opensurveyor.data.POI;

public class ButtonUIFragment extends SherlockFragment {

	private MainActivity parent;
	private View root;
	private FlowLayout flow;
	private ListView lvHist;
	private PropertyWindow propsWin;
	//private List<String> lhist;
	private ArrayAdapter<Marker> histAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		root = inflater.inflate(R.layout.frag_buttons, container, false);

		flow = (FlowLayout) root.findViewById(R.id.flow);
		
		propsWin = (PropertyWindow) root.findViewById(R.id.props);
		propsWin.setParent(this);

		lvHist = (ListView) root.findViewById(R.id.l_history);		
		List<Marker> lhist = new ArrayList<Marker>();
		histAdapter = new ArrayAdapter<Marker>(root.getContext(),
				R.layout.item_poi, lhist) {
			private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
			
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					convertView = View.inflate(parent.getContext(), R.layout.item_poi, null);
				}
				Marker item = getItem(position);
				TextView tw = (TextView) convertView.findViewById(R.id.text1);
				tw.setText(""+sdf.format(new Date( item.getTimestamp() ) ) );
				
				TextView tw2 = (TextView) convertView.findViewById(R.id.text2);
				tw2.setText(item.getDesc() );
				
				TextView tw3 = (TextView) convertView.findViewById(R.id.location);
				tw3.setText(item.hasLocation()?"gps":"");
				return convertView;
			}
		};
		lvHist.setAdapter(histAdapter);
		Utils.logd("ButtonUIFragment", "parent="+parent);
		for(Marker m: parent.getMarkers()) {
			histAdapter.add(m);
		}
		lvHist.setSelection( histAdapter.getCount()-1 );
		
		TextView empty = (TextView)root.findViewById(android.R.id.empty);
		lvHist.setEmptyView(empty);
		root.post(new Runnable() {
			public void run() { addButtons(); }
		});
		
		return root;
	}
	
	/** Should be called when flow width and height is known */
	private void addButtons() {
		Display display = getSherlockActivity().getWindowManager()
				.getDefaultDisplay();
		
		int width = flow.getWidth(); 
		int height = flow.getHeight();
		width = Math.min(width, height) * 33 / 100;
		height = width;
		
		BasePreset[] presets = new BasePreset[] {
			new TextPreset(),
			new POIPreset("Bridge", null,null, true),
			new POIPreset("Milestone"),
			new POIPreset("Bus stop", "busstop", "transport_bus_stop"),
			new POIPreset("Town start", "town-start", "village"),
			new POIPreset("Town end", "town-end"),
			new POIPreset("Speed limit", "speedlimit"),
			new POIPreset("Cross-road", "crossroad"),
			new POIPreset("Zebra", "zebracross", "transport_zebra"),
			new POIPreset("Shop", null, "shop")
		};
		
		Utils.logd("ButtonUIFragment", String.format("w/h=%d/%d; " +
				"dis w/h=%d/%d; " +
				"act w/h=%d/%d; " +
				"hist h=%d; " +
				" flow w/h=%d/%d", 
				width, height, 
				display.getWidth(), display.getHeight(),
				root.getRight(),
				root.getBottom(),
				lvHist.getHeight(),
				flow.getWidth(), flow.getHeight()
				) );

		for (int i = 0; i < presets.length; i++) {
			//MarkerButton bt = new MarkerButton(root.getContext(), presets[i], parent);
			//FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(width*4/5, height/4);

			Button bt = presets[i].createButton(root.getContext(), parent);
			bt.setWidth(width);
			bt.setHeight(height);
			bt.setId(1000+i);
			bt.setTag(Integer.valueOf(i));
			
			flow.addView(bt);// , lp);
		}
	}
	
	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		parent = (MainActivity) a;
	}

	public void onNewSession() {
	}

	public void onFinishSession() {
	}
	
	public void onPoiAdded(Marker m) {
		histAdapter.add(m);
		hideEditPropWin();
		if(parent.getCurrentFragment() == this) { 
			if( (m instanceof POI && "end".equals( ((POI)m).getProperty("linear")) ) ||
				(m.getPreset().getPropertyNames().size() == 0) ) 
				return;
			showEditPropWin(m);
		}
	}
	
	public void showEditPropWin(Marker m) {
		lvHist.setVisibility(View.GONE);
		propsWin.setVisibility(View.VISIBLE);
		propsWin.setMarker(m);
		propsWin.rearmTimeoutTimer();
	}

	public void hideEditPropWin() {
		Utils.logd(this, "finishMarkerEditing");
		propsWin.cancelTimeoutTimer();
		parent.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				propsWin.setVisibility(View.GONE);
				lvHist.setVisibility(View.VISIBLE);
				propsWin.onHide();
			}
			
		});
	}
	
	void runOnUiThread(Runnable r) {
		parent.runOnUiThread(r);
	}
	
}
