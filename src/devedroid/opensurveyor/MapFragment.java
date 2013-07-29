package devedroid.opensurveyor;

import org.apmem.tools.layouts.FlowLayout;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import devedroid.opensurveyor.data.Marker;
import devedroid.opensurveyor.presets.PresetManager;
import devedroid.opensurveyor.presets.PresetManager.PresetSet;

public class MapFragment extends SherlockFragment {

	private MapView map;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.frag_map, container, false);
		
		map = (MapView) root.findViewById(R.id.mapview);
		map.setClickable(false);
		map.setTileSource(TileSourceFactory.CYCLEMAP);
		map.setBuiltInZoomControls(true);
		map.getController().setZoom(15);
		map.getController().setCenter(new GeoPoint(55.0, 83.0));

		return root;
	}

	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);

	}

}
