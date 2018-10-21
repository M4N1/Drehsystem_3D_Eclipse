package drehsystem3d;

import static processing.core.PApplet.atan;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.dist;
import static processing.core.PApplet.sin;
import static processing.core.PConstants.DISABLE_DEPTH_TEST;
import static processing.core.PConstants.ENABLE_DEPTH_TEST;
import static processing.core.PConstants.PI;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class Point
{

	private PApplet context;
	Point parent = null;
	private final int id;
	private boolean newPosReceived = false;
	PVector setPos;
	private PVector absSetPos;
	private PVector lastPos;
	PVector pos;
	private final int size = 10;
	private PVector lastV = null;
	PVector a = null;
	PVector v = null;
	PVector setW = null;
	private PVector w = null;
	private PVector wAbs = null;
	float setAlpha = 0;
	private float alpha = 0;
	private float[] phi = new float[] { 0, 0, 0 };
	private float drawSpeed = 1;
	private long startTime = 0;
	private long lastEllapsedTime = 0;
	private float scale = 1;
	private float scaleD = 40;
	private boolean setup = true;
	private boolean reset = false;
	String name = "";
	private boolean visibilityL = true;
	private boolean visibilityV = true;
	private boolean visibilityA = false;
	boolean visibilityPath = false;
	private boolean finishedPath = false;
	private ArrayList<Point> childs = new ArrayList<>();
	private ArrayList<PVector> path = new ArrayList<>();
	private int[] pathColor = { 255, 255, 255 };
	private int pathEntryCount = 0;

	Point(PApplet context, int id, Point parent, float amp, float[] angle, PVector w, float alpha)
	{
		this(context, id, "", parent, getPosFromAngle(amp, angle), w, alpha);
	}

	Point(PApplet context, int id, String name, Point parent, float amp, float[] angle, PVector w, float alpha)
	{
		this(context, id, name, parent, getPosFromAngle(amp, angle), w, alpha);
	}

	public static PVector getPosFromAngle(float amp, float[] angle)
	{
		if (angle.length != 2)
		{
			return null;
		}
		float x = amp * cos(angle[0]) * cos(angle[1]);
		float y = amp * sin(angle[1]);
		float z = amp * sin(angle[0]) * cos(angle[1]);
		return new PVector(x, y, z);
	}

	Point(PApplet context, int id, Point parent, PVector pos, PVector w, float alpha)
	{
		this(context, id, "", parent, pos, w, alpha);
	}

	Point(PApplet context, int id, String name, Point parent, PVector pos, PVector w, float alpha)
	{
		this.context = context;
		this.id = id;
		this.name = name;
		this.parent = parent;
		this.setPos = new PVector(pos.x, pos.y, pos.z);
		this.setW = new PVector(w.x, w.y, w.z);
		this.setAlpha = alpha;
		this.lastV = new PVector(0, 0, 0);
		this.v = new PVector(0, 0, 0);
		this.a = new PVector(0, 0, 0);
		
		Logger.log(this, "\n\nCreated point '" + name + "'\n");
		Logger.log(this, "Parent object:\t\t" + (parent == null ? "null" : "'" + parent.name + "'"));
		Logger.log(this, "Initial position:\t" + setPos);
		Logger.log(this, "Initial omega:\t\t" + setW);
		Logger.log(this, "Initial alpha:\t\t" + setAlpha);
		calcPos();
	}

	public int getId()
	{
		return this.id;
	}

	public void setPos(float x, float y, float z)
	{
		setPos(new PVector(x, y, z));
	}

	public void setPos(PVector pos)
	{
		this.setPos = new PVector(pos.x, pos.y, pos.z);
		this.newPosReceived = true;
	}

	public void setW(PVector w)
	{
		this.setW = getVector(w);
	}

	public void setAlpha(float alpha)
	{
		this.setAlpha = alpha;
	}

	public String getName()
	{
		return this.name;
	}

	public void drawPath()
	{
		drawPath(true);
	}

	public void drawPath(boolean visible)
	{
		this.visibilityPath = visible;
	}

	public boolean getPathVisibility()
	{
		return this.visibilityPath;
	}

	public void setPathColor(int[] c)
	{
		this.pathColor = c;
	}

	public int[] getPathColor()
	{
		return this.pathColor;
	}

	public void initPos(PVector pos)
	{
		if (this.parent != null)
		{
			this.absSetPos = new PVector(pos.x + this.parent.pos.x, pos.y + this.parent.pos.y,
					pos.z + this.parent.pos.z);
		}
		else
		{
			this.absSetPos = new PVector(pos.x, pos.y, pos.z);
		}
		this.lastPos = new PVector(pos.x, pos.y, pos.z);
		this.pos = new PVector(pos.x, pos.y, pos.z);
		if (this.parent != null)
		{
			PVector parentPos = new PVector(this.parent.pos.x, this.parent.pos.y, this.parent.pos.z);
			this.lastPos.add(parentPos);
			this.pos.add(parentPos);
			this.wAbs = new PVector(this.parent.wAbs.x + this.w.x, this.parent.wAbs.y + this.w.y,
					this.parent.wAbs.z + this.w.z);
		}
		else
		{
			this.wAbs = new PVector(this.w.x, this.w.y, this.w.z);
		}
	}

	public void calcPos()
	{
		this.w = new PVector(this.setW.x, this.setW.y, this.setW.z);
		this.alpha = this.setAlpha;
		initPos(this.setPos);
		update();
		Logger.log(this, "\nPosition calculation finished:" + this.pos);
	}

	public void moveToStart()
	{
		this.reset = true;
		this.pathEntryCount = 0;
		resetTime();
		calcPos();
	}

	public void reset()
	{
		initPos(this.setPos);
		this.newPosReceived = false;
		moveToStart();
		this.path = new ArrayList<>();
		this.finishedPath = false;
	}

	public void setDrawSpeed(float speed)
	{
		this.drawSpeed = speed;
	}

	public void setVisibilityL(boolean visible)
	{
		this.visibilityL = visible;
	}

	public void setVisibilityV(boolean visible)
	{
		this.visibilityV = visible;
	}

	public void setVisibilityA(boolean visible)
	{
		this.visibilityA = visible;
	}

	public void setScale(float scale)
	{
		this.scale = scale;
	}

	public void setScaleD(float scale)
	{
		this.scaleD = scale;
	}

	public void clearPath()
	{
		this.path = new ArrayList<>();
		this.pathEntryCount = 0;
		this.finishedPath = false;
	}

	public void update()
	{
		update((this.context.millis() - this.startTime) * this.drawSpeed);
	}

	public void update(float dTime)
	{
		double ellapsedTime = (dTime + this.lastEllapsedTime);
		if (this.setup || this.reset)
		{
			ellapsedTime = 0;
		}
		if (ellapsedTime != 0 || this.setup || this.reset)
		{
			this.lastPos = getVector(this.pos);
			this.lastV = getVector(this.v);
			this.w.x += this.alpha * this.drawSpeed;
			this.w.y += this.alpha * this.drawSpeed;
			this.w.z += this.alpha * this.drawSpeed;
			PVector position = getVector(this.pos);
			if (this.parent != null)
			{
				this.wAbs = new PVector(this.parent.wAbs.x + this.w.x, this.parent.wAbs.y + this.w.y,
						this.parent.wAbs.z + this.w.z);
				position = position.sub(this.parent.pos);
			}
			else
			{
				this.wAbs = getVector(this.w);
			}

			this.pos = rotateV(getVector(this.w), position, ellapsedTime);

			if (this.parent != null)
			{
				PVector p = getVector(this.pos).mult(-1);
				this.v = this.w.cross(p);
				this.pos = this.pos.add(this.parent.pos);
				Point lastParent = this.parent;
				for (;;)
				{
					Point parent = lastParent.parent;
					if (parent == null)
					{
						break;
					}
					else
					{
						p = getVector(this.pos);
						this.v = this.v.add(lastParent.w.cross(p.sub(parent.pos).mult(-1)));
						lastParent = parent;
					}
				}
				if (!this.setup && !this.reset)
				{
					PVector velocity = new PVector(this.v.x, this.v.y, this.v.z);
					PVector pos = getVector(this.pos);
					this.a = pos.sub(velocity).sub(this.lastPos.sub(this.lastV)).mult(1000 / dTime);
					this.a.add(this.pos);
				}
				else
				{
					this.a = new PVector(0, 0, 0);
				}
			}

			if (this.visibilityPath && !this.reset)
			{
				boolean distanceCheck = false;
				if (this.pathEntryCount < this.path.size())
				{
					this.path.set(this.pathEntryCount, new PVector(this.pos.x, this.pos.y, this.pos.z));
					Logger.log(this, "Override path entry");
				}
				else
				{
					this.path.add(new PVector(this.pos.x, this.pos.y, this.pos.z));
					final int minData = 100;
					if (this.path.size() > 5000 || this.finishedPath)
					{
						this.path.remove(0);
					}
					if (this.path.size() > minData)
					{
						distanceCheck = true;
						for (int i = 0; i < 6; i++)
						{
							PVector p1 = this.path.get(this.path.size() - (minData / 2 - 1) - i);
							PVector p2 = this.path.get(i);
							float d = dist(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
							if (d > 0.7f)
							{
								distanceCheck = false;
								break;
							}
						}
					}
					if (distanceCheck)
					{
						this.finishedPath = true;
					}
				}
				this.pathEntryCount++;
			}
			this.setup = false;
			this.reset = false;
		}
	}

	public boolean mousePressedEvent(float mX, float mY)
	{
		float d = dist(mX, mY, this.pos.x * this.scaleD, this.pos.y * this.scaleD);
		if (d <= this.size / 2)
		{
			return true;
		}
		return false;
	}

	public ArrayList<PVector> getPath()
	{
		return this.path;
	}

	public void draw()
	{
		draw(this.context);
	}

	public void draw(PApplet context)
	{
		if (this.newPosReceived)
		{
			initPos(this.setPos);
			this.newPosReceived = false;
		}
		context.fill(255);
		context.stroke(255);
		context.strokeWeight(1);
		PVector scaledPos = new PVector(this.pos.x, this.pos.y, this.pos.z);
		scaledPos = scaledPos.mult(this.scaleD);

		if (this.visibilityL)
		{
			if (this.parent != null)
			{
				PVector scaledParentPos = new PVector(this.parent.pos.x, this.parent.pos.y, this.parent.pos.z);
				scaledParentPos = scaledParentPos.mult(this.scaleD);
				context.line(scaledParentPos.x, scaledParentPos.y, scaledParentPos.z, scaledPos.x, scaledPos.y,
						scaledPos.z);
			}
		}

		context.strokeWeight(2);
		if (this.visibilityV)
		{
			context.stroke(0, 0, 255);
			context.line(scaledPos.x, scaledPos.y, scaledPos.z, scaledPos.x + this.v.x * this.scale,
					scaledPos.y + this.v.y * this.scale, scaledPos.z + this.v.z * this.scale);
		}
		if (this.visibilityA)
		{
			context.stroke(255, 0, 0);
			context.line(scaledPos.x, scaledPos.y, scaledPos.z, scaledPos.x + this.a.x * this.scale,
					scaledPos.y + this.a.y * this.scale, scaledPos.z + this.a.z * this.scale);
		}
		// if (this.parent != null)
		// {
		// context.stroke(51);
		// context.strokeWeight(4);
		// PVector start = getVector(this.parent.pos);
		// context.stroke(255, 0, 0);
		// context.line(start.x * this.scaleD, start.y *
		// this.scaleD,
		// start.z * this.scaleD, start.x * this.scaleD
		// + this.w.x,
		// start.y * this.scaleD + this.w.y,
		// start.z * this.scaleD + this.w.z);
		// }
		context.pushMatrix();
		context.translate(scaledPos.x, scaledPos.y, scaledPos.z);
		if (this.visibilityPath)
		{
			context.fill(this.pathColor[0], this.pathColor[1], this.pathColor[2]);
		}
		else
		{
			context.fill(255, 255, 255, 50);
		}
		context.lights();
		context.noStroke();
		try
		{
			context.sphere(10);
		}
		catch (Exception e)
		{

		}
		context.popMatrix();
		context.hint(DISABLE_DEPTH_TEST);
		context.fill(255);
		if (!this.name.equals(""))
		{
			context.text(this.name, scaledPos.x + this.size, scaledPos.y + this.size, scaledPos.z + this.size);
		}
		context.hint(ENABLE_DEPTH_TEST);
	}

	public void stopTime()
	{
		this.lastEllapsedTime += (this.context.millis() - this.startTime) * this.drawSpeed;
	}

	public void startTime()
	{
		this.startTime = this.context.millis();
	}

	public void resetTime()
	{
		this.lastEllapsedTime = 0;
		startTime();
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public PVector getVector(PVector v)
	{
		return new PVector(v.x, v.y, v.z);
	}

	public PVector rotateV(PVector a, PVector vector, double ellapsedTime)
	{
		PVector result = new PVector(0, 0, 0);
		PVector position = getVector(vector);
		PVector axis = getVector(a);

		PVector normalizedAxis = getVector(a).normalize();
		PVector pn = cross(normalizedAxis, position);
		pn = cross(normalizedAxis, pn).mult(-1);
		this.context.strokeWeight(2);
		
//		 if (this.parent != null)
//		 {
//		 this.context.stroke(51);
//		 this.context.line(this.parent.pos.x * this.scaleD, this.parent.pos.y
//		 * this.scaleD,
//		 this.parent.pos.z * this.scaleD, (pn.x + this.parent.pos.x) *
//		 this.scaleD,
//		 (pn.y + this.parent.pos.y) * this.scaleD, (pn.z + this.parent.pos.z)
//		 * this.scaleD);
//		 }

		// if (this.parent != null)
		// {
		// this.context.stroke(51);
		// this.context.line(this.parent.pos.x * this.scaleD, this.parent.pos.y
		// * this.scaleD,
		// this.parent.pos.z * this.scaleD, (axis.x + this.parent.pos.x) *
		// this.scaleD,
		// (axis.y + this.parent.pos.y) * this.scaleD, (axis.z +
		// this.parent.pos.z) * this.scaleD);
		// }

		float angle = (float) (a.mag() * ellapsedTime / 1000 * PI / 180);

		PVector offset = position.sub(pn);
		position = getVector(vector);

		ArrayList<PVector> positions = new ArrayList<>();
		for (int i = 0; i < this.childs.size(); i++)
		{
			Point child = this.childs.get(i);
			PVector startPos = getVector(child.absSetPos);
			PVector relPos = getVector(child.pos);
			if (this.parent != null)
			{
				relPos.sub(this.parent.pos);
				startPos.sub(this.pos);
			}
			positions.add(relPos);
		}
		float alphaX = atan(this.w.y / this.w.z);
		if (this.w.y != 0)
		{
			float alpha = alphaX;
			axis = rotateVX(axis, alpha);
			pn = rotateVX(pn, alpha);
			for (int i = 0; i < positions.size(); i++)
			{
				PVector childPos = positions.get(i);
				positions.set(i, rotateVX(childPos, alpha));
			}
		}
		float alphaY = -atan(axis.x / axis.z);
		if (this.w.x != 0)
		{
			float alpha = alphaY;
			axis = rotateVY(axis, alpha);
			pn = rotateVY(pn, alpha);
			for (int i = 0; i < positions.size(); i++)
			{
				PVector childPos = positions.get(i);
				positions.set(i, rotateVY(childPos, alpha));
			}
		}
		if (axis.z > 0)
		{
			angle *= -1;
		}
		pn = rotateVZ(pn, angle);

		for (int i = 0; i < positions.size(); i++)
		{
			PVector childPos = positions.get(i);
			positions.set(i, rotateVZ(childPos, angle));
		}

		if (this.w.x != 0)
		{
			float alpha = -alphaY;
			axis = rotateVY(axis, alpha);
			pn = rotateVY(pn, alpha);
			for (int i = 0; i < positions.size(); i++)
			{
				PVector childPos = positions.get(i);
				positions.set(i, rotateVY(childPos, alpha));
			}
		}
		if (this.w.y != 0)
		{
			float alpha = -alphaX;
			axis = rotateVX(axis, alpha);
			pn = rotateVX(pn, alpha);
			for (int i = 0; i < positions.size(); i++)
			{
				PVector childPos = positions.get(i);
				positions.set(i, rotateVX(childPos, alpha));
			}
		}
		result = pn.add(offset);
		
		
		for (int i = 0; i < positions.size(); i++)
		{
			PVector newPos = getVector(positions.get(i));
			if (this.parent != null)
			{
				newPos.add(this.parent.pos);
			}
			this.childs.get(i).updatePos(newPos);
		}
//		if (this.parent != null)
//		{
//			this.context.strokeWeight(4);
//			PVector start = getVector(this.parent.pos);
//			this.context.stroke(51);
//			this.context.line(start.x * this.scaleD, start.y * this.scaleD, start.z * this.scaleD,
//					(start.x + offset.x) * this.scaleD, (start.y + offset.y) * this.scaleD,
//					(start.z + offset.z) * this.scaleD);
//			this.context.line((start.x + offset.x) * this.scaleD, (start.y + offset.y) * this.scaleD,
//					(start.z + offset.z) * this.scaleD, (start.x + pn.x) * this.scaleD, (start.y + pn.y) * this.scaleD,
//					(start.z + pn.z) * this.scaleD);
//		}
		return result;
	}

	private PVector cross(PVector a, PVector b)
	{
		return getVector(a).cross(b);
	}

	public void addChild(Point p)
	{
		this.childs.add(p);
		if (this.parent != null)
		{
			this.parent.addChild(p);
		}
	}

	public void removeChilds()
	{
		this.childs = new ArrayList<>();
	}

	public boolean removeChild(Point p)
	{
		if (!this.childs.contains(p))
		{
			return false;
		}
		this.childs.remove(p);
		return true;
	}

	public PVector rotateVX(PVector p, float alpha)
	{
		float x = p.x;
		float y = p.y;
		float z = p.z;
		p.x = x;
		p.y = y * cos(alpha) - z * sin(alpha);
		p.z = y * sin(alpha) + z * cos(alpha);
		return getVector(p);
	}

	public PVector rotateVY(PVector p, float alpha)
	{
		float x = p.x;
		float y = p.y;
		float z = p.z;
		p.x = z * sin(alpha) + x * cos(alpha);
		p.y = y;
		p.z = z * cos(alpha) - x * sin(alpha);
		return getVector(p);
	}

	public PVector rotateVZ(PVector p, float alpha)
	{
		float x = p.x;
		float y = p.y;
		float z = p.z;
		p.x = x * cos(alpha) - y * sin(alpha);
		p.y = x * sin(alpha) + y * cos(alpha);
		p.z = z;
		return getVector(p);
	}

	public void updatePos(PVector pos)
	{
		this.pos = pos;
	}

	public PVector getPos()
	{
		return this.pos;
	}
}