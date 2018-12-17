package ui;

import java.util.ArrayList;
import java.util.logging.Level;

import drehsystem3d.Drehsystem3d;
import drehsystem3d.Global;
import drehsystem3d.Listener.OnClickListener;
import drehsystem3d.Listener.OnItemClickListener;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class MenuItem extends View
{
	public static int instanceCount = 0;
	static final int standardWidth = 120;
	static final int standardHeight = 200;
	static final int standardOffset = 15;

	OnItemClickListener onItemClickListener;
	int startPosX;
	int startPosY;
	int tvWidth = 120;
	int tvHeight = 40;
	ArrayList<TextView> textviews;
	String[] values;
	String title = "";
	boolean visible = true;

	public MenuItem(PApplet context, String name, float x, float y, String title, String[] values)
	{
		this(context, name, x + standardOffset, y + standardOffset, standardWidth, standardHeight, title, values);
	}

	public MenuItem(PApplet context, String name, float x, float y, int w, int h, String title, String[] values)
	{
		super(context, name, x + standardOffset, y + standardOffset, w, h);
		init(title, values);
	}

	@Override
	public boolean onMousePressed(int mouseButton)
	{
		super.onMousePressed(mouseButton);
		for (TextView tv : this.textviews)
		{
			tv.onMousePressed(mouseButton);
		}
		if (!this.clicked)
		{
			this.visible = false;
		}
		return this.clicked;
	}

	private void init(String title, String[] values)
	{
		this.setMargin(10);
		this.startPosX = (int) this.pos.x + this.getMarginX();
		this.startPosY = (int) this.pos.y + this.getMarginY();
		this.textviews = new ArrayList<>();
		this.values = values;
		for (int i = 0; i < this.values.length; i++)
		{
			final TextView tv = new TextView(this.context, this.name + "_tv_" + i, this.startPosX, this.startPosY, this.tvWidth, this.tvHeight);
			tv.setText(this.values[i]);
			tv.setTextAlignment(TextView.TextAlignment.CENTER);
			tv.setTextSize(15);
			tv.setMargin(10);
			tv.setId(i + 1);
			tv.setTextColor(0);
			tv.setBackgroundgAlpha(255);
			tv.setBackgroundColor(150);
			tv.setStrokeColor(0);
			tv.setStrokeWeight(0);
			tv.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Global.logger.log(Level.FINE, "Clicked on menu item " + v.id);
					if (MenuItem.this.context.mouseButton == Drehsystem3d.LEFT)
					{
						if (MenuItem.this.visible)
						{
							MenuItem.this.visible = false;
							if (MenuItem.this.onItemClickListener != null)
							{
								MenuItem.this.onItemClickListener.onItemClick(v.id, tv.getText());
							}
						}
					}
				}
			});
			this.startPosY += this.tvHeight + 1;
			this.textviews.add(tv);
		}
		this.title = title;
		calcWidth();
		calcHeight();
		calcPos();
	}

	public void calcWidth()
	{
		int nWidth = 2 * this.getMarginX();
		int maxTvWidth = 0;
		for (TextView tv : this.textviews)
		{
			if (tv.viewWidth > maxTvWidth)
			{
				maxTvWidth = tv.viewWidth;
			}
		}
		for (TextView tv : this.textviews)
		{
			tv.setWidth(maxTvWidth);
		}
		nWidth += maxTvWidth;
		this.viewWidth = nWidth;
	}

	public void calcHeight()
	{
		int nHeight = this.getMarginY();
		for (TextView tv : this.textviews)
		{
			nHeight += tv.viewHeight + 1;
		}
		nHeight += this.getMarginY();
		this.viewHeight = nHeight;
	}

	public void calcPos()
	{
		int xMax = this.canvas.width - this.viewWidth - 10;
		int yMax = this.canvas.height - this.viewHeight - 10;
		if (this.pos.x > xMax)
		{
			updatePos(xMax - this.pos.x, 0);
			this.pos.x = xMax;
		}
		if (this.pos.y > yMax)
		{
			updatePos(0, yMax - this.pos.y);
			this.pos.y = yMax;
		}
	}

	public void updatePos(float offsetX, float offsetY)
	{
		for (TextView tv : this.textviews)
		{
			Global.logger.log(Level.FINE, "Previous pos:" + tv.pos);
			tv.setPos(new PVector(tv.pos.x + offsetX, tv.pos.y + offsetY, 0));
			Global.logger.log(Level.FINE, "Pos update:" + tv.pos);
		}
	}

	public void setOnItemClickListener(OnItemClickListener listener)
	{
		this.onItemClickListener = listener;
	}

	@Override
	public void draw(PGraphics canvas)
	{
		super.draw(canvas);
		if (this.visible)
		{
			this.canvas.fill(150, 150, 150, 255);
			this.canvas.stroke(0);
			this.canvas.strokeWeight(1);
			this.canvas.rect(this.pos.x, this.pos.y, this.viewWidth, this.viewHeight);
			for (int i = 0; i < this.textviews.size(); i++)
			{
				TextView tv = this.textviews.get(i);
				if (tv.hovered && tv.backgroundColor.r == 150 && tv.backgroundColor.g == 150 && tv.backgroundColor.b == 150)
				{
					int color = (tv.backgroundColor.r + tv.backgroundColor.g + tv.backgroundColor.b) / 3;
					tv.setBackgroundColor(255 - color);
				}
				else if (!tv.hovered)
				{
					tv.setBackgroundColor(150);
				}
				tv.draw();
			}
		}
	}

	@Override
	public boolean isClicked()
	{
		if (!this.visible)
		{
			return false;
		}
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
}