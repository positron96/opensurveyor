package devedroid.opensurveyor;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.actionbarsherlock.widget.ShareActionProvider;

import devedroid.opensurveyor.data.SessionManager;

public class ShareSessionActionProvider extends ShareActionProvider {

	private Intent shareIntent;
	private SessionManager session;
	
	public ShareSessionActionProvider(Context context) {
		super(context);
		
		shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("application/octet-stream");
		setShareIntent(shareIntent);
		setOnShareTargetSelectedListener( new OnShareTargetSelectedListener() {
			
			@Override
			public boolean onShareTargetSelected(ShareActionProvider source,Intent intent) {
				session.exportSession();
				return false;
			}
		});
	}
	
	public void updateIntent(File ff) {
		shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(ff) );
		setShareIntent(shareIntent);
	}
	
	public Intent getShareIntent() {
		return shareIntent;
	}
	
	public Intent performShareActivity() {
		session.exportSession();
		return Intent.createChooser(shareIntent, "Choose action:");
	}
	
	public void setSession(SessionManager sess) {
		session = sess;
		
	}
	
	public  boolean onPerformDefaultAction() {
		
		return super.onPerformDefaultAction();
	}

}