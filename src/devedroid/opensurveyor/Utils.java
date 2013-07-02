package devedroid.opensurveyor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;

import android.util.Log;

public class Utils {
	
	private static SimpleDateFormat sdfISOTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
	static {
		sdfISOTime.setTimeZone( TimeZone.getTimeZone("UTC" ));
	}
	
	public static String formatISOTime(Date dd) {
		return sdfISOTime.format(dd);
	}
	
	
	public final static Level level = Level.FINE;
	
	public static void logd(Object src, String mes) {
		if(level.intValue() >= Level.FINE.intValue() )
			Log.d("opensurveyor", src+": "+mes);
	}

	public static void logw(Object src, String mes, Exception e) {
		if(level.intValue() >= Level.FINE.intValue() )
			Log.w("opensurveyor", src+": "+mes, e);
		
	}

}
