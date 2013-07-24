package devedroid.opensurveyor.data;

import java.io.IOException;
import java.io.Writer;

import devedroid.opensurveyor.AudioRecordPreset;
import devedroid.opensurveyor.TextPreset;

public class AudioRecordMarker extends Marker {
	
	private String fileName;
	private long duration;

	public AudioRecordMarker(AudioRecordPreset t) {
		super(t);
	}
	
	public void setFileName(String f) {
		fileName = f;
	}
	
	@Override
	public String getDesc() {
		return "Audio ("+(duration/1000)+"s) ";
	}

	@Override
	protected void writeDataPart(Writer w) throws IOException {
		w.append("\t\t<attachment type=\"audio/3gp\" src=\"").append(fileName).append("\"/>\n");
	}

	@Override
	public void addProperty(PropertyDefinition key, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getProperty(PropertyDefinition name) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setStopTime() {
		duration = System.currentTimeMillis() - super.timeStamp;
	}

}
