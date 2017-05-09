package helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Base64;

import helpers.Logging.LOG_LEVEL;

public class CerealDispenser {

	//NOTE: this is just a serializable helper, borrowed from so I don;t have to write this
	//http://stackoverflow.com/questions/134492/how-to-serialize-an-object-into-a-string

	public static Object fromFile(File f) {
		try {
			File oldFile = f;

			if (!oldFile.exists()) {
				Logging.log("GLOBALS file did not exist, setting things to default, and creating new file", true);
				oldFile.createNewFile();
			} else {
				@SuppressWarnings("resource")
				String s = new BufferedReader(new FileReader(oldFile)).readLine();
				if (s == null) {
					throw new IOException("Read a blank from the file, File possibly corrupted");
				}
				return CerealDispenser.fromString(s);
			}

		} catch (FileNotFoundException notFound) {
			//skip trying to write the log this time.
		} catch (IOException e) {
			Logging.logError(e, true);
		} catch (ClassNotFoundException e) {
			Logging.log("Could not build the class from the file: "+e, true, LOG_LEVEL.ERROR);
		}
		return null;
	}

	/** Read the object from Base64 string. */
	public static Object fromString( String s ) throws IOException ,
	ClassNotFoundException {
		byte [] data = Base64.getDecoder().decode( s );
		ObjectInputStream ois = new ObjectInputStream( 
				new ByteArrayInputStream(  data ) );
		Object o  = ois.readObject();
		ois.close();
		return o;
	}
	
	public static boolean toFile(File f, Serializable o) {
		try {
			File file = f;
			
			if (!file.exists()) {
				file.createNewFile();
			}
			if (!file.exists() || !file.canWrite()) {
				throw new FileNotFoundException("Could not find or create the globals file");
			}
			Charset charset = Charset.forName("UTF-8");
			try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), charset)) {
				String cereal = CerealDispenser.toString(o);
				Logging.log("cereal version of globals: "+cereal, false);
			    writer.write(cereal, 0, cereal.length());
			    return true;
			} catch (IOException x) {
				Logging.logError(x, true);
			    return false;
			}
		} catch (FileNotFoundException notFound) {
			//skip trying to write the log this time.
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Logging.logError(e, true);
			return false;
		}
	}
	
	/** Write the object to a Base64 string. */
	public static String toString( Serializable o ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream( baos );
		oos.writeObject( o );
		oos.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray()); 
	}

}
