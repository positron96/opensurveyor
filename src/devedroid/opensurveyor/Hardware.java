package devedroid.opensurveyor;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.MediaStore;

public class Hardware {
	// private boolean canGPS = false;
	private boolean hasGps = false;
	private boolean gpsEnabled = false;
	private boolean canAudio = true;
	private boolean canCamera = false;
	private LocationManager locMan;
	private Location lastLoc = null;
	private boolean listening = false;

	public static final int FIRST_FIX = 0x12343567;
	public static final int GPS_ENABLED = 0x12343568;
	public static final int GPS_DISABLED = 0x12343569;

	private Collection<LocationListener> gpsListeners;

	public Hardware(Context ctx) {
		gpsListeners = new HashSet<LocationListener>();
		start(ctx);
	}

	public void update(Context ctx) {
		try {
			hasGps = locMan.getAllProviders().contains(
					LocationManager.GPS_PROVIDER);
		} catch (Exception e) {
			Utils.logw("Hardware.update", "failed to query gps", e);
			hasGps = false;
		}
		gpsEnabled = locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
		canCamera = isCameraAvailable(ctx);
	}

	private boolean isCameraAvailable(Context context) {
		// check physical camera existance
		final PackageManager pacMan = context.getPackageManager();
		if( !pacMan.hasSystemFeature(PackageManager.FEATURE_CAMERA)) 
			return false;
		
		// check application that can shot
		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		List<ResolveInfo> list = pacMan.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;

	}

	public void start(Context ctx) {
		locMan = (LocationManager) ctx
				.getSystemService(Context.LOCATION_SERVICE);
		update(ctx);
		startListening();
	}

	public void stop() {
		stopListening();
		locMan = null;
	}

	public boolean canRecordAudio() {
		return canAudio;
	}

	public boolean canCamera() {
		return canCamera;
	}

	public boolean canGPS() {
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
			Utils.logd("Hardware.ll.onStatusChanged", "provider=" + provider
					+ "; status=" + status);
			for (LocationListener l : gpsListeners)
				l.onStatusChanged(provider, status, extras);

		}

		@Override
		public void onProviderEnabled(String provider) {
			for (LocationListener l : gpsListeners)
				l.onProviderEnabled(provider);
			if (LocationManager.GPS_PROVIDER.equals(provider)) {
				gpsEnabled = true;
				Utils.logd(this, "Provider enabled");
				onStatusChanged(provider, LocationProvider.AVAILABLE, null);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			for (LocationListener l : gpsListeners)
				l.onProviderDisabled(provider);
			if (LocationManager.GPS_PROVIDER.equals(provider)) {
				onStatusChanged(provider, LocationProvider.OUT_OF_SERVICE, null);
				gpsEnabled = false;
				lastLoc = null;
			}
			Utils.logd(this, "Provider disabled");
		}

		@Override
		public void onLocationChanged(Location location) {
			// Utils.logd(this, "Location changed");
			if (!gpsEnabled) {
				onStatusChanged(location.getProvider(),
						LocationProvider.AVAILABLE, null);
				gpsEnabled = true;
			}
			if (lastLoc == null) {
				lastLoc = location;
				onStatusChanged(location.getProvider(), FIRST_FIX, null);
			}
			lastLoc = location;
			for (LocationListener l : gpsListeners)
				l.onLocationChanged(location);
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
		public void onLocationChanged(Location location) {
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

	}

	public void clearListeners() {
		gpsListeners.clear();

	}

}
