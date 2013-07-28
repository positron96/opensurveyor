package devedroid.opensurveyor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apmem.tools.layouts.FlowLayout;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ImageView.ScaleType;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import devedroid.opensurveyor.PresetManager.PresetSet;
import devedroid.opensurveyor.data.Marker;
import devedroid.opensurveyor.data.POI;
import devedroid.opensurveyor.presets.AudioRecordPreset;
import devedroid.opensurveyor.presets.BasePreset;
import devedroid.opensurveyor.presets.CameraPreset;
import devedroid.opensurveyor.presets.TextPreset;

public class ButtonUIFragment extends SherlockFragment {

	private MainActivity parent;
	private View root;
	private FlowLayout flow;
	private ListView lvHist;
	private PropertyWindow propsWin;
	// private List<String> lhist;
	private ArrayAdapter<Marker> histAdapter;

	private List<PresetManager.PresetSet> presetSets;
	private PresetSet selPresetSet = null;
	
	private static final String PREF_PRESET = "preset";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PresetManager loader = new PresetManager();
		presetSets = loader.loadPresetSets(getActivity());

		if(savedInstanceState != null) {
			String ff = savedInstanceState.getString(PREF_PRESET);
			for(PresetSet p: presetSets)
				if(p.getFileName().equals(ff)) {selPresetSet = p; break;}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		root = inflater.inflate(R.layout.frag_buttons, container, false);

		flow = (FlowLayout) root.findViewById(R.id.flow);

		propsWin = (PropertyWindow) root.findViewById(R.id.props);
		propsWin.setParent(this);

		lvHist = (ListView) root.findViewById(R.id.l_history);
		histAdapter = new PoiListAdapter(root.getContext());
		lvHist.setAdapter(histAdapter);
		
		lvHist.setSelection(histAdapter.getCount() - 1);
		TextView empty = (TextView) root.findViewById(android.R.id.empty);
		lvHist.setEmptyView(empty);

		setHasOptionsMenu(true);

		return root;
	}

	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);
		
		Utils.logd("ButtonUIFragment", "parent=" + parent);
		for (Marker m : parent.getMarkers()) {
			histAdapter.add(m);
		}
		if(selPresetSet ==null) {
			selPresetSet = presetSets.get(0);
			SharedPreferences pref = getActivity().getSharedPreferences(getActivity().getPackageName(), 0);
			String ff = pref.getString(PREF_PRESET, null); 
			for(PresetSet p: presetSets)
				if(p.getFileName().equals(ff)) {selPresetSet = p; break;}
		}
		parent.invalidateOptionsMenu();
		
		if(flow.getChildCount() == 0)
			root.post(new Runnable() {
				public void run() {
					addButtons();
				}
			});
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	private class PoiListAdapter extends ArrayAdapter<Marker> {
		public PoiListAdapter(Context ctx) {
			super(ctx,R.layout.item_poi, new ArrayList<Marker>());
		}
		private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(parent.getContext(),
						R.layout.item_poi, null);
			}
			Marker item = getItem(position);
			TextView tw = (TextView) convertView
					.findViewById(android.R.id.text1);
			tw.setText(sdf.format(new Date(item.getTimestamp())));

			TextView tw2 = (TextView) convertView
					.findViewById(android.R.id.text2);
			tw2.setText(item.getDesc());

			View tw3 = (View) convertView.findViewById(R.id.location);
			tw3.setVisibility(item.hasLocation() ? View.VISIBLE : View.GONE);

			ImageView iw = (ImageView) convertView
					.findViewById(R.id.direction);
			if (item.hasDirection()) {
				iw.setVisibility(View.VISIBLE);
				// Bitmap src = BitmapFactory.decodeResource(getResources(),
				// R.drawable.dir_mark);
				iw.setScaleType(ScaleType.MATRIX);
				Matrix matrix = new Matrix();
				matrix.postRotate(item.getDirection().getAngle(),
						iw.getWidth() / 2, iw.getHeight() / 2);
				iw.setImageMatrix(matrix);
				// iw.setImageBitmap( Bitmap.createBitmap(src, 0, 0,
				// src.getWidth(), src.getHeight(), matrix, true));
			} else
				iw.setVisibility(View.GONE);
			// tw3.setText(item.hasLocation()?"gps":"");
			return convertView;
		}
	}

	/** Should be called when flow width and height is known */
	private void addButtons() {
		Display display = getSherlockActivity().getWindowManager()
				.getDefaultDisplay();

		flow.removeAllViews();

		int width = flow.getWidth();
		int height = flow.getHeight();
		width = Math.min(width, height) * 33 / 100;
		height = width;

		ArrayList<BasePreset> presets = new ArrayList<BasePreset>();
		presets.add(0, new TextPreset());
		
		if(parent.getHardwareCaps().canRecordAudio()) {
			presets.add(presets.size(), new AudioRecordPreset() );
		}
		if(parent.getHardwareCaps().canCamera() ) {
			presets.add(presets.size(), new CameraPreset() );
		}
		
		presets.addAll( selPresetSet.getPresets() );

		Utils.logd("ButtonUIFragment", String.format("w/h=%d/%d; "
				+ "dis w/h=%d/%d; " + "act w/h=%d/%d; " + "hist h=%d; "
				+ " flow w/h=%d/%d", width, height, display.getWidth(),
				display.getHeight(), root.getRight(), root.getBottom(),
				lvHist.getHeight(), flow.getWidth(), flow.getHeight()));

		for (BasePreset p : presets) {
			Button bt = p.createButton(root.getContext(), parent);
			bt.setWidth(width);
			bt.setHeight(height);
			// bt.setId(1000+i);
			// bt.setTag(Integer.valueOf(i));
			if (p.isToggleButton())
				registerForContextMenu(bt);
			flow.addView(bt);// , lp);
		}
	}

	private void registerButtonsMenus() {
		for (int i = 0; i < flow.getChildCount(); i++) {
			Button bt = (Button) flow.getChildAt(i);
			if (bt instanceof ToggleButton)
				registerForContextMenu(bt);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		SharedPreferences pref = getActivity().getSharedPreferences(getActivity().getPackageName(), 0);
		Editor ed = pref.edit();
		ed.putString("preset", selPresetSet.getFileName());
		ed.commit();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuinfo) {
		super.onCreateContextMenu(menu, v, menuinfo);
		//if (v instanceof Button && v.getTag() instanceof BasePreset) {
		if (v instanceof ToggleButton) {
			final ToggleButton bt = (ToggleButton)v;
			android.view.MenuItem i = menu.add( bt.isChecked() ? "Toggle off" : "Toggle on");
			//i.setCheckable(true);
			//i.setChecked( ((ToggleButton)v).isChecked() );
			i.setOnMenuItemClickListener( new android.view.MenuItem.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(android.view.MenuItem item) {
					bt.toggle();
					return true;
				}
			});
		}

	}

	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		parent = (MainActivity) a;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_buttons_ui, menu);
		MenuItem miPresets = menu.findItem(R.id.mi_presets);
		OnMenuItemClickListener ll = new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				selPresetSet = presetSets.get(item.getItemId());
				addButtons();
				parent.invalidateOptionsMenu();
				return false;
			}
		};
		int i = 0;
		for (PresetSet p : presetSets) {
			MenuItem sitem = miPresets.getSubMenu().add(R.id.mg_presets, i, i, p.getName());
			//sitem.setCheckable(true);
			sitem.setOnMenuItemClickListener(ll);
			// if (p == selPresetSet) sitem.setChecked(true);
			i++;
		}
		miPresets.getSubMenu().setGroupCheckable(R.id.mg_presets, true, true);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		Utils.logd(this, "sel preset = "+selPresetSet);
		MenuItem miPresets = menu.findItem(R.id.mi_presets);

		for (int i = 0; i < miPresets.getSubMenu().size(); i++) {
			MenuItem sitem = miPresets.getSubMenu().getItem(i);
			if(presetSets.get(i) == selPresetSet)
				sitem.setChecked(true);
		}
	}

	public void onNewSession() {
	}

	public void onFinishSession() {
	}

	public void onPoiAdded(Marker m) {
		(new Throwable()).printStackTrace();
		
		histAdapter.add(m);
		histAdapter.notifyDataSetChanged();
		
		if (propsWin.getVisibility() == View.VISIBLE)
			hideEditPropWin();
		if (parent.getCurrentFragment() == this) {
			if ((m instanceof POI && "end".equals(((POI) m)
					.getProperty(BasePreset.PROP_LINEAR)))
					|| (!m.getPreset().needsPropertyWindow()) )
				return;
			showEditPropWin(m);
		}
	}
	
	public void onPoiRemoved(Marker m) {
		histAdapter.remove(m);
		histAdapter.notifyDataSetChanged();
	}

	public void showEditPropWin(Marker m) {
		// lvHist.setVisibility(View.GONE);
		propsWin.setVisibility(View.VISIBLE);
		propsWin.setMarker(m);
		
	}

	public void hideEditPropWin() {

		// Utils.logd(this, "hideEditPropWin");
		propsWin.saveProps();
		propsWin.cancelTimeoutTimer();
		parent.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				propsWin.setVisibility(View.GONE);
				histAdapter.notifyDataSetChanged();
				// lvHist.setVisibility(View.VISIBLE);
			}

		});
	}

	void runOnUiThread(Runnable r) {
		parent.runOnUiThread(r);
	}

}
