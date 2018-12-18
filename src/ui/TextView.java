package ui;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class TextView extends View
{
	public enum TextAlignment
	{
		LEFT,
		RIGHT,
		CENTER
	}

	public static int instanceCounter = 0;
	protected String text = "";
	protected final Spacing padding = new Spacing();
	protected int textSize = 20;
	protected int strokeColor = 255;
	protected int strokeWeight = 1;
	protected Color textColor = new Color(255);
	protected int cornerRadius = 0;
	protected TextAlignment textAlignment = TextAlignment.LEFT;

	public TextView(PApplet context, String name)
	{
		super(context, name);
		TextView.instanceCounter++;
	}
	
	public TextView(PApplet context, String name, float x, float y)
	{
		super(context, name, x, y);
		TextView.instanceCounter++;
	}

	public TextView(PApplet context, String name, float x, float y, int w, int h)
	{
		super(context, name, x, y, w, h);
		TextView.instanceCounter++;
	}

	public TextView(PApplet context, String name, PVector pos)
	{
		super(context, name, pos);
		TextView.instanceCounter++;
	}

	public TextView(PApplet context, String name, PVector pos, int w, int h)
	{
		super(context, name, pos, w, h);
		TextView.instanceCounter++;
	}

	public TextView(PApplet context, String name, float x, float y, String text)
	{
		super(context, name, x, y);
		this.text = text;
		TextView.instanceCounter++;
	}

	public TextView(PApplet context, String name, float x, float y, int w, int h, String text)
	{
		super(context, name, x, y, w, h);
		this.text = text;
		TextView.instanceCounter++;
	}

	public TextView(PApplet context, String name, PVector pos, String text)
	{
		super(context, name, pos);
		this.text = text;
		TextView.instanceCounter++;
	}

	public TextView(PApplet context, String name, PVector pos, int w, int h, String text)
	{
		super(context, name, pos, w, h);
		this.text = text;
		TextView.instanceCounter++;
	}

	@Override
	public void setWidth(int w)
	{
		super.setWidth(w);
		calcWidth();
	}

	@Override
	public void setHeight(int h)
	{
		super.setHeight(h);
		calcHeight();
	}

	public void setText(String text)
	{
		this.text = text;
		calcWidth();
		calcHeight();
	}

	public void setTextSize(int size)
	{
		this.textSize = size;
		calcWidth();
		calcHeight();
	}
	
	public void setTextColor(int color)
	{
		this.textColor = new Color(color);
	}
	
	public void setTextColor(Color textColor)
	{
		this.textColor = textColor;
	}

	public String getText()
	{
		return this.text;
	}

	public void setStrokeColor(int strokeColor)
	{
		this.strokeColor = strokeColor;
	}

	public void setStrokeWeight(int weight)
	{
		this.strokeWeight = weight;
	}

	public void calcWidth()
	{
		this.canvas.textSize(this.textSize);
		int nWidth = (int) this.canvas.textWidth(this.text) + this.padding.getX();
		int newWidth = this.width > nWidth ? this.width : nWidth;
		this.width = newWidth;
		this.viewWidth = newWidth + this.getMarginX();
	}

	public void calcHeight()
	{
		int nHeight = this.textSize + this.padding.getY();
		int newHeight = this.height > nHeight ? this.height : nHeight;
		this.height = newHeight;
		this.viewHeight = newHeight + this.getMarginY();
	}

	public void setTextAlignment(TextAlignment alignment)
	{
		this.textAlignment = alignment;
	}
	
	public void setPadding(int spacing)
	{
		this.padding.set(spacing);
		calcWidth();
		calcHeight();
	}
	
	public void setPadding(int x, int y)
	{
		this.padding.set(x, y);
		calcWidth();
		calcHeight();
	}
	
	public void setPaddingX(int x)
	{
		this.padding.setX(x);
		calcWidth();
	}
	
	public void setPaddingY(int y)
	{
		this.padding.setY(y);
		calcHeight();
	}

	public void setCornerRadius(int radius)
	{
		this.cornerRadius = radius;
	}

	protected float calcAlignmentX()
	{
		return calcAlignmentX(this.text);
	}
	
	protected float calcAlignmentX(String displayedText)
	{
		this.canvas.textSize(this.textSize);
		float posX = this.getActualPos().x;
		float offset = 0;
		int paddingLeft = this.padding.getLeft();
		switch (this.textAlignment)
		{
			case LEFT:
				posX += paddingLeft;
				break;

			case RIGHT:
				offset = this.width - this.canvas.textWidth(displayedText) - paddingLeft;
				if (offset < paddingLeft)
				{
					offset = paddingLeft;
				}
				posX += offset;
				break;

			case CENTER:
				offset = (this.width - this.canvas.textWidth(displayedText)) / 2;
				if (offset < paddingLeft)
				{
					offset = paddingLeft;
				}
				posX += offset;
				break;
		}
		return posX;
	}
	
	protected float calcAlignmentY()
	{
		return getActualPos().y + this.height - (this.height - this.textSize) / 2 - 2;
	}

	@Override
	public void draw(PGraphics canvas)
	{
		super.draw(canvas);
		if (this.visible)
		{
			this.canvas.beginDraw();
			this.canvas.stroke(this.strokeColor);
			if (this.strokeWeight > 0)
			{
				this.canvas.strokeWeight(this.strokeWeight);
			}
			else
			{
				this.canvas.noStroke();
			}
			PVector pos = this.getActualPos();
			this.canvas.fill(this.backgroundColor.r, this.backgroundColor.g, this.backgroundColor.b, this.backgroundColor.a);
			this.canvas.rect(pos.x, pos.y, this.width, this.height, this.cornerRadius);
			
			this.canvas.fill(this.textColor.r, this.textColor.g, this.textColor.b, this.textColor.a);
			this.canvas.textSize(this.textSize);
			float x = calcAlignmentX();
			float y = calcAlignmentY();
			this.canvas.text(this.text, x, y);
			this.canvas.endDraw();
		}
	}
}
