package drehsystem3d;

import java.util.ArrayList;
import java.util.logging.Level;

import drehsystem3d.Listener.InputBoxListener;
import drehsystem3d.Listener.KeyListener;
import drehsystem3d.Listener.OnAnimationFinishedListener;
import drehsystem3d.Listener.OnClickListener;
import drehsystem3d.Listener.TextBoxListener;
import processing.core.PApplet;

public class InputBox extends PApplet implements TextBoxListener, OnClickListener, OnAnimationFinishedListener
{
	
	protected int itemCount = 0;
	protected ArrayList<TextView> textviews = new ArrayList<>();
	protected ArrayList<TextBox> textboxes = new ArrayList<>();
	protected ArrayList<View> contents = new ArrayList<>();
	protected Button bSubmit;
	protected String[] values;
	protected String[] standardValues;
	protected String[] hintValues;
	protected float[] limitsMax;
	protected float[] limitsMin;
	protected boolean visible = true;
	protected boolean exit = false;
	protected String title = "";
	protected int windowWidth;
	protected int windowHeight;
	protected int xStart = 0;
	protected int yStart = 0;
	protected int xMax = 0;
	protected int yMax = 0;
	protected int padding = 10;
	protected int tvWidth = 100;
	protected int tvHeight = 50;
	protected int tbWidth = 150;
	protected int tbHeight = 50;
	protected int tvWidthMax = 0;
	protected int tbWidthMax = 0;
	protected int counter = 1;
	protected int minInputBoxWidth = 200;
	protected int inputType = InputTypes.FLOAT;
	protected InputBoxListener mListener;

	InputBox(String title, String[] values)
	{
		this(title, values, null, false);
	}

	InputBox(String title, String[] values, String[] standardValues)
	{
		this(title, values, standardValues, true);
	}

	InputBox(String title, String[] values, String[] descValues, boolean stdValue)
	{
		this.title = title;
		this.values = values;
		if (descValues != null)
		{
			if (descValues.length == values.length)
			{
				if (stdValue)
				{
					this.standardValues = descValues;
				}
				else
				{
					this.hintValues = descValues;
				}
			}
		}
		this.xStart = this.padding;
		this.yStart = this.padding;
	}
	
	public void run()
	{
		String[] args = { "InputBox" };
		PApplet.runSketch(args, this);
		this.surface.setTitle(this.title);
	}

	@Override
	public void settings()
	{
		size(this.tvWidth + this.tbWidth + this.padding, this.minInputBoxWidth);
		this.windowWidth = this.width;
		this.windowHeight = this.height;
	}

	@Override
	public void setup()
	{
		this.surface.setTitle(this.title);
		for (String value : this.values)
		{
			addTextView(value);
			addTextBox();
		}
		for (TextView tv : this.textviews)
		{
			tv.setWidth(this.tvWidthMax);
		}
		for (TextBox tb : this.textboxes)
		{
			tb.setWidth(this.tbWidthMax);
		}
		this.bSubmit = new Button(this, title + "_b_submit", this.xStart, this.yStart, 100, 50, "Submit");
		this.bSubmit.setBackgroundColor(255);
		this.bSubmit.setCornerRadius(5);
		this.bSubmit.setTextColor(0);
		this.bSubmit.setTextSize(25);
		this.bSubmit.setMargin(10);
		this.bSubmit.setHorizontalAlignment(View.AlignmentHorizontal.CENTER);
		this.bSubmit.setVerticalAlignment(View.AlignmentVertical.BOTTOM);
		this.bSubmit.setOnClickListener(this);
		this.bSubmit.setOnAnimationFinishedListener(this);
		this.yMax += this.yStart + this.tbHeight + this.padding;
		this.xMax += this.xStart + this.tvWidth + 2 * this.padding;
		if (this.xMax > this.windowWidth)
		{
			this.windowWidth = this.xMax;
		}
		if (this.yMax > this.windowHeight)
		{
			this.windowHeight = this.yMax;
		}
		this.surface.setSize(this.windowWidth, this.windowHeight);
	}
	
	public void setStartX(int x)
	{
		this.xStart = x;
	}
	
	public void increaseStartX(int offset)
	{
		this.xStart += offset;
	}
	
	public void setStartY(int y)
	{
		this.yStart = y;
	}
	
	public void increaseStartY(int offset)
	{
		this.yStart += offset;
	}

	public void setMaxLimits(float limit)
	{
		float[] limits = new float[this.values.length];
		for (int i = 0; i < this.values.length; i++)
		{
			limits[i] = limit;
		}
		setMaxLimits(limits);
	}

	public void setMaxLimits(float[] limits)
	{
		if (limits.length != this.values.length)
		{
			return;
		}
		this.limitsMax = limits;
	}

	public void setMinLimits(float limit)
	{
		float[] limits = new float[this.values.length];
		for (int i = 0; i < this.values.length; i++)
		{
			limits[i] = limit;
		}
		setMinLimits(limits);
	}

	public void setMinLimits(float[] limits)
	{
		this.limitsMin = limits;
	}

	public void setInputType(int type)
	{
		this.inputType = type;
	}

	private void addTextView(String text)
	{
		TextView tv = new TextView(this, title + "_tv_" + (itemCount++), this.xStart, this.yStart, this.tvWidth, this.tvHeight);
		tv.setMarginY(5);
		tv.setPadding(0);
		tv.setText(text);
		tv.setTextSize(30);
		if (textviews.size() > 0)
		{
			tv.alignBottom(this.textviews.get(this.textviews.size()-1));
		}
		tv.setTextAlignment(TextView.TextAlignment.CENTER);
		if (tv.viewWidth > this.tvWidthMax)
		{
			this.tvWidthMax = tv.viewWidth;
		}
		this.textviews.add(tv);
		this.contents.add(tv);
	}

	private void addTextBox()
	{
		final TextBox tb = new TextBox(this, title + "_tv_" + (itemCount++), this.xStart + this.tvWidth, this.yStart, this.tbWidth, this.tbHeight);
		tb.setMarginY(5);
		tb.setPadding(5);
		tb.setTextSize(30);
		tb.setId(this.counter);
		tb.alignRight(this.textviews.get(this.textviews.size()-1));
		if (this.standardValues != null)
		{
			tb.setStandardText(this.standardValues[this.counter - 1]);
		}
		else if (this.hintValues != null)
		{
			tb.setHint(this.hintValues[this.counter - 1]);
		}
		tb.setInputType(this.inputType);
		tb.setTextAlignment(TextView.TextAlignment.RIGHT);
		tb.setTextBoxListener(this);
		tb.setKeyListener(new KeyListener()
		{
			@Override
			public boolean onKeyPressed(int pressedKeyCode, char pressedKey)
			{
				if (pressedKeyCode == 10)
				{			
					tb.clicked = false;
					int id = tb.getId();

					for (int i = 0; i < InputBox.this.textboxes.size(); i++)
					{
						if (InputBox.this.textboxes.get(i).getId() == id)
						{
							int next = i + 1;
							if (next > InputBox.this.textboxes.size() - 1)
							{
								finish();
								return true;
							}
							InputBox.this.textboxes.get(next).setClicked(true, tb.calcCharPos(tb.cursorPos));
							Global.logger.log(Level.FINER, "i:" + i);
							Global.logger.log(Level.FINER, "tb id:" + id);
							Global.logger.log(Level.FINER, "size:" + InputBox.this.textboxes.size());
							Global.logger.log(Level.FINE, "next:" + next);
							break;
						}
					}
				}
				return true;
			}

			@Override
			public boolean onKeyReleased(int pressedKeyCode, char pressedKey)
			{
				return true;
			}
		});
		if (tb.viewWidth > this.xMax)
		{
			this.xMax = tb.viewWidth;
		}
		if (tb.viewWidth > this.tbWidthMax)
		{
			this.tbWidthMax = tb.viewWidth;
		}
		this.textboxes.add(tb);
		this.contents.add(tb);
		this.yStart += this.tbHeight + 10;
		this.counter++;
	}

	public void setOnEditingFinishedListener(InputBoxListener listener)
	{
		this.mListener = listener;
	}

	@Override
	public void mousePressed()
	{
		this.bSubmit.onMousePressed(this.mouseButton);
		this.contents.forEach((v) -> {
			v.onMousePressed(this.mouseButton);
		});
	}

	@Override
	public void mouseReleased()
	{
		this.contents.forEach((v) -> {
			v.onMouseReleased(this.mouseButton);
		});
	}

	@Override
	public void mouseDragged()
	{
		this.contents.forEach((v) -> {
			v.onMouseDragged();
		});
	}

	@Override
	public void keyPressed()
	{
		this.contents.forEach((v) -> {
			v.onKeyPressed(this.keyCode, this.key);
		});
	}

	@Override
	public void keyReleased()
	{
		this.contents.forEach((v) -> {
			v.onKeyReleased(this.keyCode, this.key);
		});
	}

	@Override
	public void draw()
	{
		background(0);
		this.contents.forEach((v) -> {
			v.draw();
		});
		this.bSubmit.draw();
	}

	@Override
	public void exit()
	{
		this.getSurface().setVisible(false);
		this.visible = false;
		this.exit = true;
		if (this.mListener != null)
		{
			this.mListener.onExit();
		}
		this.noLoop();
	}

	public void exited()
	{
		this.exit = false;
	}

	@Override
	public void pause()
	{
		this.getSurface().setVisible(false);
		this.visible = false;
		this.noLoop();
	}

	@Override
	public void resume()
	{
		this.getSurface().setVisible(true);
		this.visible = true;
		this.exit = false;
		this.loop();
	}

	public boolean isVisible()
	{
		return this.visible;
	}

	public boolean waitingForExit()
	{
		return this.exit;
	}

	public String getName()
	{
		return this.title;
	}

	public void finish()
	{
		String[] data = new String[this.textboxes.size()];
		for (int i = 0; i < this.textboxes.size(); i++)
		{
			data[i] = this.textboxes.get(i).getText();
		}
		if (this.mListener != null)
		{
			this.mListener.finishedEditing(data);
		}
		exit();
	}

	@Override
	public void textEditingFinished(TextBox textBox, String text)
	{
		trimTextBoxContents(textBox, text);
	}
	
	@Override
	public void textEdited(TextBox textBox, String text)
	{
		Global.logger.log(Level.FINER, "text:'" + text + "'");
		Global.logger.log(Level.FINER, "text length: " + text.length());
		if (text.isEmpty())
		{
			Global.logger.log(Level.FINER, "Returning because text is empty");
			return;
		}
		trimTextBoxContents(textBox, text);
	}
	
	private void trimTextBoxContents(TextBox textBox, String text)
	{		
		if (textBox.inputType == InputTypes.FLOAT)
		{
			float value;
			try
			{
				value = Float.parseFloat(text);
				if (InputBox.this.limitsMax != null && InputBox.this.limitsMax.length > textBox.id - 1)
				{
					float maxValue = InputBox.this.limitsMax[textBox.id - 1];
					if (value > maxValue)
					{
						textBox.setText(Float.toString(maxValue));
						return;
					}
					float minValue = InputBox.this.limitsMin[textBox.id - 1];
					if (value < minValue)
					{
						textBox.setText(Float.toString(minValue));
						return;
					}
				}
			}
			catch (NumberFormatException e)
			{
				Global.logger.log(Level.SEVERE, e.getStackTrace().toString());
				textBox.setText(Float.toString(InputBox.this.limitsMax[textBox.id - 1]));
			}
		}
		else if (textBox.inputType == InputTypes.INTEGER)
		{
			int value;
			try
			{
				value = Integer.parseInt(text);
				if (InputBox.this.limitsMax != null && InputBox.this.limitsMax.length > textBox.id - 1)
				{
					int maxValue = (int) InputBox.this.limitsMax[textBox.id - 1];
					if (value > maxValue)
					{
						textBox.setText(Integer.toString(maxValue));
						return;
					}
					int minValue = (int) InputBox.this.limitsMin[textBox.id - 1];
					if (value < minValue)
					{
						textBox.setText(Integer.toString(minValue));
						return;
					}
				}
			}
			catch (NumberFormatException e)
			{
				Global.logger.log(Level.SEVERE, e.getStackTrace().toString());
				textBox.setText(Integer.toString((int) InputBox.this.limitsMax[textBox.id - 1]));
			}
		}
	}

	@Override
	public void previousTextBox(TextBox textBox, int cursorPosX)
	{
		for (int i = 0; i < InputBox.this.textboxes.size(); i++)
		{
			if (InputBox.this.textboxes.get(i).getId() == textBox.id)
			{
				int next = i == 0 ? InputBox.this.textboxes.size() - 1 : i - 1;
				InputBox.this.textboxes.get(next).setClicked(true, cursorPosX);
			}
		}
	}

	@Override
	public void nextTextBox(TextBox textBox, int cursorPosX)
	{
		for (int i = 0; i < InputBox.this.textboxes.size(); i++)
		{
			if (InputBox.this.textboxes.get(i).getId() == textBox.id)
			{
				int next = (i + 1) % InputBox.this.textboxes.size();
				InputBox.this.textboxes.get(next).setClicked(true, cursorPosX);
				Global.logger.log(Level.FINER, "i:" + i);
				Global.logger.log(Level.FINER, "tb id:" + textBox.id);
				Global.logger.log(Level.FINER, "size:" + InputBox.this.textboxes.size());
				Global.logger.log(Level.FINE, "next:" + next);
			}
		}
	}

	@Override
	public void onClick(int id)
	{
	}

	@Override
	public void onAnimationFinished()
	{
		finish();
	}
}