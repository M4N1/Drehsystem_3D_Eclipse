package ui;

import processing.core.PApplet;
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
	String text = "";
	protected final Spacing padding = new Spacing();
	int textSize = 20;
	int strokeColor = 255;
	int strokeWeight = 1;
	Color textColor = new Color(255);
	int cornerRadius = 0;
	TextAlignment textAlignment = TextAlignment.LEFT;

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
		this.context.textSize(this.textSize);
		int nWidth = (int) this.context.textWidth(this.text) + this.padding.getSpacingX() * 2;
		int newWidth = this.viewWidth > nWidth ? this.viewWidth : nWidth;
		this.viewWidth = newWidth;
	}

	public void calcHeight()
	{
		int nHeight = this.textSize + 2 * this.padding.getSpacingY();
		int newHeight = this.viewHeight > nHeight ? this.viewHeight : nHeight;
		this.viewHeight = newHeight;
	}

	public void setTextAlignment(TextAlignment alignment)
	{
		this.textAlignment = alignment;
	}
	
	public void setPadding(int spacing)
	{
		this.padding.setSpacing(spacing);
	}
	
	public void setPadding(int x, int y)
	{
		this.padding.setSpacing(x, y);
	}
	
	public void setPaddingX(int x)
	{
		this.padding.setSpacingX(x);
	}
	
	public void setPaddingY(int y)
	{
		this.padding.setSpacingY(y);
	}

	public void setCornerRadius(int radius)
	{
		this.cornerRadius = radius;
	}

	public float calcAlignment()
	{
		this.context.textSize(this.textSize);
		float posX = this.pos.x;
		float offset = 0;
		int paddingSpacingX = this.padding.getSpacingX();
		switch (this.textAlignment)
		{
			case LEFT:
				posX += paddingSpacingX;
				break;

			case RIGHT:
				offset = this.viewWidth - this.context.textWidth(this.text) - paddingSpacingX;
				if (offset < paddingSpacingX)
				{
					offset = paddingSpacingX;
				}
				posX += offset;
				break;

			case CENTER:
				offset = (this.viewWidth - this.context.textWidth(this.text)) / 2;
				if (offset < paddingSpacingX)
				{
					offset = paddingSpacingX;
				}
				posX += offset;
				break;
		}
		return posX;
	}

	@Override
	public boolean isClicked()
	{
		float mX = this.context.mouseX;
		float mY = this.context.mouseY;
		return (mX > this.pos.x && mX <= this.pos.x + this.viewWidth && mY > this.pos.y
				&& mY <= this.pos.y + this.viewHeight);
	}

	@Override
	public boolean isHovered()
	{
		float mX = this.context.mouseX;
		float mY = this.context.mouseY;
		return (mX > this.pos.x && mX <= this.pos.x + this.viewWidth && mY > this.pos.y
				&& mY <= this.pos.y + this.viewHeight);
	}
	
	public void highlightOnHover(int[] color)
	{
		
	}

	@Override
	public void draw()
	{
		super.draw();
		if (this.visible)
		{
			this.context.stroke(this.strokeColor);
			if (this.strokeWeight > 0)
			{
				this.context.strokeWeight(this.strokeWeight);
			}
			else
			{
				this.context.noStroke();
			}
			this.context.fill(this.backgroundColor.r, this.backgroundColor.g, this.backgroundColor.b, this.backgroundColor.a);
			this.context.rect(this.pos.x, this.pos.y, this.viewWidth, this.viewHeight, this.cornerRadius);
			
			this.context.fill(this.textColor.r, this.textColor.g, this.textColor.b, this.textColor.a);
			this.context.textSize(this.textSize);
			calcWidth();
			calcHeight();
			float x = calcAlignment();
			float y = this.pos.y + this.viewHeight - (this.viewHeight - this.textSize) / 2 - 2;
			this.context.text(this.text, x, y);
		}
	}
}
