package drehsystem3d;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter
{
	public static final String ANSI_RESET = "\033[0m";
	public static final String ANSI_YELLOW = "\033[38;2;255;255;0m";
	public static final String ANSI_WHITE = "\033[38;2;255;255;255m";
	public static String ANSI_RED = "\033[38;2;255;0;0m";
	
	private boolean colorOutput;
	
	public LogFormatter()
	{
		this(false);
	}
	
	public LogFormatter(boolean colorOutput)
	{
		this(colorOutput, false);
	}
	
	public LogFormatter(boolean colorOutput, boolean debug)
	{
		this.colorOutput = colorOutput;
		
		if (debug)
			ANSI_RED = "\033[38;2;235;50;70m";
	}
	
	@Override
	public String format(LogRecord record)
	{
		StringBuilder builder = new StringBuilder();
		if (this.colorOutput)
			builder.append(ANSI_YELLOW);
		
		builder.append("[");
		builder.append(calcDate(record.getMillis()));
		builder.append("]");

		builder.append(" [");
		builder.append(record.getSourceClassName());
		builder.append("]");
		
		builder.append(" [");
		builder.append(String.format("%-7s", record.getLevel().getName()));
		builder.append("]");
		
		builder.append(" - ");
		if (this.colorOutput)
			builder.append(ANSI_WHITE);
		
		builder.append(String.format("%-30s", record.getMessage()));
		
		Object[] params = record.getParameters();
		
		if (params != null)
		{
			builder.append(String.format("%-5s", " "));
			for (int i = 0; i < params.length; i++)
			{
				builder.append(params[i]);
				if (i < params.length - 1)
					builder.append(", ");
			}
		}
		
		if (record.getThrown() != null)
		{
			builder.append("\n");
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			record.getThrown().printStackTrace(pw);
			
			if (this.colorOutput)
				builder.append(ANSI_RED);
			builder.append(sw.toString());
		}
		
		
		if (this.colorOutput)
			builder.append(ANSI_RESET);
		builder.append("\n");
		return builder.toString();
	}
	
	private String calcDate(long millisecs) {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date resultdate = new Date(millisecs);
        return date_format.format(resultdate);
    }
}
