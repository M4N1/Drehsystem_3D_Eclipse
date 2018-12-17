package ui;

public class ColorInputBox extends InputBox {
	
	private TextView colorView;
	private int colorViewWidth = 30;
	
	public ColorInputBox(String title, String[] standardValues) {
		this(title, standardValues, true);
	}
	
	public ColorInputBox(String title, String[] descValues, boolean stdValue)
	{
		super(title, new String[] {"r", "g", "b"}, descValues, stdValue);
		
		increaseStartX(this.colorViewWidth + this.padding);
	}
	
	@Override
	public void setup()
	{
		super.setup();
		
		this.colorView = new TextView(this, title + "_tv_" + (itemCount++));
		this.colorView.setMarginX(this.padding);
		this.colorView.setMarginY((int) (3.0 / 2 * this.padding));
		Color initialColor = new Color();
		if (this.standardValues != null)
		{
			int r = Integer.parseInt(this.standardValues[0]);
			int g = Integer.parseInt(this.standardValues[1]);
			int b = Integer.parseInt(this.standardValues[2]);
			initialColor.setColor(r, g, b);
		}
		else
		{
			initialColor.setColor(255);
		}
		colorView.setBackgroundColor(initialColor);
		colorView.setSize(this.colorViewWidth, (int) (2 * this.padding + 3 * this.tvHeight));
		this.contents.add(colorView);
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
