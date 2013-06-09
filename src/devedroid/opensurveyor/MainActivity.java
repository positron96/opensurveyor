package devedroid.opensurveyor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import devedroid.opensurveyor.data.Session;
import devedroid.opensurveyor.data.TextPOI;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Session sess;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btNewSession = (Button) findViewById(R.id.bt_new_session);
		btNewSession.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				newSession();
			}
		});

		Button btAddPOI = (Button) findViewById(R.id.bt_add_text);
		btAddPOI.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				int dir= ((SwipeButton)v).getSelectedDirection();
				addPOI(dir);
			}
		});
		btAddPOI.setEnabled(false);
		Button btAddPOI2 = (Button) findViewById(R.id.bt_add_poi2);
		btAddPOI2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				int dir= ((SwipeButton)v).getSelectedDirection();
				addPOI2(dir);
			}
		});
		btAddPOI2.setEnabled(false);

		Button btFinish = (Button) findViewById(R.id.bt_finish);
		btFinish.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finishSession();
				saveSession();
			}
		});
		btFinish.setEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.win_main, menu);
		return true;
	}

	public void newSession() {
		sess = new Session();
		((Button) findViewById(R.id.bt_new_session)).setEnabled(false);
		((Button) findViewById(R.id.bt_add_text)).setEnabled(true);
		((Button) findViewById(R.id.bt_add_poi2)).setEnabled(true);
		((Button) findViewById(R.id.bt_finish)).setEnabled(true);

	}

	public void finishSession() {
		((Button) findViewById(R.id.bt_new_session)).setEnabled(true);
		((Button) findViewById(R.id.bt_add_text)).setEnabled(false);
		((Button) findViewById(R.id.bt_add_poi2)).setEnabled(false);
		((Button) findViewById(R.id.bt_finish)).setEnabled(false);
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

	public void addPOI(int dir) {
		//Toast.makeText(this, "poi added", Toast.LENGTH_SHORT).show();
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("New POI");
		alert.setMessage("Set title"+(dir!=0?" (dir="+dir+")":""));

		final EditText input = new EditText(this);
		input.setText("I'm a new POI");
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				sess.addPOI(new TextPOI(value, ""));
			}
		});
		alert.setNegativeButton("Cancel",
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});
		alert.show();
	}
	public void addPOI2(int dir) {
		Toast.makeText(this, "poi added, dir="+dir, Toast.LENGTH_SHORT).show();
		sess.addPOI(new TextPOI("a poi", "dir="+dir));
		
	}
}
