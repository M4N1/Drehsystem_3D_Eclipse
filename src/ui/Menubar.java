package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import drehsystem3d.Global;
import drehsystem3d.Listener.OnClickListener;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import ui.TextView.TextAlignment;

public class Menubar extends View
{
	List<List<Button>> menuItems = new ArrayList<List<Button>>();
	private Color menuColor = new Color(255, 255, 255);
	private Color submenuColor = new Color(255, 255, 255);
	private Color itemHoveredColor = new Color(200, 200, 200);
	private int mainTextSize = 20;
	private int subTextSize = 17;
	private int mainTextColor = 0;
	private int subTextColor = mainTextColor;

	public Menubar(PApplet context, String name)
	{
		super(context, name);
		this.setWidth(this.canvas.width);
		this.backgroundColor = menuColor;
	}
	
	/**
	 * Adds a menu item to the menu bar.
	 * @param title Displayed title of the button, by which it can be identified.
	 * @param clickEvent Action that is triggered when the item is clicked.
	 */
	public void addMenuItem(String title, Callable<Void> clickEvent)
	{
		List<Button> menuList = new ArrayList<Button>();
		Button b = new Button(this.context, this.name + "_i_" + title);
		b.setText(title);
		b.setCornerRadius(0);
		b.setPaddingX(15);
		b.setPaddingY(5);
		b.setStrokeWeight(1);
		b.setTextSize(this.mainTextSize);
		b.setTextColor(mainTextColor);
		b.setBackgroundColor(menuColor);
		b.setOnHoverAction(new Runnable()
		{
			@Override
			public void run()
			{
				b.backgroundColor = itemHoveredColor;
				for (int j = 1; j < menuList.size(); j++)
					menuList.get(j).setVisibility(true);
			}
		});
		b.setOnHoverEndAction(new Runnable()
		{
			@Override
			public void run()
			{
				b.backgroundColor = menuColor;
				boolean subItemHovered = false;
				for (int j = 1; j < menuList.size(); j++)
				{
					if (menuList.get(j).isHovered())
					{
						subItemHovered = true;
						break;
					}
				}
				if (!subItemHovered)
					for (int j = 1; j < menuList.size(); j++)
						menuList.get(j).setVisibility(false);
			}
		});
		b.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Global.logger.log(Level.FINE, "Menubar item clicked", b.getText());
				if (clickEvent != null)
				{
					try
					{
						clickEvent.call();
					}
					catch (Exception e)
					{
						Global.logger.log(Level.INFO, "Click action for menu item '" + v.name + "' failed!", e);
					}
				}
			}
		});
		if (menuItems.size() > 0)
		{
			b.alignRight(this.menuItems.get(this.menuItems.size()-1).get(0));
		}
		else
		{
			b.setPos(new PVector(b.getMarginX(), b.getMarginY(), 0));
		}
		menuList.add(b);
		this.menuItems.add(menuList);
		this.setHeight(b.viewHeight);
	}
	
	
	/**
	 * Adds a sub menu item for the latest created menu item.
	 */
	public void addMenuSubItem(String title, Runnable clickEvent)
	{
		int i = this.menuItems.size() - 1;
		if (i < 0) return;
		addMenuSubItem(this.menuItems.get(i), title, clickEvent);
	}
	
	/**
	 * Adds a sub menu item for the first menu item with the entered title
	 * @param title Name of the menu item, to which the sub item should be added to.
	 */
	public void addMenuSubItem(String parentTitle, String title, Runnable clickEvent)
	{
		for (List<Button> subItems : this.menuItems)
		{
			Button b = subItems.get(0);
			if (b.text.equals(parentTitle))
			{
				addMenuSubItem(subItems, title, clickEvent);
				return;
			}
		}
	}
	
	private void addMenuSubItem(List<Button> subItems, String title, Runnable clickEvent)
	{
		Button b = new Button(this.context, this.name + "_si_" + title);
		b.setText(title);
		b.setTextAlignment(TextAlignment.LEFT);
		b.setCornerRadius(0);
		b.setPaddingX(15);
		b.setPaddingY(15);
		b.setMarginY(0);
		b.setStrokeWeight(1);
		b.setTextSize(subTextSize);
		b.setTextColor(subTextColor);
		b.setBackgroundColor(submenuColor);
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
				boolean itemHovered = false;
				for (int i = 0; i < subItems.size(); i++)
				{
					if (subItems.get(i).isHovered())
					{
						itemHovered = true;
						break;
					}
				}
				if (!itemHovered)
					for (int i = 1; i < subItems.size(); i++)
						subItems.get(i).setVisibility(false);
			}
		});
		b.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (clickEvent != null)
				{
					try
					{
						clickEvent.run();
					}
					catch (Exception e)
					{
						Global.logger.log(Level.INFO, "Click action for menu item '" + v.name + "' failed!", e);
					}
				}
				else
				{
					Global.logger.log(Level.FINE, "Sub item '" + b.text + "' from menu '" + subItems.get(0).text + "' clicked");
				}
			}
		});
		b.alignBottom(subItems.get(subItems.size()-1));
		b.setVisibility(false);
		for (int i = 1; i < subItems.size(); i++)
		{
			Button si = subItems.get(i);
			if (si.viewWidth > b.viewWidth)
			{
				b.setWidth(si.viewWidth);
			}
			else if (si.viewWidth < b.viewWidth)
			{
				si.setWidth(b.viewWidth);
			}
		}
		subItems.add(b);
	}
	
	/**
	 * Removes the first menu item and its sub items, with the given title, from the menu bar
	 * @param title The title of the menu item to remove
	 * @return Whether the removal was successful, or not.
	 */
	public boolean removeMenuItem(String title)
	{
		for (int i = 0; i < this.menuItems.size(); i++)
		{
			if (this.menuItems.get(i).get(0).text == title)
			{
				this.menuItems.remove(i);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes the menu item and its sub items from the menu bar.
	 * @param item The item to remove.
	 * @return Whether the removal was successful, or not.
	 */
	public boolean removeMenuItem(View item)
	{
		for (int i = 0; i < this.menuItems.size(); i++)
		{
			if (this.menuItems.get(i).get(0) == item)
			{
				this.menuItems.remove(i);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onMousePressed(int mouseButton)
	{
		for (List<Button> subItems : this.menuItems)
		{
			for (Button b : subItems)
			{
				b.onMousePressed(mouseButton);
			}			
		}
		return super.onMousePressed(mouseButton);
	}
	
	@Override
	public void draw(PGraphics canvas)
	{
		super.draw(canvas);
		for (List<Button> subItems : this.menuItems)
		{
			Button b = subItems.get(0);
			int h = b.viewHeight + 2 * b.getMarginX();
			if (h > this.viewHeight)
				this.viewHeight = h;
		}
		this.canvas.beginDraw();
		this.canvas.fill(this.backgroundColor.r, this.backgroundColor.g, this.backgroundColor.b, this.backgroundColor.a);
		this.canvas.noStroke();
		this.canvas.rect(0, 0, this.width, this.height);
		this.canvas.strokeWeight(1);
		this.canvas.stroke(255);
		this.canvas.line(0, this.height, this.width, this.height);
		this.canvas.endDraw();
		
		for (List<Button> subItems : this.menuItems)
		{
			for (Button b : subItems)
			{
				b.draw();
			}			
		}
	}
}
