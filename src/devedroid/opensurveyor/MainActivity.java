package devedroid.opensurveyor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import devedroid.opensurveyor.data.POI;
import devedroid.opensurveyor.data.Session;
import devedroid.opensurveyor.data.TextPOI;

public class MainActivity extends SherlockFragmentActivity {
	private Session sess;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar ab = getSupportActionBar();
		
		ab.setDisplayShowTitleEnabled(false);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		SpinnerAdapter sp = ArrayAdapter.createFromResource(this, 
				R.array.arr_uis,
				R.layout.sherlock_spinner_dropdown_item);
				//android.R.layout.simple_spinner_dropdown_item);
		final String[] strings = {"ButtUI", "MapUI"};
		
		ab.setListNavigationCallbacks(sp, new ActionBar.OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				Fragment newFragment = null;
				if(itemPosition==0) {
					newFragment = new ButtonUIFragment();
				} else if (itemPosition == 1) {
					newFragment = new MapFragment();
				}
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(android.R.id.content, newFragment, strings[itemPosition]);
				ft.commit();
				return true;
			}
		});
	}
	
	public void onStart() {
		super.onStart();
		newSession();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.win_main, menu);
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
		return super.onPrepareOptionsMenu(menu);
	}
	
	public void newSession() {
		sess = new Session();
		invalidateOptionsMenu();
		
		ButtonUIFragment fr1 = 
				(ButtonUIFragment)(getSupportFragmentManager().findFragmentByTag("ButtUI"));
		if(fr1!=null) fr1.onNewSession();		

	}
	
	public boolean isSessionRunning() {
		return sess!=null && sess.isRunning();
	}

	public void finishSession() {
		ButtonUIFragment fr1 = 
				(ButtonUIFragment)(getSupportFragmentManager().findFragmentByTag("ButtUI"));
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

	public void addPOI(POI poi) {
		Toast.makeText(this, "Added poi "+poi, Toast.LENGTH_LONG).show();
		sess.addPOI(poi);
		
	}

}
