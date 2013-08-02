package devedroid.opensurveyor.data;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import devedroid.opensurveyor.presets.AudioRecordPreset;
import devedroid.opensurveyor.presets.BasePreset;

import android.content.res.Resources;

public abstract class MarkerWithExternals extends Marker {
	protected String fileName;
	
	public MarkerWithExternals(BasePreset t) {
		super(t);
	}

	
	public void setFileName(String f) {
		fileName = f;
	}

	public void deleteExternals() throws IOException { 
		(new File(fileName)).delete();
	}
	
	public abstract ExternalPackage getExternals() ;

}
