package devedroid.opensurveyor.data;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

public interface ExternalPackage {
	
	public void saveExternals(ZipOutputStream out) throws IOException;
	
}
