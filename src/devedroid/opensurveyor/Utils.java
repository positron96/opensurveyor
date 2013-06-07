package devedroid.opensurveyor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {
	
	private static SimpleDateFormat sdfISOTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
	static {
		sdfISOTime.setTimeZone( TimeZone.getTimeZone("UTC" ));
	}
	
	public static String formatTime(Date dd) {
		return sdfISOTime.format(dd);
	}

}
