package drehsystem3d;

import processing.core.PApplet;
import processing.core.PVector;

class TextView extends View
{
	public enum TextAlignment
	{
		LEFT,
		RIGHT,
		CENTER
	}

	String text = "";
	protected final Spacing padding = new Spacing();
	int textSize = 20;
	int strokeColor = 255;
	int strokeWeight = 1;
	int textColor = 255;
	int cornerRadius = 0;
	TextAlignment textAlignment = TextAlignment.LEFT;

	TextView(PApplet context)
	{
		super(context);
	}
	
	TextView(PApplet context, float x, float y)
	{
		super(context, x, y);
	}

	TextView(PApplet context, float x, float y, int w, int h)
	{
		super(context, x, y, w, h);
	}

	TextView(PApplet context, PVector pos)
	{
		super(context, pos);
	}

	TextView(PApplet context, PVector pos, int w, int h)
	{
		super(context, pos, w, h);
	}

	TextView(PApplet context, float x, float y, String text)
	{
		super(context, x, y);
		this.text = text;
	}

	TextView(PApplet context, float x, float y, int w, int h, String text)
	{
		super(context, x, y, w, h);
		this.text = text;
	}

	TextView(PApplet context, PVector pos, String text)
	{
		super(context, pos);
		this.text = text;
	}

	TextView(PApplet context, PVector pos, int w, int h, String text)
	{
		super(context, pos, w, h);
		this.text = text;
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

	public void setTextColor(int textColor)
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
		return (mX >= this.pos.x && mX <= this.pos.x + this.viewWidth && mY >= this.pos.y
				&& mY <= this.pos.y + this.viewHeight);
	}

	@Override
	public boolean isHovered()
	{
		float mX = this.context.mouseX;
		float mY = this.context.mouseY;
		return (mX >= this.pos.x && mX <= this.pos.x + this.viewWidth && mY >= this.pos.y
				&& mY <= this.pos.y + this.viewHeight);
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
			this.context.fill(this.backgroundColor[0], this.backgroundColor[1], this.backgroundColor[2], this.backgroundAlpha);
			this.context.rect(this.pos.x, this.pos.y, this.viewWidth, this.viewHeight, this.cornerRadius);
			this.context.fill(this.textColor);
			this.context.textSize(this.textSize);
			calcWidth();
			calcHeight();
			float x = calcAlignment();
			float y = this.pos.y + this.viewHeight - (this.viewHeight - this.textSize) / 2 - 2;
			this.context.text(this.text, x, y);
		}
	}
}
