package devedroid.opensurveyor;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import devedroid.opensurveyor.data.Drawing;
import devedroid.opensurveyor.data.LocationData;
import devedroid.opensurveyor.data.Marker;
import devedroid.opensurveyor.data.TextMarker;
import devedroid.opensurveyor.data.SessionManager.SessionListener;

public class MapFragment extends SherlockFragment implements SessionListener,
		LocationListener {

	private MapView map;
	private ItemizedIconOverlay<MarkerOverlayItem> markersOvl;
	private DrawingsOverlay drawingsOverlay;
	private FreehandOverlay freehandOverlay;
	private MarkerEditOverlay markerEditOverlay;
	private MarkerOverlayItem cMarker;
	private List<MarkerOverlayItem> markers;
	private MainActivity parent;
	private PathOverlay track;
	private MyLocationOverlay myLoc;

	private static final String PREF_CENTER_LAT = "centerlat";
	private static final String PREF_CENTER_LON = "centerlon";
	private static final String PREF_ZOOM = "zoom";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		parent = (MainActivity) a;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.frag_map, container, false);

		Button btAdd = (Button) root.findViewById(R.id.btAddSmth);
		btAdd.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//parent.addMarker(new TextMarker(map.getMapCenter(), "preved"));
				parent.startActionMode( newMarkerCallback );
				markerEditOverlay = new MarkerEditOverlay(parent, map);
				map.getOverlays().add(markerEditOverlay);
				map.invalidate();
				v.setEnabled(false);
			}
		});
		Button btFreehand = (Button) root.findViewById(R.id.bt_free_hand);
		btFreehand.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				parent.startActionMode( freehandCallback );
				freehandOverlay = new FreehandOverlay(parent, map);
				map.getOverlays().add(freehandOverlay);
				v.setEnabled(false);
			}
		});

		map = (MapView) root.findViewById(R.id.mapview);
		map.setClickable(false);
		map.setTileSource(TileSourceFactory.MAPNIK);
		map.setBuiltInZoomControls(true);
		map.setMultiTouchControls(true);
		// map.setMinZoomLevel(16);
		map.setMaxZoomLevel(20);
		map.getController().setZoom(3);
		map.getController().setCenter(new GeoPoint(0, 0));
		markers = new ArrayList<MarkerOverlayItem>();
		markersOvl = new ItemizedIconOverlay<MarkerOverlayItem>(parent, markers,
				new ItemizedIconOverlay.OnItemGestureListener<MarkerOverlayItem>() {

					@Override
					public boolean onItemSingleTapUp(final int index,
							final MarkerOverlayItem item) {
						Marker m = item.getMarker();
						Utils.toast(parent, m.getDesc(getResources()) );
						return false;// true;
					}

					@Override
					public boolean onItemLongPress(final int index,
							final MarkerOverlayItem item) {
						parent.startActionMode( newMarkerCallback );
						markerEditOverlay = new MarkerEditOverlay(parent, map);
						markerEditOverlay.setLocation(item.getMarker().getLocation() );
						map.getOverlays().add(markerEditOverlay);
						cMarker = item;
						item.setEditing(true);
						map.invalidate();
						return true;
					}
				});
		map.getOverlays().add(markersOvl);

		track = new PathOverlay(Color.GREEN, parent);
		map.getOverlays().add(track);

		myLoc = new MyLocationOverlay(parent, map);
		map.getOverlays().add(myLoc);
		
		drawingsOverlay = new DrawingsOverlay(parent);
		map.getOverlays().add( drawingsOverlay );

		map.getOverlays().add(new ScaleBarOverlay(parent));

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
		if (lm != null) {
			map.getController().animateTo(lm.getLocation().getGeoPoint());
			map.getController().setZoom(15);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		SharedPreferences pref = getActivity().getSharedPreferences(
				getActivity().getPackageName(), 0);
		int lat,lon;
		int zoom;
		try {
			lat = pref.getInt(PREF_CENTER_LAT, 0) ;
			lon = pref.getInt(PREF_CENTER_LON, 0) ;
			zoom  = pref.getInt(PREF_ZOOM, 2);
		} catch(RuntimeException e) {
			lat=0;
			lon=0;
			zoom = 2;
		}
		GeoPoint pt = new GeoPoint(lat, lon);
		map.getController().setZoom(zoom);
		map.getController().setCenter(pt);
		map.getController().animateTo(pt);
		//Utils.logi("Map", "Set "+lat +"/"+ lon +" pt= "+pt);
		//Utils.logi("Map", "Got "+map.getMapCenter().getLatitudeE6() +"/"+ map.getMapCenter().getLongitudeE6() );
	}

	@Override
	public void onResume() {
		super.onResume();
		myLoc.enableMyLocation();
		Hardware hw = parent.getHardwareCaps();
		if (hw.canGPS()) {
			hw.addListener(this);
		}
	}

	@Override
	public void onPause() {
		SharedPreferences pref = getActivity().getSharedPreferences(
				getActivity().getPackageName(), 0);
		Editor ed = pref.edit();
		ed.putInt(PREF_CENTER_LAT,	map.getMapCenter().getLatitudeE6());
		ed.putInt(PREF_CENTER_LON,	map.getMapCenter().getLongitudeE6());
		ed.putInt(PREF_ZOOM, map.getZoomLevel());
		//Utils.logi("Map", "Saved "+map.getMapCenter().getLatitudeE6() +"/"+ map.getMapCenter().getLongitudeE6() );
		ed.commit();

		myLoc.disableMyLocation();
		parent.getHardwareCaps().removeListener(this);
		super.onPause();
	}

	@Override
	public void onPoiAdded(Marker m) {
		if (!m.hasLocation())
			return;
		if(m instanceof Drawing) {
			drawingsOverlay.addDrawing( (Drawing)m);
			map.invalidate();
			
		} else {
			MarkerOverlayItem oo = new MarkerOverlayItem(m, getResources() );
			markersOvl.addItem(oo);
			map.invalidate();
		}
	}
	
	private static class MarkerOverlayItem extends OverlayItem {
		private final Marker marker;
		private Drawable dr;
		private Drawable empty;

		public MarkerOverlayItem(Marker m, Resources r) {
			super(m.getDesc(r), m.getDesc(r), m.getLocation().getGeoPoint());
			marker = m;
			dr = r.getDrawable(R.drawable.map_marker);
			empty = r.getDrawable(R.drawable.empty);
			setMarker(dr);
		}
		
		public void setEditing(boolean editing) {
			if(editing) 
				setMarker(empty);
			else 
				setMarker(dr);
		}

		public Marker getMarker() { return marker; }

		public void updateFromMarker() {
			LocationData loc = marker.getLocation();
			super.getPoint().setCoordsE6( (int)(loc.lat*1e6), (int)(loc.lon*1e6) );
		}
		
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

	@Override
	public void onLocationChanged(Location location) {
		track.addPoint(new GeoPoint(location));
		map.invalidate();
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	private void finishMoveMarker(boolean cancel) {
		if(!cancel) {
			if(cMarker==null) {
				TextMarker m = new TextMarker(markerEditOverlay.getLocation(), "It works!");
				parent.addMarker(m );
			} else {
				cMarker.getMarker().setLocation(markerEditOverlay.getLocation() );
				cMarker.updateFromMarker();
				cMarker.setEditing(false);
			}
		}
		
		map.getOverlays().remove(markerEditOverlay);
		getView().findViewById(R.id.btAddSmth).setEnabled(true);
		map.invalidate();
		cMarker = null;
	}
	
	private void finishFreehand(boolean cancel) {
		map.getOverlays().remove(freehandOverlay);
		if(!cancel) parent.addMarker(freehandOverlay.createDrawing() );
		
		getView().findViewById(R.id.bt_free_hand).setEnabled(true);
		map.invalidate();
	}
	
	private Callback freehandCallback = new Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode,
				Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.ctx_freehand, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode,
				Menu menu) {
			//menu.findItem(R.id.group1).set
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode,
				MenuItem item) {
			switch(item.getItemId()) {
				case R.id.mi_freehand_delete :
					mode.setTag(Boolean.FALSE);
					mode.finish();
					break;
				case R.id.mi_freehand_del_last:
					freehandOverlay.deleteLastSegment();
					map.invalidate();
					break;
				case R.id.mi_red:
				case R.id.mi_black:
				case R.id.mi_blue:
				case R.id.mi_green:
					switch(item.getAlphabeticShortcut()) {
						case 'k' : freehandOverlay.setPenColor(Color.BLACK); break;
						case 'r' : freehandOverlay.setPenColor(Color.RED); break;
						case 'g' : freehandOverlay.setPenColor(Color.GREEN); break;
						case 'b' : freehandOverlay.setPenColor(Color.BLUE); break;
					}
					item.setChecked(true);
					map.invalidate();
					break;
				case R.id.mi_width1:
				case R.id.mi_width2:
				case R.id.mi_width3:
					freehandOverlay.setPenWidth( Integer.parseInt(""+item.getAlphabeticShortcut() ) );
					map.invalidate();
					item.setChecked(true);
					break;
			}
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			finishFreehand(mode.getTag() != null);
		}
	};

	private Callback newMarkerCallback = new Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode,
				Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.ctx_newmarker, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode,
				Menu menu) {
			//menu.findItem(R.id.group1).set
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode,
				MenuItem item) {
			switch(item.getItemId()) {
				case R.id.mi_freehand_delete:
					mode.setTag(Boolean.FALSE);
					mode.finish();
					break;
			}
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			finishMoveMarker(mode.getTag() != null);
		}
	};
}
