package drehsystem3d;

import java.util.logging.Logger;

public class Global
{
	public static final String logFile = "drehsystem.log";

	public static boolean DEBUG = false;
	public static Logger logger;

	static
	{
		//System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
		logger = Logger.getLogger(logFile);
	}
}
