package drehsystem3d;

import static drehsystem3d.Global.DEBUG;
import static processing.core.PApplet.atan;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.dist;
import static processing.core.PApplet.sin;
import static processing.core.PConstants.DISABLE_DEPTH_TEST;
import static processing.core.PConstants.ENABLE_DEPTH_TEST;
import static processing.core.PConstants.PI;

import java.util.ArrayList;
import java.util.logging.Level;

import processing.core.PApplet;
import processing.core.PVector;

public class Point
{

	private static boolean restrictPathLength = true;
	private static int maxPathLength = 5000;
	
	private PVector offset = new PVector(0, 0, 0);
	private PVector pn = new PVector(0, 0, 0);
	private PApplet context;
	private final int id;
	private boolean newPosReceived = false;
	private PVector absSetPos;
	private PVector lastAbsPos;
	private final int size = 10;
	private PVector lastV = null;
	private PVector w = null;
	private PVector wAbs = null;
	private float alpha = 0;
	private float drawSpeed = 1;
	private float scale = 1;
	private float scaleD = 40;
	private boolean setup = true;
	private boolean reset = false;
	private boolean visibilityL = true;
	private boolean visibilityV = true;
	private boolean visibilityA = false;
	boolean visibilityPath = false;
	private boolean finishedPath = false;
	private ArrayList<Point> childs = new ArrayList<>();
	private ArrayList<PVector> path = new ArrayList<>();
	private int[] pathColor = { 255, 255, 255 };
	private int pathEntryCount = 0;
	private String logPrefix = "";

	public Point parent = null;
	public PVector setPos;
	public PVector pos;
	public PVector absPos;
	public PVector a = null;
	public PVector v = null;
	public PVector setW = null;
	public float setAlpha = 0;
	public String name = "";

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
		if (angle.length != 2) { return null; }
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
		this.logPrefix = "(Point " + name + ") ";
		this.parent = parent;
		this.setPos = pos.copy();
		this.setW = w.copy();
		this.setAlpha = alpha;
		this.lastV = new PVector(0, 0, 0);
		this.v = new PVector(0, 0, 0);
		this.a = new PVector(0, 0, 0);

		Global.logger.log(Level.FINER, logPrefix + "Created point");
		Global.logger.log(Level.FINER, logPrefix + "Parent object", (parent == null ? "null" : "'" + parent.name + "'"));
		Global.logger.log(Level.FINER, logPrefix + "Initial position", setPos);
		Global.logger.log(Level.FINER, logPrefix + "Initial omega", setW);
		Global.logger.log(Level.FINER, logPrefix + "Initial alpha", setAlpha);
		setup();
	}
	
	public static void restrictPathLength(boolean state)
	{
		Point.restrictPathLength = state;
	}
	
	public static void restrictPathLength(int amount)
	{
		Point.maxPathLength = amount;
	}
	
	public static boolean IspathRestricted()
	{
		return Point.restrictPathLength;
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
		this.setW = w.copy();
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
		this.absSetPos = pos.copy();
		this.pos = pos.copy();
		this.lastAbsPos = pos.copy();
		this.absPos = pos.copy();
		this.wAbs = this.w.copy();
		
		if (this.parent != null)
		{
			this.absSetPos.add(this.parent.absPos);
			this.lastAbsPos.add(this.parent.absPos);
			this.absPos.add(this.parent.absPos);
			this.wAbs.add(this.parent.wAbs);
		}
	}

	public void setup()
	{
		this.w = this.setW.copy();
		this.alpha = this.setAlpha;
		
		initPos(this.setPos);
		update(0, 0);
		Global.logger.log(Level.FINEST, logPrefix + "Abs. set pos", this.absSetPos);
		Global.logger.log(Level.FINEST, logPrefix + "Abs. pos", this.absPos);
		Global.logger.log(Level.FINE, logPrefix + "Finished setup!");
		if (Global.logger.isLoggable(Level.FINER))
		{
			System.out.print("\n\n");
		}
		this.reset = false;
		this.setup = false;
	}

	public void moveToStart()
	{
		this.reset = true;
		this.pathEntryCount = 0;
		setup();
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

	public void update(float dTime, double ellapsedTime)
	{
		calcNewPos(dTime, ellapsedTime);
		updatePath();
	}

	private void calcNewPos(float dTime, double ellapsedTime)
	{
		this.lastAbsPos = this.absPos.copy();
		this.lastV = this.v.copy();
		float speedIncrease = this.alpha * this.drawSpeed;
		this.w.x += speedIncrease;
		this.w.y += speedIncrease;
		this.w.z += speedIncrease;
		if (this.parent != null)
		{
			this.wAbs = this.parent.wAbs.copy().add(this.w);
		}
		else
		{
			this.wAbs = this.w.copy();
		}

		PVector absPosParent = this.parent == null ? new PVector(0, 0, 0) : this.parent.absPos.copy();
		
		// Rotate relative position
		this.pos = rotateV(this.w.copy(), this.pos.copy(), ellapsedTime, true);
		this.absPos = this.pos.copy().add(absPosParent);
		
		for (int i = 0; i < this.childs.size(); i++)
		{
			Point child = this.childs.get(i);
			PVector pos = child.absPos.copy().sub(absPosParent);
			child.absPos = rotateV(this.w.copy(), pos.copy(), ellapsedTime, false);
			child.absPos.add(absPosParent);
			child.pos = child.absPos.copy();
			if (child.parent != null)
			{
				child.pos.sub(child.parent.absPos);
			}			
		}
		
		if (this.parent != null)
		{
			this.v = this.w.cross(this.pos.copy().mult(-1));
			
			Point lastParent = this.parent;
			PVector p = this.absPos.copy();
			for (;;)
			{
				Point parent = lastParent.parent;
				if (parent == null)
				{
					break;
				}
				else
				{
					this.v.add(lastParent.w.cross(p.copy().sub(parent.pos).mult(-1)));
					lastParent = parent;
				}
			}
			if (!this.setup && !this.reset)
			{
				this.a = this.v.copy().sub(this.lastV).mult(1/dTime);
			}
			else
			{
				this.a = new PVector(0, 0, 0);
			}
		}
		Global.logger.log(Level.FINEST, logPrefix + "Pos", this.pos);
		Global.logger.log(Level.FINEST, logPrefix + "Mag", this.pos.mag());
		Global.logger.log(Level.FINEST, logPrefix + "Abs. Pos", this.absPos);
	}

	private void updatePath()
	{
		if (this.visibilityPath && !this.reset)
		{
			boolean distanceCheck = false;
			if (this.pathEntryCount < this.path.size())
			{
				this.path.set(this.pathEntryCount, this.absPos.copy());
				Global.logger.log(Level.FINER, "Override path entry");
			}
			else
			{
				this.path.add(this.absPos.copy());
				final int minData = 100;
				if ((this.path.size() > Point.maxPathLength && Point.restrictPathLength) || this.finishedPath)
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
						if (d > 0.5f)
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
	}

	public boolean mousePressedEvent(float mX, float mY)
	{
		float d = dist(mX, mY, this.pos.x * this.scaleD, this.pos.y * this.scaleD);
		if (d <= this.size / 2) { return true; }
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
		PVector scaledPos = this.absPos.copy();
		scaledPos = scaledPos.mult(this.scaleD);

		if (this.visibilityL)
		{
			if (this.parent != null)
			{
				PVector scaledParentPos = this.parent.absPos.copy();
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
			PVector scaledAcceleration = this.a.copy().mult(this.scale);
			
			context.stroke(255, 0, 0);
			context.line(scaledPos.x, scaledPos.y, scaledPos.z, scaledPos.x + scaledAcceleration.x,
					scaledPos.y + scaledAcceleration.y, scaledPos.z + scaledAcceleration.z);
		}
		if (DEBUG && this.parent != null)
		{
			// Draw w
			PVector scaledStart = this.parent.absPos.copy().mult(this.scaleD);
			
			context.strokeWeight(4);
			context.stroke(255, 0, 0);
			context.line(scaledStart.x, scaledStart.y, scaledStart.z,
					scaledStart.x + this.w.x, scaledStart.y + this.w.y,
					scaledStart.z + this.w.z);
						
			// Draw offset between normal vector and w
			this.context.strokeWeight(4);
			this.context.stroke(51);
			
			PVector start = this.parent.absPos.copy();
			PVector scaledOffsetStart = start.copy().mult(this.scaleD);
			PVector scaledOffsetEnd = start.copy().add(offset).mult(this.scaleD);
			
			this.context.line(scaledOffsetStart.x, scaledOffsetStart.y, scaledOffsetStart.z,
					scaledOffsetEnd.x, scaledOffsetEnd.y,
					scaledOffsetEnd.z);
			
			// Draw normal vector from w
			PVector scaledPnStart = start.copy().add(offset).mult(this.scaleD);
			PVector scaledPnEnd = start.copy().add(pn).mult(this.scaleD);
			this.context.line(scaledPnStart.x, scaledPnStart.y, scaledPnStart.z,
					scaledPnEnd.x, scaledPnEnd.y,
					scaledPnEnd.z);
		}
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

	public void setName(String name)
	{
		this.name = name;
	}

	private PVector rotateV(PVector a, PVector vector, double ellapsedTime, boolean store)
	{
		PVector result = new PVector(0, 0, 0);
		PVector position = vector.copy();
		PVector axis = a.copy();

		PVector normalizedAxis = a.copy().normalize();
		PVector pn = cross(normalizedAxis, position);
		pn = cross(normalizedAxis, pn).mult(-1);

		float angle = (float) (a.mag() * ellapsedTime / 1000 * PI / 180);

		PVector offset = position.sub(pn);
		position = vector.copy();

		float alphaX = atan(this.w.y / this.w.z);
		float alphaY = -atan(axis.x / axis.z);
		
		rotatePointsVX(axis, pn, alphaX);
		rotatePointsVY(axis, pn, alphaY);
		
		rotatePointsVZ(axis, pn, angle);
				
		rotatePointsVY(axis, pn, -alphaY);
		rotatePointsVX(axis, pn, -alphaX);
		result = pn.add(offset);

		if (store)
		{
			this.offset = offset.copy();
			this.pn = pn.copy();
		}
		return result;
	}
	
	private void rotatePointsVZ(PVector axis, PVector pn, float angle)
	{
		if (axis.z > 0)
		{
			angle *= -1;
		}
		pn = rotateVZ(pn, angle);
	}

	private void rotatePointsVX(PVector axis, PVector pn, float angle)
	{
		if (w.y != 0)
		{
			axis = rotateVX(axis, angle);
			pn = rotateVX(pn, angle);
		}
	}

	private void rotatePointsVY(PVector axis, PVector pn, float angle)
	{
		if (w.x != 0)
		{
			axis = rotateVY(axis, angle);
			pn = rotateVY(pn, angle);
		}
	}

	private PVector cross(PVector a, PVector b)
	{
		return a.copy().cross(b);
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
		if (!this.childs.contains(p)) { return false; }
		this.childs.remove(p);
		return true;
	}

	private PVector rotateVX(PVector p, float alpha)
	{
		float x = p.x;
		float y = p.y;
		float z = p.z;
		p.x = x;
		p.y = y * cos(alpha) - z * sin(alpha);
		p.z = y * sin(alpha) + z * cos(alpha);
		return p.copy();
	}

	private PVector rotateVY(PVector p, float alpha)
	{
		float x = p.x;
		float y = p.y;
		float z = p.z;
		p.x = z * sin(alpha) + x * cos(alpha);
		p.y = y;
		p.z = z * cos(alpha) - x * sin(alpha);
		return p.copy();
	}

	private PVector rotateVZ(PVector p, float alpha)
	{
		float x = p.x;
		float y = p.y;
		float z = p.z;
		p.x = x * cos(alpha) - y * sin(alpha);
		p.y = x * sin(alpha) + y * cos(alpha);
		p.z = z;
		return p.copy();
	}

	public void updateAbsPos(PVector pos)
	{
		this.absPos = pos.copy();
		this.pos = pos.copy();
		if (this.parent != null)
		{
			this.pos.sub(this.parent.absPos);
		}
	}

	public PVector getAbsPos()
	{
		return this.absPos;
	}
	
	public PVector getPos()
	{
		return this.pos;
	}
}