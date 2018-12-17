package ui;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public class Container extends View
{
	protected List<View> children = new ArrayList<>();
	protected int strokeWeight = 1;
	protected Color strokeColor = new Color(255);
	
	public Container(PApplet context, String name)
	{
		super(context, name);
		this.visible = false;
	}
	
	public void addChild(View v)
	{
		this.children.add(v);
		v.setParent(this);
	}
	
	public boolean removeChild(View v)
	{
		if (v.parent == this) v.parent = null;
		return this.children.remove(v);
	}

	@Override
	public boolean isClicked()
	{
		return (this.context.mouseX >= this.pos.x && this.context.mouseX <= this.pos.x + this.viewWidth
				&& this.context.mouseY >= this.pos.y && this.context.mouseY <= this.pos.y + this.viewHeight);
	}

	@Override
	public boolean isHovered()
	{
		return (this.context.mouseX >= this.pos.x && this.context.mouseX <= this.pos.x + this.viewWidth
				&& this.context.mouseY >= this.pos.y && this.context.mouseY <= this.pos.y + this.viewHeight);
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
			this.context.strokeWeight(this.strokeWeight);
			this.context.stroke(this.strokeColor.r, this.strokeColor.g, this.strokeColor.b, this.strokeColor.a);
			this.context.fill(this.backgroundColor.r, this.backgroundColor.g, this.backgroundColor.b, this.backgroundColor.a);
			this.context.rect(this.pos.x, this.pos.y, this.viewWidth, this.viewHeight);
			
			this.children.forEach((c) -> {
				c.draw();
			});
		}
	}
}
