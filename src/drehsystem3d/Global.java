package drehsystem3d;

import java.util.logging.Logger;

public class Global
{
	/**
	 * Current version of the program
	 */
	public static final String VERSION = "0.0.3";
	
	/**
	 * OS version of the user system
	 */
	public static final String OS = System.getProperty("os.name");
	
	private static final String os = OS.toLowerCase();
	public static final boolean isUnix = os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0;
	public static final boolean isWindows = os.indexOf("win") >= 0;
	public static final boolean isMac = os.indexOf("mac") >= 0;
	
	/**
	 * Log save location
	 */
	public static final String logFile = "drehsystem.log";

	/**
	 * Status of debug mode
	 */
	public static boolean DEBUG = false;
	
	/**
	 * Status of view box visibility
	 */
	public static boolean SHOW_VIEWBOXES = false;
	
	/**
	 * Logger class
	 */
	public static Logger logger;

	static
	{
		logger = Logger.getLogger(logFile);
	}
}
