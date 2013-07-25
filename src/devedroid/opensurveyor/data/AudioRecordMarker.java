package devedroid.opensurveyor.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
		return "Audio (" + (duration / 1000) + "s) ";
	}

	@Override
	protected void writeDataPart(Writer w) throws IOException {
		w.append("\t\t<attachment type=\"audio/3gp\" src=\"").append(fileName)
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
	}

	@Override
	public boolean containsExternals() {
		return true;
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
