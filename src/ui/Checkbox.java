package ui;

import java.util.ArrayList;

import processing.core.PApplet;

public class Checkbox extends View
{
	ArrayList<Checkbox> group = new ArrayList<>();
	private int padding = 4;
	private boolean checked = false;
	String text = "";

	public Checkbox(PApplet context, String name, String text, ArrayList<Checkbox> group)
	{
		this(context, name, 0, 0, 0, text, group);
	}
	
	public Checkbox(PApplet context, String name, int posX, int posY, int size)
	{
		this(context, name, posX, posY, size, "", null);
	}

	public Checkbox(PApplet context, String name, int posX, int posY, int size, String text)
	{
		this(context, name, posX, posY, size, text, null);
	}

	public Checkbox(PApplet context, String name, int posX, int posY, int size, ArrayList<Checkbox> group)
	{
		this(context, name, posX, posY, size, "", group);
	}

	public Checkbox(PApplet context, String name, int posX, int posY, int size, String text, ArrayList<Checkbox> group)
	{
		super(context, name, posX, posY, size, size);
		this.text = text;
		if (group != null)
		{
			addGroupMembers(group);
		}
	}

	public void addGroupMember(Checkbox newMember)
	{
		if (!containsMember(newMember))
		{
			this.group.add(newMember);
			newMember.addGroupMember(this);
		}
	}

	public void addGroupMembers(ArrayList<Checkbox> newGroupMembers)
	{
		for (Checkbox member : newGroupMembers)
		{
			if (!containsMember(member))
			{
				this.group.add(member);
				member.addGroupMember(this);
			}
		}
	}

	public void dropMember(Checkbox member)
	{
		this.group.remove(member);
	}

	public boolean containsMember(Checkbox member)
	{
		for (Checkbox c : this.group)
		{
			if (c.equals(member))
			{
				return true;
			}
		}
		return false;
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}

	public boolean isChecked()
	{
		return this.checked;
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
		if (this.clicked)
		{
			this.checked = !this.checked;
			if (this.group != null)
			{
				for (Checkbox c : this.group)
				{
					c.setChecked(false);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void draw()
	{
		super.draw();
		this.canvas.noFill();
		this.canvas.stroke(255);
		this.canvas.strokeWeight(this.hovered ? 2 : 1);
		this.canvas.textSize(this.viewHeight);
		this.canvas.rect(this.pos.x, this.pos.y, this.viewWidth, this.viewHeight);
		this.canvas.strokeWeight(1);
		if (this.checked)
		{
			this.canvas.line(this.pos.x + padding, this.pos.y + padding, this.pos.x + this.viewWidth - padding, this.pos.y + this.viewHeight - padding);
			this.canvas.line(this.pos.x + padding, this.pos.y + this.viewHeight - padding, this.pos.x + this.viewWidth - padding, this.pos.y + padding);
		}
		if (!this.text.equals(""))
		{
			this.canvas.fill(255);
			this.canvas.text(this.text, this.pos.x + this.viewWidth + 10, this.pos.y + this.viewHeight);
		}
	}
}
