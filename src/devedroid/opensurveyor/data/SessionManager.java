package devedroid.opensurveyor.data;


public interface SessionManager {
	
	public void newSession() ;
	
	public boolean isSessionRunning() ;

	public void finishSession();

	public void saveSession();
	
	public void exportSession();

	public void addMarker(Marker poi);
	
	public Iterable<Marker> getMarkers();
	public Marker getMarker(int i);
	public int getMarkerCount();
	
}
