package devedroid.opensurveyor;

import java.util.Collection;
import java.util.HashSet;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class Hardware {
	//private boolean canGPS = false;
	private boolean hasGps = false;
	private boolean gpsEnabled = false;
	private LocationManager locMan;
	private Location lastLoc= null;
	private boolean listening = false;
	
	private Collection<LocationListener> gpsListeners;
	
	public Hardware(Context ctx) {
		gpsListeners = new HashSet<LocationListener>();
		start(ctx);
	}
	
	public void update(Context ctx) {		
		hasGps = locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	public void start(Context ctx) {
		locMan = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		update(ctx);
		startListening();
	}
	
	public void stop() {
		stopListening();
		locMan = null;
	}
	
	public boolean canGPS() {
		try {
			hasGps = locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch(Exception e) {
			Utils.logw("Hardware.canGPS", "failed to query gps", e);
			hasGps = false;
		}
		return hasGps;
	}
	
	public boolean isGPSEnabled() {
		return hasGps && gpsEnabled;
	}
	
	public boolean hasFix() {
		return lastLoc != null;
	}
	
	public void addListener(LocationListener ll) {
		gpsListeners.add(ll);
	}
	
	public void removeListener(LocationListener ll) {
		gpsListeners.remove(ll);
	}
	
	private LocationListener ll = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Utils.logd("Hardware.ll.onStatusChanged", "provider="+provider+"; status="+status);
			for(LocationListener l: gpsListeners)l.onStatusChanged(provider, status, extras);
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			if(LocationManager.GPS_PROVIDER.equals( provider) )
				gpsEnabled = true;
			Utils.logd(this, "Provider enabled");
			for(LocationListener l: gpsListeners)l.onProviderEnabled(provider);
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			for(LocationListener l: gpsListeners)l.onProviderDisabled(provider);
			if(LocationManager.GPS_PROVIDER.equals( provider) )
				gpsEnabled = false;
			Utils.logd(this, "Provider disabled");
		}
		
		@Override
		public void onLocationChanged(Location location) {
			for(LocationListener l: gpsListeners)l.onLocationChanged(location);
			Utils.logd(this, "Location changed");
			gpsEnabled = true;
			lastLoc = location;
		}
	};
	
	public void startListening() {
		assert !listening : "Listening already started";
		locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, ll);
		listening = true;
	}
	
	public void stopListening() {
		assert listening : "Stopping without starting listening"; 
		locMan.removeUpdates(ll);
		listening = false;
	}
	

	public Location getLastLocation() {
		return lastLoc;
	}
	
	public static class SimpleLocationListener implements LocationListener {

		
		@Override
		public void onLocationChanged(Location location) {}

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		
	}

	public void clearListeners() {
		gpsListeners.clear();
		
	}

}
