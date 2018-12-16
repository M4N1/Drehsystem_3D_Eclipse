package drehsystem3d;

public class Color
{
	public int r, g, b, a;
	
	public Color()
	{
		this(0, 0, 0, 255);
	}
	
	public Color(int r, int g, int b)
	{
		this(r, g, b, 255);
	}
	
	public Color(int r, int g, int b, int a)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public void setColor(int c)
	{
		setColor(c, c, c);
	}
	
	public void setColor(int r, int g, int b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public void setColor(int index, int c)
	{
		switch (index)
		{
		case 0:
			this.r = c;
			break;
			
		case 1:
			this.g = c;
			break;
			
		case 2:
			this.b = c;
			break;
			
		case 3:
			this.a = c;
			break;
		}
	}
	
	public Color clone()
	{
		return new Color(this.r, this.g, this.b, this.a);
	}
}
