package drehsystem3d;

import static processing.core.PApplet.abs;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;
import static processing.core.PApplet.sqrt;
import static processing.core.PApplet.tan;
import static processing.core.PConstants.PI;
import static processing.core.PConstants.TWO_PI;

import java.util.ArrayList;

import drehsystem3d.Listener.OnAnimationFinishedListener;
import drehsystem3d.Listener.OnClickListener;
import processing.core.PApplet;
import processing.core.PVector;

public class Button extends TextView
{
	OnClickListener onClickListener = null;
	OnAnimationFinishedListener onAnimationFinishedListener = null;
	boolean clickAnimationVisible = false;
	PVector clickAnimationPos = null;
	long clickAnimationStartTime = 0;
	long clickAnimationLastTime = 0;
	int clickAnimationSize = 0;

	Button(PApplet context)
	{
		super(context);
		init();
	}
	
	Button(PApplet context, float x, float y)
	{
		super(context, x, y);
		init();
	}

	Button(PApplet context, float x, float y, int width, int height)
	{
		super(context, x, y, width, height);
		init();
	}

	Button(PApplet context, PVector pos)
	{
		super(context, pos);
		init();
	}

	Button(PApplet context, PVector pos, int width, int height)
	{
		super(context, pos, width, height);
		init();
	}

	Button(PApplet context, float x, float y, String text)
	{
		super(context, x, y, text);
		init();
	}

	Button(PApplet context, float x, float y, int width, int height, String text)
	{
		super(context, x, y, width, height, text);
		init();
	}

	Button(PApplet context, PVector pos, String text)
	{
		super(context, pos, text);
		init();
	}

	Button(PApplet context, PVector pos, int width, int height, String text)
	{
		super(context, pos, width, height, text);
		init();
	}

	public void init()
	{
		this.textAlignment = TextView.TextAlignment.CENTER;
	}

	ArrayList<PVector> points = new ArrayList<>();

	@Override
	public void draw()
	{
		super.draw();
		if (this.clickAnimationVisible)
		{
			this.context.fill(255 - this.backgroundColor[0], 255 - this.backgroundColor[1], 255 - this.backgroundColor[2], 100);
			this.context.noStroke();
			boolean finished = true;
			if (this.visible)
			{
				this.context.beginShape();
				float x = 0, y = 0;
				if (this.points.size() == 0)
				{
					this.clickAnimationSize = 1;
					for (float angle = 0; angle < TWO_PI; angle += PI / 32)
					{
						x = this.clickAnimationPos.x + this.clickAnimationSize * cos(angle);
						y = this.clickAnimationPos.y + this.clickAnimationSize * sin(angle);
						this.points.add(new PVector(x, y, 0));
					}
				}
				int counter = 0;
				for (float angle = 0; angle < TWO_PI; angle += PI / 32)
				{
					PVector point = this.points.get(counter);
					PVector shapeDimPos = checkShapeDim(point.x, point.y);
					if (shapeDimPos == null)
					{
						x = this.clickAnimationPos.x + this.clickAnimationSize * cos(angle);
						y = this.clickAnimationPos.y + this.clickAnimationSize * sin(angle);
						int size = this.clickAnimationSize;
						while (checkShapeDim(x, y) != null && size > 0)
						{
							size--;
							x = this.clickAnimationPos.x + size * cos(angle);
							y = this.clickAnimationPos.y + size * sin(angle);
						}
						size++;
						x = this.clickAnimationPos.x + size * cos(angle);
						y = this.clickAnimationPos.y + size * sin(angle);
						this.points.set(counter, new PVector(x, y, 0));
						finished = false;
					}
					else
					{
						x = shapeDimPos.x;
						y = shapeDimPos.y;
					}
					// Logger.log("x:"+x);
					// Logger.log("y:"+y);
					counter++;
					this.context.stroke(255);
					this.context.vertex(x, y);
				}
				this.context.endShape();
			}
			if (this.context.millis() - this.clickAnimationLastTime > 1)
			{
				this.clickAnimationSize += 9;
				this.clickAnimationLastTime = this.context.millis();
			}

			if (finished)
			{
				this.clickAnimationVisible = false;
				this.clickAnimationSize = 0;
				this.points = new ArrayList<>();
				if (this.onAnimationFinishedListener != null)
				{
					this.onAnimationFinishedListener.onAnimationFinished();
				}
			}
		}
	}

	public PVector checkShapeDim(float x, float y)
	{
		float xOff = 0;
		float f = 0;
		float xMin = 0;
		float xMax = 0;
		float yMin = 0;
		float yMax = 0;
		if (x <= this.pos.x + this.cornerRadius)
		{
			xMin = this.pos.x;
			if (x < xMin)
			{
				return new PVector(xMin, y);
			}
			xOff = this.cornerRadius - (x - this.pos.x);
			f = this.cornerRadius - sqrt(this.cornerRadius * this.cornerRadius - xOff * xOff);
			yMin = this.pos.y + f;
			yMax = this.pos.y + this.viewHeight - f;
			if (y < yMin)
			{
				return new PVector(x, yMin);
			}
			if (y > yMax)
			{
				return new PVector(x, yMax);
			}
			return null;
		}
		if (x >= this.pos.x + this.viewWidth - this.cornerRadius)
		{
			xMax = this.pos.x + this.viewWidth;
			if (x > xMax)
			{
				return new PVector(xMax, y);
			}
			xOff = x - (xMax - this.cornerRadius);
			// Logger.log("xOff:"+xOff);
			f = this.cornerRadius - sqrt(this.cornerRadius * this.cornerRadius - xOff * xOff);
			// Logger.log("f:"+f);
			yMin = this.pos.y + f;
			yMax = this.pos.y + this.viewHeight - f;
			// Logger.log("yMin:"+yMin);
			// Logger.log("yMax:"+yMax);
			if (y < yMin)
			{
				return new PVector(x, yMin);
			}
			if (y > yMax)
			{
				return new PVector(x, yMax);
			}
			return null;
		}
		xMin = this.pos.x;
		xMax = this.pos.x + this.viewWidth;
		yMin = this.pos.y;
		yMax = this.pos.y + this.viewHeight;
		if (x < xMin)
		{
			return new PVector(xMin, y);
		}
		if (x > xMax)
		{
			return new PVector(xMax, y);
		}
		if (y < yMin)
		{
			return new PVector(x, yMin);
		}
		if (y > yMax)
		{
			return new PVector(x, yMax);
		}
		return null;
	}

	public PVector calcShapeDim(float startX, float startY, float angle)
	{
		float x = startX - this.pos.x;
		float y = startY - abs(x * tan(angle));
		float k = tan(angle);
		float r = this.cornerRadius;
		float distX = (r - sqrt(2 * k * r * r - y + 2 * y * r - 2 * y * k * r) - y * k + k * r) / (k * k + 1);
		float distY = r - sqrt(r * r - (r - distX) * (r - distX));
		return new PVector(this.pos.x + distX, this.pos.y + distY, 0);
	}

	@Override
	public void setOnClickListener(OnClickListener listener)
	{
		this.onClickListener = listener;
	}

	public void setOnAnimationFinishedListener(OnAnimationFinishedListener listener)
	{
		this.onAnimationFinishedListener = listener;
	}

	@Override
	public boolean onMousePressed(int mouseButton)
	{
		float mX = this.context.mouseX;
		float mY = this.context.mouseY;
		if (mX >= this.pos.x && mX <= this.pos.x + this.viewWidth && mY >= this.pos.y
				&& mY <= this.pos.y + this.viewHeight)
		{
			this.clicked = true;
			this.clickAnimationVisible = true;
			this.clickAnimationPos = new PVector(mX, mY, 0);
			this.clickAnimationStartTime = this.context.millis();
			this.clickAnimationLastTime = this.context.millis();
			if (this.onClickListener != null)
			{
				this.onClickListener.onClick(this.id);
			}
		}
		else
		{
			this.clicked = false;
		}
		return this.clicked;
	}
}
