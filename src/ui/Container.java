package ui;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
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
		v.setParent(this);
	}
	
	public boolean removeChild(View v)
	{
		if (v.container == this) v.container = null;
		return this.children.remove(v);
	}
	
	@Override
	public boolean onMousePressed(int mouseButton)
	{
		super.onMousePressed(mouseButton);
		this.children.forEach((v) -> {
			v.onMousePressed(mouseButton);
		});		
		return this.clicked;
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
	public boolean onKeyPressed(int keyCode, char key)
	{
		super.onKeyPressed(keyCode, key);
		this.children.forEach((v) -> {
			v.onKeyPressed(keyCode, key);
		});
		return this.clicked;
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
	public void draw()
	{
		super.draw();
		if (this.visible)
		{
			this.canvas.beginDraw();
			this.canvas.background(this.backgroundColor.r, this.backgroundColor.g, this.backgroundColor.b, this.backgroundColor.a);
			this.children.forEach((c) -> {
				c.draw(this.canvas);
			});
			this.canvas.endDraw();
			
			this.context.image(this.canvas, this.pos.x, this.pos.y);
		}
	}
}
