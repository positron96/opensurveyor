package devedroid.opensurveyor.data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import devedroid.opensurveyor.Utils;

public class Session {
	
	private long startTime, endTime;
	
	private List<POI> pois = new ArrayList<POI>();
	
	public Session() {
		startTime = System.currentTimeMillis();
	}
	
	public void addPOI(POI poi) {
		pois.add(poi);
	}
	
	public void finish() {
		endTime = System.currentTimeMillis();
	}
	
	public void writeTo(Writer os) throws IOException {
		os.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
		os.write("<survey " +
				"start=\""+Utils.formatTime(new Date(startTime))+"\" " +
				"end=\""+Utils.formatTime(new Date(endTime))+"\">");
		for(POI p: pois) {
			os.write("  ");
			p.writeXML(os);
		}
		os.write("</survey>\n");
	}

}
