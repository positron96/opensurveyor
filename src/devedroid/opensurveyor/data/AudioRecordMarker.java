package devedroid.opensurveyor.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.content.res.Resources;
import devedroid.opensurveyor.R;
import devedroid.opensurveyor.presets.AudioRecordPreset;

public class AudioRecordMarker extends MarkerWithExternals {

	private long duration;
	private boolean recordFinished = false;

	public AudioRecordMarker(AudioRecordPreset t) {
		super(t);
	}

	@Override
	public String getDesc(Resources res) {
		return res.getString(R.string.poi_audio, duration / 1000 );
	}

	@Override
	protected void writeDataPart(Writer w) throws IOException {
		w.append("\t\t<attachment type=\"audio/3gp\" src=\"").append(new File(fileName).getName() )
				.append("\"/>\n");
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
		recordFinished = true;
	}
	
	public boolean isRecordFinished() {
		return recordFinished;
	}
	
	public long getDuration() {
		return duration;
	}
	
	@Override
	public ExternalPackage getExternals() {
		return new AudioExternals();
	}

	public class AudioExternals implements ExternalPackage {

		@Override
		public void saveExternals(ZipOutputStream out) throws IOException {
			File ff = new File(fileName);
			ZipEntry ze = new ZipEntry(ff.getName());
			out.putNextEntry(ze);
			FileInputStream fi = new FileInputStream(ff);
			int count = 0;
			byte data[] = new byte[1024];
			while ((count = fi.read(data, 0, data.length)) != -1) {
				out.write(data, 0, count);
			}
			out.closeEntry();
		}

	}

}
