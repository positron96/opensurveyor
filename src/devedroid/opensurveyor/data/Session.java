package devedroid.opensurveyor.data;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import devedroid.opensurveyor.Utils;

public class Session implements Serializable {

	private long startTime, endTime=-1;
	private int externalCount;
	
	private List<Marker> markers = new ArrayList<Marker>();
	
	public Session() {
		startTime = System.currentTimeMillis();
	}
	
	public void addMarker(Marker poi) {
		markers.add(poi);
		if(poi instanceof MarkerWithExternals) externalCount++;
	}
	
	public void finish() {
		endTime = System.currentTimeMillis();
	}
	
	public boolean isRunning() {
		return endTime==-1;		
	}
	
	public static final String FILE_EXT = ".svx";
	public static final String FILE_EXT_ARCHIVE = ".svp";
	
	public void exportArchive(File file) throws IOException {
		FileOutputStream fo = new FileOutputStream(file);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(fo));
		ZipEntry ee = new ZipEntry("survey"+FILE_EXT);
		out.putNextEntry(ee);
		writeTo(new OutputStreamWriter(out));
		out.closeEntry();
		for(Marker p: markers) {
			if(p instanceof MarkerWithExternals) {
				((MarkerWithExternals)p).getExternals().saveExternals(out);
			}
		}
		out.close();
	}
	
	public boolean hasExternals() {
		return externalCount!=0;
	}
	
	public void writeTo(Writer os) throws IOException {
		os.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
		os.write("<survey " +
				"start=\""+Utils.formatISOTime(new Date(startTime))+"\" " +
				"end=\""+Utils.formatISOTime(new Date(endTime))+"\">\n");
		for(Marker p: markers) {
			os.write("  ");
			p.writeXML(os);
		}
		os.write("</survey>\n");
		os.flush();
	}
	
	public Iterable<Marker> getMarkers() {
		return markers;
	}
	
	public int markerCount() {
		return markers.size();
	}
	
	public Marker getMarker(int index) {
		return markers.get(index);
	}
	/**
	 * 
	 * @throws IOException when marker contains externals and there was an error cleaning up
	 */
	public Marker deleteMarker(int index) throws IOException {
		Marker m = markers.remove(index);
		if(m instanceof MarkerWithExternals) {
			externalCount--;
			((MarkerWithExternals)m).deleteExternals();
		}
		return m;
	}
	public void deleteMarker(Marker m) {
		markers.remove(m);
	}


}
