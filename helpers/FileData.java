package helpers;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;

public class FileData {

	public static ByteArrayBuffer getFileBytes(String path) {
		FileInputStream fin = null;
		BufferedInputStream bin = null;
		try
		{
			fin = new FileInputStream(path);
			bin = new BufferedInputStream(fin);
			//do things with the things
			byte [] b = new byte [bin.available()];
			bin.read(b);
			//Don't forget to clean up resources.
			ByteArrayBuffer buff = new ByteArrayBuffer(b);
			fin.close();
			bin.close();
			return buff;
		} catch (FileNotFoundException e) {
			Logging.logError(e, true);
		} catch (IOException e) {
			Logging.logError(e, true);
		}
		return null;	
	}

}
