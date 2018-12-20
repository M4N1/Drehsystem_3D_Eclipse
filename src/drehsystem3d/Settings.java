package drehsystem3d;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;

public class Settings
{
	private static String[] args;
	private static int optionLength = 25;
	private static int statusLength = 15;
	private static int totalLength = 60;
	private static int spaces = (Settings.totalLength - Settings.optionLength - Settings.statusLength) - 4;
	private static boolean printColored = false;
	
	public static void setup(String[] args)
	{
		StringBuilder settings = new StringBuilder();
		Settings.args = args;
		
		Global.DEBUG = getModeState("DEBUG");
		Global.SHOW_VIEWBOXES = Global.DEBUG && getModeState("VIEWBOXES");
		Point.restrictPathLength(!getModeState("FULL_PATH"));
		
		Settings.printColored = Global.isUnix || getModeState("PRINT_COLOR");
		
		settings.append(getSeparatorLine());
		settings.append(String.format("# %-" + Settings.optionLength + "s:%-" + (Settings.totalLength - Settings.optionLength - 4) + "s#\n", "Settings status", ""));
		settings.append(getSeparatorLine());
		settings.append(getEmptyInfoLine());		
		
		settings.append(getStateString("Version", Global.VERSION));
		settings.append(getStateString("OS", Global.OS));
		settings.append(getEmptyInfoLine());
		
		settings.append(getStateString("Debug mode", Global.DEBUG));
		if (Global.DEBUG)
			settings.append(getSubStateString("Show viewboxes", Global.SHOW_VIEWBOXES));
		
		settings.append(getStateString("Show endless path", !Point.pathRestricted()));
		settings.append(setLogLevel());
		
		settings.append(getEmptyInfoLine());
		settings.append(getSeparatorLine());
		settings.append("\n");
		
		
		printColored(settings.toString());
	}
	
	private static void printColored(String message)
	{
		if (printColored)
			System.out.print(LogFormatter.ANSI_YELLOW + message + LogFormatter.ANSI_RESET);
		else
			System.out.print(message);
	}
	
	private static String getSeparatorLine()
	{
		StringBuilder line = new StringBuilder();
		for (int i = 0; i < Settings.totalLength; i++)
		{
			line.append('#');
		}
		line.append("\n");
		return line.toString();
	}
	
	private static String getEmptyInfoLine()
	{
		return String.format("#%-" + (Settings.totalLength - 2) + "s#\n", "");
	}
	
	private static String setLogLevel()
	{
		StringBuilder status = new StringBuilder();
		
		Level level = getLogLevel();
		status.append(getStateString("Log level", level.getName()));
		
		Global.logger.setUseParentHandlers(false);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(level);
		LogFormatter formatter = new LogFormatter(true, Global.DEBUG);
		handler.setFormatter(formatter);
		
		Global.logger.setLevel(level);
		Global.logger.addHandler(handler);
		
		boolean storeLog = getModeState("STORE_LOG");
		status.append(getSubStateString("Save log", storeLog));
		if (storeLog)
		{
			try
			{
				FileHandler fileHandler = new FileHandler(Global.logFile, 8096, 1, true);
				formatter = new LogFormatter(false);
				fileHandler.setFormatter(formatter);
				Global.logger.addHandler(fileHandler);
			}
			catch (SecurityException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return status.toString();
	}
	
	private static Level getLogLevel()
	{
		Level level = Level.OFF;
		
		if (getModeState("LOG_ALL")) level = Level.ALL;
		else if (getModeState(new String[] {"LOG_INFO", "LOG"})) level = Level.INFO;
		else if (getModeState("LOG_FINE")) level = Level.FINE;
		else if (getModeState("LOG_FINER")) level = Level.FINER;
		else if (getModeState("LOG_ERROR")) level = Level.SEVERE;
		else if (getModeState("LOG_WARNING")) level = Level.WARNING;
		
		return level;
	}
	
	private static boolean getModeState(String identificator)
	{
		return getModeState(new String[] { identificator });
	}
	
	private static boolean getModeState(String[] identificators)
	{
		if (Settings.args == null) return false;
		for (String identificator : identificators)
		{
			if (containsArgument(Settings.args, identificator)) return true;
		}
		return false;
	}
	
	private static String getStateString(String messageSignature, boolean state)
	{
		return getStateString(messageSignature, (state ? "active" : "inactive"));
	}
	
	private static String getSubStateString(String messageSignature, boolean state)
	{
		return getStateString(" - " + messageSignature, state);
	}
	
	private static String getStateString(String messageSignature, String state)
	{
		String out = "# %-" + Settings.optionLength + "s:%3$-" + (spaces/2.0+0.5) + "s%-" + Settings.statusLength + "s%-" + (spaces/2) + "s#\n";
		return String.format(out, messageSignature, state, "");
	}
	
	private static String getSubStateString(String messageSignature, String state)
	{
		return getStateString(" - " + messageSignature, state);
	}
	
	private static boolean containsArgument(String[] arr, String arg)
	{
		for (String s : arr)
		{
			if (s.equals(arg)) return true;
		}
		return false;
	}
	
	public static boolean printColored()
	{
		return Settings.printColored;
	}
}
