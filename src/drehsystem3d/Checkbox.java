package drehsystem3d;

import java.util.ArrayList;

import processing.core.PApplet;

public class Checkbox extends View
{
	ArrayList<Checkbox> group = new ArrayList<>();
	private int padding = 4;
	private boolean checked = false;
	String text = "";

	Checkbox(PApplet context, String text, ArrayList<Checkbox> group)
	{
		this(context, 0, 0, 0, text, group);
	}
	
	Checkbox(PApplet context, int posX, int posY, int size)
	{
		this(context, posX, posY, size, "", null);
	}

	Checkbox(PApplet context, int posX, int posY, int size, String text)
	{
		this(context, posX, posY, size, text, null);
	}

	Checkbox(PApplet context, int posX, int posY, int size, ArrayList<Checkbox> group)
	{
		this(context, posX, posY, size, "", group);
	}

	Checkbox(PApplet context, int posX, int posY, int size, String text, ArrayList<Checkbox> group)
	{
		super(context, posX, posY, size, size);
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
		return false;
	}

	@Override
	public boolean isHovered()
	{
		return false;
	}

	@Override
	public boolean onMousePressed(int mouseButton)
	{
		if (this.context.mouseX >= this.pos.x && this.context.mouseX <= this.pos.x + this.viewWidth
				&& this.context.mouseY >= this.pos.y && this.context.mouseY <= this.pos.y + this.viewHeight)
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
		this.context.noFill();
		this.context.stroke(255);
		this.context.strokeWeight(1);
		this.context.textSize(this.viewHeight);
		this.context.rect(this.pos.x, this.pos.y, this.viewWidth, this.viewHeight);
		if (this.checked)
		{
			this.context.line(this.pos.x + padding, this.pos.y + padding, this.pos.x + this.viewWidth - padding, this.pos.y + this.viewHeight - padding);
			this.context.line(this.pos.x + padding, this.pos.y + this.viewHeight - padding, this.pos.x + this.viewWidth - padding, this.pos.y + padding);
		}
		if (!this.text.equals(""))
		{
			this.context.fill(255);
			this.context.text(this.text, this.pos.x + this.viewWidth + 10, this.pos.y + this.viewHeight);
		}
	}
}
