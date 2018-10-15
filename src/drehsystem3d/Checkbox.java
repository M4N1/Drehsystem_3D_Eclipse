package drehsystem3d;

import java.util.ArrayList;

import processing.core.PApplet;

public class Checkbox extends View
{
	ArrayList<Checkbox> group = new ArrayList<>();;
	private int posX = 0;
	private int posY = 0;
	private int size = 0;
	private boolean checked = false;
	String text = "";

	Checkbox(PApplet context, int posX, int posY, int size)
	{
		super(context, posX, posY, size, size);
		init(posX, posY, size, "", null);
	}

	Checkbox(PApplet context, int posX, int posY, int size, String text)
	{
		super(context, posX, posY, size, size);
		init(posX, posY, size, text, null);
	}

	Checkbox(PApplet context, int posX, int posY, int size, ArrayList<Checkbox> group)
	{
		super(context, posX, posY, size, size);
		init(posX, posY, size, "", group);
	}

	Checkbox(PApplet context, int posX, int posY, int size, String text, ArrayList<Checkbox> group)
	{
		super(context, posX, posY, size, size);
		init(posX, posY, size, text, group);
	}

	private void init(int posX, int posY, int size, String text, ArrayList<Checkbox> group)
	{
		this.posX = posX;
		this.posY = posY;
		this.size = size;
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
		if (this.context.mouseX >= this.posX && this.context.mouseX <= this.posX + this.size
				&& this.context.mouseY >= this.posY && this.context.mouseY <= this.posY + this.size)
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
		this.context.textSize(this.size);
		this.context.rect(this.posX, this.posY, this.size, this.size);
		if (this.checked)
		{
			this.context.line(this.posX, this.posY, this.posX + this.size, this.posY + this.size);
			this.context.line(this.posX, this.posY + this.size, this.posX + this.size, this.posY);
		}
		if (!this.text.equals(""))
		{
			this.context.fill(255);
			this.context.text(this.text, this.posX + this.size + 10, this.posY + this.size);
		}
	}
}
