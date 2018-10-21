package drehsystem3d;

import java.util.ArrayList;

import drehsystem3d.Listener.OnClickListener;
import drehsystem3d.Listener.OnItemClickListener;
import processing.core.PApplet;
import processing.core.PVector;

public class MenuItem extends View
{
	
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

	MenuItem(PApplet context, float x, float y, String title, String[] values)
	{
		this(context, x + standardOffset, y + standardOffset, standardWidth, standardHeight, title, values);
	}

	MenuItem(PApplet context, float x, float y, int w, int h, String title, String[] values)
	{
		super(context, x + standardOffset, y + standardOffset, w, h);
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
		this.margin.setSpacing(10);
		this.startPosX = (int) this.pos.x + this.margin.getSpacingX();
		this.startPosY = (int) this.pos.y + this.margin.getSpacingY();
		this.textviews = new ArrayList<>();
		this.values = values;
		for (int i = 0; i < this.values.length; i++)
		{
			final TextView tv = new TextView(this.context, this.startPosX, this.startPosY, this.tvWidth, this.tvHeight);
			tv.setText(this.values[i]);
			tv.setTextAlignment(TextView.TextAlignment.CENTER);
			tv.setTextSize(15);
			tv.setMargin(10);
			tv.setId(i + 1);
			tv.setTextColor(0);
			tv.setBackgroundgAlpha(255);
			tv.setBackground(150);
			tv.setStrokeColor(0);
			tv.setStrokeWeight(0);
			tv.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(int id)
				{
					Logger.log(this, "clicked on menu item " + id);
					if (MenuItem.this.context.mouseButton == Drehsystem3d.LEFT)
					{
						if (MenuItem.this.visible)
						{
							MenuItem.this.visible = false;
							if (MenuItem.this.onItemClickListener != null)
							{
								MenuItem.this.onItemClickListener.onItemClick(id, tv.getText());
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
		int nWidth = 2 * this.margin.getSpacingX();
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
		int nHeight = this.margin.getSpacingY();
		for (TextView tv : this.textviews)
		{
			nHeight += tv.viewHeight + 1;
			Logger.log(this, "viewHeight:" + tv.viewHeight);
		}
		nHeight += this.margin.getSpacingY();
		this.viewHeight = nHeight;
		Logger.log(this, "this.viewHeight:" + this.viewHeight);
	}

	public void calcPos()
	{
		int xMax = this.context.width - this.viewWidth - 10;
		int yMax = this.context.height - this.viewHeight - 10;
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
			Logger.log(this, "\nprevious pos:" + tv.pos);
			tv.setPos(new PVector(tv.pos.x + offsetX, tv.pos.y + offsetY, 0));
			Logger.log(this, "pos update:" + tv.pos);
		}
	}

	public void setOnItemClickListener(OnItemClickListener listener)
	{
		this.onItemClickListener = listener;
	}

	@Override
	public void draw()
	{
		// super.draw();
		if (this.visible)
		{
			this.context.fill(150, 150, 150, 255);
			this.context.stroke(0);
			this.context.strokeWeight(1);
			this.context.rect(this.pos.x, this.pos.y, this.viewWidth, this.viewHeight);
			for (int i = 0; i < this.textviews.size(); i++)
			{
				TextView tv = this.textviews.get(i);
				if (tv.hovered && tv.backgroundColor == 150)
				{
					tv.setBackground(255 - tv.backgroundColor);
				}
				else if (!tv.hovered)
				{
					tv.setBackground(150);
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