package drehsystem3d;

public class ColorInputBox extends InputBox {

	ColorInputBox(String title, String[] values) {
		this(title, values, null, false);
	}
	
	public ColorInputBox(String title, String[] values, String[] standardValues) {
		this(title, values, standardValues, true);
	}
	
	public ColorInputBox(String title, String[] values, String[] descValues, boolean stdValue)
	{
		super(title, values, descValues, stdValue);
		increaseStartX(100);
	}
	
	
}
