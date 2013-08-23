package devedroid.opensurveyor;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import devedroid.opensurveyor.data.Marker;
import devedroid.opensurveyor.data.SessionManager.SessionListener;

public class MapFragment extends SherlockFragment implements SessionListener {

	private MapView map;
	private ItemizedOverlayWithFocus<OverlayItem> oMarkers;
	private List<OverlayItem> markers;
	private MainActivity parent;

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
		parent = (MainActivity) getActivity();
		map = (MapView) root.findViewById(R.id.mapview);
		map.setClickable(false);
		map.setTileSource(TileSourceFactory.MAPNIK);
		map.setBuiltInZoomControls(true);
		// map.setMinZoomLevel(16);
		// map.setMaxZoomLevel(16);
		map.getController().setZoom(19);
		map.getController().setCenter(new GeoPoint(55.0, 83.0));
		markers = new ArrayList<OverlayItem>();
		oMarkers = new ItemizedOverlayWithFocus<OverlayItem>(parent, markers,
				new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

					@Override
					public boolean onItemSingleTapUp(final int index,
							final OverlayItem item) {
						return false;//true;
					}

					@Override
					public boolean onItemLongPress(final int index,
							final OverlayItem item) {
						return false;
					}
				});
		map.getOverlays().add(oMarkers);
		return root;
	}

	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);

		Marker lm = null;
		Utils.logd("MapFragment", "parent=" + parent);
		for (Marker m : parent.getMarkers()) {
			onPoiAdded(m);
			if (m.hasLocation())
				lm = m;
		}
		if (lm != null)
			map.getController().animateTo(lm.getLocation().getGeoPoint());
	}

	@Override
	public void onStart() {
		super.onStart();
		SharedPreferences pref = getActivity().getSharedPreferences(
				getActivity().getPackageName(), 0);
		double lat = pref.getFloat(PREF_CENTER_LAT, 0);
		double lon = pref.getFloat(PREF_CENTER_LON, 0);

		map.getController().setCenter(new GeoPoint(lat, lon));
		map.getController().setZoom(pref.getInt(PREF_ZOOM, 2));
	}

	@Override
	public void onPause() {
		SharedPreferences pref = getActivity().getSharedPreferences(
				getActivity().getPackageName(), 0);
		Editor ed = pref.edit();
		ed.putFloat(PREF_CENTER_LAT,
				map.getMapCenter().getLatitudeE6() / 1000000f);
		ed.putFloat(PREF_CENTER_LON,
				map.getMapCenter().getLongitudeE6() / 1000000f);
		ed.putInt(PREF_ZOOM, map.getZoomLevel());
		ed.commit();

		super.onPause();
	}

	@Override
	public void onPoiAdded(Marker m) {
		if (!m.hasLocation())
			return;
		Utils.logi("", "added marker " + m);
		OverlayItem oo = new OverlayItem(m.toString(), 
				m.getDesc(getResources()),
				m.getLocation().getGeoPoint());
		oo.setMarker(getResources().getDrawable(R.drawable.ic_launcher));
		//markers.add(oo);
		oMarkers.addItem(oo);
		map.invalidate();
	}

	@Override
	public void onPoiRemoved(Marker m) {
	}

	@Override
	public void onSessionStarted() {
	}

	@Override
	public void onSessionFinished() {
	}

}
