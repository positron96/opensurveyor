package devedroid.opensurveyor;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import devedroid.opensurveyor.data.Drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.view.MotionEvent;

class FreehandOverlay extends Overlay {
	// List<List> paths
	private List<List<IGeoPoint>> paths = new ArrayList<List<IGeoPoint>>();
	private List<IGeoPoint> path;
	private Paint paint;
	private MapView map;
	
	private int width = 4;
	private int color = Color.BLACK;

	public FreehandOverlay(Context ctx, MapView map) {
		super(ctx);
		this.map = map;
		paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(4);
		paint.setStyle(Style.STROKE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapview) {

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				path = new ArrayList<IGeoPoint>();
				paths.add(path);
			case MotionEvent.ACTION_MOVE:
				IGeoPoint p = map.getProjection().fromPixels(
						(int) event.getX(), (int) event.getY());
				path.add(p);
				map.invalidate();
				break;
			case MotionEvent.ACTION_UP:
				optimizePath(path);
				map.invalidate();
				break;
		}

		return true;
	}
	
	public Drawing createDrawing() {
		Drawing res = new Drawing();
		res.setData(paths);
		res.setColor(color);
		res.setWidth(width);
		return res;
	}

	@Override
	protected void draw(Canvas arg0, MapView map, boolean arg2) {
		if (path == null)
			return;
		Point pt = new Point();
		paint.setColor( color);
		paint.setStrokeWidth(width);
		for (List<IGeoPoint> path : paths) {
			Path pth = new Path();
			boolean first = true;
			for (IGeoPoint gpt : path) {
				pt = map.getProjection().toMapPixels(gpt, pt);
				if (!first)
					pth.lineTo(pt.x, pt.y);
				else
					pth.moveTo(pt.x, pt.y);
				first = false;
			}
			arg0.drawPath(pth, paint);
		}
	}

	public void deleteLastSegment() {
		if(paths.size()>0)
			paths.remove( paths.size()-1 );
		if(paths.size()>=1)
			path = paths.get(paths.size()-1);
		else path = null;
	}
	
	public void setPenWidth(int width) {
		this.width = width;
	}
	
	public void setPenColor(int color) {
		this.color = color;
	}
	
	private void optimizePath(List<IGeoPoint> path) {
		List<IGeoPoint> res = ramerDouglasPeucker(path);
		Utils.logi(this, "optimizing path from " + path.size() + " to "
				+ res.size());
		path.clear();
		path.addAll(res);
	}

	private List<IGeoPoint> sublist(List<IGeoPoint> src, int start, int end) {
		List<IGeoPoint> res = new ArrayList<IGeoPoint>();
		for (int i = start; i <= end; i++)
			res.add(src.get(i));
		return res;
	}

	private List<IGeoPoint> ramerDouglasPeucker(List<IGeoPoint> points) {
		if (points.size() < 3)
			return points;
		int endIndex = points.size() - 1;
		int maxDistIndex = findMaxPerpDistPoint(points);
		if (maxDistIndex > 0) {
			List<IGeoPoint> result1 = ramerDouglasPeucker(sublist(points,
					0, maxDistIndex));
			List<IGeoPoint> result2 = ramerDouglasPeucker(sublist(points,
					maxDistIndex, endIndex));
			result1.addAll(result2);
			return result1;

		} else {
			List<IGeoPoint> result = new ArrayList<IGeoPoint>();
			result.add(points.get(0));
			result.add(points.get(endIndex));
			return result;
		}
	}

	private int findMaxPerpDistPoint(List<IGeoPoint> points) {
		double maxDistance = 0D;
		int index = 0;
		for (int i = 0; i < points.size() - 1; i++) {
			double distance = dist(points.get(0),
					points.get(points.size() - 1), points.get(i));
			if (distance > maxDistance) {
				maxDistance = distance;
				index = i;
			}
		}
		if (maxDistance > 2) {
			return index;
		}
		return -1;
	}

	private double dist(IGeoPoint p1, IGeoPoint p2, IGeoPoint pt) {
		Point pp1 = map.getProjection().toPixels(p1, null);
		Point pp2 = map.getProjection().toPixels(p2, null);
		Point ppt = map.getProjection().toPixels(pt, null);
		int a, b, c;
		a = pp2.y - pp1.y;
		b = pp1.x - pp2.x;
		c = -a * pp1.x - b * pp1.y;
		return dist(a, b, c, ppt.x, ppt.y);
	}

	private double dist(int a, int b, int c, int x, int y) {
		return Math.abs(a * x + b * y + c) / Math.sqrt(a * a + b * b);
	}
}