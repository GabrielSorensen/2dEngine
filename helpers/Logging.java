package helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Logging {

	public enum LOG_LEVEL{
		ERROR(2), INFO(1);
		private int value;
		private LOG_LEVEL(int value) {
			this.value = value;
		}
		public int getValue(){
			return value;
		}
	}

	private static String logFile = "";

	public static String log(String string) {
		return log(string, false, LOG_LEVEL.INFO);
	}
	
	public static String log(String string, boolean printToSYSOUT) {
		return log(string, printToSYSOUT, LOG_LEVEL.INFO);
	}

	public static String log(String string, boolean printToSYSOUT, LOG_LEVEL level) {
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DateFormat df2 = new SimpleDateFormat("yyyy_MM_dd");
		Date today = Calendar.getInstance().getTime();        
		String sDate = df.format(today);
		String write = sDate + ", " + string + "\n";
		String logString = write;

		logFile = "./log"+ df2.format(today) +".txt";

		if (printToSYSOUT) {
			switch(level) {

			case INFO:
				System.out.println(write);
				break;

			case ERROR:
				System.err.println(write);
				break;
			}
		}

		try {
			File log = new File(logFile);

			if (!log.exists()) {
				log.createNewFile();
			}
			if (log.length() >= 500000000l) { //should not fill two logfiles per day, hopefully... ESP if the max is half a GB.
				int dotPlacement = logFile.lastIndexOf(".");
				logFile = logFile.substring(0, dotPlacement) + "(2)" + logFile.substring(dotPlacement, logFile.length());
			}
			if (!log.exists() || !log.canWrite()) {
				throw new FileNotFoundException("Could not find or create the Log");
			}
			Charset charset = Charset.forName("UTF-8");
			OpenOption app = StandardOpenOption.APPEND;
			try (BufferedWriter writer = Files.newBufferedWriter(log.toPath(), charset, app)) {
				writer.write(write, 0, write.length());
			} catch (IOException x) {
				logError(x, true);
			}
		} catch (FileNotFoundException notFound) {
			//skip trying to write the log this time.
			//Logging.logError(notFound, false); 
			//NOTE!!! if writing once doesn't work this wont either.
			//	and would get us stuck in a loop.
			notFound.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			//Logging.logError(e, true);
			//NOTE!!! if writing once doesn't work this wont either.
			//	and would get us stuck in a loop.
		}
		return logString;
	}

	public static String log(String string, LOG_LEVEL level) {
		return log(string, false, LOG_LEVEL.INFO);
	}
	
	public static void logError(Exception error, boolean alsoPrintStacktrace) {
		if (alsoPrintStacktrace) {
			error.printStackTrace();
		}
		log("Exception happened. Type: "+error.getCause()+". Message: "+error.getMessage(), false, LOG_LEVEL.INFO);
	}

	public static void silentLog(String string) {
		log(string, false, LOG_LEVEL.INFO);
	}

}
