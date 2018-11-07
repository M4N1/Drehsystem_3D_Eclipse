package drehsystem3d;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public final class Logger {
	
	private static boolean log = true;
	private static boolean storeLog = false;
	private static final String logFile = "drehsystem.log";
	
	public static void setLogStatus(boolean status)
	{
		log = status;
	}
	
	public static void setLogStoreStatus(boolean status)
	{
		storeLog = status;
	}
	
	public static void log(Object sender, Object message)
	{
		if (!log) return;
		
		String logMessage = String.format("%-40s" + message + "\n", (sender.toString() + ":"));
		System.out.format(logMessage);
		if (storeLog)
		{
			PrintWriter writer = null;
			try 
			{
				writer = new PrintWriter(logFile, "UTF-8");
				writer.println(logMessage);
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (UnsupportedEncodingException e) 
			{
				e.printStackTrace();
			} 
			finally 
			{
				if (writer != null)
				{
					writer.close();
				}
			}
			
		}
	}
}
