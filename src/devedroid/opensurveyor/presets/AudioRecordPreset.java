package devedroid.opensurveyor.presets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import devedroid.opensurveyor.R;
import devedroid.opensurveyor.Utils;
import devedroid.opensurveyor.data.AudioRecordMarker;
import devedroid.opensurveyor.data.PropertyDefinition;
import devedroid.opensurveyor.data.SessionManager;

public class AudioRecordPreset extends BasePreset {

	private MediaRecorder rec;
	
	private AudioRecordMarker currentMarker;

	public AudioRecordPreset(Resources res) {
		super(res.getString(R.string.preset_audio));
	}

	@Override
	public boolean isToggleButton() {
		return false;
	}

	@Override
	public boolean isDirected() {
		return true;
	}

	private synchronized AudioRecordMarker startRecord() throws IOException {
		if(currentMarker!=null || rec!=null) 
			throw new IllegalStateException("Recording while not terminated previous recording");
		
		currentMarker = new AudioRecordMarker(AudioRecordPreset.this);
		
		String ff = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/OpenSurveyor/rec" + System.currentTimeMillis() + ".3gp";
		currentMarker.setFileName(ff);
		new File(ff).getParentFile().mkdirs();
		
		rec = new MediaRecorder();
		rec.setAudioSource(MediaRecorder.AudioSource.MIC);
		rec.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		rec.setOutputFile(ff);
		rec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			rec.prepare();
		} catch (IOException e) {
			Utils.loge(this+".startRecord", e);

			currentMarker = null;
			rec.release();
			rec=null;
			throw e;
		}
		rec.start();
		
		
		
		return currentMarker;
	}
	
	public synchronized boolean isRecording() {
		return rec!=null;
	}

	public synchronized void stopRecord() {
		if(rec==null) return;
		rec.stop();
		rec.release();
		rec = null;
		currentMarker.setStopTime();
		currentMarker = null;
	}

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
				try {
					AudioRecordMarker mm = startRecord();
					if (isDirected())
						mm.setDirection(btl.dir);
					sm.addMarker(mm);
				}catch(IOException e) {
					Utils.toast(context, "Could not start recording: "+e);
				}
			}
		});
		return res;
	}

	private List<PropertyDefinition> props = new ArrayList<PropertyDefinition>();

	@Override
	public List<PropertyDefinition> getProperties() {
		return props;
	}
	
	@Override
	public boolean needsPropertyWindow() {
		return true;
	}

}
