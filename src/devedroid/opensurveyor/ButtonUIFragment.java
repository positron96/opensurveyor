package devedroid.opensurveyor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import devedroid.opensurveyor.data.Session;
import devedroid.opensurveyor.data.TextPOI;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ButtonUIFragment extends Fragment {

	private MainActivity parent;
	private View root;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
	    // Inflate the layout for this fragment
	    root = inflater.inflate(R.layout.frag_buttons, container, false);
	    parent = (MainActivity)getActivity();
		
		Button btAddPOI = (Button) root.findViewById(R.id.bt_add_text);
		btAddPOI.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				addPOI();
			}
		});
		btAddPOI.setEnabled(false);

	    
	    return root;
	}

	public void onNewSession() {
		//((Button) root.findViewById(R.id.bt_new_session)).setEnabled(false);
		((Button) root.findViewById(R.id.bt_add_text)).setEnabled(true);
		//((Button) root.findViewById(R.id.bt_finish)).setEnabled(true);
	}

	public void onFinishSession() {
		//((Button) root.findViewById(R.id.bt_new_session)).setEnabled(true);
		((Button) root.findViewById(R.id.bt_add_text)).setEnabled(false);
		//((Button) root.findViewById(R.id.bt_finish)).setEnabled(false);
	}
	
	public void addPOI() {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("New POI");
		alert.setMessage("Set title");
		
		final EditText input = new EditText(getActivity());
		input.setText("I'm a new POI");
		alert.setView(input);
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
 			public void onClick(DialogInterface dialog, int whichButton) {
 				String value = input.getText().toString();
 				parent.addPOI(new TextPOI(value, ""));
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


}
