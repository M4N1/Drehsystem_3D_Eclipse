package ui;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Container extends View
{
	protected List<View> children = new ArrayList<>();
	protected int strokeWeight = 1;
	protected Color strokeColor = new Color(255);
	
	public Container(PApplet context, String name, PVector pos, int w, int h)
	{
		super(context, name, pos, w, h);
		this.visible = false;
		this.canvas = this.context.createGraphics(w, h);
	}
	
	public void addChild(View v)
	{
		this.children.add(v);
		v.setPositioning(View.Positioning.RELATIVE);
		v.setContainer(this);
		v.setCanvas(this.canvas);
	}
	
	public View getChild(String name)
	{
		for (View v : this.children)
			if (v.name.equals(name)) return v;
		return null;
	}
	
	public boolean removeChild(View v)
	{
		if (v == null) return false;
		if (v.container == this)
		{
			v.container = null;
			v.setPositioning(View.Positioning.ABSOLUTE);
		}
		return this.children.remove(v);
	}
	
	@Override
	public void setVisibility(boolean visible)
	{
		super.setVisibility(visible);
		this.children.forEach((v) -> {
			v.setVisibility(visible);
		});	
	}
	
	@Override
	public void setCanvas(PGraphics canvas)
	{
		super.setCanvas(canvas);
		this.children.forEach((v) -> {
			v.setCanvas(canvas);
		});	
	}
	
	@Override
	public View isPressed()
	{
		View childPressed = super.isPressed();
		for(View v : this.children)
		{
			View current = v.isPressed();
			childPressed = current != null ? current : childPressed;
		}
		return childPressed;
	}
	
	@Override
	public void onMousePressed(int mouseButton)
	{
		super.onMousePressed(mouseButton);
	}
	
	@Override
	public void onMouseReleased(int mouseButton)
	{
		super.onMouseReleased(mouseButton);
		this.children.forEach((v) -> {
			v.onMouseReleased(mouseButton);
		});
	}
	
	@Override
	public void onMouseDragged()
	{
		super.onMouseDragged();
		this.children.forEach((v) -> {
			v.onMouseDragged();
		});
	}
	
	@Override
	public boolean onKeyPressed(int keyCode, char key, boolean repeat)
	{
		boolean clicked = super.onKeyPressed(keyCode, key, repeat);
		for (View v : this.children)
			clicked = clicked || v.onKeyPressed(keyCode, key, repeat);
		return clicked;
	}
	
	@Override
	public boolean onKeyReleased(int keyCode, char key)
	{
		super.onKeyReleased(keyCode, key);
		this.children.forEach((v) -> {
			v.onKeyReleased(keyCode, key);
		});
		return this.clicked;
	}
	
	@Override
	public View updateHoverState()
	{
		View childHovered = super.updateHoverState();
		for (View c : this.children)
		{
			View current = c.updateHoverState();
			childHovered = current != null ? current : childHovered;
		}
		return childHovered;
	}
	
	@Override
	public void draw()
	{
		//this.update(canvas);
		if (this.visible)
		{
			this.canvas.hint(PApplet.DISABLE_DEPTH_TEST);
			this.canvas.beginDraw();
			this.canvas.background(this.backgroundColor.r, this.backgroundColor.g, this.backgroundColor.b, this.backgroundColor.a);
			this.children.forEach((c) -> {
				c.draw();
			});
			this.canvas.endDraw();
			this.canvas.hint(PApplet.ENABLE_DEPTH_TEST);
			
			PVector pos = this.getActualPos();
			this.context.image(this.canvas, pos.x, pos.y);
		}
	}
}
