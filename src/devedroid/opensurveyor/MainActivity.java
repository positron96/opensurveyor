package devedroid.opensurveyor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;

import devedroid.opensurveyor.data.Marker;
import devedroid.opensurveyor.data.Session;
import devedroid.opensurveyor.data.SessionManager;

public class MainActivity extends SherlockFragmentActivity implements SessionManager {
	private Session sess;
	private Hardware hw;
	private Fragment cFragment;
	private int cFragmentIndex;
	private ShareActionProvider shareActionProvider;
	
	private static final String FRAG_BUTT = "ButtUI",
			FRAG_MAP = "MapUI";
	private static final String[] FRAGMENT_TAGS = {FRAG_BUTT, FRAG_MAP};
	
	private static final String PREF_UI = "ui";
	private static final String PREF_SESSION = "session";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar ab = getSupportActionBar();
		
		ab.setDisplayShowTitleEnabled(false);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		ab.setListNavigationCallbacks(new ActionBarSpinner(this), new ActionBar.OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				setFragment(itemPosition);
				return true;
			}
		});
		
		if(savedInstanceState!=null) {
			//setFragment( );
			ab.setSelectedNavigationItem(savedInstanceState.getInt(PREF_UI));
			Session ss = (Session)savedInstanceState.getSerializable(PREF_SESSION);
			Utils.logd("MainActivity", "Session loaded: "+ss);
			installSession( ss );
		} else {
			SharedPreferences pref = getSharedPreferences(getPackageName(), 0);
			ab.setSelectedNavigationItem( pref.getInt(PREF_UI, 0) );
			newSession();
		}
	}
	
	private void setFragment(int itemPos) {
		Fragment newFragment = null;
		//Utils.logd("MainActivity", "nav item "+itemPos);
		if(itemPos==0) {
			newFragment = new ButtonUIFragment();
		} else if (itemPos == 1) {
			newFragment = new MapFragment();
		}
		cFragment = newFragment;
		cFragmentIndex = itemPos;
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, newFragment, FRAGMENT_TAGS[itemPos]);
		ft.commit();
	}
	
	public void onStart() {
		super.onStart();
		//if(sess==null) newSession();
	}
	
	public void onResume() {
		super.onResume();
		hw = new Hardware(this);
		hw.addListener( new Hardware.SimpleLocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				invalidateOptionsMenu();
			}
			@Override
			public void onLocationChanged(Location location) {
				//if( hw.getLastLocation()==null )
				//	invalidateOptionsMenu();
			}

		});
	}
	
	@Override
	public void onPause() {
		super.onPause();
		hw.stop();
		hw.clearListeners();
		Utils.logd(this, "State saving should be here");
		
		SharedPreferences pref = getSharedPreferences(getPackageName(), 0);
		Editor ed = pref.edit();
		ed.putInt(PREF_UI, cFragmentIndex);
		ed.commit();
	}
	
	@Override
	public void onSaveInstanceState(Bundle b) {
		Utils.logd(this, "Saving ui index "+cFragmentIndex);
		b.putInt(PREF_UI, cFragmentIndex);
		if(sess!=null)
			b.putSerializable(PREF_SESSION, sess);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.win_main, menu);
		shareActionProvider = (ShareActionProvider)(menu.findItem(R.id.mi_share).getActionProvider());
		//shareActionProvider.setShareHistoryFileName("my_test.xml"); 
		return true;
	}
	
	public boolean onOptionsItemSelected (MenuItem item) {
		switch(item.getItemId()) {
			case R.id.mi_start_session: 
				newSession();
				item.setEnabled(false);
				
				break;
			case R.id.mi_stop_save_session: 
				item.setEnabled(false);
				finishSession();
				saveSession();
				break;
		}
		return true;
	}
	
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean v = isSessionRunning();
		menu.findItem(R.id.mi_start_session).setEnabled(!v);
		menu.findItem(R.id.mi_stop_save_session).setEnabled(v);
		MenuItem ii = menu.findItem(R.id.mi_gps);
		if(hw!=null) 
			if(hw.canGPS()) {
				ii.setVisible( true );
				if(hw.isGPSEnabled())
					if(hw.hasFix())
						ii.setIcon( R.drawable.ic_ab_goodgps);
						//ii.setTitle("++");
					else 
						ii.setIcon( R.drawable.ic_ab_gps);
						//ii.setTitle("+");
				else 
					ii.setIcon(R.drawable.ic_ab_nogps);
					//ii.setTitle("-");
			} else {
				ii.setVisible(false);
			}
			
		return super.onPrepareOptionsMenu(menu);
	}
	
	public void newSession() {
		installSession( new Session() );
	}
	
	private void installSession(Session sess) {
		this.sess = sess;
		invalidateOptionsMenu();
		
		ButtonUIFragment fr1 = 
				(ButtonUIFragment)(getSupportFragmentManager().findFragmentByTag(FRAG_BUTT));
		if(fr1!=null) fr1.onNewSession();
	}
	
	public boolean isSessionRunning() {
		return sess!=null && sess.isRunning();
	}

	public void finishSession() {
		ButtonUIFragment fr1 = 
				(ButtonUIFragment)(getSupportFragmentManager().findFragmentByTag(FRAG_BUTT));
		if(fr1!=null) fr1.onFinishSession()	;
		sess.finish();
		invalidateOptionsMenu();
	}

	public void saveSession() {
		try {
			Writer w = new FileWriter(new File(
					Environment.getExternalStorageDirectory(), "surveyor.xml"));
			sess.writeTo(w);
			w.flush();
			Toast.makeText(this,
					"Successfully saved to 'surveyor.xml' on SD card",
					Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Toast.makeText(this, "" + e, Toast.LENGTH_LONG).show();
		}
	}

	public void addMarker(Marker poi) {
		//Toast.makeText(this, "Added poi "+poi, Toast.LENGTH_LONG).show();
		
		if(hw.canGPS() && hw.isGPSEnabled() && hw.hasFix() )
			poi.setLocation( hw.getLastLocation() );
		
		sess.addMarker(poi);
		ButtonUIFragment fr1 = 
				(ButtonUIFragment)(getSupportFragmentManager().findFragmentByTag("ButtUI"));
		if(fr1!=null) fr1.onPoiAdded(poi);
		
		
	}
	
	public Iterable<Marker> getMarkers() {
		return sess.getMarkers();
	}
	
	public Fragment getCurrentFragment() {
		return cFragment;
	}

}
