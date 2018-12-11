package drehsystem3d;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.Action;

import drehsystem3d.Listener.OnClickListener;
import processing.core.PApplet;
import processing.core.PVector;

public class Menubar extends View
{
	List<Button> menuItems = new ArrayList<Button>();
	private int[] menuColor = new int[] {255, 255, 255};
	private int[] itemHoveredColor = new int[] {200, 200, 200};

	Menubar(PApplet context)
	{
		super(context);
		this.setWidth(this.context.displayWidth);
		this.backgroundColor = menuColor;
	}
	
	public void addMenuItem(String title, Action clickEvent)
	{
		Button b = new Button(this.context);
		b.setText(title);
		b.setCornerRadius(0);
		b.setPaddingX(15);
		b.setPaddingY(5);
		b.setStrokeWeight(1);
		b.setBackgroundColor(menuColor);
		b.setTextColor(0);
		b.setTextSize(20);
		b.setOnHoverAction(new Runnable()
		{
			@Override
			public void run()
			{
				b.backgroundColor = itemHoveredColor;
			}
		});
		b.setOnHoverEndAction(new Runnable()
		{
			@Override
			public void run()
			{
				b.backgroundColor = menuColor;
			}
		});
		b.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(int id)
			{
				Global.logger.log(Level.FINE, "Menubar item clicked");
				
			}
		});
		if (menuItems.size() > 0)
		{
			b.alignRight(this.menuItems.get(this.menuItems.size()-1));
		}
		else
		{
			b.setPos(new PVector(b.getMarginX(), b.getMarginY(), 0));
		}
		this.menuItems.add(b);
		this.setHeight(b.viewHeight);
	}
	
	public boolean removeMenuItem(View item)
	{
		return this.menuItems.remove(item);
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
	public boolean onMousePressed(int mouseButton)
	{
		for (Button b : menuItems)
		{
			b.onMousePressed(mouseButton);
		}
		return super.onMousePressed(mouseButton);
	}

	@Override
	public void draw()
	{
		for (Button b : menuItems)
		{
			int h = b.viewHeight + 2 * b.getMarginX();
			if (h > this.viewHeight)
				this.viewHeight = h;
		}
		this.context.fill(this.backgroundColor[0], this.backgroundColor[1], this.backgroundColor[2], this.backgroundAlpha);
		this.context.noStroke();
		this.context.rect(0, 0, this.viewWidth, this.viewHeight);
		this.context.strokeWeight(1);
		this.context.stroke(255);
		this.context.line(0, this.viewHeight, this.viewWidth, this.viewHeight);
		
		for (View v : menuItems)
		{
			v.draw();
		}
	}
}
