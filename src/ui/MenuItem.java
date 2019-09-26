package ui;

import java.util.ArrayList;
import java.util.logging.Level;

import drehsystem3d.Drehsystem3d;
import drehsystem3d.Global;
import drehsystem3d.Listener.OnClickListener;
import drehsystem3d.Listener.OnItemClickListener;
import processing.core.PApplet;
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
	protected Spacing padding = new Spacing();

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
	public void onMousePressed(int mouseButton)
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
	}

	private void init(String title, String[] values)
	{
		this.padding.set(10);
		this.startPosX = 0;
		this.startPosY = 0;
		this.textviews = new ArrayList<>();
		this.values = values;
		for (int i = 0; i < this.values.length; i++)
		{
			final TextView tv = new TextView(this.context, this.name + "_tv_" + i);
			if (i != 0 && this.textviews.size() > 0)
			{
				tv.alignBottom(this.textviews.get(this.textviews.size() - 1));
			}
			else
			{
				tv.setPos(new PVector(this.startPosX, this.startPosY));
			}
			tv.setSize(this.tvWidth, this.tvHeight);
			tv.setContainer(this);
			tv.setText(this.values[i]);
			tv.setTextAlignment(TextView.TextAlignment.CENTER);
			tv.setTextSize(15);
			tv.setMarginX(10);
			if (i == 0)
				tv.setMarginTop(10);
			if (i == this.values.length - 1)
				tv.setMarginBottom(10);
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
		this.setWidth(maxTvWidth + this.padding.getX());
	}

	public void calcHeight()
	{
		View first = this.textviews.get(0);
		View last = this.textviews.get(this.textviews.size()-1);
		int startPos = (int)first.getAbsPos().y;
		int endPos = (int)last.getAbsPos().y + last.getViewHeight();
		this.setHeight(endPos - startPos);
	}

	public void calcPos()
	{
		int xMax = this.canvas.width - this.width - 10;
		int yMax = this.canvas.height - this.height - 10;
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
	public void draw()
	{
		//this.update(canvas);
		if (this.visible)
		{
			PVector pos = getActualPos();
			this.canvas.beginDraw();
			this.canvas.fill(150, 150, 150, 255);
			this.canvas.stroke(0);
			this.canvas.strokeWeight(1);
			this.canvas.rect(pos.x, pos.y, this.width, this.height);
			this.canvas.endDraw();
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
				tv.draw(canvas);
			}
		}
	}
}