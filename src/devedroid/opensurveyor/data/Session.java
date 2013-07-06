package devedroid.opensurveyor.data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import devedroid.opensurveyor.Utils;

public class Session implements Serializable {

	private long startTime, endTime=-1;
	
	private List<Marker> markers = new ArrayList<Marker>();
	
	public Session() {
		startTime = System.currentTimeMillis();
	}
	
	public void addMarker(Marker poi) {
		markers.add(poi);
	}
	
	public void finish() {
		endTime = System.currentTimeMillis();
	}
	
	public boolean isRunning() {
		return endTime==-1;		
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
	}
	
	public Iterable<Marker> getMarkers() {
		return markers;
	}


}
