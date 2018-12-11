package drehsystem3d;

import processing.core.PApplet;
import processing.core.PVector;

public class Toast extends View
{
	
	private static final int STATE_ENTER = 0;
	private static final int STATE_SHOW = 1;
	private static final int STATE_EXIT = 2;
	private static final int standardWidth = 150;
	private static final int standardHeight = 40;
	private static final int standardRadius = 10;
	static final int DURATION_SHORT = 0;
	static final int DURATION_LONG = 1;
	static final int DURATION_INFINITE = 2;
	private PVector mousePosDiff = new PVector(0, 0, 0);
	private boolean destroy = false;
	long startTime = 0;
	long lastTime = 0;
	int duration = 2000;
	int speed = 5;
	String text = "";
	int state = Toast.STATE_ENTER;

	Toast(PApplet context, String name, String text, int duration)
	{
		super(context, name, context.width / 2 - standardWidth / 2, context.height);
		this.context = context;
		this.text = text;
		switch (duration)
		{
			case Toast.DURATION_SHORT:
				this.duration = 2000;
				break;

			case Toast.DURATION_LONG:
				this.duration = 4000;
				break;

			case Toast.DURATION_INFINITE:
				this.duration = -1;
				break;
		}
		this.pos = new PVector(context.width / 2 - standardWidth / 2, context.height, 0);
	}

	@Override
	public boolean isClicked()
	{
		float mX = this.context.mouseX;
		float mY = this.context.mouseY;
		if (mX >= this.pos.x && mX <= this.pos.x + Toast.standardWidth && mY >= this.pos.y
				&& mY <= this.pos.y + Toast.standardHeight)
		{
			this.mousePosDiff = new PVector(mX - this.pos.x, mY - this.pos.y, 0);
			return true;
		}
		return false;
	}

	@Override
	public boolean isHovered()
	{
		float mX = this.context.mouseX;
		float mY = this.context.mouseY;
		return (mX >= this.pos.x && mX <= this.pos.x + Toast.standardWidth && mY >= this.pos.y
				&& mY <= this.pos.y + Toast.standardHeight);
	}
	
	@Override
	public void onMouseReleased(int mouseButton) {
		super.onMouseReleased(mouseButton);
		this.clicked = false;
	}

	public void update()
	{
		switch (this.state)
		{
			case Toast.STATE_ENTER:
				this.pos.y -= this.speed;
				if (this.pos.y < this.context.height - 100)
				{
					this.state = Toast.STATE_SHOW;
					this.lastTime = this.context.millis();
				}
				break;

			case Toast.STATE_SHOW:
				if (this.context.millis() - this.lastTime >= this.duration && this.duration > 0)
				{
					this.state = Toast.STATE_EXIT;
				}
				break;

			case Toast.STATE_EXIT:
				this.pos.y += this.speed;
				if (this.pos.y > this.context.height)
				{
					this.visible = false;
				}
				break;
		}
	}

	@Override
	public int getWidth()
	{
		return Toast.standardWidth;
	}

	@Override
	public int getHeight()
	{
		return Toast.standardHeight;
	}

	@Override
	public void draw()
	{
		if (this.visible)
		{
			float x, y;
			if (!this.clicked)
			{
				if (this.destroy) 
				{
					this.destroy = false;
					this.visible = false;
					return;
				}
				else
				{
					update();
					x = this.pos.x;
					y = this.pos.y;					
				}
			}
			else
			{
				x = this.context.mouseX - this.mousePosDiff.x;
				y = this.context.mouseY - this.mousePosDiff.y;
				if (x + getWidth() / 2 < 0 || x + getWidth() / 2 > this.context.width || y + getHeight() / 2 < 0
						|| y + getHeight() / 2 > this.context.height)
				{
					this.visible = false;
				}
				else if (x - getWidth() < 0)
				{
					x -= getWidth() / 2;
					destroy = true;
				}
				else if (x + 2 * getWidth() > this.context.width)
				{
					x += getWidth() / 2;
					destroy = true;
				}
				else
				{
					destroy = false;
				}
			}

			this.context.fill(255);
			this.context.noStroke();
			this.context.textSize(20);

			this.context.rect(x, y, getWidth(), getHeight(), Toast.standardRadius);
			this.context.fill(0);
			float pX = x + (getWidth() - this.context.textWidth(this.text)) / 2;
			float pY = y + 18 + (getHeight() - 20) / 2;
			this.context.text(this.text, pX, pY);
		}
	}
}