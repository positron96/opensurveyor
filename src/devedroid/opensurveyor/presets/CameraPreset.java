package devedroid.opensurveyor.presets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import devedroid.opensurveyor.R;
import devedroid.opensurveyor.data.PictureMarker;
import devedroid.opensurveyor.data.PropertyDefinition;
import devedroid.opensurveyor.data.SessionManager;

public class CameraPreset extends BasePreset {

	public CameraPreset(Resources res) {
		super(res.getString(R.string.preset_photo));
	}

	@Override
	public boolean isToggleButton() {
		return false;
	}

	@Override
	public boolean isDirected() {
		return true;
	}
	private String cFilename;
	
	public Intent getCameraIntent() {
		Intent res = new Intent(MediaStore.ACTION_IMAGE_CAPTURE );
		File ff = new File(cFilename);
		ff.getParentFile().mkdirs();
		res.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(ff));
		return res;
	}
	
	public static final int CAMERA_REQUEST = 0x1234;

	@Override
	public Button createButton(final Context context, final SessionManager sm) {
		final Button res;
		res = new Button(context);
		res.setTag(this);
		res.setText(title);
		final ButtonTouchListener btl = new ButtonTouchListener(res);
		if (isDirected())
			res.setOnTouchListener(btl);

		res.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					cFilename = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/OpenSurveyor/photo" + System.currentTimeMillis() + ".jpg";
					PictureMarker mm = new PictureMarker(CameraPreset.this);
					mm.setFileName(cFilename);
					if (isDirected())
						mm.setDirection(btl.dir);
					sm.addMarker(mm);
			}
		});
		return res;
	}

	private List<PropertyDefinition> props = new ArrayList<PropertyDefinition>();

	@Override
	public List<PropertyDefinition> getProperties() {
		return props;
	}

}
