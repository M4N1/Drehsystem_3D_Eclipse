package drehsystem3d;

public class ColorInputBox extends InputBox {
	
	private TextView colorView;
	
	public ColorInputBox(String title, String[] standardValues) {
		this(title, standardValues, true);
	}
	
	public ColorInputBox(String title, String[] descValues, boolean stdValue)
	{
		super(title, new String[] {"r", "g", "b"}, descValues, stdValue);
		
		int colorViewWidth = 30;
		increaseStartX(colorViewWidth + this.padding);
		this.colorView = new TextView(this, title + "_tv_" + (itemCount++), this.padding, this.padding, colorViewWidth, this.height);
		Color initialColor = new Color();
		if (stdValue)
		{
			int r = Integer.parseInt(descValues[0]);
			int g = Integer.parseInt(descValues[1]);
			int b = Integer.parseInt(descValues[2]);
			initialColor.setColor(r, g, b);
		}
		else
		{
			initialColor.setColor(255);
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
		super.textEdited(textBox, text);
		Color newColor = colorView.backgroundColor.clone();
		int index = textBox.id - 1;
		if (text.isEmpty())
		{
			newColor.setColor(index, 0);
		}
		else
		{
			newColor.setColor(index, Integer.parseInt(text));
		}
		colorView.setBackgroundColor(newColor);
	}
}
