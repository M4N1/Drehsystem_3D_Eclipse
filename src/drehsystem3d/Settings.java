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
		Settings.args = args;
		System.out.println(System.getProperty("os.name"));
		Settings.printColored = Global.isUnix;
		printSeparatorLine();
		printColored(String.format("# %-" + Settings.optionLength + "s:%-" + (Settings.totalLength - Settings.optionLength - 4) + "s#\n", "Settings status", ""));
		printSeparatorLine();
		printEmptyInfoLine();
		
		
		printModeState("OS", Global.OS);
		Global.DEBUG = getAndPrintModeState("DEBUG", "Debug mode");
		Point.restrictPathLength(!getAndPrintModeState("FULL_PATH", "Show endless path"));
		setLogLevel();
		
		printEmptyInfoLine();
		printSeparatorLine();
		System.out.print("\n");
	}
	
	private static void printColored(String message)
	{
		if (printColored)
			System.out.print(LogFormatter.ANSI_YELLOW + message + LogFormatter.ANSI_RESET);
		else
			System.out.print(message);
	}
	
	private static void printSeparatorLine()
	{
		for (int i = 0; i < Settings.totalLength; i++)
		{
			printColored("#");
		}
		System.out.print("\n");
	}
	
	private static void printEmptyInfoLine()
	{
		printColored(String.format("#%-" + (Settings.totalLength - 2) + "s#\n", ""));
	}
	
	private static void setLogLevel()
	{
		Level level = getLogLevel();
		
		Global.logger.setUseParentHandlers(false);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(level);
		LogFormatter formatter = new LogFormatter(true, Global.DEBUG);
		handler.setFormatter(formatter);
		
		Global.logger.setLevel(level);
		Global.logger.addHandler(handler);
		
		if (getModeState("STORE_LOG"))
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
		
		printModeState("Log level", level.getName());
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
	
	private static boolean getAndPrintModeState(String identificator)
	{
		return getAndPrintModeState(identificator, identificator);
	}
	
	private static boolean getAndPrintModeState(String identificator, String messageSignature)
	{
		return getAndPrintModeState(new String[] { identificator }, messageSignature);
	}
	
	private static boolean getAndPrintModeState(String[] identificators, String messageSignature)
	{
		boolean state = getModeState(identificators);
		printModeState(messageSignature, (state ? "active" : "inactive"));
		return state;
	}
	
	private static void printModeState(String messageSignature, String state)
	{
		String out = "# %-" + Settings.optionLength + "s:%3$-" + (spaces/2.0+0.5) + "s%-" + Settings.statusLength + "s%-" + (spaces/2) + "s#\n";
		printColored(String.format(out, messageSignature, state, ""));
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
