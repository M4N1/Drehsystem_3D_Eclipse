package drehsystem3d;

import processing.core.PApplet;
import processing.core.PVector;

class TextView extends View
{
	final static int TEXTALIGNMENT_LEFT = 0;
	final static int TEXTALIGNMENT_RIGHT = 1;
	final static int TEXTALIGNMENT_CENTER = 2;

	int setWidth = 0, setHeight = 0;
	String text = "";
	int margin = 5;
	int textSize = 20;
	int strokeColor = 255;
	int strokeWeight = 1;
	// int backgroundColor = 0;
	// int backgroundgAlpha = 255;
	int textColor = 255;
	int textAlignment = TextView.TEXTALIGNMENT_LEFT;
	int horizontalAlignment = TextView.ALIGNMENT_MANUALL;
	int verticalAlignment = TextView.ALIGNMENT_MANUALL;
	int cornerRadius = 0;
	PApplet context = null;

	TextView(PApplet context, float x, float y)
	{
		super(context, x, y);
		this.context = context;
		this.pos = new PVector(x, y, 0);
	}

	TextView(PApplet context, float x, float y, int w, int h)
	{
		super(context, x, y, w, h);
		this.context = context;
		this.pos = new PVector(x, y, 0);
		this.setWidth = w;
		this.setHeight = h;
	}

	TextView(PApplet context, PVector pos)
	{
		super(context, pos);
		this.context = context;
		this.pos = new PVector(pos.x, pos.y, pos.z);
	}

	TextView(PApplet context, PVector pos, int w, int h)
	{
		super(context, pos, w, h);
		this.context = context;
		this.pos = new PVector(pos.x, pos.y, pos.z);
		this.setWidth = w;
		this.setHeight = h;
	}

	TextView(PApplet context, float x, float y, String text)
	{
		super(context, x, y);
		this.context = context;
		this.pos = new PVector(x, y, 0);
		this.text = text;
	}

	TextView(PApplet context, float x, float y, int w, int h, String text)
	{
		super(context, x, y, w, h);
		this.context = context;
		this.pos = new PVector(x, y, 0);
		this.setWidth = w;
		this.setHeight = h;
		this.text = text;
	}

	TextView(PApplet context, PVector pos, String text)
	{
		super(context, pos);
		this.context = context;
		this.pos = new PVector(pos.x, pos.y, pos.z);
		this.text = text;
	}

	TextView(PApplet context, PVector pos, int w, int h, String text)
	{
		super(context, pos, w, h);
		this.context = context;
		this.pos = new PVector(pos.x, pos.y, pos.z);
		this.setWidth = w;
		this.setHeight = h;
		this.text = text;
	}

	@Override
	public void setWidth(int w)
	{
		this.setWidth = w;
		calcWidth();
	}

	@Override
	public void setHeight(int h)
	{
		this.setHeight = h;
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

	public void calcPosX()
	{
		switch (this.horizontalAlignment)
		{
			case TextView.ALIGNMENT_MANUALL:
				break;

			case TextView.ALIGNMENT_CENTER:
				this.pos.x = (this.context.width - this.viewWidth) / 2;
				break;

			case TextView.ALIGNMENT_LEFT:
				this.pos.x = this.padding;
				break;

			case TextView.ALIGNMENT_RIGHT:
				this.pos.x = this.context.width - this.viewWidth - this.padding;
				break;
		}
	}

	public void calcPosY()
	{
		switch (this.verticalAlignment)
		{
			case TextView.ALIGNMENT_MANUALL:
				break;

			case TextView.ALIGNMENT_CENTER:
				this.pos.y = (this.context.height - this.viewHeight) / 2;
				break;

			case TextView.ALIGNMENT_TOP:
				this.pos.y = this.padding;
				break;

			case TextView.ALIGNMENT_BOTTOM:
				this.pos.y = this.context.height - this.viewHeight - this.padding;
				break;
		}
	}

	public void calcWidth()
	{
		this.context.textSize(this.textSize);
		int nWidth = (int) this.context.textWidth(this.text) + this.margin * 2;
		int newWidth = this.setWidth > nWidth ? this.setWidth : nWidth;
		this.viewWidth = newWidth;
	}

	public void calcHeight()
	{
		int nHeight = this.textSize + 2 * this.margin;
		int newHeight = this.setHeight > nHeight ? this.setHeight : nHeight;
		this.viewHeight = newHeight;
	}

	public void setHorizontalAlignment(int alignment)
	{
		this.horizontalAlignment = alignment;
		calcPosX();
	}

	public void setVerticalAlignment(int alignment)
	{
		this.verticalAlignment = alignment;
		calcPosY();
	}

	public void setTextAlignment(int alignment)
	{
		if (alignment >= 0 && alignment < 3)
			this.textAlignment = alignment;
	}

	public void setMargin(int margin)
	{
		this.margin = margin;
	}

	public void setPadding(int padding)
	{
		this.padding = padding;
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
		switch (this.textAlignment)
		{
			case TEXTALIGNMENT_LEFT:
				posX += this.margin;
				break;

			case TEXTALIGNMENT_RIGHT:
				offset = this.viewWidth - this.context.textWidth(this.text) - this.margin;
				if (offset < this.margin)
					offset = this.margin;
				posX += offset;
				break;

			case TEXTALIGNMENT_CENTER:
				offset = (this.viewWidth - this.context.textWidth(this.text)) / 2;
				if (offset < this.margin)
					offset = this.margin;
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

	public void draw()
	{
		super.draw();
		if (this.visible)
		{
			this.context.stroke(this.strokeColor);
			if (this.strokeWeight > 0)
				this.context.strokeWeight(this.strokeWeight);
			else
				this.context.noStroke();
			this.context.fill(this.backgroundColor, this.backgroundColor, this.backgroundColor, this.backgroundAlpha);
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
