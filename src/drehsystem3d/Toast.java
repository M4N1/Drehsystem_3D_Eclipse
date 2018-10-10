package drehsystem3d;

import processing.core.PApplet;
import processing.core.PVector;

public class Toast extends View
{
	/**
	 * 
	 */
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
	long startTime = 0;
	long lastTime = 0;
	int duration = 2000;
	int speed = 5;
	String text = "";
	int state = Toast.STATE_ENTER;

	Toast(Drehsystem3d drehsystem3d, PApplet context, String text, int duration)
	{
		super(context, context.width / 2 - standardWidth / 2, context.height);
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
			mousePosDiff = new PVector(mX - this.pos.x, mY - this.pos.y, 0);
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

	public void update()
	{
		switch (this.state)
		{
			case Toast.STATE_ENTER:
				this.pos.y -= this.speed;
				if (this.pos.y < this.context.height - 100)
				{
					this.state = Toast.STATE_SHOW;
					this.lastTime = context.millis();
				}
				break;

			case Toast.STATE_SHOW:
				if (context.millis() - this.lastTime >= this.duration && this.duration > 0)
					this.state = Toast.STATE_EXIT;
				break;

			case Toast.STATE_EXIT:
				this.pos.y += this.speed;
				if (this.pos.y > this.context.height)
					this.visible = false;
				break;
		}
	}

	public void windowResized(int w, int h)
	{
		this.pos.x = (float) (this.context.width / 2 - Toast.standardWidth / 2);
		this.pos.y = this.pos.y / h * this.context.height;
	}

	@Override
	public void draw()
	{
		if (this.visible)
		{
			if (!this.clicked)
			{
				update();
			}
			this.context.fill(255);
			this.context.stroke(100);
			this.context.strokeWeight(1);
			this.context.textSize(20);
			float x = this.pos.x;
			float y = this.pos.y;
			if (this.clicked)
			{
				x = this.context.mouseX - this.mousePosDiff.x;
				y = this.context.mouseY - this.mousePosDiff.y;
			}
			this.context.rect(x, y, Toast.standardWidth, Toast.standardHeight, Toast.standardRadius);
			this.context.fill(0);
			float pX = x + (Toast.standardWidth - this.context.textWidth(this.text)) / 2;
			float pY = y + 18 + (Toast.standardHeight - 20) / 2;
			this.context.text(this.text, pX, pY);
		}
	}
}