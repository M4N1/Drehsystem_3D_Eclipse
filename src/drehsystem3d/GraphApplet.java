package drehsystem3d;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import processing.core.PApplet;

public class GraphApplet extends PApplet
{
	
	ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
	float graphAddBuffer;
	boolean bufferLoaded = false;
	float dx = 2;
	float yMax = 0;
	int maxSize = 0;
	float margin = 50;
	float xOffset = margin / 2;
	boolean visible = true;
	boolean waitingForExit = false;
	boolean exited = false;
	String title = "";
	DecimalFormat df = new DecimalFormat("#");
	Drehsystem3d drehsystem3d;

	public GraphApplet(Drehsystem3d drehsystem3d, String title)
	{
		this.drehsystem3d = drehsystem3d;
		String[] args = { "Velocity" };
		PApplet.runSketch(args, this);
		this.title = title;
		surface.setTitle(title);
	}

	public void settings()
	{
		size(1500, 200);
	}

	public void setup()
	{
		df.setRoundingMode(RoundingMode.CEILING);
	}

	@Override
	public void exit()
	{
		this.getSurface().setVisible(false);
		this.visible = false;
		this.waitingForExit = true;
		this.noLoop();
	}

	public void exited()
	{
		this.waitingForExit = false;
		this.exited = true;
	}

	public void pause()
	{
		this.getSurface().setVisible(false);
		this.visible = false;
		for (int i = 0; i < this.dataSets.size(); i++)
		{
			this.dataSets.get(i).reset();
		}
		this.maxSize = 0;
		this.noLoop();
	}

	public void resume()
	{
		this.getSurface().setVisible(true);
		this.visible = true;
		this.exited = false;
		this.loop();
	}

	public boolean isVisible()
	{
		return this.visible;
	}

	public boolean waitingForExit()
	{
		return this.waitingForExit;
	}

	public String getName()
	{
		return this.title;
	}

	public void keyPressed()
	{
		drehsystem3d.handleKeyPressedEvent(keyCode, key);
	}

	public void keyReleased()
	{
		drehsystem3d.handleKeyReleasedEvent(keyCode, key);
	}

	public boolean createDataSet(String dataSetName)
	{
		return createDataSet(dataSetName, 0, 0, 0);
	}

	public boolean createDataSet(String dataSetName, int r, int g, int b)
	{
		for (DataSet d : dataSets)
		{
			if (d.name.equals(dataSetName))
			{
				return false;
			}
		}
		this.dataSets.add(new DataSet(dataSetName));
		this.dataSets.get(this.dataSets.size() - 1).setColor(r, g, b);
		return true;
	}

	public boolean addPoint(String dataSetName, float point)
	{
		for (DataSet d : this.dataSets)
		{
			if (d.name.equals(dataSetName))
			{
				d.addPointToBuffer(point);
				if (d.data.size() + 1 > this.maxSize)
				{
					this.maxSize = d.data.size() + 1;
				}
				return true;
			}
		}
		return false;
	}

	public void addGraphPoint(float point)
	{
		this.graphAddBuffer = point;
		this.bufferLoaded = true;
	}

	public void setDelta(float dx)
	{
		this.dx = dx;
	}

	public void draw()
	{
		background(255);
		noFill();
		int startIdx = 0;
		for (DataSet ds : this.dataSets)
		{
			if (ds.isBufferLoaded())
			{
				ds.addPointFromBuffer();
			}
			Logger.log(this, "data size:" + ds.data.size());
			startIdx = ds.getDataSize() - floor((width - this.margin - 50 * this.dataSets.size()) / (this.dx));
			if (startIdx > 0)
			{
				for (int i = 0; i < startIdx; i++)
					ds.data.remove(0);
			}
			if (ds.getDataSize() > this.maxSize)
			{
				this.maxSize = ds.getDataSize();
			}
		}

		float x = xOffset - 30;
		strokeWeight(1);
		line(0, height / 2, width, height / 2);

		for (DataSet ds : this.dataSets)
		{
			x += 50;
			line(x, 0, x, height);
			float dy = ds.getMax() / 10;
			float y = 0;
			for (int i = 0; i < 11; i++)
			{
				float mappedY = map(y, 0, ds.getMax(), this.margin / 2, height - this.margin / 2);
				line(x - 5, mappedY, x + 5, mappedY);
				fill(0);
				String s = "" + df.format((ds.getMax() - 2 * y));
				text(s, x - textWidth(s) - 4, mappedY + 5);
				y += dy;
			}
		}

		for (int i = 1; i < this.maxSize; i++)
		{
			for (DataSet d : this.dataSets)
			{
				float p1 = 0;
				float p2 = 0;
				if (d.getDataSize() > i)
				{
					p1 = d.getPoint(i - 1);
					p2 = d.getPoint(i);
				}
				p1 = map(p1, -d.yMax, d.yMax, height - this.margin / 2, this.margin / 2);
				p2 = map(p2, -d.yMax, d.yMax, height - this.margin / 2, this.margin / 2);
				stroke(d.r, d.g, d.b);
				strokeWeight(2);
				line(x, p1, x + this.dx, p2);
			}
			x += this.dx;
		}

	}

	private class DataSet
	{
		private ArrayList<Float> data = new ArrayList<Float>();
		private float pointBuffer;
		private boolean bufferLoaded = false;
		private float yMax = 10;
		private String name = "";
		private int r = 0;
		private int g = 0;
		private int b = 0;

		DataSet(String name)
		{
			this.name = name;
		}

		public void reset()
		{
			this.data = new ArrayList<Float>();
			this.yMax = 10;
		}

		public String getName()
		{
			return this.name;
		}

		public int getDataSize()
		{
			return this.data.size();
		}

		public float getPoint(int idx)
		{
			return this.data.get(idx);
		}

		public void setColor(int r, int g, int b)
		{
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public void addPointToBuffer(float point)
		{
			this.pointBuffer = point;
			this.bufferLoaded = true;
		}

		public void addPointFromBuffer()
		{
			this.data.add(this.pointBuffer);
			if (this.pointBuffer > this.yMax)
				this.yMax = this.pointBuffer;
			this.bufferLoaded = false;
		}

		public void removePoint(int idx)
		{
		}

		public float getMax()
		{
			return this.yMax;
		}

		public boolean isBufferLoaded()
		{
			return this.bufferLoaded;
		}
	}
}