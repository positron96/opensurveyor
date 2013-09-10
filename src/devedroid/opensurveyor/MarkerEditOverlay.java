package devedroid.opensurveyor;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import devedroid.opensurveyor.data.LocationData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.view.MotionEvent;

public class MarkerEditOverlay extends Overlay {

	private MapView map;
	//private Drawable marker;
	private Bitmap marker;
	private int mw2,mh2;
	private IGeoPoint pt;

	public MarkerEditOverlay(Context ctx, MapView map) {
		super(ctx);
		this.map = map;
		//marker = map.getResources().getDrawable(R.drawable.map_marker_move);
		marker = BitmapFactory.decodeResource( map.getResources(), R.drawable.map_marker_move);
		mw2 = marker.getWidth() / 2;
		mh2 = marker.getHeight() / 2 ;
				
		pt = map.getMapCenter();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapview) {

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_MOVE:
				pt = map.getProjection().fromPixels(
						(int) event.getX(), (int) event.getY());
				
				map.invalidate();
				break;
		}

		return true;
	}

	@Override
	protected void draw(Canvas arg0, MapView map, boolean arg2) {
		Point pt = new Point();
		pt = map.getProjection().toMapPixels(this.pt, pt);
		//marker.setBounds(pt.x, pt.y, pt.x+marker.get, bottom)
		//marker.draw(arg0);
		arg0.drawBitmap(marker, pt.x-mw2, pt.y-mh2, null);
	}

	public IGeoPoint getLocation() {
		return pt;
	}

	public void setLocation(LocationData location) {
		pt = new GeoPoint(location.lat, location.lon);
	}

}
