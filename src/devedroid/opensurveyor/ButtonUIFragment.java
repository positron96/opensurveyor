package devedroid.opensurveyor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apmem.tools.layouts.FlowLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import devedroid.opensurveyor.data.POI;
import devedroid.opensurveyor.data.TextPOI;

public class ButtonUIFragment extends SherlockFragment {

	private MainActivity parent;
	private View root;
	private FlowLayout flow;
	private ListView hist;
	//private List<String> lhist;
	private ArrayAdapter<POI> ahist;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		root = inflater.inflate(R.layout.frag_buttons, container, false);

		flow = (FlowLayout) root.findViewById(R.id.flow);

		hist = (ListView) root.findViewById(R.id.l_history);
		List<POI> lhist = new ArrayList<POI>();
		ahist = new ArrayAdapter<POI>(root.getContext(),
				R.layout.item_poi, lhist) {
			private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS");
			
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					convertView = View.inflate(parent.getContext(), R.layout.item_poi, null);
				}
				POI item = getItem(position);
				TextView tw = (TextView) convertView.findViewById(R.id.text1);
				tw.setText(""+sdf.format(new Date( item.getTimestamp() ) ) );
				
				TextView tw2 = (TextView) convertView.findViewById(R.id.text2);
				tw2.setText(item.getTitle() );
				
				TextView tw3 = (TextView) convertView.findViewById(R.id.location);
				tw3.setText(item.hasLocation()?"gps":"");
				return convertView;
			}
		};
		hist.setAdapter(ahist);

		Display display = getSherlockActivity().getWindowManager()
				.getDefaultDisplay();
		int width = display.getWidth() - flow.getPaddingLeft() - flow.getPaddingRight(); 
		int height = display.getHeight()- hist.getHeight() - flow.getPaddingTop() - flow.getPaddingBottom();
		
		width = width / 3 - 10;
		height = height / 3 - 10;
		width = Math.min(width,  height);
		height = width;
		System.out.println("widtrh=" + width + " height=" + height);

		for (int i = 0; i < 9; i++) {
			Button bt = new Button(root.getContext());
			//FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(width*4/5, height/4);
			
			bt.setText("#"+i);
			bt.setWidth(width);
			bt.setHeight(height);
			bt.setId(1000+i);
			bt.setTag(Integer.valueOf(i));
			bt.setOnClickListener( new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					clickButton((Button)v);					
				}
			});
			//bt.setBackgroundColor(0xFFFF0000);
			
			flow.addView(bt);// , lp);
		}

		return root;
	}
	
	public void clickButton(Button bt) {
		//Toast.makeText(parent, "Button clicked: ", Toast.LENGTH_LONG).show();
		parent.addPOI( new TextPOI("This is POI "+bt.getTag(), "Clicked button "+bt.getText() ));
	}

	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		parent = (MainActivity) a;
	}

	public void onNewSession() {
		// ((Button) root.findViewById(R.id.bt_new_session)).setEnabled(false);
		// ((Button) root.findViewById(R.id.bt_add_text)).setEnabled(true);
		// ((Button) root.findViewById(R.id.bt_finish)).setEnabled(true);
	}

	public void onFinishSession() {
		// ((Button) root.findViewById(R.id.bt_new_session)).setEnabled(true);
		// ((Button) root.findViewById(R.id.bt_add_text)).setEnabled(false);
		// ((Button) root.findViewById(R.id.bt_finish)).setEnabled(false);
	}
	
	public void onPoiAdded(POI poi) {
		ahist.add(poi);
	}

	public void addPOIGui() {
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
