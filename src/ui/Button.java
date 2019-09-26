package ui;

import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;
import static processing.core.PApplet.sqrt;
import static processing.core.PConstants.PI;
import static processing.core.PConstants.TWO_PI;

import java.util.ArrayList;
import java.util.logging.Level;

import drehsystem3d.Global;
import drehsystem3d.Listener.OnAnimationFinishedListener;
import drehsystem3d.Listener.OnClickListener;
import processing.core.PApplet;
import processing.core.PVector;

public class Button extends TextView
{
	OnAnimationFinishedListener onAnimationFinishedListener = null;
	boolean clickAnimationVisible = false;
	PVector clickAnimationPos = null;
	long clickAnimationStartTime = 0;
	long clickAnimationLastTime = 0;
	int clickAnimationSize = 0;
	ArrayList<PVector> points = new ArrayList<>();

	public Button(PApplet context, String name)
	{
		super(context, name);
		init();
	}
	
	public Button(PApplet context, String name, float x, float y)
	{
		super(context, name, x, y);
		init();
	}

	public Button(PApplet context, String name, float x, float y, int width, int height)
	{
		super(context, name, x, y, width, height);
		init();
	}

	public Button(PApplet context, String name, PVector pos)
	{
		super(context, name, pos);
		init();
	}

	public Button(PApplet context, String name, PVector pos, int width, int height)
	{
		super(context, name, pos, width, height);
		init();
	}

	public Button(PApplet context, String name, float x, float y, String text)
	{
		super(context, name, x, y, text);
		init();
	}

	public Button(PApplet context, String name, float x, float y, int width, int height, String text)
	{
		super(context, name, x, y, width, height, text);
		init();
	}

	public Button(PApplet context, String name, PVector pos, String text)
	{
		super(context, name, pos, text);
		init();
	}

	public Button(PApplet context, String name, PVector pos, int width, int height, String text)
	{
		super(context, name, pos, width, height, text);
		init();
	}

	public void init()
	{
		this.textAlignment = TextView.TextAlignment.CENTER;
	}

	public PVector checkShapeDim(float x, float y)
	{
		float xOff = 0;
		float f = 0;
		float xMin = 0;
		float xMax = 0;
		float yMin = 0;
		float yMax = 0;
		PVector pos = getActualPos();
		if (x <= pos.x + this.cornerRadius)
		{
			xMin = pos.x;
			if (x < xMin)
			{
				return new PVector(xMin, y);
			}
			xOff = this.cornerRadius - (x - pos.x);
			f = this.cornerRadius - sqrt(this.cornerRadius * this.cornerRadius - xOff * xOff);
			yMin = pos.y + f;
			yMax = pos.y + this.height - f;
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
		if (x >= pos.x + this.width - this.cornerRadius)
		{
			xMax = pos.x + this.width;
			if (x > xMax)
			{
				return new PVector(xMax, y);
			}
			xOff = x - (xMax - this.cornerRadius);
			f = this.cornerRadius - sqrt(this.cornerRadius * this.cornerRadius - xOff * xOff);
			yMin = pos.y + f;
			yMax = pos.y + this.height - f;
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
		xMin = pos.x;
		xMax = pos.x + this.width;
		yMin = pos.y;
		yMax = pos.y + this.height;
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
	public void onMousePressed(int mouseButton)
	{
		super.onMousePressed(mouseButton);
		Global.logger.log(Level.FINEST, "Button '" + this.name + "'", new Object[] {this.clicked, this.pos, this.width, this.height});
		if (this.clicked)
		{
			this.clickAnimationVisible = true;
			this.clickAnimationPos = getRelPos(new PVector(this.context.mouseX, this.context.mouseY));
			this.clickAnimationStartTime = this.context.millis();
			this.clickAnimationLastTime = this.context.millis();
		}
	}
	
	@Override
	public void draw()
	{
		super.draw();
		if (this.clickAnimationVisible)
		{
			
			boolean finished = true;
			if (this.visible)
			{
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
				this.canvas.beginDraw();
				this.canvas.fill(255 - this.backgroundColor.r, 255 - this.backgroundColor.g, 255 - this.backgroundColor.b, 100);
				this.canvas.noStroke();
				this.canvas.beginShape();
				int counter = 0;
				for (float angle = 0; angle < TWO_PI; angle += PI / 32)
				{
					PVector point = this.points.get(counter);
					PVector shapeDimPos = checkShapeDim(point.x, point.y);
					if (shapeDimPos == null)
					{
						int size = this.clickAnimationSize;
						x = this.clickAnimationPos.x + size * cos(angle);
						y = this.clickAnimationPos.y + size * sin(angle);
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
					counter++;
					this.canvas.vertex(x, y);
				}
				this.canvas.endShape();
				this.canvas.endDraw();
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
}
