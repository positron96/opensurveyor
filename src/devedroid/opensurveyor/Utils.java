package devedroid.opensurveyor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Utils {
	
	private static SimpleDateFormat sdfISOTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
	static {
		//sdfISOTime.setTimeZone( TimeZone.getTimeZone("UTC" ));
	}
	
	public static String formatISOTime(Date dd) {
		return sdfISOTime.format(dd);
	}
	
	
	public final static Level level = Level.FINE;
	
	public static void logi(Object src, String mes) {
		if(level.intValue() <= Level.INFO.intValue() )
			Log.i("opensurveyor", src+": "+mes);
	}
	
	public static void logd(Object src, String mes) {
		if(level.intValue() <= Level.FINE.intValue() )
			Log.d("opensurveyor", src+": "+mes);
	}

	public static void logw(Object src, String mes, Exception e) {
		if(level.intValue() <= Level.WARNING.intValue() )
			Log.w("opensurveyor", src+": "+mes, e);		
	}
	public static void logw(Object src, String mes) {
		if(level.intValue() <= Level.WARNING.intValue() )
			Log.w("opensurveyor", src+": "+mes);		
	}
	
	public static void loge(Object src, Exception e) {
		if(level.intValue() <= Level.SEVERE.intValue() )
			Log.e("opensurveyor", src+": "+e, e);
		
	}
	
	public static void toast(Context ctx, String mes) {
		Toast.makeText(ctx, mes, Toast.LENGTH_LONG).show();
		logd("Toast",mes);
	}

}
