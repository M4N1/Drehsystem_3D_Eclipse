package drehsystem3d;

import java.util.logging.Logger;

public class Global
{
	public static final String VERSION = "0.0.3";
	public static final String OS = System.getProperty("os.name");
	private static final String os = OS.toLowerCase();
	public static final boolean isUnix = os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0;
	public static final boolean isWindows = os.indexOf("win") >= 0;
	public static final boolean isMac = os.indexOf("mac") >= 0;
	public static final String logFile = "drehsystem.log";

	public static boolean DEBUG = false;
	public static boolean SHOW_VIEWBOXES = false;
	public static Logger logger;

	static
	{
		//System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
		logger = Logger.getLogger(logFile);
	}
}
