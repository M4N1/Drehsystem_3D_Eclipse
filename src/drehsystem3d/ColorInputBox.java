package drehsystem3d;

public class ColorInputBox extends InputBox {
	
	TextView colorView;
	
	public ColorInputBox(String title, String[] standardValues) {
		this(title, standardValues, true);
	}
	
	public ColorInputBox(String title, String[] descValues, boolean stdValue)
	{
		super(title, new String[] {"r", "g", "b"}, descValues, stdValue);
		
		int colorViewWidth = 30;
		increaseStartX(colorViewWidth + this.padding);
		colorView = new TextView(this, this.padding, this.padding, colorViewWidth, this.height);
		int[] initialColor = new int[3];
		if (stdValue)
		{
			for (int i = 0; i < initialColor.length; i++)
			{
				initialColor[i] = Integer.parseInt(descValues[i]);
			}
		}
		else
		{
			for (int i = 0; i < initialColor.length; i++)
			{
				initialColor[i] = 255;
			}
		}
		colorView.setBackgroundColor(initialColor);
		this.contents.add(colorView);
	}
	
	@Override
	public void setup()
	{
		super.setup();
		colorView.setHeight(this.height - 3 * this.padding - this.tvHeight);
	}
	
	@Override
	public void textEdited(TextBox textBox, String text)
	{
		Logger.log(this, "Text edited");
		super.textEdited(textBox, text);
		int[] newColor = colorView.backgroundColor.clone();
		int index = textBox.id - 1;
		if (text.isEmpty())
		{
			newColor[index] = 0;
		}
		else
		{
			newColor[index] = Integer.parseInt(text);
		}
		colorView.setBackgroundColor(newColor);
	}
}
