package devedroid.opensurveyor;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class MapFragment extends SherlockFragment {

	private MapView map;
	
	private static final String PREF_CENTER_LAT = "centerlat";
	private static final String PREF_CENTER_LON = "centerlon";
	private static final String PREF_ZOOM = "zoom";

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
		//map.setTileSource(TileSourceFactory.MAPNIK);
		map.setBuiltInZoomControls(true);
		//map.setMinZoomLevel(16);
		//map.setMaxZoomLevel(16);
		map.getController().setZoom(16);
		map.getController().setCenter(new GeoPoint(55.0, 83.0));

		return root;
	}

	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		SharedPreferences pref = getActivity().getSharedPreferences(
				getActivity().getPackageName(), 0);
		double lat = pref.getFloat( PREF_CENTER_LAT, 0);
		double lon = pref.getFloat(PREF_CENTER_LON, 0);
		
		map.getController().setCenter( new GeoPoint(lat,lon) );
		map.getController().setZoom( pref.getInt(PREF_ZOOM, 2));
	}
	
	@Override
	public void onPause() {
		SharedPreferences pref = getActivity().getSharedPreferences(
				getActivity().getPackageName(), 0);
		Editor ed = pref.edit();
		ed.putFloat(PREF_CENTER_LAT, map.getMapCenter().getLatitudeE6()/1000000f);
		ed.putFloat(PREF_CENTER_LON, map.getMapCenter().getLongitudeE6()/1000000f);
		ed.putInt(PREF_ZOOM, map.getZoomLevel() );
		ed.commit();
		
		super.onPause();
	}

}
